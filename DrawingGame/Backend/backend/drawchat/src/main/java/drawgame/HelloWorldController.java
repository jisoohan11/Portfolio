package drawgame;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController

public class HelloWorldController {
	
	@GetMapping("/hello")
    public String sayHello() {
        return "Hello This is a test \n yay it worked";
    }

}
