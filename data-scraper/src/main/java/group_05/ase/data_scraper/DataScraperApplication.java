package group_05.ase.data_scraper;

import group_05.ase.data_scraper.Service.Implementation.WikiDataScraperService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DataScraperApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(DataScraperApplication.class, args);
		/*WikiDataScraperService service = applicationContext.getBean(WikiDataScraperService.class);


		// String continueToken = "0|387470"; //Karlskirche
		String continueToken = "0|697568"; // Arsenal
		// Sample Dataset: 300 entries - max number of nodes in neo4j-ui
		service.batchSearch(100, 1,"");
		service.batchSearch(200, 1,continueToken);

		service.upsertLinkages();*/
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
