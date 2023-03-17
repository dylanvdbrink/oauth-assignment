package nl.dylanvdbrink.oauthassignment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthorizationResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("id_token") String idToken,
        @JsonProperty("expires_in") int expiresIn,
        String state
) { }
