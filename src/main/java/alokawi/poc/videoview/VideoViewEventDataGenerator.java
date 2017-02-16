/**
 * 
 */
package alokawi.poc.videoview;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author alokkumar
 *
 */
public class VideoViewEventDataGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		int numberOfRecords = 10000;

		VideoViewEventDataGenerator eventDataGenerator = new VideoViewEventDataGenerator();
		eventDataGenerator.generate(numberOfRecords);

	}

	private void generate(int numberOfRecords) {

		Date date = new Date(1485907200000L);

		List<VideoViewEvent> videoViewEvents = new ArrayList<>();
		for (int i = 0; i < numberOfRecords; i++) {
			VideoViewEvent videoViewEvent = new VideoViewEvent();

			videoViewEvent.setUserId("u-" + UUID.randomUUID().toString());
			videoViewEvent.setVideoId("v-" + UUID.randomUUID().toString());
			videoViewEvent.setSessionId("s-" + UUID.randomUUID().toString());

			// Randomly increase and set time
			videoViewEvent.setEventStartTimestamp(date.getTime() + ((i + 1) * (i + 1) * (i + 1)));

			// Get duration in seconds from 5 to 45 seconds
			videoViewEvent.setViewDurationInSeconds(5 * (i % 9) + 5);

			System.out.println(i + " : " + videoViewEvent);
			videoViewEvents.add(videoViewEvent);
		}
	}

}
