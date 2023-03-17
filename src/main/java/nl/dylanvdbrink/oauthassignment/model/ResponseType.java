package nl.dylanvdbrink.oauthassignment.model;

import jakarta.persistence.*;

@Entity
public class ResponseType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String type;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name="client_id", nullable=false)
    private Client client;

    public ResponseType() {
        this.type = "";
    }

    public ResponseType(String type, Client client) {
        this.type = type;
        this.client = client;
    }

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

}
