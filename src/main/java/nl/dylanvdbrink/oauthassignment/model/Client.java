package nl.dylanvdbrink.oauthassignment.model;

import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private UUID clientId;

    private String name;

    @OneToMany(mappedBy="client", cascade=CascadeType.ALL)
    private Set<RedirectURI> redirectURIs;

    @OneToMany(mappedBy="client", cascade=CascadeType.ALL)
    private Set<ResponseType> allowedResponseTypes;

    public Client() {
        this.clientId = UUID.randomUUID();
        this.name = "";
    }

    public Client(String name) {
        this.clientId = UUID.randomUUID();
        this.name = name;
    }

    public Client(String name, UUID clientId) {
        this.clientId = clientId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public String getName() {
        return name;
    }

    public Set<RedirectURI> getRedirectURIs() {
        return redirectURIs;
    }

    public void setRedirectURIs(Set<RedirectURI> redirectURIs) {
        this.redirectURIs = redirectURIs;
    }

    public Set<ResponseType> getAllowedResponseTypes() {
        return allowedResponseTypes;
    }

    public void setAllowedResponseTypes(Set<ResponseType> allowedResponseTypes) {
        this.allowedResponseTypes = allowedResponseTypes;
    }
}
