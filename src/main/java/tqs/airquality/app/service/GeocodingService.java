package tqs.airquality.app.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import tqs.airquality.app.models.AirQuality;
import tqs.airquality.app.utils.Location;

import java.net.URI;
import java.sql.Date;

@Service
public class GeocodingService {

    @Value("${api.positionstack.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final static String GEOCODING = "http://api.positionstack.com/v1/forward?access_key={apiKey}&query={address}";

    public Location getCoordinatesFromAddress(String address) {
        URI url = new UriTemplate(GEOCODING).expand(apiKey,address);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return convertJsonToLocation(response);
    }

    private Location convertJsonToLocation(ResponseEntity<String> response) {
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            float lat = (float) root.path("data").get(0).path("latitude").asDouble();
            float lon = (float) root.path("data").get(0).path("longitude").asDouble();
            String city = root.path("data").get(0).path("name").asText();
            String region = root.path("data").get(0).path("region").asText();
            String country = root.path("data").get(0).path("country").asText();
            return new Location(lat,lon,city,region,country);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }
}
