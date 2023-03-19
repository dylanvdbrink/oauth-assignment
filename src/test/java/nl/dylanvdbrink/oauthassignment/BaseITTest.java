package nl.dylanvdbrink.oauthassignment;

import nl.dylanvdbrink.oauthassignment.model.Client;
import nl.dylanvdbrink.oauthassignment.model.RedirectURI;
import nl.dylanvdbrink.oauthassignment.model.ResponseType;
import nl.dylanvdbrink.oauthassignment.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
public class BaseITTest {
    private static final Logger logger = LoggerFactory.getLogger(BaseITTest.class);

    @Autowired
    protected ClientRepository clientRepository;

    protected void cleanDatabase() {
        logger.debug("Cleaning database");
        clientRepository.deleteAll();
    }

    protected Client createClient(String name) {
        Client client = new Client(name);
        RedirectURI redirectURI = new RedirectURI("https://vodafoneziggo.nl", client);
        ResponseType responseType = new ResponseType(ResponseType.ID_TOKEN, client);
        client.setAllowedResponseTypes(Set.of(responseType));
        client.setRedirectURIs(Set.of(redirectURI));

        return clientRepository.save(client);
    }
}
