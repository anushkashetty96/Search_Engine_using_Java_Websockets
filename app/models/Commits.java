package models;

/**
 * Represents the object used to set and fetch custom commit stats data
 * @author Anmol Malhtora 40201452
 */
public class Commits {
    private String name;
    private int additions;
    private int deletions;
    private String sha;

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAdditions() {
        return additions;
    }

    public void setAdditions(int additions) {
        this.additions = additions;
    }

    public int getDeletions() {
        return deletions;
    }

    public void setDeletions(int deletions) {
        this.deletions = deletions;
    }

    public Commits(String name, int additions, int deletions){
        this.name = name;
        this.additions = additions;
        this.deletions = deletions;
    }

    public Commits(){}

}
