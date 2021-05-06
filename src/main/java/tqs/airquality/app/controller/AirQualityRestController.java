package tqs.airquality.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tqs.airquality.app.cache.Cache;
import tqs.airquality.app.models.AirQuality;
import tqs.airquality.app.service.AirQualityService;
import tqs.airquality.app.service.GeocodingService;
import tqs.airquality.app.utils.Location;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AirQualityRestController {

    @Autowired
    private AirQualityService airQualityService;

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private Cache<AirQuality> currentDayCache;

    @Autowired
    private Cache<List<AirQuality>> historicalCache;

    @Autowired
    private Cache<List<AirQuality>> forecastCache;

    private static final String LOCATION = "location";

    @GetMapping("")
    public String home(){
        return "Welcome to AirQuality REST API!";
    }

    @GetMapping("/cache/currentDay")
    public Cache<AirQuality> currentDayCache(){
        return currentDayCache;
    }

    @GetMapping("/cache/historical")
    public Cache<List<AirQuality>> historicalCache(){
        return historicalCache;
    }

    @GetMapping("/cache/forecast")
    public Cache<List<AirQuality>> forecastCache(){
        return forecastCache;
    }


    @GetMapping(value = "/today")
    public AirQuality getAirQualityOfTodayFromCoordinates(String address) {

        var airQuality = currentDayCache.getRequestFromCache(address);
        Location location;

        if (airQuality == null){
            location = geocodingService.getCoordinatesFromAddress(address);
            airQuality = airQualityService.getCurrentAirQuality(location);
        } else {
            location = airQuality.getLocation();
        }

        currentDayCache.saveRequestToCache(address,airQuality);

        return airQuality;
    }

    @GetMapping(value = "/forecast")
    public List<AirQuality> getAirQualityForecastFromCoordinates( String address) {

        List<AirQuality> airQualities = forecastCache.getRequestFromCache(address);
        Location location;

        if (airQualities == null){
            location = geocodingService.getCoordinatesFromAddress(address);
            airQualities = airQualityService.getForecastAirQuality(location);
        } else {
            location = airQualities.get(0).getLocation();
        }

        forecastCache.saveRequestToCache(address,airQualities);

        return airQualities;
    }

    @GetMapping(value = "/historical")
    public List<AirQuality> getAirQualityHistoricalFromCoordinatesAndStartDateAndEndDate(
            String address,
            String startDate,
            String endDate) {

        String identifier = address+startDate+endDate;

        List<AirQuality> airQualities = historicalCache.getRequestFromCache(identifier);
        Location location;

        if (airQualities == null){
            location = geocodingService.getCoordinatesFromAddress(address);
            airQualities = airQualityService.getHistoricalAirQuality(location,startDate,endDate);
        } else {
            location = airQualities.get(0).getLocation();
        }

        historicalCache.saveRequestToCache(identifier,airQualities);

        return airQualities;
    }
}
