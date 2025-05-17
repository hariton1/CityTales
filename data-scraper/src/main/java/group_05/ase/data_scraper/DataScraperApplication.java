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

		BuildingService buildingService = applicationContext.getBean(BuildingService.class);
		PersonService personService = applicationContext.getBean(PersonService.class);
		EventService eventService = applicationContext.getBean(EventService.class);
		LinkService linkService = applicationContext.getBean(LinkService.class);

		// Scraper-Run: Note you can change the params and comment out sections that you already inserted

		// Initialize Nodes
		System.out.println("events: ");
		eventService.search();
		System.out.println("buildings: ");
		buildingService.search(1000);
		System.out.println("persons: ");
		personService.search(1000);

		// Initialize Links
		System.out.println("linking: ");
		linkService.createLinkages();
	}
}
