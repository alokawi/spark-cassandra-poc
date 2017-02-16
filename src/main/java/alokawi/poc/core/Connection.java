/**
 * 
 */
package alokawi.poc.core;

import alokawi.poc.exception.QueryExecutionException;
import alokawi.poc.videoview.VideoViewEvent;

/**
 * @author alokkumar
 *
 */
public interface Connection<C> {

	public ResultSet<C> execute(Query<C> query) throws QueryExecutionException;

	public void insertVideoEvent(VideoViewEvent videoViewEvent);

}
