package com.atelierlocal.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import com.atelierlocal.dto.GeocodeRequest;

/**
 * Contrôleur REST pour le géocodage et géocodage inverse.
 * Permet de convertir une adresse en coordonnées GPS et inversement.
 */
@RestController
@RequestMapping("/api/geocode")
public class GeocodeController {

    // Clé API LocationIQ injectée depuis application.properties
    @Value("${locationiq.key}")
    private String apiKey;

    // --------------------
    // ADRESSE → COORDONNÉES
    // --------------------

    /**
     * Transforme une adresse en coordonnées GPS (latitude et longitude).
     * 
     * @param request objet contenant l'adresse à géocoder
     * @return ResponseEntity contenant latitude et longitude ou message d'erreur
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> geocode(@RequestBody GeocodeRequest request) {
        String address = request.getAddress();
        if (address == null || address.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Adresse manquante"));
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://us1.locationiq.com/v1/search.php?key=" 
                        + apiKey + "&q=" + URLEncoder.encode(address, StandardCharsets.UTF_8) + "&format=json";

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

    // --------------------
    // COORDONNÉES → ADRESSE
    // --------------------

    /**
     * Transforme des coordonnées GPS en adresse lisible.
     * 
     * @param latitude latitude GPS
     * @param longitude longitude GPS
     * @return ResponseEntity contenant l'adresse complète ou message d'erreur
     */
    @GetMapping("/reverse")
    public ResponseEntity<Map<String, Object>> reverseGeocode(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        
        if (latitude == null || longitude == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Coordonnées manquantes"));
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = String.format(
                "https://us1.locationiq.com/v1/reverse.php?key=%s&lat=%s&lon=%s&format=json",
                apiKey, latitude, longitude
            );

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

            if (response.getBody() != null) {
                Map<String, Object> result = response.getBody();
                Map<String, Object> address = (Map<String, Object>) result.get("address");
                
                // Construit une adresse lisible
                String formattedAddress = buildAddress(address);
                
                return ResponseEntity.ok(Map.of(
                    "address", formattedAddress,
                    "city", address.getOrDefault("city", address.getOrDefault("town", address.getOrDefault("village", ""))),
                    "postcode", address.getOrDefault("postcode", ""),
                    "country", address.getOrDefault("country", "")
                ));
            } else {
                return ResponseEntity.ok(Map.of("address", "Adresse inconnue"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("error", "Erreur géocodage inverse"));
        }
    }

    /**
     * Construit une adresse lisible à partir des champs retournés par LocationIQ.
     * 
     * @param address map contenant les éléments d'adresse
     * @return adresse complète sous forme de chaîne
     */
    private String buildAddress(Map<String, Object> address) {
        StringBuilder sb = new StringBuilder();
        
        // Numéro + rue
        if (address.containsKey("house_number")) {
            sb.append(address.get("house_number")).append(" ");
        }
        if (address.containsKey("road")) {
            sb.append(address.get("road")).append(", ");
        }
        
        // Ville
        String city = (String) address.getOrDefault("city", 
                      address.getOrDefault("town", 
                      address.getOrDefault("village", "")));
        if (!city.isEmpty()) {
            sb.append(city);
        }
        
        // Code postal
        if (address.containsKey("postcode")) {
            sb.append(" ").append(address.get("postcode"));
        }
        
        return sb.toString().trim();
    }
}
