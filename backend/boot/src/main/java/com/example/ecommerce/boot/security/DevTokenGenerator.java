package com.example.ecommerce.boot.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.nio.charset.StandardCharsets;

/**
 * Pequeno utilitário para gerar um JWT HS256 de exemplo a ser usado em documentação.
 * Executar via: mvn -pl boot exec:java -Dexec.mainClass=com.example.ecommerce.boot.security.DevTokenGenerator
 */
public class DevTokenGenerator {

    public static void main(String[] args) throws Exception {
        // Must match the value in application.yaml for dev testing
        String secret = "secret-change-me-for-production-0123456789abcd";
        String subject = "example-user";
        List<String> roles = List.of("ROLE_USER");
        long expiresIn = 3600L; // 1h

        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expiresIn);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .claim("roles", roles)
                .issueTime(Date.from(now))
                .expirationTime(Date.from(exp))
                .build();

        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        JWSSigner signer = new MACSigner(keyBytes);

        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new com.nimbusds.jose.Payload(claims.toJSONObject()));
        jwsObject.sign(signer);

        String token = jwsObject.serialize();
        System.out.println(token);
    }
}
