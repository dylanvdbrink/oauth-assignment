package nl.dylanvdbrink.oauthassignment.controller;

import jakarta.validation.constraints.NotBlank;
import nl.dylanvdbrink.oauthassignment.dto.AuthorizationResponse;
import nl.dylanvdbrink.oauthassignment.service.AuthorizationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.MessageFormat;

@RestController()
@RequestMapping("/authorize")
public class AuthorizeController {
    private final AuthorizationService authorizationService;

    public AuthorizeController(final AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @GetMapping("")
    public ResponseEntity<Void> authorize(@RequestParam(name = "response_type") @NotBlank String responseType,
                                          @RequestParam(name = "client_id") @NotBlank String clientId,
                                          @RequestParam(name = "redirect_uri") @NotBlank String redirectUri,
                                          @RequestParam(name = "nonce") @NotBlank String nonce,
                                          String scope,
                                          String state) {

        AuthorizationResponse response = authorizationService.authorize(responseType, clientId, redirectUri, nonce, scope, state);
        String location = MessageFormat.format("{0}#id_token={1}&token_type={2}&access_token={3}&state={4}",
                redirectUri, response.idToken(), response.tokenType(), response.accessToken(), response.state());

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, location)
                .build();
    }
}
