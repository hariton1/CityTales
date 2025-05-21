package group_05.ase.neo4j_data_access.ServiceTests;

import group_05.ase.neo4j_data_access.Service.Implementation.FunFactExtractorService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class FunFactExtractorServiceTest {

    @Test
    public void testExtractFunFact_SampleStories() {
        FunFactExtractorService service = new FunFactExtractorService();
        List<MockStory> stories = List.of(
            new MockStory(
                "Vienna’s Oldest Coffee House",
                "Vienna is famous for its coffee culture. The oldest coffee house opened in 1685. Legend says spies once met here to exchange secrets. Today, it serves the best Sachertorte in town."
            ),
            new MockStory(
                "The Mysterious Tower",
                "In the heart of Vienna stands a tower built by Emperor Maximilian. It was once a prison, then an observatory. People say it is haunted by a ghostly astronomer."
            ),
            new MockStory(
                "Mozart’s Midnight Adventure",
                "Mozart loved midnight walks through the city. One night, he helped a lost child find her parents. The grateful family invited him for strudel. Mozart wrote about this in a letter to his sister."
            )
        );

        String funFact0 = service.extractFunFact(stories.get(0).getText());
        String funFact1 = service.extractFunFact(stories.get(1).getText());
        String funFact2 = service.extractFunFact(stories.get(2).getText());

        assertEquals("The oldest coffee house opened in 1685.", funFact0);
        assertEquals("People say it is haunted by a ghostly astronomer.", funFact1);
        assertEquals("One night, he helped a lost child find her parents.", funFact2);
    }
}
