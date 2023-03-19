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
import java.util.UUID;

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

        Client clientA = new Client("Client A", UUID.fromString("00000000-0000-0000-0000-000000000000"));
        Client clientB = new Client("Client B", UUID.fromString("11111111-1111-1111-1111-111111111111"));
        Client clientC = new Client("Client C", UUID.fromString("22222222-2222-2222-2222-222222222222"));

        clientA.setRedirectURIs(Set.of(new RedirectURI("https://vodafoneziggo.nl/", clientA), new RedirectURI("https://ziggo.nl/", clientA)));
        clientA.setAllowedResponseTypes(Set.of(new ResponseType(ResponseType.ID_TOKEN, clientA)));

        clientB.setRedirectURIs(Set.of(new RedirectURI("https://ziggo.nl/", clientB)));
        clientB.setAllowedResponseTypes(Set.of(new ResponseType(ResponseType.ID_TOKEN, clientB)));

        clientC.setRedirectURIs(Set.of(new RedirectURI("https://vodafone.nl/", clientC)));
        clientC.setAllowedResponseTypes(Set.of(new ResponseType(ResponseType.ID_TOKEN, clientC)));

        clientRepository.save(clientA);
        clientRepository.save(clientB);
        clientRepository.save(clientC);
    }

}
