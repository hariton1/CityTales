package group_05.ase.user_db.endpoints;


import group_05.ase.user_db.restData.PriceDTO;
import group_05.ase.user_db.services.PriceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prices")
public class PriceController {

    private static final Logger logger = LoggerFactory.getLogger(PriceController.class);

    private final PriceService service;

    public PriceController(PriceService service) {
        this.service = service;
    }

    @GetMapping("/find/location_id={location_id}")
    @ResponseStatus(HttpStatus.OK)
    public List<PriceDTO> getPricesByLocation(@PathVariable("location_id") int location_id) {
        try {
            return this.service.getPricesByLocation(location_id);
        } catch (Exception e) {
            logger.error("Error fetching prices for location {}: {}", location_id, e.getMessage());
            throw new RuntimeException("Error fetching prices", e);
        }
    }

    @GetMapping("/find/multiple")
    @ResponseStatus(HttpStatus.OK)
    public List<List<PriceDTO>> getPricesByLocations(@RequestBody int[] location_ids) {
        try {
            return this.service.getPricesByLocations(location_ids);
        } catch (Exception e) {
            logger.error("Error fetching prices for locations {}: {}", location_ids, e.getMessage());
            throw new RuntimeException("Error fetching prices", e);
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public PriceDTO createOrUpdate(@RequestBody PriceDTO dto) {
        try {
            return this.service.createOrUpdatePrice(dto);
        } catch (Exception e) {
            logger.error("Error creating price {}: {}", dto.toString(), e.getMessage());
            throw new RuntimeException("Error creating price", e);
        }
    }

    @DeleteMapping("/id={id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePrice(@PathVariable("id") int id) {
        try {
            this.service.deletePrice(id);
        } catch (Exception e) {
            logger.error("Error deleting price by id {}: {}", id, e.getMessage());
            throw new RuntimeException("Error deleting price", e);
        }
    }
}
