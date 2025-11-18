package com.example.ecommerce.boot.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jose.JWSObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.nio.charset.StandardCharsets;

/**
 * Endpoint de desenvolvimento para gerar tokens JWT de teste assinados com HS256.
 * NÃO HABILITE em produção. Use apenas em ambientes locais ou de desenvolvimento.
 */
@RestController
@RequestMapping("/dev")
public class DevTokenController {

    @Value("${security.jwt.secret}")
    private String jwtSecret;

    public record TokenRequest(String sub, List<String> roles, long expiresInSeconds) {}

    public record TokenResponse(String token, long expiresAt) {}

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> token(@RequestBody(required = false) TokenRequest req) throws JOSEException {
        String subject = (req != null && req.sub != null) ? req.sub : "dev-user";
        List<String> roles = (req != null && req.roles != null) ? req.roles : List.of("ROLE_USER");
        long expiresIn = (req != null && req.expiresInSeconds > 0) ? req.expiresInSeconds : 3600L;

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expiresIn);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .claim("roles", roles)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .build();

        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        JWSSigner signer = new MACSigner(keyBytes);

        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new com.nimbusds.jose.Payload(claims.toJSONObject()));
        jwsObject.sign(signer);

        String token = jwsObject.serialize();

        return ResponseEntity.ok(new TokenResponse(token, exp.getEpochSecond()));
    }
}
