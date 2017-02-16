/**
 * 
 */
package alokawi.poc.flume.core;

import java.util.HashMap;

import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.JSONEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import alokawi.poc.videoview.VideoViewEvent;

/**
 * @author alokkumar
 *
 */
public class FlumeDataWriter {
	private RpcClient client;

	public void init(String hostname, int port) {
		// Setup the RPC connection
		this.client = RpcClientFactory.getDefaultInstance(hostname, port);
	}

	public FlumeDataWriter() {
		init("localhost", 41414);
	}
	
	public void sendDataToFlume(VideoViewEvent data) {
		JSONEvent jsonEvent = new JSONEvent();
		HashMap<String, String> headers = new HashMap<String, String>();

		Gson gson = new GsonBuilder().create();

		jsonEvent.setHeaders(headers);
		jsonEvent.setBody(gson.toJson(data).getBytes());

		// Send the event
		try {
			client.append(jsonEvent);
		} catch (EventDeliveryException e) {
			e.printStackTrace();
		}
	}

	public void cleanUp() {
		// Close the RPC connection
		client.close();
	}
}
