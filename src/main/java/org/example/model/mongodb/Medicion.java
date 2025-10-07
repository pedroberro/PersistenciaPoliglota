// package org.tp.mongo;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.time.Instant;
import java.util.Map;

@Document(collection = "mediciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicion {
  @Id
  private String id;
  private String sensorId;
  private Instant timestamp;
  private Double temperature;
  private Double humidity;
  private Map<String,Object> metadata; // battery, signal...
  private Location locationSnapshot;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Location {
    private Double lat;
    private Double lon;
    private String city;
    private String country;
  }
}
