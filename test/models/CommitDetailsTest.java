package models;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class CommitDetailsTest {

    @Test
    public void commitDetailsTest() {
        CommitDetails commitDetails = new CommitDetails();
        commitDetails.setMaximumAdditions(11);
        commitDetails.setMinimumDeletions(11);
        commitDetails.setAverageAdditions(11);
        commitDetails.setAverageDeletions(11);
        commitDetails.setMaximumDeletions(11);
        commitDetails.setMinimumAdditions(11);
        commitDetails.setTotalCommitsOnRepository(11);
        commitDetails.setRepositoryName("Test Repo");
        commitDetails.setMapOfUserAndCommits(new HashMap<>());

        assertEquals(11, commitDetails.getMaximumAdditions());
        assertEquals(11, commitDetails.getMinimumDeletions());
        assertEquals(11, commitDetails.getAverageAdditions());
        assertEquals(11, commitDetails.getAverageDeletions());
        assertEquals(11, commitDetails.getMaximumDeletions());
        assertEquals(11, commitDetails.getMinimumAdditions());
        assertEquals(11, commitDetails.getTotalCommitsOnRepository());
        assertEquals("Test Repo", commitDetails.getRepositoryName());
        assertEquals(0, commitDetails.getMapOfUserAndCommits().size());
    }
}
