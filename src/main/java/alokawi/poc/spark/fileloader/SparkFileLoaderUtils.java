/**
 * 
 */
package alokawi.poc.spark.fileloader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
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

		List<VideoViewEvent> videoViewEvents = populateVideoViewEvents(numberOfUsers, numberOfVideos, numberOfRecords,
				timeOrigin, timeEnd, viewDurationInterval);

		String localFilePath = "/tmp/video_view_event.json";
		writeVideoViewEventsToFile(videoViewEvents, localFilePath);

		SparkFileLoaderUtils cassandraUtils = new SparkFileLoaderUtils();
		cassandraUtils.execute(localFilePath);
	}

	private static void writeVideoViewEventsToFile(List<VideoViewEvent> videoViewEvents, String localFilePath)
			throws IOException {
		try (FileWriter fileWriter = new FileWriter(localFilePath)) {
			for (VideoViewEvent videoViewEvent : videoViewEvents) {
				fileWriter.write(new Gson().toJson(videoViewEvent) + "\n");
			}
		}
	}

	private static List<VideoViewEvent> populateVideoViewEvents(int numberOfUsers, int numberOfVideos,
			int numberOfRecords, long timeOrigin, long timeEnd, int viewDurationInterval) {
		VideoViewEventDataGenerator eventDataGenerator = new VideoViewEventDataGenerator();
		List<VideoViewEvent> videoViewEvents = eventDataGenerator.generate(numberOfUsers, numberOfVideos,
				numberOfRecords, timeOrigin, timeEnd, viewDurationInterval);
		return videoViewEvents;
	}

	private void execute(String localFilePath) throws QueryExecutionException {
		SparkConf conf = new SparkConf();
		conf.setAppName("file-loader-poc");
		conf.setMaster("local[*]");

		SparkContext sparkContext = new SparkContext(conf);

		JavaRDD<VideoViewEvent> videoEventRDD = prepareVideoRDD(localFilePath, sparkContext);

		SparkSession sparkSession = SparkSession.builder().getOrCreate();
		SQLContext sqlContext = new SQLContext(sparkSession);

		Dataset<Row> videoEventDF = sqlContext.createDataFrame(videoEventRDD, VideoViewEvent.class);

		videoEventDF.createOrReplaceTempView("videoEventTempView");

		String query = "select videoId, viewDurationInSeconds, count(*) as view_counts from videoEventTempView group by 1, 2";

		List<Row> collectAsList = sqlContext.sql(query).collectAsList();
		for (Row row : collectAsList) {
			System.out.println(row.get(0) + "," + row.get(1) + "," + row.get(2));
		}

		// Push directly to Cassandra
		Connection<CassandraDBContext> connection = new CassandraConnection("localhost", 9042);
		connection.execute(new CassandraQuery("CREATE KEYSPACE IF NOT EXISTS wootag WITH replication"
				+ " = {'class': 'SimpleStrategy'," + " 'replication_factor': '1'}  AND durable_writes " + "= true;"));

		String tableName = "video_view_count";
		connection.execute(new CassandraQuery("create table IF NOT EXISTS wootag." + tableName + " ("
				+ " video_id text, view_duration_in_second int, view_counts int,"
				+ " PRIMARY KEY ( video_id, view_duration_in_second )" + ");"));

		connection.insertRows(collectAsList, tableName, new HashMap<>());

		System.out.println("Output size : " + collectAsList.size());

		saveAsParquetFiles(sqlContext, query);

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
