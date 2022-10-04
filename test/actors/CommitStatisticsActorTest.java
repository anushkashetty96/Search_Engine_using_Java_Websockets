package actors;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.JsonNode;
import models.CommitDetails;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import services.GithubService;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Test class for CommitStatisticsActor
 * @author Anmol Malhotra
 */

@RunWith(MockitoJUnitRunner.Silent.class)
public class CommitStatisticsActorTest {

    static ActorSystem actorSystem;
    private static TestKit testProbe;
    private static GithubService githubServiceMock;

    @Before
    public void setup() {
        actorSystem = ActorSystem.create();
        testProbe = new TestKit(actorSystem);
        githubServiceMock = Mockito.mock(GithubService.class);
    }

    public CompletionStage<CommitDetails> commitStatisticsCompletionStage() {
        return CompletableFuture.supplyAsync(() -> {
            CommitDetails commitDetails = new CommitDetails();
            commitDetails.setRepositoryName("repo");
            commitDetails.setTotalCommitsOnRepository(1);
            commitDetails.setMinimumAdditions(160);
            commitDetails.setMinimumDeletions(0);
            commitDetails.setMaximumAdditions(160);
            commitDetails.setMaximumDeletions(0);
            commitDetails.setAverageAdditions(160);
            commitDetails.setAverageDeletions(0);
            HashMap<String, Integer> userCommitsMap = new HashMap<>();
            userCommitsMap.put("anmol", 1);
            commitDetails.setMapOfUserAndCommits(userCommitsMap);
            return commitDetails;
        });
    }

    /**
     * Test Case for Commits Page
     *
     * @author Anmol Malhotra 40201452
     */
    @Test
    public void actorTest() {
        new TestKit(actorSystem) {
            {
                Mockito.when(githubServiceMock.getCommitStatisticsForRepository(anyString(), anyString())).thenReturn(commitStatisticsCompletionStage());
                final ActorRef commitStatActor = actorSystem
                        .actorOf(CommitStatisticsActor.props(testProbe.getRef(), githubServiceMock));
                commitStatActor.tell(new Messages.GetCommitStatisticsActor("username", "MockRepositoryName"), testProbe.getRef());
                Messages.CommitStatistics commitStatisticsResponse = testProbe.expectMsgClass(Messages.CommitStatistics.class);
                JsonNode commitStatistics = commitStatisticsResponse.commitStatistics.get("commitStatsList");
                Assert.assertEquals(1, commitStatistics.get("mapOfUserAndCommits").size());
                assertEquals("MockRepositoryName",commitStatisticsResponse.commitStatistics.get("repositoryName").asText());
            }
        };
    }
}
