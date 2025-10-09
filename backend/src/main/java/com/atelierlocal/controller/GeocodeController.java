package com.atelierlocal.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/geocode")
@CrossOrigin(origins = "*")
public class GeocodeController {

    @Value("${locationiq.key}")
    private String apiKey;

    @PostMapping
    public ResponseEntity<Map<String, Object>> geocode(@RequestBody Map<String, String> body) {
        String adresse = body.get("adresse");
        if (adresse == null || adresse.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Adresse manquante"));
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://us1.locationiq.com/v1/search.php?key=" 
                        + apiKey + "&q=" + URLEncoder.encode(adresse, StandardCharsets.UTF_8) + "&format=json";

            ResponseEntity<Object[]> response = restTemplate.getForEntity(url, Object[].class);

            if (response.getBody() != null && Objects.requireNonNull(response.getBody()).length > 0) {
                Map<String, Object> firstResult = (Map<String, Object>) Objects.requireNonNull(response.getBody())[0];
                Map<String, Object> coords = Map.of(
                    "latitude", Double.parseDouble((String) firstResult.get("lat")),
                    "longitude", Double.parseDouble((String) firstResult.get("lon"))
                );
                return ResponseEntity.ok(coords);
            } else {
                return ResponseEntity.ok(Map.of("latitude", null, "longitude", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Erreur LocationIQ"));
        }
    }
}