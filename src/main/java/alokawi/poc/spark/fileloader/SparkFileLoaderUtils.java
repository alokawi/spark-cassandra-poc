/**
 * 
 */
package alokawi.poc.spark.fileloader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;

import com.google.gson.Gson;

import alokawi.poc.cassandra.core.CassandraConnection;
import alokawi.poc.cassandra.core.CassandraDBContext;
import alokawi.poc.cassandra.core.CassandraQuery;
import alokawi.poc.core.Connection;
import alokawi.poc.exception.QueryExecutionException;
import alokawi.poc.videoview.VideoViewEvent;
import alokawi.poc.videoview.VideoViewEventDataGenerator;

/**
 * @author alokkumar
 *
 */
public class SparkFileLoaderUtils implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 * @throws IOException
	 * @throws QueryExecutionException
	 */
	public static void main(String[] args) throws IOException, QueryExecutionException {

		int numberOfUsers = 1000;
		int numberOfVideos = 100;

		int numberOfRecords = 30000;

		// timeRange = 1Feb to 15Feb
		long timeOrigin = 1485907200000L;
		long timeEnd = 1487226374000L;

		// interval k = 5 here
		int viewDurationInterval = 5;

		SparkFileLoaderUtils sparkFileLoaderUtils = new SparkFileLoaderUtils();

		List<VideoViewEvent> videoViewEvents = sparkFileLoaderUtils.populateVideoViewEvents(numberOfUsers,
				numberOfVideos, numberOfRecords, timeOrigin, timeEnd, viewDurationInterval);

		String localFilePath = "/tmp/video_view_event.json";
		sparkFileLoaderUtils.writeVideoViewEventsToFile(videoViewEvents, localFilePath);

		sparkFileLoaderUtils.prepareRDDAndWriteToCassandra(localFilePath);
	}

	private void writeVideoViewEventsToFile(List<VideoViewEvent> videoViewEvents, String localFilePath)
			throws IOException {
		try (FileWriter fileWriter = new FileWriter(localFilePath)) {
			for (VideoViewEvent videoViewEvent : videoViewEvents) {
				fileWriter.write(new Gson().toJson(videoViewEvent) + "\n");
			}
		}
	}

	private List<VideoViewEvent> populateVideoViewEvents(int numberOfUsers, int numberOfVideos, int numberOfRecords,
			long timeOrigin, long timeEnd, int viewDurationInterval) {
		VideoViewEventDataGenerator eventDataGenerator = new VideoViewEventDataGenerator();
		List<VideoViewEvent> videoViewEvents = eventDataGenerator.generate(numberOfUsers, numberOfVideos,
				numberOfRecords, timeOrigin, timeEnd, viewDurationInterval);
		return videoViewEvents;
	}

	private void prepareRDDAndWriteToCassandra(String localFilePath) throws QueryExecutionException {
		SparkConf conf = new SparkConf();
		conf.setAppName("file-loader-poc");
		conf.setMaster("local[*]");

		SparkContext sparkContext = new SparkContext(conf);

		JavaRDD<VideoViewEvent> videoEventRDD = prepareVideoRDD(localFilePath, sparkContext);

		SparkSession sparkSession = SparkSession.builder().getOrCreate();
		SQLContext sqlContext = new SQLContext(sparkSession);

		Dataset<Row> videoEventDF = sqlContext.createDataFrame(videoEventRDD, VideoViewEvent.class);

		videoEventDF.createOrReplaceTempView("videoEventTempView");

		String videoViewCountQuery = "select userId, viewDurationInSeconds, count(*) as view_counts from videoEventTempView group by 1, 2";

		List<Row> collectAsList = sqlContext.sql(videoViewCountQuery).collectAsList();
		// printRows(collectAsList);

		// Push directly to Cassandra
		String tableName = "video_view_count";
		Connection<CassandraDBContext> connection = new CassandraConnection("localhost", 9042);
		writeVideoViewCountResultToCassandra(collectAsList, connection, tableName);

		tableName = "user_view_count";
		String userViewCountQuery = "select userId, viewDurationInSeconds, count(*) as view_counts from videoEventTempView group by 1, 2";

		collectAsList = sqlContext.sql(userViewCountQuery).collectAsList();

		writeUserViewCountResultToCassandra(collectAsList, tableName, connection);

		saveAsParquetFiles(sqlContext, videoViewCountQuery);

	}

	private void writeUserViewCountResultToCassandra(List<Row> collectAsList, String tableName,
			Connection<CassandraDBContext> connection) throws QueryExecutionException {
		connection.execute(new CassandraQuery("DROP table if exists wootag." + tableName + ";"));
		connection.execute(new CassandraQuery("create table IF NOT EXISTS wootag." + tableName + " ("
				+ " user_id text, view_duration_in_second int, view_counts int,"
				+ " PRIMARY KEY ( user_id, view_duration_in_second )" + ");"));

		connection.insertRows(collectAsList, tableName,
				Arrays.asList("user_id", "view_duration_in_second", "view_counts"));
		System.out.println("Output size : " + collectAsList.size());
	}

	private void writeVideoViewCountResultToCassandra(List<Row> collectAsList,
			Connection<CassandraDBContext> connection, String tableName) throws QueryExecutionException {
		connection.execute(new CassandraQuery("CREATE KEYSPACE IF NOT EXISTS wootag WITH replication"
				+ " = {'class': 'SimpleStrategy'," + " 'replication_factor': '1'}  AND durable_writes " + "= true;"));

		connection.execute(new CassandraQuery("DROP table if exists wootag." + tableName + ";"));
		connection.execute(new CassandraQuery("create table IF NOT EXISTS wootag." + tableName + " ("
				+ " video_id text, view_duration_in_second int, view_counts int,"
				+ " PRIMARY KEY ( video_id, view_duration_in_second )" + ");"));

		connection.insertRows(collectAsList, tableName,
				Arrays.asList("video_id", "view_duration_in_second", "view_counts"));
		System.out.println("Output size : " + collectAsList.size());
	}

	@SuppressWarnings("unused")
	private void printRows(List<Row> collectAsList) {
		for (Row row : collectAsList) {
			System.out.println(row.get(0) + "," + row.get(1) + "," + row.get(2));
		}
	}

	private void saveAsParquetFiles(SQLContext sqlContext, String query) {
		sqlContext.sql(query).write().format("parquet")
				.save("video_event_drop_off_table.parquet." + System.currentTimeMillis());
	}

	private JavaRDD<VideoViewEvent> prepareVideoRDD(String localFilePath, SparkContext sparkContext) {
		JavaRDD<VideoViewEvent> videoEventRDD = sparkContext.textFile(localFilePath, 2).toJavaRDD()
				.map(new Function<String, VideoViewEvent>() {
					private static final long serialVersionUID = 1L;

					@Override
					public VideoViewEvent call(String line) throws Exception {
						return new Gson().fromJson(line, VideoViewEvent.class);
					}
				});
		return videoEventRDD;
	}

}
