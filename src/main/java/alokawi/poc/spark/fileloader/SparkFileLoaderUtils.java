/**
 * 
 */
package alokawi.poc.spark.fileloader;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
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
	 */
	public static void main(String[] args) throws IOException {

		int numberOfUsers = 1000;
		int numberOfVideos = 100;

		int numberOfRecords = 30000;

		// timeRange = 1Feb to 15Feb
		long timeOrigin = 1485907200000L;
		long timeEnd = 1487226374000L;

		// interval k = 5 here
		int viewDurationInterval = 5;

		VideoViewEventDataGenerator eventDataGenerator = new VideoViewEventDataGenerator();
		List<VideoViewEvent> videoViewEvents = eventDataGenerator.generate(numberOfUsers, numberOfVideos,
				numberOfRecords, timeOrigin, timeEnd, viewDurationInterval);

		String localFilePath = "/tmp/video_view_event.json";
		try (FileWriter fileWriter = new FileWriter(localFilePath)) {
			for (VideoViewEvent videoViewEvent : videoViewEvents) {
				fileWriter.write(new Gson().toJson(videoViewEvent) + "\n");
			}
		}

		SparkFileLoaderUtils cassandraUtils = new SparkFileLoaderUtils();
		cassandraUtils.execute(localFilePath);
	}

	private void execute(String localFilePath) {
		SparkConf conf = new SparkConf();
		conf.setAppName("file-loader-poc");
		conf.setMaster("local[*]");

		SparkContext sparkContext = new SparkContext(conf);

		JavaRDD<VideoViewEvent> videoEventRDD = sparkContext.textFile(localFilePath, 2).toJavaRDD()
				.map(new Function<String, VideoViewEvent>() {
					private static final long serialVersionUID = 1L;

					@Override
					public VideoViewEvent call(String line) throws Exception {
						return new Gson().fromJson(line, VideoViewEvent.class);
					}
				});

		SparkSession sparkSession = SparkSession.builder().getOrCreate();
		SQLContext sqlContext = new SQLContext(sparkSession);

		Dataset<Row> videoEventDF = sqlContext.createDataFrame(videoEventRDD, VideoViewEvent.class);

		videoEventDF.createOrReplaceTempView("videoEventTempView");

		String query = "select videoId, viewDurationInSeconds, count(*) from videoEventTempView group by 1, 2";

		List<Row> collectAsList = sqlContext.sql(query).collectAsList();
		for (Row row : collectAsList) {
			System.out.println(row.get(0) + "," + row.get(1) + "," + row.get(2));
		}

	}

}
