// WeatherService.java
package com.weather.service.service;

import com.weather.service.dto.*;
import com.weather.service.strategy.WeatherRecommendationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    private final RestTemplate restTemplate;
    private final WeatherRecommendationStrategy recommendationStrategy;
    
    @Value("${weather.api.key:dummy_key}")
    private String apiKey;
    
    @Value("${weather.api.url:https://api.openweathermap.org/data/2.5/forecast}")
    private String apiUrl;

    @Cacheable(value = "weather", key = "#city")
    public WeatherResponse getWeatherForecast(String city, boolean offline) {
        if (offline) {
            return getFallbackWeatherData(city);
        }

        try {
            String url = String.format("%s?q=%s&appid=%s&cnt=24&units=metric", apiUrl, city, apiKey);
            OpenWeatherMapResponse apiResponse = restTemplate.getForObject(url, OpenWeatherMapResponse.class);
            
            return processWeatherData(apiResponse, city);
        } catch (Exception e) {
            log.error("Error calling weather API", e);
            return null;
        }
    }

    private WeatherResponse processWeatherData(OpenWeatherMapResponse apiResponse, String city) {
        List<DailyForecast> forecasts = new ArrayList<>();
        Map<String, List<OpenWeatherMapResponse.WeatherData>> dailyData = groupByDate(apiResponse.getList());
        
        int dayCount = 0;
        for (Map.Entry<String, List<OpenWeatherMapResponse.WeatherData>> entry : dailyData.entrySet()) {
            if (dayCount >= 3) break;
            
            List<OpenWeatherMapResponse.WeatherData> dayData = entry.getValue();
            double maxTemp = dayData.stream().mapToDouble(data -> data.getMain().getTempMax()).max().orElse(0.0);
            double minTemp = dayData.stream().mapToDouble(data -> data.getMain().getTempMin()).min().orElse(0.0);
            
            // Get weather conditions and wind data
            boolean hasRain = dayData.stream().anyMatch(data -> 
                data.getWeather().stream().anyMatch(w -> w.getMain().toLowerCase().contains("rain")));
            boolean hasThunderstorm = dayData.stream().anyMatch(data ->
                data.getWeather().stream().anyMatch(w -> w.getMain().toLowerCase().contains("thunderstorm")));
            double maxWindSpeed = dayData.stream().mapToDouble(data -> 
                data.getWind() != null ? data.getWind().getSpeed() * 2.237 : 0).max().orElse(0.0); // Convert m/s to mph
            
            List<String> recommendations = recommendationStrategy.generateRecommendations(
                maxTemp, hasRain, hasThunderstorm, maxWindSpeed);
            
            forecasts.add(DailyForecast.builder()
                    .date(entry.getKey())
                    .highTemp(Math.round(maxTemp * 10.0) / 10.0)
                    .lowTemp(Math.round(minTemp * 10.0) / 10.0)
                    .windSpeed(Math.round(maxWindSpeed * 10.0) / 10.0)
                    .hasRain(hasRain)
                    .hasThunderstorm(hasThunderstorm)
                    .recommendations(recommendations)
                    .build());
            
            dayCount++;
        }
        
        return WeatherResponse.builder()
                .city(city)
                .forecasts(forecasts)
                .timestamp(LocalDateTime.now().toString())
                .offline(false)
                .build();
    }

    private Map<String, List<OpenWeatherMapResponse.WeatherData>> groupByDate(List<OpenWeatherMapResponse.WeatherData> weatherList) {
        Map<String, List<OpenWeatherMapResponse.WeatherData>> grouped = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (OpenWeatherMapResponse.WeatherData data : weatherList) {
            LocalDateTime dateTime = LocalDateTime.parse(data.getDtTxt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String dateKey = dateTime.toLocalDate().format(formatter);
            
            grouped.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(data);
        }
        
        return grouped;
    }

    public WeatherResponse getFallbackWeatherData(String city) {
        log.info("Using fallback weather data for city: {}", city);
        
        List<DailyForecast> forecasts = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < 3; i++) {
            double highTemp = 20 + random.nextDouble() * 25; // 20-45°C
            double lowTemp = highTemp - 5 - random.nextDouble() * 10;
            double windSpeed = random.nextDouble() * 15; // 0-15 mph
            boolean hasRain = random.nextBoolean();
            boolean hasThunderstorm = random.nextDouble() < 0.2; // 20% chance
            
            List<String> recommendations = recommendationStrategy.generateRecommendations(
                highTemp, hasRain, hasThunderstorm, windSpeed);
            
            forecasts.add(DailyForecast.builder()
                    .date(LocalDate.now().plusDays(i).toString())
                    .highTemp(Math.round(highTemp * 10.0) / 10.0)
                    .lowTemp(Math.round(lowTemp * 10.0) / 10.0)
                    .windSpeed(Math.round(windSpeed * 10.0) / 10.0)
                    .hasRain(hasRain)
                    .hasThunderstorm(hasThunderstorm)
                    .recommendations(recommendations)
                    .build());
        }
        
        return WeatherResponse.builder()
                .city(city)
                .forecasts(forecasts)
                .timestamp(LocalDateTime.now().toString())
                .offline(true)
                .build();
    }
}