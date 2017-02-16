/**
 * 
 */
package alokawi.poc.cassandra.core;

/**
 * @author alokkumar
 *
 */
public abstract class DBContext {
	
	private String dbUrl;
	private String dbHost;
	private String dbPassword;

	public String getDbUrl() {
		return dbUrl;
	}

	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}

	public String getDbHost() {
		return dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	
}
