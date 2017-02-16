/**
 * 
 */
package alokawi.poc.cassandra.core;

import alokawi.poc.core.DBContext;

/**
 * @author alokkumar
 *
 */
public class CassandraDBContext extends DBContext {

	private String tableName;

	public CassandraDBContext() {
		super();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}