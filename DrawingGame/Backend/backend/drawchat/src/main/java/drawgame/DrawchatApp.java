package drawgame;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket

public class DrawchatApp {

	public static void main(String[] args) {
		SpringApplication.run(DrawchatApp.class, args);
	}

}