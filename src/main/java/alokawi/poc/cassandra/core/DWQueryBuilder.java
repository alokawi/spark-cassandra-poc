/**
 * 
 */
package alokawi.poc.cassandra.core;

import alokawi.poc.exception.QueryBuilderException;

/**
 * @author alokkumar
 *
 */
public class DWQueryBuilder implements QueryBuilder<CassandraDBContext> {

	private static final String SPACE = " ";

	@Override
	public Query<CassandraDBContext> build(CassandraDBContext context) throws QueryBuilderException {

		StringBuilder querySB = new StringBuilder();
		querySB.append("Select * from").append(SPACE);
		querySB.append(context.getTableName()).append(SPACE);
		
		return new CassandraQuery(querySB.toString());
	}

}