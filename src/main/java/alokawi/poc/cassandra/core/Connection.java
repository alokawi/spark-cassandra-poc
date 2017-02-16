/**
 * 
 */
package alokawi.poc.cassandra.core;

import alokawi.poc.exception.QueryExecutionException;

/**
 * @author alokkumar
 *
 */
public interface Connection<C> {

	public ResultSet<C> execute(Query<C> query) throws QueryExecutionException;

}
