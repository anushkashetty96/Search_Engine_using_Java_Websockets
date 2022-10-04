package models;

import java.util.Map;
/**
 * Represents the object to be displayed on the page - Commit Stats
 * @author Anmol Malhtora 40201452
 */
public class CommitDetails {
    private String repositoryName;
    private int totalCommitsOnRepository;
    private int minimumAdditions;
    private int minimumDeletions;
    private int maximumAdditions;
    private int maximumDeletions;
    private int averageDeletions;
    private int averageAdditions;
    private Map<String, Integer> mapOfUserAndCommits;

    public Map<String, Integer> getMapOfUserAndCommits() {
        return mapOfUserAndCommits;
    }

    public void setMapOfUserAndCommits(Map<String, Integer> mapOfUserAndCommits) {
        this.mapOfUserAndCommits = mapOfUserAndCommits;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public int getTotalCommitsOnRepository() {
        return totalCommitsOnRepository;
    }

    public void setTotalCommitsOnRepository(int totalCommitsOnRepository) {
        this.totalCommitsOnRepository = totalCommitsOnRepository;
    }

    public int getMinimumAdditions() {
        return minimumAdditions;
    }

    public void setMinimumAdditions(int minimumAdditions) {
        this.minimumAdditions = minimumAdditions;
    }

    public int getMinimumDeletions() {
        return minimumDeletions;
    }

    public void setMinimumDeletions(int minimumDeletions) {
        this.minimumDeletions = minimumDeletions;
    }

    public int getMaximumAdditions() {
        return maximumAdditions;
    }

    public void setMaximumAdditions(int maximumAdditions) {
        this.maximumAdditions = maximumAdditions;
    }

    public int getMaximumDeletions() {
        return maximumDeletions;
    }

    public void setMaximumDeletions(int maximumDeletions) {
        this.maximumDeletions = maximumDeletions;
    }

    public int getAverageDeletions() {
        return averageDeletions;
    }

    public void setAverageDeletions(int averageDeletions) {
        this.averageDeletions = averageDeletions;
    }

    public int getAverageAdditions() {
        return averageAdditions;
    }

    public void setAverageAdditions(int averageAdditions) {
        this.averageAdditions = averageAdditions;
    }
}
