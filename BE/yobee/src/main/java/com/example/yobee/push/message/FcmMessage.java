package com.example.yobee.push.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class FcmMessage {
    private boolean validate_only;
    private Message message;



	/** Message
	 * 
	 * @author USER1
	 *
	 */
	@Getter
	@Setter
	@AllArgsConstructor
	public static class Message {
        private Notification notification;
        private String token;
		private Map<String, String> data;

    }

	/** Notification
	 * 
	 * @author USER1
	 *
	 */
	@AllArgsConstructor
	@Getter
	@Setter
    public static class Notification {
        private String title;
        private String body;
        private String image;
    }
}
