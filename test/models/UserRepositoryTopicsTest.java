package models;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class UserRepositoryTopicsTest {

    @Test
    public void getTopicsTest()
    {
        UserRepositoryTopics userRepositoryTopics = new UserRepositoryTopics("owner","name");
        userRepositoryTopics.setTopics(Arrays.asList("topic1"));
        assertEquals("topic1",userRepositoryTopics.getTopics().get(0));
    }
}
