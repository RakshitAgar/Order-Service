package com.example.order_service.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class CatalogServiceClient {
    private static final String CATALOG_SERVICE_URL = "http://localhost:8081/catalog/restaurants";
    private static final Logger logger = Logger.getLogger(CatalogServiceClient.class.getName());

    public List<Map<String, Object>> getRestaurants() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForObject(CATALOG_SERVICE_URL, List.class);
        } catch (Exception e) {
            logger.severe("Error fetching restaurants: " + e.getMessage());
            throw new RuntimeException("Failed to fetch restaurants", e);
        }
    }

    public Map<String, Object> getRestaurantById(Long restaurantId) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            return restTemplate.getForObject(CATALOG_SERVICE_URL + "/" + restaurantId, Map.class);
        } catch (Exception e) {
            logger.severe("Error fetching restaurant: " + e.getMessage());
            throw new RuntimeException("Failed to fetch restaurant", e);
        }
    }

}