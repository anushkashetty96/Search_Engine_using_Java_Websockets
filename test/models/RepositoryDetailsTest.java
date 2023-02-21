package models;

import org.eclipse.egit.github.core.Issue;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class RepositoryDetailsTest {

    @Test
    public void getIssuesTest()
    {
        RepositoryDetails repositoryDetails = new RepositoryDetails();
        Issue issue = new Issue();
        issue.setTitle("title");
        repositoryDetails.setIssues(Arrays.asList(issue));
        List<Issue> issues = repositoryDetails.getIssues();
        assertEquals("title",issues.get(0).getTitle());
    }
}
