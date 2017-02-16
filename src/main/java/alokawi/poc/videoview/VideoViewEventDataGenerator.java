/**
 * 
 */
package alokawi.poc.videoview;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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

		int numberOfRecords = 100000;

		// timeRange = 1Feb to 15Feb
		long timeOrigin = 1485907200000L;
		long timeEnd = 1487226374000L;

		// interval k = 5 here
		int viewDurationInterval = 5;

		VideoViewEventDataGenerator eventDataGenerator = new VideoViewEventDataGenerator();
		eventDataGenerator.generate(numberOfUsers, numberOfVideos, numberOfRecords, timeOrigin, timeEnd,
				viewDurationInterval);

	}

	private void generate(int numberOfUsers, int numberOfVideos, int numberOfRecords, long timeOrigin, long timeEnd,
			int viewDurationInterval) {

		Random randomUser = new Random();
		Random randomVideo = new Random();

		List<VideoViewEvent> videoViewEvents = new ArrayList<>();
		for (int i = 0; i < numberOfRecords; i++) {
			VideoViewEvent videoViewEvent = new VideoViewEvent();

			videoViewEvent.setUserId("u-" + randomUser.nextInt(numberOfUsers));
			videoViewEvent.setVideoId("v-" + randomVideo.nextInt(numberOfVideos));
			videoViewEvent.setSessionId("s-" + UUID.randomUUID().toString());

			// Randomly get time from 1Feb to 15Feb duration
			videoViewEvent.setEventStartTimestamp(ThreadLocalRandom.current().nextLong(timeOrigin, timeEnd));

			// Get duration in seconds from 5 to 45 seconds
			videoViewEvent.setViewDurationInSeconds(viewDurationInterval * ThreadLocalRandom.current().nextInt(1, 9));

			System.out.println(i + " : " + videoViewEvent);
			videoViewEvents.add(videoViewEvent);
		}
	}

}
