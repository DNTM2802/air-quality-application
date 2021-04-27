package tqs.airquality.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import tqs.airquality.app.cache.Cache;
import tqs.airquality.app.models.AirQuality;
import tqs.airquality.app.service.AirQualityService;
import tqs.airquality.app.service.GeocodingService;
import tqs.airquality.app.utils.Location;

import java.util.List;

@Controller
public class AirQualityController {

    @Autowired
    private AirQualityService airQualityService;

    @Autowired
    private GeocodingService geocodingService;

    private Cache<AirQuality> currentDayCache = new Cache<>(120);
    private Cache<List<AirQuality>> historicalCache = new Cache<>(120);
    private Cache<List<AirQuality>> forecastCache = new Cache<>(120);

    @RequestMapping("/")
    public String home(){
        return "homepage-air-quality";
    }

    @GetMapping("/cache")
    public String cache(Model model){
        model.addAttribute("currentDayCache", currentDayCache);
        model.addAttribute("historicalCache", historicalCache);
        model.addAttribute("forecastCache", forecastCache);
        return "cache-information";
    }

    @GetMapping("/search")
    public RedirectView redirectWithUsingRedirectView(
            RedirectAttributes attributes,
            String address,
            String scope,
            String startDate,
            String endDate
    ) {
        System.out.println(startDate);
        attributes.addAttribute("address", address);
        assert scope != null;
        switch (scope) {
            case "forecast":
                return new RedirectView("forecast");
            case "historical":
                attributes.addAttribute("startDate", startDate);
                attributes.addAttribute("endDate", endDate);
                return new RedirectView("historical");
            default:
                return new RedirectView("today");
        }
    }


    @GetMapping(value = "/today")
    public String getAirQualityOfTodayFromCoordinates(String address, Model model) {

        AirQuality airQuality = currentDayCache.getRequestFromCache(address);
        Location location;

        if (airQuality == null){
            location = geocodingService.getCoordinatesFromAddress(address);
            airQuality = airQualityService.getCurrentAirQuality(location);
        } else {
            location = airQuality.getLocation();
        }

        currentDayCache.saveRequestToCache(address,airQuality);

        model.addAttribute("location", location);
        model.addAttribute("airQuality", airQuality);

        return "current-air-quality";
    }

    @GetMapping(value = "/forecast")
    public String getAirQualityForecastFromCoordinates( String address, Model model) {

        List<AirQuality> airQualities = forecastCache.getRequestFromCache(address);
        Location location;

        if (airQualities == null){
            location = geocodingService.getCoordinatesFromAddress(address);
            airQualities = airQualityService.getForecastAirQuality(location);
        } else {
            location = airQualities.get(0).getLocation();
        }

        forecastCache.saveRequestToCache(address,airQualities);

        model.addAttribute("location", location);
        model.addAttribute("airQualities", airQualities);

        return "forecast-air-quality";
    }

    @GetMapping(value = "/historical")
    public String getAirQualityHistoricalFromCoordinatesAndStartDateAndEndDate(
            String address,
            String startDate,
            String endDate,
            Model model) {

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

        model.addAttribute("location", location);
        model.addAttribute("airQualities", airQualities);

        return "historical-air-quality";
    }
}
