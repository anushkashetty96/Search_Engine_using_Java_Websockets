package models;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the object to be displayed on the page - User profile
 * @author Sourav Uttam Sinha 40175660
 */

public class UserDetails {
    private User user;
    private List<Repository> repository;

    public UserDetails() {
        this.repository = new ArrayList<>();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Repository> getRepository() {
        return repository;
    }

    public void setRepository(List<Repository> repository) {
        this.repository = repository;
    }
}
