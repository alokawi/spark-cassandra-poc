/**
 * 
 */
package alokawi.poc.videoview;

import java.util.Date;

/**
 * @author alokkumar
 *
 */
public class VideoViewEvent {

	/*- Event(
		 * user_id, 
		 * video_id, 
		 * session_id, 
		 * event_start_timestamp, 
		 * view_duration_in_second
	 * )
	*/

	private String userId;
	private String videoId;
	private String sessionId;
	private long eventStartTimestamp;
	private long viewDurationInSeconds;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public long getEventStartTimestamp() {
		return eventStartTimestamp;
	}

	public void setEventStartTimestamp(long eventStartTimestamp) {
		this.eventStartTimestamp = eventStartTimestamp;
	}

	public long getViewDurationInSeconds() {
		return viewDurationInSeconds;
	}

	public void setViewDurationInSeconds(long viewDurationInSeconds) {
		this.viewDurationInSeconds = viewDurationInSeconds;
	}

	@Override
	public String toString() {
		return "VideoViewEvent [userId=" + userId + ", videoId=" + videoId + ", sessionId=" + sessionId
				+ ", eventStartTimestamp=" + eventStartTimestamp 
				+ ", eventStartTimestampAsDate=" + new Date(eventStartTimestamp)
				+ ", viewDurationInSeconds=" + viewDurationInSeconds
				+ "]";
	}

}
