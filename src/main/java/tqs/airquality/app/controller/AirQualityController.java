package tqs.airquality.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import tqs.airquality.app.repository.AirQualityRepository;
import tqs.airquality.app.service.AirQualityService;

@Controller
@RequestMapping("/api")
public class AirQualityController {

    @Value("${api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AirQualityRepository airQualityRepository;

    @Autowired
    private AirQualityService airQualityService;

    @GetMapping(value = "/air_quality/today")
    public String getAirQualityOfTodayFromCoordinates(@RequestParam float coordLat, @RequestParam float coordLon, Model model) {

        model.addAttribute("airQuality", airQualityService.getCurrentAirQuality(coordLat, coordLon));

        return "current-air-quality";
    }

    @GetMapping(value = "/air_quality/forecast")
    public String getAirQualityForecastFromCoordinates(@RequestParam float coordLat, @RequestParam float coordLon, Model model) {

        model.addAttribute("airQualities", airQualityService.getForecastAirQuality(coordLat, coordLon));

        return "forecast-air-quality";
    }

    @GetMapping(value = "/air_quality/historical")
    public String getAirQualityHistoricalFromCoordinatesAndStartDateAndEndDate(
            @RequestParam float coordLat,
            @RequestParam float coordLon,
            @RequestParam String startDate,
            @RequestParam String endDate,
            Model model) {

        model.addAttribute("airQualities", airQualityService.getHistoricalAirQuality(coordLat, coordLon,startDate,endDate));

        return "historical-air-quality";
    }
}
