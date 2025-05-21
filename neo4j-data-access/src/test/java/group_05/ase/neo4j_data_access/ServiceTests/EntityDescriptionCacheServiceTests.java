package group_05.ase.neo4j_data_access.ServiceTests;

import group_05.ase.neo4j_data_access.Service.Implementation.EntityDescriptionCacheService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntityDescriptionCacheServiceTests {
    private EntityDescriptionCacheService service = new EntityDescriptionCacheService();


    @Test
    void testAddDescriptionToCache() {
        service.addDescriptionToCache("https://en.wikipedia.org/wiki/Wolfgang_Amadeus_Mozart", "This is a test description");
        assertTrue(service.isInCache("https://en.wikipedia.org/wiki/Wolfgang_Amadeus_Mozart"));
    }

    @Test
    void testGetCachedDescription() {
        service.addDescriptionToCache("https://en.wikipedia.org/wiki/Wolfgang_Amadeus_Mozart", "This is a test description");
        assertEquals("This is a test description", service.getCachedDescription("https://en.wikipedia.org/wiki/Wolfgang_Amadeus_Mozart"));
    }

    @Test
    void testCacheOverflow(){
        for(int i = 0; i < 1000; i++){
            service.addDescriptionToCache(""+ i, ""+i);
        }
        service.addDescriptionToCache("1001", "1001");
        assertTrue(service.isInCache("1001"));
        assertFalse(service.isInCache("0"));
    }
}
