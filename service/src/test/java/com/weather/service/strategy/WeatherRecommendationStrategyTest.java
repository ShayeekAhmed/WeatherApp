package com.weather.service.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeatherRecommendationStrategyTest {

    private WeatherRecommendationStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new WeatherRecommendationStrategy();
    }

    @Test
    void testHighTemperatureRecommendation() {
        // Given
        double highTemp = 45.0;
        
        // When
        List<String> recommendations = strategy.generateRecommendations(highTemp, false, false, 5.0);
        
        // Then
        assertTrue(recommendations.contains("Use sunscreen lotion"));
    }

    @Test
    void testRainRecommendation() {
        // Given
        boolean hasRain = true;
        
        // When
        List<String> recommendations = strategy.generateRecommendations(25.0, hasRain, false, 5.0);
        
        // Then
        assertTrue(recommendations.contains("Carry umbrella"));
    }

    @Test
    void testHighWindRecommendation() {
        // Given
        double windSpeed = 15.0;
        
        // When
        List<String> recommendations = strategy.generateRecommendations(25.0, false, false, windSpeed);
        
        // Then
        assertTrue(recommendations.contains("It's too windy, watch out!"));
    }

    @Test
    void testThunderstormRecommendation() {
        // Given
        boolean hasThunderstorm = true;
        
        // When
        List<String> recommendations = strategy.generateRecommendations(45.0, true, hasThunderstorm, 15.0);
        
        // Then
        assertEquals(1, recommendations.size());
        assertTrue(recommendations.contains("Don't step out! A Storm is brewing!"));
    }

    @Test
    void testMultipleConditions() {
        // Given - High temp + rain, no thunderstorm
        
        // When
        List<String> recommendations = strategy.generateRecommendations(42.0, true, false, 8.0);
        
        // Then
        assertTrue(recommendations.contains("Use sunscreen lotion"));
        assertTrue(recommendations.contains("Carry umbrella"));
        assertEquals(2, recommendations.size());
    }
}