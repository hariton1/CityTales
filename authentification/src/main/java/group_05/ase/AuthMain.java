package group_05.ase;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "group_05.ase")
public class AuthMain {
    public static void main(String[] args) {
        SpringApplication.run(AuthMain.class, args);
    }
}
