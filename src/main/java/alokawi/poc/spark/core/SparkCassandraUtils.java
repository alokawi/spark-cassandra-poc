/**
 * 
 */
package alokawi.poc.spark.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

		String query = "select video_id, view_duration_in_second, count(*) from temptable group by 1, 2";

		List<Row> collectAsList = sqlContext.sql(query).collectAsList();
		for (Row row : collectAsList) {
			System.out.println(row.get(0) + "," + row.get(1) + "," + row.get(2));
		}

		// sqlContext.sql(query).show(1000);

		long startTime = 1485907200000L;
		long endTime = 1487226374000L;

		for (long i = startTime; i <= endTime; i = i + TimeUnit.DAYS.toMillis(1)) {

			dataset.filter(new Column("event_start_timestamp").geq(i))
					.filter(new Column("event_start_timestamp").leq(i + TimeUnit.DAYS.toMillis(1)))
					.groupBy(new Column("view_duration_in_second"), new Column("video_id")).count()
					.orderBy("view_duration_in_second").show(1000);
			sleepDelay();
			
		}

	}

	private void sleepDelay() {
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
