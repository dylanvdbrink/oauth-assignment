package nl.dylanvdbrink.oauthassignment.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import nl.dylanvdbrink.oauthassignment.dto.AuthorizationResponse;
import nl.dylanvdbrink.oauthassignment.model.Client;
import nl.dylanvdbrink.oauthassignment.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Service
public class AuthorizationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);
    private static final String ISSUER = "VZ";

    private final ClientRepository clientRepository;

    private final Algorithm algorithm;
    private final int expiresIn;

    public AuthorizationService(ClientRepository clientRepository, @Value("${oauth.token.expires-in:3600}") int expiresIn,
                                @Value("${oauth.jwt.secret}") String jwtSecret) {
        this.clientRepository = clientRepository;
        this.expiresIn = expiresIn;
        algorithm = Algorithm.HMAC512(jwtSecret);
    }

    public AuthorizationResponse authorize(String responseType, String clientId, String redirectUri, String scope, String state) throws IOException {
        Client client = clientRepository.findByClientId(UUID.fromString(clientId));
        validateClient(client, responseType, redirectUri);

        try {
            String token = JWT.create()
                    .withIssuer(ISSUER)
                    .sign(algorithm);

            return new AuthorizationResponse(token, "Bearer", token, expiresIn, state);
        } catch (JWTCreationException e) {
            logger.error("Token creation error: " + e.getMessage(), e);
            throw new IOException("Could not create token: " + e.getMessage(), e);
        }
    }

    public DecodedJWT verifyToken(String accessToken) throws IOException {
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build();

        try {
            return verifier.verify(accessToken);
        } catch (JWTVerificationException e) {
            throw new IOException("Token was invalid: " + e.getMessage());
        }
    }

    private void validateClient(Client client, String responseType, String redirectUri) throws IOException {
        if (client == null) {
            throw new IOException("Could not find client for client_id");
        }

        if (client.getRedirectURIs().stream().noneMatch((redirectURI -> redirectURI.getUrl().equals(redirectUri)))) {
            throw new IOException("Redirect URI is not in permitted redirectURIs for the passed in client");
        }

        if (client.getAllowedResponseTypes().stream().noneMatch((allowedResponseType -> allowedResponseType.getType().equals(responseType)))) {
            throw new IOException("Response type is not allowed by passed in client");
        }
    }

}
