package group_05.ase.qdrant_adapter;

import com.cohere.api.types.EmbedByTypeResponseEmbeddings;
import group_05.ase.qdrant_adapter.Service.TestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class QdrantAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(QdrantAdapterApplication.class, args);

		TestService testService = new TestService();

		// Create collection
		testService.createHistoricPersonsList();
		// 2 Users with different interests
		EmbedByTypeResponseEmbeddings person1Interests = testService.getInterestEmbedding("music, magic flute");
		EmbedByTypeResponseEmbeddings person2Interests = testService.getInterestEmbedding("literature, ns-time");
		EmbedByTypeResponseEmbeddings person3Interests = testService.getInterestEmbedding("architecture");

		// 2 People; 1 - w.a.mozart, 2 - stefan zweig, 1 Place: schönbrun
		testService.upsertHistoricPersons("Wolfgang Amadeus Mozart[a][b] (27 January 1756 – 5 December 1791) was a prolific and influential composer of the Classical period. Despite his short life, his rapid pace of composition and proficiency from an early age resulted in more than 800 works representing virtually every Western classical genre of his time. Many of these compositions are acknowledged as pinnacles of the symphonic, concertante, chamber, operatic, and choral repertoires. Mozart is widely regarded as one of the greatest composers in the history of Western music,[1] with his music admired for its \"melodic beauty, its formal elegance and its richness of harmony and texture\".[2]\n" +
						"\n" +
						"Born in Salzburg, Mozart showed prodigious ability from his earliest childhood. At age five, he was already competent on keyboard and violin, had begun to compose, and performed before European royalty. His father Leopold Mozart took him on a grand tour of Europe and then three trips to Italy. At 17, he was a musician at the Salzburg court but grew restless and travelled in search of a better position. Mozart's search for employment led to positions in Paris, Mannheim, Munich, and again in Salzburg, during which he wrote his five violin concertos, Sinfonia Concertante, and Concerto for Flute and Harp, as well as sacred pieces and masses, the motet Exsultate Jubilate, and the opera Idomeneo, among other works.\n" +
						"\n" +
						"While visiting Vienna in 1781, Mozart was dismissed from his Salzburg position. He stayed in Vienna, where he achieved fame but little financial security. During Mozart's early years in Vienna, he produced several notable works, such as the opera Die Entführung aus dem Serail, the Great Mass in C minor, the \"Haydn\" Quartets and a number of symphonies. Throughout his Vienna years, Mozart composed over a dozen piano concertos, many considered some of his greatest achievements. In the final years of his life, Mozart wrote many of his best-known works, including his last three symphonies, culminating in the Jupiter Symphony, the serenade Eine kleine Nachtmusik, his Clarinet Concerto, the operas The Marriage of Figaro, Don Giovanni, Così fan tutte and The Magic Flute and his Requiem. The Requiem was largely unfinished at the time of his death at age 35, the circumstances of which are uncertain and much mythologised.",1);
		testService.upsertHistoricPersons("Stefan Zweig (/zwaɪɡ, swaɪɡ/ ZWYGHE, SWYGHE;[1] German: [ˈʃtɛfan tsvaɪk] ⓘ or Austrian German pronunciation: [tsvaɪg]; 28 November 1881 – 22 February 1942) was an Austrian writer. At the height of his literary career, in the 1920s and 1930s, he was one of the most widely translated and popular writers in the world.[2]\n" +
				"\n" +
				"Zweig was raised in Vienna, Austria-Hungary. He wrote historical studies of famous literary figures, such as Honoré de Balzac, Charles Dickens, and Fyodor Dostoevsky in Drei Meister (1920; Three Masters), and decisive historical events in Decisive Moments in History (1927). He wrote biographies of Joseph Fouché (1929), Mary Stuart (1935) and Marie Antoinette (Marie Antoinette: The Portrait of an Average Woman, 1932), among others. Zweig's best-known fiction includes Letter from an Unknown Woman (1922), Amok (1922), Fear (1925), Confusion of Feelings (1927), Twenty-Four Hours in the Life of a Woman (1927), the psychological novel Ungeduld des Herzens (Beware of Pity, 1939), and The Royal Game (1941).\n" +
				"\n" +
				"In 1934, as a result of the Nazi Party's rise in Germany and the establishment of the Ständestaat regime in Austria, Zweig emigrated to England and then, in 1940, moved briefly to New York and then to Brazil, where he settled. In his final years, he would declare himself in love with the country, writing about it in the book Brazil, Land of the Future. Nonetheless, as the years passed Zweig became increasingly disillusioned and despairing at the future of Europe, and he and his wife Lotte were found dead of a barbiturate overdose in their house in Petrópolis on 23 February 1942; they had died the previous day. His work has been the basis for several film adaptations. Zweig's memoir, Die Welt von Gestern (The World of Yesterday, 1942), is noted for its description of life during the waning years of the Austro-Hungarian Empire under Franz Joseph I and has been called the most famous book on the Habsburg Empire.[3]",2);

		testService.upsertHistoricPersons("Schönbrunn Palace (German: Schloss Schönbrunn [ˈʃlɔs ʃøːnˈbʁʊn] ⓘ) was the main summer residence of the Habsburg rulers, located in Hietzing, the 13th district of Vienna. The name Schönbrunn (meaning \"beautiful spring\") has its roots in an artesian well from which water was consumed by the court.\n" +
						"\n" +
						"The 1,441-room Baroque palace is one of the most important architectural, cultural, and historic monuments in the country. The history of the palace and its vast gardens spans over 300 years, reflecting the changing tastes, interests, and aspirations of successive Habsburg monarchs. It has been a major tourist attraction since the mid-1950s.[1]",3);
		// matching
		System.out.println("Person 1 likes music and the magic flute");
		testService.matching(person1Interests);

		System.out.println("Person 2 likes literature and is interested in the history of the ns time");
		testService.matching(person2Interests);

		System.out.println("Person 3 likes architecture and is interested in the history of the ns time");
		testService.matching(person3Interests);
	}

}
