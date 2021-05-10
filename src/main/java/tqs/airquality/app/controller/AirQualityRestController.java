package tqs.airquality.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tqs.airquality.app.cache.Cache;
import tqs.airquality.app.models.AirQuality;
import tqs.airquality.app.service.AirQualityService;
import tqs.airquality.app.service.GeocodingService;
import tqs.airquality.app.utils.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    // Constants
    private static final String ADDRESS_NOT_FOUND = "{\"code\" : 404, \"message\" : \"Address not found.\"}";
    private static final String WELCOME = "{\"code\" : 200, \"message\" : \"Welcome to AirQuality REST API!\"}";

    @GetMapping(value="",produces = MediaType.APPLICATION_JSON_VALUE)
    public String welcome(){
        return WELCOME;
    }

    @GetMapping(value="/cache", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> cache() {
        return new ResponseEntity<>(
                Stream.of(
                        currentDayCache,
                        forecastCache,
                        historicalCache)
                .collect(Collectors.toList()), HttpStatus.OK);
    }

    @GetMapping(value = "/today", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAirQualityOfTodayFromCoordinates(String address) {

        var airQuality = currentDayCache.getRequestFromCache(address);
        Location location;

        if (airQuality == null){
            location = geocodingService.getCoordinatesFromAddress(address);
            if (location == null) {
                return new ResponseEntity<>(ADDRESS_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            airQuality = airQualityService.getCurrentAirQuality(location);
        }

        currentDayCache.saveRequestToCache(address,airQuality);

        return new ResponseEntity<>(airQuality, HttpStatus.OK);
    }

    @GetMapping(value = "/forecast", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAirQualityForecastFromCoordinates( String address) {

        List<AirQuality> airQualities = forecastCache.getRequestFromCache(address);
        Location location;

        if (airQualities == null){
            location = geocodingService.getCoordinatesFromAddress(address);
            if (location == null) {
                return new ResponseEntity<>(ADDRESS_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            airQualities = airQualityService.getForecastAirQuality(location);
        }

        forecastCache.saveRequestToCache(address,airQualities);

        return new ResponseEntity<>(airQualities, HttpStatus.OK);
    }

    @GetMapping(value = "/historical", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getAirQualityHistoricalFromCoordinatesAndStartDateAndEndDate(
            String address,
            String startDate,
            String endDate) {

        String identifier = address+startDate+endDate;

        List<AirQuality> airQualities = historicalCache.getRequestFromCache(identifier);
        Location location;

        if (airQualities == null){
            location = geocodingService.getCoordinatesFromAddress(address);
            if (location == null) {
                return new ResponseEntity<>(ADDRESS_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            airQualities = airQualityService.getHistoricalAirQuality(location,startDate,endDate);
        }

        historicalCache.saveRequestToCache(identifier,airQualities);

        return new ResponseEntity<>(airQualities, HttpStatus.OK);
    }
}
