/**
 * 
 */
package alokawi.test.videoview;

import java.util.List;

import org.apache.log4j.Logger;

import alokawi.poc.cassandra.core.CassandraConnection;
import alokawi.poc.cassandra.core.CassandraDBContext;
import alokawi.poc.cassandra.core.CassandraQuery;
import alokawi.poc.core.Connection;
import alokawi.poc.exception.QueryExecutionException;
//import alokawi.poc.flume.core.FlumeDataWriter;
import alokawi.poc.videoview.VideoViewEvent;
import alokawi.poc.videoview.VideoViewEventDataGenerator;

/**
 * @author alokkumar
 *
 */
public class CassandraExample {

	Logger logger = Logger.getLogger(getClass());

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
		List<VideoViewEvent> videoViewEvents = eventDataGenerator.generate(numberOfUsers, numberOfVideos,
				numberOfRecords, timeOrigin, timeEnd, viewDurationInterval);

		// or Push directly to Cassandra
		Connection<CassandraDBContext> connection = new CassandraConnection(node, port);
		connection.execute(new CassandraQuery("CREATE KEYSPACE IF NOT EXISTS wootag WITH replication"
				+ " = {'class': 'SimpleStrategy'," + " 'replication_factor': '1'}  AND durable_writes " + "= true;"));

		connection.execute(new CassandraQuery("create table IF NOT EXISTS wootag.video_view (" + "user_id text, \n"
				+ " video_id text, \n" + " session_id text, \n" + " event_start_timestamp bigint, \n"
				+ " view_duration_in_second int, PRIMARY KEY ( user_id, video_id, session_id )" + ");"));

		logger.info("Starting data insert to table");
		connection.insertVideoEvents(videoViewEvents);
		logger.info("Data insert complete");

	}

}
