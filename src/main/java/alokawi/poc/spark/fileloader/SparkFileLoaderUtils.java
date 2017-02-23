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

		int numberOfRecords = 3000;

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

		JavaRDD<String> javaRDD = sparkContext.textFile(localFilePath, 2).toJavaRDD();

		javaRDD.filter(new Function<String, Boolean>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Boolean call(String v1) throws Exception {
				return new Gson().fromJson(v1, VideoViewEvent.class).getVideoId().equals(v1);
			}
		}).groupBy(new Function<String, VideoViewEvent>() {
			private static final long serialVersionUID = 1L;

			@Override
			public VideoViewEvent call(String v1) throws Exception {
				return new Gson().fromJson(v1, VideoViewEvent.class);
			}
		}).saveAsTextFile("/tmp/video_view_event.result");

		System.out.println(javaRDD);
	}

}
