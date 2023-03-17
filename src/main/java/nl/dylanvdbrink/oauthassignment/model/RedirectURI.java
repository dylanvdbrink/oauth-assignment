package nl.dylanvdbrink.oauthassignment.model;

import jakarta.persistence.*;

@Entity
public class RedirectURI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String url;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="client_id", nullable=false)
    private Client client;

    public RedirectURI() {
        this.url = "";
    }

    public RedirectURI(String url, Client client) {
        this.url = url;
        this.client = client;
    }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

}
