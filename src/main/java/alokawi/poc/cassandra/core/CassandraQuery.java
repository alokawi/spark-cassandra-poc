/**
 * 
 */
package alokawi.poc.cassandra.core;

import alokawi.poc.core.Query;

/**
 * @author alokkumar
 *
 */
public class CassandraQuery extends Query<CassandraDBContext> {

	public CassandraQuery(String query) {
		super(query);
	}

}
