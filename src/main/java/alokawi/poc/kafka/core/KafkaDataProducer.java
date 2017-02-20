/**
 * 
 */
package alokawi.poc.kafka.core;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import alokawi.poc.videoview.VideoViewEvent;

/**
 * @author alokkumar
 *
 */
public class KafkaDataProducer {

	public static void main(String[] args) {
		KafkaDataProducer dataProducer = new KafkaDataProducer();

		Gson gson = new GsonBuilder().create();

		dataProducer.sendData(gson.toJson(new VideoViewEvent()));
	}

	public void sendData(String data) {

		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, String> producer = new KafkaProducer<>(props);

		Map<MetricName, ? extends Metric> metrics = producer.metrics();
		System.out.println(metrics);

		for (int i = 0; i < 100; i++)
			producer.send(new ProducerRecord<String, String>("video_view", data));

		producer.close();

	}

}
