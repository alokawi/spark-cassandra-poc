/**
 * 
 */
package alokawi.poc.videoview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import alokawi.poc.utils.WeightedRandom;

/**
 * @author alokkumar
 *
 */
public class VideoViewEventDataGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int numberOfUsers = 1000;
		int numberOfVideos = 100;

		int numberOfRecords = 1000;

		// timeRange = 1Feb to 15Feb
		long timeOrigin = 1485907200000L;
		long timeEnd = 1487226374000L;

		// interval k = 5 here
		int viewDurationInterval = 5;

		VideoViewEventDataGenerator eventDataGenerator = new VideoViewEventDataGenerator();
		eventDataGenerator.generate(numberOfUsers, numberOfVideos, numberOfRecords, timeOrigin, timeEnd,
				viewDurationInterval);

	}

	public List<VideoViewEvent> generate(int numberOfUsers, int numberOfVideos, int numberOfRecords, long timeOrigin,
			long timeEnd, int viewDurationInterval) {

		Random randomUser = new Random();
		Random randomVideo = new Random();

		List<VideoViewEvent> videoViewEvents = new ArrayList<>();

		Map<Integer, Double> weightedMap = prepareDefaultWeightedMap();

		WeightedRandom<Integer> weightedRandom = new WeightedRandom<>(weightedMap, 100);

		for (int i = 0; i < numberOfRecords; i++) {
			VideoViewEvent videoViewEvent = new VideoViewEvent();

			videoViewEvent.setUserId("u-" + randomUser.nextInt(numberOfUsers));
			videoViewEvent.setVideoId("v-" + randomVideo.nextInt(numberOfVideos));
			videoViewEvent.setSessionId("s-" + UUID.randomUUID().toString());

			// Randomly get time from 1Feb to 15Feb duration
			videoViewEvent.setEventStartTimestamp(ThreadLocalRandom.current().nextLong(timeOrigin, timeEnd));

			// Get duration in seconds from 5 to 45 seconds
			videoViewEvent.setViewDurationInSeconds(viewDurationInterval * weightedRandom.next());

			System.out.println(i + " : " + videoViewEvent);
			videoViewEvents.add(videoViewEvent);
		}

		return videoViewEvents;
	}

	private Map<Integer, Double> prepareDefaultWeightedMap() {
		Map<Integer, Double> weightedMap = new HashMap<Integer, Double>();
		weightedMap.put(1, 0.7);
		weightedMap.put(2, 0.6);
		weightedMap.put(3, 0.5);
		weightedMap.put(4, 0.45);
		weightedMap.put(5, 0.4);
		weightedMap.put(6, 0.35);
		weightedMap.put(7, 0.3);
		weightedMap.put(8, 0.2);
		weightedMap.put(9, 0.1);
		return weightedMap;
	}

}
