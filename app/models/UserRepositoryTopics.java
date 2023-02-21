package models;

import org.eclipse.egit.github.core.SearchRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class to represent the returned repositories that matched the searched keyword or topic
 * @author Ashwin Raghunath, Trusha Patel
 */

public class UserRepositoryTopics extends SearchRepository {

    List<String> topics;
    String owner;
    String name;
    Date pushedAt;
    String description;

    /**
     * Contstructor for UserRepository object's creation
     * @param owner The owner name of the repository
     * @param name The name of the repository
     */

    public UserRepositoryTopics(String owner, String name) {
        super(owner, name);
        topics = new ArrayList<>();
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public Date getPushedAt() {
        return this.pushedAt;
    }

    public void setPushedAt(Date pushedAt) {
        this.pushedAt = pushedAt;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}