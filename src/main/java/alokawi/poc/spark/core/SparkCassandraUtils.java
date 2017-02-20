/**
 * 
 */
package alokawi.poc.spark.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;

/**
 * @author alokkumar
 *
 */
public class SparkCassandraUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SparkCassandraUtils cassandraUtils = new SparkCassandraUtils();
		cassandraUtils.execute();
	}

	@SuppressWarnings("deprecation")
	private void execute() {
		SparkConf conf = new SparkConf();
		conf.setAppName("cassandra-spark-poc");
		conf.setMaster("local[*]");

		SparkContext sparkContext = new SparkContext(conf);

		System.out.println(sparkContext);

		SparkSession sparkSession = SparkSession.builder().appName("cassandra-spark-poc").master("local[*]")
				.getOrCreate();

		SQLContext sqlContext = new SQLContext(sparkSession);

		Map<String, String> options = new HashMap<String, String>();
		options.put("keyspace", "wootag");
		options.put("table", "video_view");

		Dataset<Row> dataset = sqlContext.read().format("org.apache.spark.sql.cassandra").options(options).load()
				.cache();

		dataset.registerTempTable("temptable");
		dataset.filter(new Column("view_duration_in_second").equalTo("15")).show();

		String query = "select * from temptable where video_id = 'v-44'";

		sqlContext.sql(query).show();

		System.out.println(sqlContext);

	}

}
