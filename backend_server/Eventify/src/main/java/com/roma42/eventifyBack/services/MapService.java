package com.roma42.eventifyBack.services;

import com.roma42.eventifyBack.models.Location;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MapService {

    @Value(value = "${maps.api.key}")
    private String apikey;
    private static GeoApiContext context;

    @PostConstruct
    public void contextInit() {
        context = new GeoApiContext.Builder()
                .apiKey(apikey)
                .build();
    }

    public static String makeRequest(String address) throws InterruptedException, ApiException, IOException {
        GeocodingResult[] results = GeocodingApi.geocode(context, address).await();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        ObjectMapper objectMapper = new ObjectMapper();
        Location location = objectMapper.readValue(gson.toJson(results[0].geometry.location), Location.class);
        return (location.getCoordinates());
    }

    @PreDestroy
    public void shutdown() {
        context.shutdown();
    }
}