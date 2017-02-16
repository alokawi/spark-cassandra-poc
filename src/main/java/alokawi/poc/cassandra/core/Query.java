/**
 * 
 */
package alokawi.poc.cassandra.core;

/**
 * @author alokkumar
 *
 */
public abstract class Query<C> {

	private String query;

	public Query(String query) {
		this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}