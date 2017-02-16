/**
 * 
 */
package alokawi.poc.cassandra.demo;

import alokawi.poc.cassandra.core.CassandraConnection;
import alokawi.poc.cassandra.core.CassandraDBContext;
import alokawi.poc.cassandra.core.CassandraQuery;
import alokawi.poc.cassandra.core.Connection;
import alokawi.poc.exception.QueryExecutionException;

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

		Connection<CassandraDBContext> connection = new CassandraConnection(node, port);
		
		connection.execute(new CassandraQuery("Select * from test.words"));
	}

}
