// WeatherController.java
package com.weather.service.controller;

import com.weather.service.dto.WeatherResponse;
import com.weather.service.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Weather API", description = "Weather forecast operations")
@CrossOrigin(origins = "http://localhost:4200")
public class WeatherController {

    private final WeatherService weatherService;

    @Operation(summary = "Get 3-day weather forecast", 
               description = "Returns weather forecast for next 3 days with recommendations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved forecast"),
        @ApiResponse(responseCode = "404", description = "City not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/forecast")
    public ResponseEntity<WeatherResponse> getWeatherForecast(
            @Parameter(description = "City name", required = true)
            @RequestParam String city,
            @Parameter(description = "Enable offline mode", required = false)
            @RequestParam(defaultValue = "false") boolean offline) {
        
        log.info("Fetching weather forecast for city: {} with offline mode: {}", city, offline);
        
        try {
            WeatherResponse response = weatherService.getWeatherForecast(city, offline);
            if(response == null) {
                return ResponseEntity.ok(new WeatherResponse());
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching weather data for city: {}", city, e);
            WeatherResponse fallbackResponse = weatherService.getFallbackWeatherData(city);
            return ResponseEntity.ok(fallbackResponse);
        }
    }

    @Operation(summary = "Health check", description = "Check if the service is running")
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Weather service is running!");
    }
}