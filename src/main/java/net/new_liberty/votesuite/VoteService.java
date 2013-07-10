package net.new_liberty.votesuite;

/**
 * Represents a service that sends votes to Votifier.
 */
public class VoteService {
    private final String id;

    private String name;

    public VoteService(String id, String name) {
        this.id = id;
        this.name = name;
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
}
