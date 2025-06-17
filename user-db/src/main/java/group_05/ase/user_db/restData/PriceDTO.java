package group_05.ase.user_db.restData;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceDTO {

    @Getter
    @JsonProperty(required = true, value = "priceId")
    private int priceId;


    @Getter
    @Setter
    @JsonProperty(required = true, value = "locationId")
    private int locationId;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "price")
    private float price;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "name")
    private String name;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "description")
    private String description;

    @Getter
    @Setter
    @JsonProperty(required = true, value = "created_at")
    private LocalDateTime created_at;

    @Override
    public String toString() {
        return "PriceDTO{" +
                "priceId=" + priceId +
                ", locationId=" + locationId +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", created_at=" + created_at +
                '}';
    }

}
