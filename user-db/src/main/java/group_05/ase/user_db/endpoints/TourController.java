package group_05.ase.user_db.endpoints;


import group_05.ase.user_db.restData.TourDTO;
import group_05.ase.user_db.services.TourService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tours")
public class TourController {

    private static final Logger logger = LoggerFactory.getLogger(TourController.class);

    private final TourService tourService;
    public TourController(TourService tourService) {
        this.tourService = tourService;
        System.out.println("TourController created");
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<TourDTO> getAllTours() {
        try {
            return this.tourService.findAllTours();
        } catch (Exception e) {
            logger.error("Error fetching tours: {}", e.getMessage());
            throw new RuntimeException("Error fetching tours", e);
        }
    }

    @GetMapping("/user/id={userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<TourDTO> getToursByUserId(@PathVariable("userId") String userId) {
        try {
            return this.tourService.findToursByUserId(userId);
        } catch (Exception e) {
            logger.error("Error fetching tours by user_id {}: {}", userId, e.getMessage());
            throw new RuntimeException("Error fetching tours for user", e);
        }
    }

    @GetMapping("/tour/id={tourId}")
    @ResponseStatus(HttpStatus.OK)
    public TourDTO getToursByTourId(@PathVariable("tourId") Integer tourId) {
        try {
            return this.tourService.findTourByTourId(tourId);
        } catch (Exception e) {
            logger.error("Error fetching tours by id {}: {}", tourId, e.getMessage());
            throw new RuntimeException("Error fetching tours for user", e);
        }
    }

    @DeleteMapping("/id={tourId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteTourById(@PathVariable("tourId") Integer tourId) {
        try {
            this.tourService.deleteTourById(tourId);
        } catch (Exception e) {
            logger.error("Error deleting tour by id {}: {}", tourId, e.getMessage());
            throw new RuntimeException("Error deleting tour", e);
        }
    }

    @PatchMapping("/id={tourId}")
    @ResponseStatus(HttpStatus.OK)
    public TourDTO updateTourById(@PathVariable("tourId") Integer tourId, @RequestBody TourDTO updatedValues) {
        try {
            return this.tourService.updateTourById(tourId, updatedValues);
        } catch (Exception e) {
            logger.error("Error updating tour by id {}, with {}: {}", tourId, updatedValues.toString(), e.getMessage());
            throw new RuntimeException("Error updating tour", e);
        }
    }

    @PostMapping("/createTour")
    @ResponseStatus(HttpStatus.OK)
    public void createTour(@RequestBody TourDTO tourDTO) {
        try {
            this.tourService.createTour(tourDTO);
        } catch (Exception e) {
            logger.error("Error creating tour {}: {}", tourDTO.toString(), e.getMessage());
            throw new RuntimeException("Error creating tour", e);
        }
    }



}
