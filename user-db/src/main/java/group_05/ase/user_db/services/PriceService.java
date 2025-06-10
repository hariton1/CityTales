package group_05.ase.user_db.services;

import group_05.ase.user_db.entities.PriceEntity;
import group_05.ase.user_db.repositories.PriceRepository;
import group_05.ase.user_db.restData.PriceDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PriceService {

    private final PriceRepository repository;

    public PriceService(PriceRepository repository) {
        this.repository = repository;
    }

    public List<PriceDTO> getPricesByLocation(int location_id) {

        List<PriceEntity> list = this.repository.findByLocationIdOrderByPriceIdAsc(location_id);
        return list.stream().map(this::mapEntityToDto).toList();
    }

    public List<List<PriceDTO>> getPricesByLocations(int[] location_ids) {

        List<List<PriceDTO>> result = new ArrayList<>();
        for (int location : location_ids) {
            List<PriceDTO> pricesForLocation = getPricesByLocation(location);
            result.add(pricesForLocation);
        }
        return result;
    }

    public PriceDTO createOrUpdatePrice(PriceDTO dto) {

        return mapEntityToDto(this.repository.save(mapDtoToEntity(dto)));
    }

    public void deletePrice(int priceId) {

        this.repository.deleteById(priceId);
    }

    private PriceDTO mapEntityToDto(PriceEntity entity) {

        return new PriceDTO(entity.getPriceId(), entity.getLocationId(), entity.getPrice(), entity.getName(), entity.getDescription(), entity.getCreated_at());
    }

    private PriceEntity mapDtoToEntity(PriceDTO dto) {

        PriceEntity entity = new PriceEntity();
        if(dto.getPriceId() != 0) {
            entity.setPriceId(dto.getPriceId());
        }

        entity.setLocationId(dto.getLocationId());
        entity.setPrice(dto.getPrice());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setCreated_at(dto.getCreated_at());

        return entity;
    }
}
