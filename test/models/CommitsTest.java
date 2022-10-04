package models;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CommitsTest {

    @Test
    public void commitTest() {
        Commits commits = new Commits();
        commits.setName("anmol");
        commits.setAdditions(11);
        commits.setSha("abcd");
        commits.setDeletions(11);

        assertEquals("anmol", commits.getName());
        assertEquals(11, commits.getAdditions());
        assertEquals("abcd", commits.getSha());
        assertEquals(11, commits.getDeletions());
    }
}
