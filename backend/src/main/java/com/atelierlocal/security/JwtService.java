package com.atelierlocal.security;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

/**
 * Service pour la gestion des JSON Web Tokens (JWT).
 * 
 * Fournit des méthodes pour :
 * - générer des tokens JWT pour un utilisateur,
 * - extraire des informations depuis un token,
 * - vérifier la validité et l'expiration d'un token,
 * - gérer une liste de tokens blacklistés.
 */
@Service
public class JwtService {

    /**
     * Ensemble thread-safe contenant les tokens blacklistés.
     */
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();

    /**
     * Clé secrète utilisée pour signer les JWT.
     * Injectée depuis application.properties via ${jwt.secret}.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Durée d'expiration des tokens en millisecondes.
     * Injectée depuis application.properties via ${jwt.expiration-ms}.
     */
    @Value("${jwt.expiration-ms}")
    private String expirationMs;

    /**
     * Retourne la clé de signature HMAC à partir du secret.
     * 
     * @return clé de signature
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    /**
     * Extrait le nom d'utilisateur (subject) depuis un JWT.
     * 
     * @param token JWT
     * @return nom d'utilisateur
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait une réclamation spécifique depuis un JWT.
     * 
     * @param <T> type de la réclamation
     * @param token JWT
     * @param claimsResolver fonction pour extraire la réclamation
     * @return valeur de la réclamation
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrait toutes les réclamations d'un JWT.
     * 
     * @param token JWT
     * @return objet Claims contenant les informations du token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Vérifie si un token est expiré.
     * 
     * @param token JWT
     * @return true si expiré, false sinon
     */
    boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Extrait la date d'expiration depuis un JWT.
     * 
     * @param token JWT
     * @return date d'expiration
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Vérifie la validité d'un token pour un utilisateur donné.
     * 
     * @param token JWT
     * @param userDetails informations de l'utilisateur
     * @return true si le token est valide et non expiré pour l'utilisateur
     */
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Génère un token JWT pour un utilisateur donné.
     * 
     * @param userDetails informations de l'utilisateur
     * @return JWT signé
     */
    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        long expirationMsLong = Long.parseLong(expirationMs);
        Instant expiration = now.plus(expirationMsLong, ChronoUnit.MILLIS);

        // Récupération du rôle de l'utilisateur (premier rôle trouvé)
        String role = userDetails.getAuthorities().stream()
                                 .findFirst()
                                 .map(GrantedAuthority::getAuthority)
                                 .orElse("ROLE_USER");

        // Construction du token JWT
        return Jwts.builder()
            .setSubject(userDetails.getUsername())
            .claim("role", role)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /**
     * Ajoute un token à la liste des tokens blacklistés.
     * 
     * @param token JWT à blacklister
     */
    public void blacklistToken(String token) {
        blacklistedTokens.add(token);
    }

    /**
     * Vérifie si un token est blacklisté.
     * 
     * @param token JWT
     * @return true si le token est dans la blacklist
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
