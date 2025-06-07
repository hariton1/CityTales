package group_05.ase.user_db.endpoints;


import group_05.ase.user_db.restData.PriceDTO;
import group_05.ase.user_db.services.PriceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/prices")
public class PriceController {

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
            System.err.println(e.getMessage());
            return new ArrayList<PriceDTO>();
        }
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public PriceDTO createOrUpdate(@RequestBody PriceDTO dto) {
        try {
            return this.service.createOrUpdatePrice(dto);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new PriceDTO();
        }
    }

    @DeleteMapping("/id={id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePrice(@PathVariable("id") int id) {
        try {
            this.service.deletePrice(id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
