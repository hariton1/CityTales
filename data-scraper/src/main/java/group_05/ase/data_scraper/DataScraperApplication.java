package group_05.ase.data_scraper;

import group_05.ase.data_scraper.Service.Implementation.WikiDataScraperService;
import group_05.ase.data_scraper.Service.Implementation.WikipediaLinkExtractor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DataScraperApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(DataScraperApplication.class, args);
		WikiDataScraperService service = applicationContext.getBean(WikiDataScraperService.class);

		String continueToken = "0|387470"; //Karlskirche
		// Sample Dataset: 200 entries
		service.batchSearch(500, 4,"");
		service.upsertLinkages();
	}
}
