/**
 * 
 */
package alokawi.poc.exception;

/**
 * @author alokkumar
 *
 */
public class QueryBuilderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public QueryBuilderException() {
	}

	/**
	 * @param message
	 */
	public QueryBuilderException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public QueryBuilderException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public QueryBuilderException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public QueryBuilderException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
