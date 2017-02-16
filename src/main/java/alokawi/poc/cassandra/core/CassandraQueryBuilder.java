/**
 * 
 */
package alokawi.poc.cassandra.core;

import alokawi.poc.core.Query;
import alokawi.poc.core.QueryBuilder;
import alokawi.poc.exception.QueryBuilderException;

/**
 * @author alokkumar
 *
 */
public class CassandraQueryBuilder implements QueryBuilder<CassandraDBContext> {

	private static final String SPACE = " ";

	@Override
	public Query<CassandraDBContext> build(CassandraDBContext context) throws QueryBuilderException {

		StringBuilder querySB = new StringBuilder();
		querySB.append("Select * from").append(SPACE);
		querySB.append(context.getTableName()).append(SPACE);
		
		return new CassandraQuery(querySB.toString());
	}

}