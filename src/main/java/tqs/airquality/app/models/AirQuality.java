package tqs.airquality.app.models;

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
    private double coordLat;
    @NotEmpty
    private double coordLon;
    @NotEmpty
    private Date dt;
    private int aqi;

    private double co;
    private double no;
    private double no2;
    private double o3;
    private double so2;
    private double pm2_5;
    private double pm10;
    private double nh3;

    public AirQuality(@NotEmpty double coordLat,
                      @NotEmpty double coordLon,
                      @NotEmpty Date dt,
                      int aqi,
                      double co,
                      double no,
                      double no2,
                      double o3,
                      double so2,
                      double pm2_5,
                      double pm10,
                      double nh3) {
        this.coordLat = coordLat;
        this.coordLon = coordLon;
        this.dt = dt;
        this.aqi = aqi;
        this.co = co;
        this.no = no;
        this.no2 = no2;
        this.o3 = o3;
        this.so2 = so2;
        this.pm2_5 = pm2_5;
        this.pm10 = pm10;
        this.nh3 = nh3;
    }
}
