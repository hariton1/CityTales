package group_05.ase.data_scraper;

import group_05.ase.data_scraper.Service.events.ViennaHistoryWikiEventService;
import group_05.ase.data_scraper.Service.general.ViennaHistoryWikiLinkService;
import group_05.ase.data_scraper.Service.persons.ViennaHistoryWikiPersonService;
import group_05.ase.data_scraper.Service.buildings.ViennaHistoryWikiBuildingService;
import group_05.ase.data_scraper.Old_Scraper.Service.Implementation.WikiDataScraperService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class DataScraperApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(DataScraperApplication.class, args);
		WikiDataScraperService service = applicationContext.getBean(WikiDataScraperService.class);
		ViennaHistoryWikiBuildingService buildingService = applicationContext.getBean(ViennaHistoryWikiBuildingService.class);
		ViennaHistoryWikiPersonService personService = applicationContext.getBean(ViennaHistoryWikiPersonService.class);
		ViennaHistoryWikiLinkService linkService = applicationContext.getBean(ViennaHistoryWikiLinkService.class);

		ViennaHistoryWikiEventService eventService = applicationContext.getBean(ViennaHistoryWikiEventService.class);
		String eventSeed = "https://www.geschichtewiki.wien.gv.at/Kategorie:Ereignisse";

		/*try {
			eventService.scrapeCategory(eventSeed);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		eventService.getAllEvents();*/
		linkService.createLinkRelationshipsFromEvents();

		// Whole thing takes like ~30min
		// init nodes
		//buildingService.search();
		//personService.search();

		// init links
		//linkService.createLinkRelationshipsFromBuildings();
		//linkService.createLinkRelationshipsFromPersons();

		/*// String continueToken = "0|387470"; //Karlskirche
		String continueToken = "0|697568"; // Arsenal
		// Sample Dataset: 300 entries - max number of nodes in neo4j-ui
		service.batchSearch(100, 1,"");
		service.batchSearch(200, 1,continueToken);

		service.upsertLinkages();*/
	}
}
