/**
 
 */
package alokawi.poc.cassandra.core;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ExecutionInfo;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.google.common.collect.Lists;

import alokawi.poc.core.Connection;
import alokawi.poc.core.Query;
import alokawi.poc.core.ResultSet;
import alokawi.poc.exception.QueryExecutionException;
import alokawi.poc.videoview.VideoViewEvent;

/**
 * @author alokkumar
 *
 */
public class CassandraConnection implements Connection<CassandraDBContext> {

	private String node;
	private int port;
	private int batchSize = 500;

	public CassandraConnection(String node, int port) {
		super();
		this.node = node;
		this.port = port;
	}

	@Override
	public ResultSet<CassandraDBContext> execute(Query<CassandraDBContext> query) throws QueryExecutionException {
		try (Cluster cassandraConnection = buildConnection()) {

			final Metadata metadata = cassandraConnection.getMetadata();
			System.out.printf("Connected to cluster: %s", metadata.getClusterName());
			for (final Host host : metadata.getAllHosts()) {
				System.out.printf("Datacenter: %s; Host: %s; Rack: %s", host.getDatacenter(), host.getAddress(),
						host.getRack());
			}

			try (Session session = cassandraConnection.connect()) {

				String queryToExecute = query.getQuery();
				System.out.println(queryToExecute);
				com.datastax.driver.core.ResultSet resultSet = session.execute(queryToExecute);
				printResultSet(resultSet);

				ExecutionInfo executionInfo = resultSet.getExecutionInfo();
				System.out.println(executionInfo);
			}
		}
		// There isn't any resultset for these use-case
		return new CassandraResultSet();
	}

	private void printResultSet(com.datastax.driver.core.ResultSet resultSet) {
		Iterator<Row> iterator = resultSet.iterator();
		while (iterator.hasNext()) {
			Row row = (Row) iterator.next();
			System.out.println(row);
		}
	}

	private Cluster buildConnection() {
		return Cluster.builder().addContactPoint(node).withPort(port).build();
	}

	@Override
	public void insertVideoEvent(VideoViewEvent videoViewEvent) {
		try (Cluster cassandraConnection = buildConnection()) {
			try (Session session = cassandraConnection.connect()) {
				String insertQuery = "insert into wootag.video_view (" + "user_id," + "	video_id, " + "	session_id, "
						+ "	event_start_timestamp, " + "	view_duration_in_second) VALUES (" + "'"
						+ videoViewEvent.getUserId() + "'" + "," + "'" + videoViewEvent.getVideoId() + "'" + "," + "'"
						+ videoViewEvent.getSessionId() + "'" + "," + videoViewEvent.getEventStartTimestamp() + ","
						+ videoViewEvent.getViewDurationInSeconds() + ")";

				System.out.println(insertQuery);
				session.execute(insertQuery);
			}
		}
	}

	@Override
	public void insertVideoEvents(List<VideoViewEvent> videoViewEvents) {
		try (Cluster cassandraConnection = buildConnection()) {
			try (Session session = cassandraConnection.connect()) {

				List<List<VideoViewEvent>> partition = Lists.partition(videoViewEvents, batchSize);
				int total = 0;
				for (List<VideoViewEvent> list : partition) {

					String q = "BEGIN BATCH \n";

					for (VideoViewEvent videoViewEvent : list) {
						String insertQuery = "insert into wootag.video_view (" + "user_id," + "	video_id, "
								+ "	session_id, " + "	event_start_timestamp, "
								+ "	view_duration_in_second) VALUES ";
						insertQuery += "\n (" + "'" + videoViewEvent.getUserId() + "'" + "," + "'"
								+ videoViewEvent.getVideoId() + "'" + "," + "'" + videoViewEvent.getSessionId() + "'"
								+ "," + videoViewEvent.getEventStartTimestamp() + ","
								+ videoViewEvent.getViewDurationInSeconds() + ");\n";
						q += insertQuery;
					}
					session.execute(q + " APPLY BATCH; ");
					total += batchSize;
					System.out.println("Executing batch of " + batchSize + ", Total : " + total);
				}
			}
		}
	}

	@Override
	public void insertRows(List<org.apache.spark.sql.Row> collectAsList, String tableName,
			Map<String, String> columnMeta) throws QueryExecutionException {

		try (Cluster cassandraConnection = buildConnection()) {
			try (Session session = cassandraConnection.connect()) {

				List<List<org.apache.spark.sql.Row>> partition = Lists.partition(collectAsList, batchSize);
				int total = 0;
				for (List<org.apache.spark.sql.Row> list : partition) {

					String q = "BEGIN BATCH \n";

					for (org.apache.spark.sql.Row row : list) {
						String insertQuery = "insert into wootag." + tableName + " ("
								+ "video_id, view_duration_in_second, view_counts) VALUES ";
						insertQuery += "\n (" + "'" + row.getString(0) + "'" + "," + row.getLong(1) + ","
								+ row.getLong(2) + ");\n";
						q += insertQuery;
					}
					session.execute(q + " APPLY BATCH; ");
					total += batchSize;
					System.out.println("Executing batch of " + batchSize + ", Total : " + total);
				}
			}
		}

	}

}