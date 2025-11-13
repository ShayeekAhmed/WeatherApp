package com.weather.service.service;

import com.weather.service.dto.WeatherResponse;
import com.weather.service.strategy.WeatherRecommendationStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WeatherRecommendationStrategy recommendationStrategy;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        // Set up test data
    }

    @Test
    void testGetWeatherForecast_Success() {
        // Given
        String city = "London";
        
        // When
        WeatherResponse response = weatherService.getWeatherForecast(city, false);
        
        // Then
        assertNotNull(response);
        assertEquals(city, response.getCity());
        assertNotNull(response.getForecasts());
        assertTrue(response.getForecasts().size() <= 3);
    }

    @Test
    void testGetWeatherForecast_OfflineMode() {
        // Given
        String city = "Paris";
        
        // When
        WeatherResponse response = weatherService.getWeatherForecast(city, true);
        
        // Then
        assertNotNull(response);
        assertEquals(city, response.getCity());
        assertTrue(response.isOffline());
        assertEquals(3, response.getForecasts().size());
    }

    @Test
    void testGetFallbackWeatherData() {
        // Given
        String city = "Tokyo";
        
        // When
        WeatherResponse response = weatherService.getFallbackWeatherData(city);
        
        // Then
        assertNotNull(response);
        assertEquals(city, response.getCity());
        assertTrue(response.isOffline());
        assertEquals(3, response.getForecasts().size());
        
        response.getForecasts().forEach(forecast -> {
            assertNotNull(forecast.getDate());
            assertNotNull(forecast.getHighTemp());
            assertNotNull(forecast.getLowTemp());
            assertTrue(forecast.getHighTemp() >= forecast.getLowTemp());
        });
    }
}