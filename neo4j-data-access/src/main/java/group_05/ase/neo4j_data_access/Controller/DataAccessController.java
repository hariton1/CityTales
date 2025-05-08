package group_05.ase.neo4j_data_access.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DataAccessController {

    @GetMapping("/health")
    public String healthCheck() {
        return "OK";
    }
}
