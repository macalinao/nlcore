package net.new_liberty.votesuite;

/**
 * Represents a service that sends votes to Votifier.
 */
public class VoteService {
    private final String id;

    private String name;

    private String link;

    public VoteService(String id, String name, String link) {
        this.id = id;
        this.name = name;
        this.link = link;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }
}
