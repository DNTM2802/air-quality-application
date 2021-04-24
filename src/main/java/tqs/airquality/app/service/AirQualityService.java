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

import java.net.URI;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class AirQualityService {

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private final static String AIR_QUALITY_TODAY = "http://api.openweathermap.org/data/2.5/air_pollution?lat={coordLat}&lon={coordLon}&appid={apiKey}";
    private final static String AIR_QUALITY_HISTORICAL = "http://api.openweathermap.org/data/2.5/air_pollution/history?lat={coordLat}&lon={coordLon}&start={startDate}&end={endDate}&appid={apiKey}";
    private final static String AIR_QUALITY_FORECAST = "http://api.openweathermap.org/data/2.5/air_pollution/forecast?lat={coordLat}&lon={coordLon}&appid={apiKey}";

    public AirQuality getCurrentAirQuality(float coordLat, float coordLon) {
        URI url = new UriTemplate(AIR_QUALITY_TODAY).expand(coordLat,coordLon,apiKey);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return convertJsonToAirQuality(response);
    }

    public List<AirQuality> getHistoricalAirQuality(float coordLat, float coordLon, String startDate, String endDate) {
        URI url = new UriTemplate(AIR_QUALITY_HISTORICAL).expand(coordLat,coordLon,startDate,endDate,apiKey);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return convertJsonToAirQualityList(response);
    }

    public List<AirQuality> getForecastAirQuality(float coordLat, float coordLon) {
        URI url = new UriTemplate(AIR_QUALITY_FORECAST).expand(coordLat,coordLon,apiKey);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        return convertJsonToAirQualityList(response);
    }

    private AirQuality convertJsonToAirQuality(ResponseEntity<String> response) {
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            return new AirQuality(
                root.path("coord").path("lat").asDouble(),
                root.path("coord").path("lon").asDouble(),
                new Date(Long.parseLong(root.path("list").get(0).path("dt").asText() + "000")),
                root.path("main").path("aqi").asInt(),
                root.path("list").get(0).path("components").path("co").asDouble(),
                root.path("list").get(0).path("components").path("no").asDouble(),
                root.path("list").get(0).path("components").path("no2").asDouble(),
                root.path("list").get(0).path("components").path("o3").asDouble(),
                root.path("list").get(0).path("components").path("so2").asDouble(),
                root.path("list").get(0).path("components").path("pm2_5").asDouble(),
                root.path("list").get(0).path("components").path("pm10").asDouble(),
                root.path("list").get(0).path("components").path("nh3").asDouble()
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }

    private List<AirQuality> convertJsonToAirQualityList(ResponseEntity<String> response) {
        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            double lat = root.path("coord").path("lat").asDouble();
            double lon = root.path("coord").path("lon").asDouble();
            List<AirQuality> historical = new ArrayList<AirQuality>();
            Iterator<JsonNode> iterator = root.withArray("list").elements();
            Date lastDay = null;
            if (iterator.hasNext())
                lastDay = new Date(Long.parseLong(objectMapper.readTree(iterator.next().toString()).path("dt") + "000"));
            while (iterator.hasNext()){
                JsonNode n = iterator.next();
                System.out.println(n);
                Date day = new Date(Long.parseLong(objectMapper.readTree(n.toString()).path("dt") + "000"));
                if (day.toString().compareTo(lastDay.toString()) != 0) {
                    historical.add(
                        new AirQuality(
                            lat,
                            lon,
                            day,
                            n.path("main").path("aqi").asInt(),
                            n.path("components").path("co").asDouble(),
                            n.path("components").path("no").asDouble(),
                            n.path("components").path("no2").asDouble(),
                            n.path("components").path("o3").asDouble(),
                            n.path("components").path("so2").asDouble(),
                            n.path("components").path("pm2_5").asDouble(),
                            n.path("components").path("pm10").asDouble(),
                            n.path("components").path("nh3").asDouble()
                        )
                    );
                    lastDay=day;
                }
            }

            return historical;

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }



}
