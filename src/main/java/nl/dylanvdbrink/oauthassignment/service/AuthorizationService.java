package nl.dylanvdbrink.oauthassignment.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import nl.dylanvdbrink.oauthassignment.auth.AuthenticationException;
import nl.dylanvdbrink.oauthassignment.dto.AuthorizationResponse;
import nl.dylanvdbrink.oauthassignment.model.Client;
import nl.dylanvdbrink.oauthassignment.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class AuthorizationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);
    private static final String ISSUER = "VZ";

    private final ClientRepository clientRepository;

    private final Algorithm algorithm;

    public AuthorizationService(ClientRepository clientRepository, @Value("${oauth.jwt.secret}") String jwtSecret) {
        this.clientRepository = clientRepository;
        algorithm = Algorithm.HMAC512(jwtSecret);
    }

    public AuthorizationResponse authorize(String responseType, String clientId, String redirectUri, String nonce, String scope, String state) {
        validateParameters(scope);

        Client client = clientRepository.findByClientId(UUID.fromString(clientId));
        validateClient(client, responseType, redirectUri);

        try {
            String accessToken = JWT.create()
                    .withIssuer(ISSUER)
                    .sign(algorithm);

            String idToken = JWT.create()
                    .withIssuer(ISSUER)
                    .withClaim("nonce", nonce)
                    .sign(algorithm);

            return new AuthorizationResponse(accessToken, "Bearer", idToken, state);
        } catch (JWTCreationException e) {
            logger.error("Token creation error: " + e.getMessage(), e);
            throw new AuthenticationException("Could not create token: " + e.getMessage(), e);
        }
    }

    public DecodedJWT verifyToken(String accessToken) throws AuthenticationException {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build();
            return verifier.verify(accessToken);
        } catch (JWTVerificationException e) {
            throw new AuthenticationException("Token was invalid: " + e.getMessage(), e);
        }
    }

    private void validateClient(Client client, String responseType, String redirectUri) throws AuthenticationException {
        if (client == null) {
            throw new AuthenticationException("Could not find client for client_id");
        }

        if (client.getRedirectURIs().stream().noneMatch((redirectURI -> redirectURI.getUrl().equals(redirectUri)))) {
            throw new AuthenticationException("Redirect URI is not in permitted redirectURIs for the passed in client");
        }

        if (client.getAllowedResponseTypes().stream().noneMatch((allowedResponseType -> allowedResponseType.getType().equals(responseType)))) {
            throw new AuthenticationException("Response type is not allowed by passed in client");
        }
    }

    private void validateParameters(String scope) {
        if (!StringUtils.hasText(scope) || !scope.toLowerCase().contains("openid")) {
            throw new AuthenticationException("Scope did not contain openid");
        }
    }
}
