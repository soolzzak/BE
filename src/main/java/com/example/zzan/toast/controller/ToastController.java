package com.example.zzan.toast.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

public class ToastController {

	@Controller
	public class ChatController {
		@MessageMapping("/message")
		@SendTo("/topic/messages")
		public String send(String message) {
			return message;
		}
	}


}
