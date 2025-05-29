package group_05.ase.user_db.services;


import group_05.ase.user_db.entities.TourEntity;
import group_05.ase.user_db.repositories.TourRepository;
import group_05.ase.user_db.restData.TourDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class TourService {

    private final TourRepository tourRepository;

    public TourService(TourRepository tourRepository) {this.tourRepository = tourRepository;}

    public List<TourDTO> findAllTours() {
        List<TourDTO> tours = new ArrayList<>();
        List<TourEntity> entities = this.tourRepository.findAll();

        for (TourEntity entity : entities) {
            tours.add(new TourDTO(entity.getName(),
                    entity.getDescription(),
                    entity.getStart_lat(),
                    entity.getStart_lng(),
                    entity.getEnd_lat(),
                    entity.getEnd_lng(),
                    entity.getStops(),
                    entity.getDistance(),
                    entity.getDurationEstimate(),
                    entity.getUserId()));
        }
        return tours;
    }

    public List<TourDTO> findToursByUserId(String userId) {
        List<TourDTO> tours = new ArrayList<>();
        List<TourEntity> entities = this.tourRepository.findAllByUserId(userId);

        for (TourEntity entity : entities) {
            tours.add(new TourDTO(entity.getName(),
                    entity.getDescription(),
                    entity.getStart_lat(),
                    entity.getStart_lng(),
                    entity.getEnd_lat(),
                    entity.getEnd_lng(),
                    entity.getStops(),
                    entity.getDistance(),
                    entity.getDurationEstimate(),
                    entity.getUserId()));
        }
        return tours;
    }

    public TourDTO findTourByTourId(UUID tourId) {
        TourEntity entity = this.tourRepository.findAllById(tourId);

        return new TourDTO(entity.getName(),
                    entity.getDescription(),
                    entity.getStart_lat(),
                    entity.getStart_lng(),
                    entity.getEnd_lat(),
                    entity.getEnd_lng(),
                    entity.getStops(),
                    entity.getDistance(),
                    entity.getDurationEstimate(),
                    entity.getUserId());
    }

    public TourDTO findTourByName(String name) {
        TourEntity entity = this.tourRepository.findByName(name);

        return new TourDTO(entity.getName(),
                    entity.getDescription(),
                    entity.getStart_lat(),
                    entity.getStart_lng(),
                    entity.getEnd_lat(),
                    entity.getEnd_lng(),
                    entity.getStops(),
                    entity.getDistance(),
                    entity.getDurationEstimate(),
                    entity.getUserId());
    }

    public void deleteTourById(Integer id){
        if (id == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        this.tourRepository.deleteById(id);
    }

    public void createTour(TourDTO tourDTO) {
        TourEntity tourEntity = new TourEntity();
        tourEntity.setName(tourDTO.getName());
        tourEntity.setDescription(tourDTO.getDescription());
        tourEntity.setStart_lat(tourDTO.getStart_lat());
        tourEntity.setStart_lng(tourDTO.getStart_lng());
        tourEntity.setEnd_lat(tourDTO.getEnd_lat());
        tourEntity.setEnd_lng(tourDTO.getEnd_lng());
        tourEntity.setStops(tourDTO.getStops());
        tourEntity.setDistance(tourDTO.getDistance());
        tourEntity.setDurationEstimate(tourDTO.getDurationEstimate());
        tourEntity.setUserId(tourDTO.getUserId());

        this.tourRepository.save(tourEntity);
    }


    public TourDTO updateTourById(Integer id, TourDTO updatedValues) {
        TourEntity existingTour = this.tourRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Tour not found"));
        this.tourRepository.deleteById(id);

        if (updatedValues.getName() != null) {
            existingTour.setName(updatedValues.getName());
        }

        if (updatedValues.getDescription() != null) {
            existingTour.setDescription(updatedValues.getDescription());
        }

        if (updatedValues.getStart_lat() != null) {
            existingTour.setStart_lat(updatedValues.getStart_lat());
        }

        if (updatedValues.getStart_lng() != null) {
            existingTour.setStart_lng(updatedValues.getStart_lng());
        }

        if (updatedValues.getEnd_lat() != null) {
            existingTour.setEnd_lat(updatedValues.getEnd_lat());
        }

        if (updatedValues.getEnd_lng() != null) {
            existingTour.setEnd_lng(updatedValues.getEnd_lng());
        }

        if (updatedValues.getStops() != null) {
            existingTour.setStops(updatedValues.getStops());
        }

        if (updatedValues.getDistance() != null) {
            existingTour.setDistance(updatedValues.getDistance());
        }

        if (updatedValues.getDurationEstimate() != null) {
            existingTour.setDurationEstimate(updatedValues.getDurationEstimate());
        }

        if (updatedValues.getUserId() != null) {
            existingTour.setUserId(updatedValues.getUserId());
        }

        TourEntity updatedTour = this.tourRepository.save(existingTour);

        return new TourDTO(updatedTour.getName(), updatedTour.getDescription(), updatedTour.getStart_lat(), updatedTour.getStart_lng(), updatedTour.getEnd_lat(), updatedTour.getEnd_lng(), updatedTour.getStops(), updatedTour.getDistance(), updatedTour.getDurationEstimate(), updatedTour.getUserId());


    }





}
