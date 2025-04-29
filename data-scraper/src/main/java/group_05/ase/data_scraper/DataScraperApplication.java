package group_05.ase.data_scraper;

import group_05.ase.data_scraper.Service.Implementation.WikiDataObjectPersistenceService;
import group_05.ase.data_scraper.Service.Implementation.WikiDataScraperService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DataScraperApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(DataScraperApplication.class, args);
		WikiDataScraperService service = applicationContext.getBean(WikiDataScraperService.class);
		service.batchSearch(10, 10);
		WikiDataObjectPersistenceService persistenceService = applicationContext.getBean(WikiDataObjectPersistenceService.class);
	}
}
