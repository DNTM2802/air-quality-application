package tqs.airquality.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tqs.airquality.app.models.AirQuality;

@Repository
public interface AirQualityRepository extends JpaRepository<AirQuality,Long> {
    AirQuality findByCoordLatAndCoordLon(float coordLat, float coordLon);
}
