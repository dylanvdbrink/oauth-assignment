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
                                    @NotBlank String scope,
                                    @NotBlank String state) throws IOException {

        AuthorizationResponse response = authorizationService.authorize(responseType, clientId, redirectUri, scope, state);
        String location = MessageFormat.format("{0}#access_token={1}&id_token={2}&token_type={3}&expires_in={4}&state={5}",
                redirectUri, response.accessToken(), response.idToken(), response.tokenType(), Integer.toString(response.expiresIn()), response.state());

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, location)
                .build();
    }

}
