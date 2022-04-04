package earthquakelister.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Earthquake {
    private String country;
    private String place;
    private Double magnitude;
    private String dateTime;
}
