package com.weather.service.strategy;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeatherRecommendationStrategy {

    private static final double HIGH_TEMP_THRESHOLD = 40.0;
    private static final double HIGH_WIND_THRESHOLD = 10.0;

    public List<String> generateRecommendations(double temperature, boolean hasRain, 
                                               boolean hasThunderstorm, double windSpeed) {
        List<String> recommendations = new ArrayList<>();

        // Temperature-based recommendations
        if (temperature > HIGH_TEMP_THRESHOLD) {
            recommendations.add("Use sunscreen lotion");
        }

        // Rain-based recommendations
        if (hasRain) {
            recommendations.add("Carry umbrella");
        }

        // Wind-based recommendations
        if (windSpeed > HIGH_WIND_THRESHOLD) {
            recommendations.add("It's too windy, watch out!");
        }

        // Thunderstorm-based recommendations (highest priority)
        if (hasThunderstorm) {
            recommendations.clear(); // Clear other recommendations
            recommendations.add("Don't step out! A Storm is brewing!");
        }

        return recommendations;
    }
}