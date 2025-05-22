package group_05.ase.data_scraper;

import group_05.ase.data_scraper.Service.events.EventService;
import group_05.ase.data_scraper.Service.general.ContentService;
import group_05.ase.data_scraper.Service.general.LinkService;
import group_05.ase.data_scraper.Service.persons.PersonService;
import group_05.ase.data_scraper.Service.buildings.BuildingService;
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
		ContentService contentService = applicationContext.getBean(ContentService.class);

		// Scraper-Run: Note you can change the params and comment out sections that you already inserted

		// Initialize Nodes
		System.out.println("buildings: ");
		buildingService.search(2);
		System.out.println("persons: ");
		personService.search(2);
		System.out.println("events: ");
		eventService.search();

		// Initialize Links
		System.out.println("linking: ");
		linkService.createLinkages();
	}
}
