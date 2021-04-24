package tqs.airquality.app.data;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.sql.Date;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class AirQuality {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    @NotEmpty
    private float coordLat;
    @NotEmpty
    private float coordLon;
    @NotEmpty
    private Date dt;
    private int aqi;

    private float co;
    private float no;
    private float no2;
    private float o3;
    private float so2;
    private float pm2_5;
    private float pm10;
    private float nh3;


}
