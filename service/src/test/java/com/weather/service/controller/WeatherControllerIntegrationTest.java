package com.weather.service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weather.service.dto.WeatherResponse;
import com.weather.service.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)
class WeatherControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidCity_whenGetWeatherForecast_thenReturnsWeatherData() throws Exception {
        // Given
        String city = "London";
        WeatherResponse mockResponse = WeatherResponse.builder()
                .city(city)
                .offline(false)
                .build();
        
        when(weatherService.getWeatherForecast(anyString(), anyBoolean()))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/weather/forecast")
                        .param("city", city))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value(city))
                .andExpect(jsonPath("$.offline").value(false));
    }

    @Test
    void givenOfflineMode_whenGetWeatherForecast_thenReturnsOfflineData() throws Exception {
        // Given
        String city = "Paris";
        WeatherResponse mockResponse = WeatherResponse.builder()
                .city(city)
                .offline(true)
                .build();
        
        when(weatherService.getWeatherForecast(anyString(), anyBoolean()))
                .thenReturn(mockResponse);

        // When & Then
        mockMvc.perform(get("/api/weather/forecast")
                        .param("city", city)
                        .param("offline", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value(city))
                .andExpect(jsonPath("$.offline").value(true));
    }

    @Test
    void givenHealthEndpoint_whenCalled_thenReturnsOk() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/weather/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Weather service is running!"));
    }
}