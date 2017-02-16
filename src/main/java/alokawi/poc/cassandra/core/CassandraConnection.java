/**
 * 
 */
package alokawi.poc.cassandra.core;

import java.util.Iterator;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ExecutionInfo;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import alokawi.poc.exception.QueryExecutionException;

/**
 * @author alokkumar
 *
 */
public class CassandraConnection implements Connection<CassandraDBContext> {

	private String node;
	private int port;

	public CassandraConnection(String node, int port) {
		super();
		this.node = node;
		this.port = port;
	}

	@Override
	public ResultSet<CassandraDBContext> execute(Query<CassandraDBContext> query) throws QueryExecutionException {
		try (Cluster cassandraConnection = buildConnection()) {

			final Metadata metadata = cassandraConnection.getMetadata();
			System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
			for (final Host host : metadata.getAllHosts()) {
				System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(), host.getAddress(),
						host.getRack());
			}

			Session session = cassandraConnection.connect();

			com.datastax.driver.core.ResultSet resultSet = session.execute(query.getQuery());
			printResultSet(resultSet);

			ExecutionInfo executionInfo = resultSet.getExecutionInfo();
			System.out.println(executionInfo);

			session.close();
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

}