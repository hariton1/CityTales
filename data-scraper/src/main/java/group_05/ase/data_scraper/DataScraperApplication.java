package group_05.ase.data_scraper;

import group_05.ase.data_scraper.Service.buildings.BuildingService;
import group_05.ase.data_scraper.Service.embeddings.OpenAiService;
import group_05.ase.data_scraper.Service.embeddings.QdrantService;
import group_05.ase.data_scraper.Service.events.EventService;
import group_05.ase.data_scraper.Service.general.ContentService;
import group_05.ase.data_scraper.Service.general.LinkService;
import group_05.ase.data_scraper.Service.persons.PersonService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DataScraperApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(DataScraperApplication.class, args);

		BuildingService buildingService = applicationContext.getBean(BuildingService.class);
		PersonService personService = applicationContext.getBean(PersonService.class);
		EventService eventService = applicationContext.getBean(EventService.class);
		LinkService linkService = applicationContext.getBean(LinkService.class);
		ContentService contentService = applicationContext.getBean(ContentService.class);
		OpenAiService openAiService = applicationContext.getBean(OpenAiService.class);
		QdrantService qdrantService = applicationContext.getBean(QdrantService.class);

		// Scraper-Run: Note you can change the params and comment out sections that you already inserted

		qdrantService.deleteCollection("WienGeschichteWikiBuildings");
		qdrantService.createCollection("WienGeschichteWikiBuildings");

		// Initialize Nodes*System.out.println("buildings: ");
		buildingService.search(10);

		float[] interestedInMusic = openAiService.getEmbedding("Hotel");
		System.out.println("matching: .................");
		qdrantService.doMatching(interestedInMusic,"WienGeschichteWikiBuildings",3).forEach(x -> System.out.println(x));

		/*System.out.println("persons: ");
		personService.search(2);
		System.out.println("events: ");
		eventService.search();

		// Initialize Links
		System.out.println("linking: ");
		linkService.createLinkages();*/


		// Testing AI stuff

		/*ConfigurableApplicationContext run = new SpringApplicationBuilder(DataScraperApplication.class).web(WebApplicationType.NONE).run(args);
		run.getBean(OpenAiService.class).doStuff();*/

		// Testing qdrant stuff

	}
}
