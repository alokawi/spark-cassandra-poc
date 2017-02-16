/**
 * 
 */
package alokawi.poc.cassandra.core;

/**
 * @author alokkumar
 *
 */
public class CassandraDBContext extends DBContext {

	private String tableName;
	private String incrementalColumnName;
	private String incrementalColumnLastLoadedValue;

	public CassandraDBContext() {
		super();
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getIncrementalColumnName() {
		return incrementalColumnName;
	}

	public void setIncrementalColumnName(String incrementalColumnName) {
		this.incrementalColumnName = incrementalColumnName;
	}

	public String getIncrementalColumnLastLoadedValue() {
		return incrementalColumnLastLoadedValue;
	}

	public void setIncrementalColumnLastLoadedValue(String incrementalColumnLastLoadedValue) {
		this.incrementalColumnLastLoadedValue = incrementalColumnLastLoadedValue;
	}

}