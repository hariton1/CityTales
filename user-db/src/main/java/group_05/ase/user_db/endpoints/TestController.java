package group_05.ase.user_db.endpoints;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public String testEndpoint() {
        return "Connection to PostgreDB is working!";
    }
}
