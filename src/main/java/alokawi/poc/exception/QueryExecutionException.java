/**
 * 
 */
package alokawi.poc.exception;

/**
 * @author alokkumar
 *
 */
public class QueryExecutionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public QueryExecutionException() {
	}

	/**
	 * @param message
	 */
	public QueryExecutionException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public QueryExecutionException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public QueryExecutionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public QueryExecutionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
