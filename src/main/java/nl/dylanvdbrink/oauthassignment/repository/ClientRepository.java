package nl.dylanvdbrink.oauthassignment.repository;

import nl.dylanvdbrink.oauthassignment.model.Client;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ClientRepository extends CrudRepository<Client, UUID> {
}
