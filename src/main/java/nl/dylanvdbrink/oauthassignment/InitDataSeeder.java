package nl.dylanvdbrink.oauthassignment;

import jakarta.annotation.PostConstruct;
import nl.dylanvdbrink.oauthassignment.model.Client;
import nl.dylanvdbrink.oauthassignment.model.RedirectURI;
import nl.dylanvdbrink.oauthassignment.model.ResponseType;
import nl.dylanvdbrink.oauthassignment.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class InitDataSeeder {
    private final ClientRepository clientRepository;
    private static final Logger logger = LoggerFactory.getLogger(InitDataSeeder.class);

    public InitDataSeeder(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @PostConstruct
    public void seedData() {
        if (clientRepository.count() > 0) {
            return;
        }

        logger.info("Creating initial data");

        Client clientA = new Client("Client A");
        Client clientB = new Client("Client B");
        Client clientC = new Client("Client C");

        clientA.setRedirectURIs(Set.of(new RedirectURI("https://vodafoneziggo.nl/", clientA), new RedirectURI("https://ziggo.nl/", clientA)));
        clientA.setAllowedResponseTypes(Set.of(new ResponseType("id token", clientA)));

        clientB.setRedirectURIs(Set.of(new RedirectURI("https://ziggo.nl/", clientB)));
        clientB.setAllowedResponseTypes(Set.of(new ResponseType("id token", clientB)));

        clientC.setRedirectURIs(Set.of(new RedirectURI("https://vodafone.nl/", clientC)));
        clientC.setAllowedResponseTypes(Set.of(new ResponseType("id token", clientC)));

        clientRepository.save(clientA);
        clientRepository.save(clientB);
        clientRepository.save(clientC);
    }

}
