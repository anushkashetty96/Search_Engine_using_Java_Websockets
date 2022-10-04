package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.JsonNode;
import models.IssueWordStatistics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import services.GithubService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Test class for IssueStatisticsActor
 * @author Anushka R Shetty
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class IssueStatisticsActorTest {

    static ActorSystem actorSystem;
    private static TestKit testProbe;
    private static GithubService githubServiceMock;

    @Before
    public  void setup() {
        actorSystem = ActorSystem.create();
        testProbe = new TestKit(actorSystem);
        githubServiceMock = Mockito.mock(GithubService.class);
    }

    /**
     * Mock IssueStatistics object
     * @return future of IssueStatistics
     */
    public CompletionStage<IssueWordStatistics> issueStatisticsCompletionStage()
    {
        return CompletableFuture.supplyAsync(() -> {
            Map<String,Integer> wordFrequency = new HashMap<>();
            wordFrequency.put("null",2);
            wordFrequency.put("pointer",1);
            wordFrequency.put("exception",1);
            wordFrequency.put("reference",1);
            wordFrequency.put("index",1);
            wordFrequency.put("out",1);
            wordFrequency.put("bound",1);
            IssueWordStatistics issueWordStatistics = new IssueWordStatistics(wordFrequency);
            return issueWordStatistics;
        });
    }
    /**
     * Test case for IssueStatisticsActor
     */
    @Test
    public void actorTest() {

        new TestKit(actorSystem) {
            {
                Mockito.when(githubServiceMock.getAllIssues(anyString(), anyString())).thenReturn(issueStatisticsCompletionStage());
                final ActorRef issueStatsActor = actorSystem.actorOf(
                        IssueStatisticsActor.props(testProbe.getRef(), githubServiceMock));
                issueStatsActor.tell(new Messages.GetIssueStatisticsActor("username","MockRepoName"), testProbe.getRef());
                Messages.IssueStatistics issueStatisticsResponse = testProbe.expectMsgClass(Messages.IssueStatistics.class);
                JsonNode issueStatistics = issueStatisticsResponse.issueStatistics.get("issueStatList");
                assertEquals(2,issueStatistics.get("wordfrequency").get("null").asInt());
                assertEquals("issueStatisticsPage",issueStatisticsResponse.issueStatistics.get("responseType").asText());
                assertEquals("MockRepoName",issueStatisticsResponse.issueStatistics.get("respositoryName").asText());
                assertEquals(7,issueStatistics.get("wordfrequency").size());
            }
        };
    }

}
