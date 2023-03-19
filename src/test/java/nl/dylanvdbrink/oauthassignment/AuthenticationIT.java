package nl.dylanvdbrink.oauthassignment;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import nl.dylanvdbrink.oauthassignment.model.Client;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Arrays;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationIT extends BaseIT {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        cleanDatabase();
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    void afterEach() {
        cleanDatabase();
    }

    @Test
    void shouldErrorWhenRequiredParamsAreEmpty() throws Exception {
        this.mockMvc.perform(get("/authorize?client_id=test&redirect_uri=test&scope=openid&state=test&nonce=test"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Required parameter 'response_type' is not present.")));

        this.mockMvc.perform(get("/authorize?response_type=test&redirect_uri=test&scope=openid&state=test&nonce=test"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Required parameter 'client_id' is not present")));

        this.mockMvc.perform(get("/authorize?response_type=test&client_id=test&scope=openid&state=test&nonce=test"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Required parameter 'redirect_uri' is not present")));
    }

    @Test
    void shouldErrorWhenScopeDoesNotContainOpenID() throws Exception {
        this.mockMvc.perform(get("/authorize?response_type=id%20token&client_id=ba199d73-5018-4c91-8fe3-914702ef30ee" +
                        "&redirect_uri=test&scope=test&state=test&nonce=test"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Scope did not contain openid")));
    }

    @Test
    void shouldErrorWhenClientIdIsUnregistered() throws Exception {
        this.mockMvc.perform(get("/authorize?response_type=id%20token&client_id=ba199d73-5018-4c91-8fe3-914702ef30ee" +
                        "&redirect_uri=test&scope=openid&state=test&nonce=test"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Could not find client for client_id")));
    }

    @Test
    void shouldErrorWhenRedirectURIIsNotRegistered() throws Exception {
        createClient("ClientA");
        this.mockMvc.perform(get("/authorize?response_type=id%20token&client_id=ba199d73-5018-4c91-8fe3-914702ef30ee" +
                        "&redirect_uri=test&scope=openid&state=test&nonce=test"))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(containsString("Could not find client for client_id")));
    }

    @Test
    void shouldReturnTokenOnSuccesfulRequest() throws Exception {
        Client createdClient = createClient("ClientA");

        String url = MessageFormat.format("/authorize?response_type={0}&client_id={1}&redirect_uri={2}&scope={3}&state={4}&nonce={5}",
                createdClient.getAllowedResponseTypes().iterator().next().getType(), createdClient.getClientId().toString(),
                createdClient.getRedirectURIs().iterator().next().getUrl(), "openid", "test", "test");

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("id_token=ey")));
    }

    @Test
    void idTokenShouldContainCorrectClaims() throws Exception {
        Client createdClient = createClient("ClientA");

        String url = MessageFormat.format("/authorize?response_type={0}&client_id={1}&redirect_uri={2}&scope={3}&state={4}&nonce={5}",
                createdClient.getAllowedResponseTypes().iterator().next().getType(), createdClient.getClientId().toString(),
                createdClient.getRedirectURIs().iterator().next().getUrl(), "openid", "test", "nonce-test");

        MvcResult result = this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String location = result.getResponse().getHeader("Location");
        assertNotNull(location);

        String fragment = new URI(location).getFragment();
        String[] fragments = fragment.split("&");
        String idToken = Arrays.stream(fragments)
                .filter((param) -> param.startsWith("id_token"))
                .findFirst()
                .orElseThrow()
                .split("=")[1];

        assertNotNull(idToken);
        DecodedJWT jwt = JWT.decode(idToken);

        assertEquals("VZ", jwt.getIssuer());
        assertEquals("nonce-test", jwt.getClaim("nonce").asString());
    }

    @Test
    void redirectionUrlShouldContainPassedState() throws Exception {
        Client createdClient = createClient("ClientA");

        String url = MessageFormat.format("/authorize?response_type={0}&client_id={1}&redirect_uri={2}&scope={3}&state={4}&nonce={5}",
                createdClient.getAllowedResponseTypes().iterator().next().getType(), createdClient.getClientId().toString(),
                createdClient.getRedirectURIs().iterator().next().getUrl(), "openid", "state-value", "nonce-test");

        this.mockMvc.perform(get(url))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("state=state-value")));
    }

    @Test
    void shouldReturnErrorWhenUsingIncorrectToken() throws Exception {
        // Token with incorrect shared secret
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJJbmNvcnJlY3QifQ.BAR3M2KEc2al8AGxCEEEogsDgmSEclU04faculfhbP00_" +
                "1FbiliyeqGHYRa3QcwxjSTcwIydWJYwxarn7lyJ_Q";
        this.mockMvc.perform(get("/test/ping").header("Authorization", token))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnPongWhenUsingCorrectToken() throws Exception {
        // Correct token
        String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJpc3MiOiJWWiJ9.VnzhWsG3MTIJb_RsDtMNuHnHQdYS_MFFj_" +
                "TmCBuqZLPnHDf78wqTEIrtLHbB7gVFbwd2_AzXjED1pRBK15D09w";
        this.mockMvc.perform(get("/test/ping").header("Authorization", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("pong"));
    }
}
