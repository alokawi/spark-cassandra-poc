/**
 * 
 */
package alokawi.poc.core;

import java.util.List;
import java.util.Map;

import org.apache.spark.sql.Row;

import alokawi.poc.exception.QueryExecutionException;
import alokawi.poc.videoview.VideoViewEvent;

/**
 * @author alokkumar
 *
 */
public interface Connection<C> {

	public ResultSet<C> execute(Query<C> query) throws QueryExecutionException;

	public void insertVideoEvent(VideoViewEvent videoViewEvent) throws QueryExecutionException;

	public void insertVideoEvents(List<VideoViewEvent> videoViewEvents) throws QueryExecutionException;

	public void insertRows(List<Row> collectAsList, String tableName, Map<String, String> columnMeta)
			throws QueryExecutionException;

}
