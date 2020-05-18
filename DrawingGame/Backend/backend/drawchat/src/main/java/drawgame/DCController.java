package drawgame;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import drawgame.ChatInput;
@RestController
@Controller
public class DCController {
	
	@MessageMapping("/chat.register")
	@SendTo("/lobby/public")
	public ChatInput register(@Payload ChatInput input, SimpMessageHeaderAccessor ha) {
		ha.getSessionAttributes().put("player", input.getPlayer());
		return input;
	}

	@MessageMapping("/chat.send")
	@SendTo("/lobby/public")
	public ChatInput sendInput(@Payload ChatInput input) {
		return input;
	}
}
