/**
 * 
 */
package alokawi.test.videoview;

import java.util.List;

import alokawi.poc.cassandra.core.CassandraConnection;
import alokawi.poc.cassandra.core.CassandraDBContext;
import alokawi.poc.core.Connection;
import alokawi.poc.exception.QueryExecutionException;
import alokawi.poc.flume.core.FlumeDataWriter;
import alokawi.poc.videoview.VideoViewEvent;
import alokawi.poc.videoview.VideoViewEventDataGenerator;

/**
 * @author alokkumar
 *
 */
public class CassandraExample {

	/**
	 * @param args
	 * @throws QueryExecutionException
	 */
	public static void main(String[] args) throws QueryExecutionException {
		CassandraExample cassandraExample = new CassandraExample();
		cassandraExample.loadDataToCassandra("localhost", 9042);
	}

	private void loadDataToCassandra(String node, int port) throws QueryExecutionException {

		int numberOfUsers = 1000;
		int numberOfVideos = 100;

		int numberOfRecords = 100000;

		// timeRange = 1Feb to 15Feb
		long timeOrigin = 1485907200000L;
		long timeEnd = 1487226374000L;

		// interval k = 5 here
		int viewDurationInterval = 5;

		VideoViewEventDataGenerator eventDataGenerator = new VideoViewEventDataGenerator();
		List<VideoViewEvent> generateData = eventDataGenerator.generate(numberOfUsers, numberOfVideos, numberOfRecords,
				timeOrigin, timeEnd, viewDurationInterval);

		
		// Push data to Flume
		FlumeDataWriter flumeDataWriter = new FlumeDataWriter();

		for (VideoViewEvent videoViewEvent : generateData) {
			System.out.println(videoViewEvent);

			flumeDataWriter.sendDataToFlume(videoViewEvent);

		}

		// or Push directly to Cassandra
		Connection<CassandraDBContext> connection = new CassandraConnection(node, port);

		for (VideoViewEvent videoViewEvent : generateData) {
			System.out.println(videoViewEvent);

			connection.insertVideoEvent(videoViewEvent);
		}

	}

}
