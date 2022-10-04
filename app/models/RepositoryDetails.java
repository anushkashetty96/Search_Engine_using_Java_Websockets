package models;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the object to be displayed on the page - Repository profile
 * @author Ashwin Raghunath 40192120
 */
public class RepositoryDetails {
    private Repository repository;
    private List<Issue> issues;

    public RepositoryDetails() {
        this.issues=new ArrayList<>();
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }


    public List<Issue> getIssues() {
        return this.issues;
    }

    public void setIssues(List<Issue> issues) {
        this.issues = issues;
    }
}
