package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.JsonNode;
import models.RepositoryDetails;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import services.GithubService;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;


/**
 * Test class for RepositoryDetailsActor
 * @author Ashwin Raghunath
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class RepositoryDetailsActorTest {

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
     * Mock RepositoryDetails object
     * @return future of RepositoryDetails
     */
    public CompletionStage<RepositoryDetails> repositoryDetailsCompletionStage()
    {
        return CompletableFuture.supplyAsync(() -> {

            RepositoryDetails repositoryDetails = new RepositoryDetails();
            repositoryDetails.setRepository(repository());
            repositoryDetails.setIssues(issues());
            return repositoryDetails;

        });
    }

    /**
     * mock repository to be added to RepositoryDetails
     * @return Repository with name set
     */
    private Repository repository()
    {
        Repository repository = new Repository();
        repository.setName("MockRepoName");
        return repository;
    }

    /**
     * mock issues to be added to RepositoryDetails
     * @return issues list with title set
     */
    private List<Issue> issues()
    {
        Issue issue = new Issue();
        issue.setTitle("title");
        return Arrays.asList(issue);
    }

    /**
     * Test case for RepositoryDetailsActor
     */
    @Test
    public void actorTest() {

        new TestKit(actorSystem) {
            {
                Mockito.when(githubServiceMock.getRepositoryDetails(anyString(), anyString())).thenReturn(repositoryDetailsCompletionStage());
                final ActorRef repositoryActor = actorSystem.actorOf(
                        RepositoryDetailsActor.props(testProbe.getRef(), githubServiceMock));
                repositoryActor.tell(new Messages.GetRepositoryDetailsActor("username","MockRepoName"), testProbe.getRef());
                Messages.RepositoryDetails repositoryDetailsResponse = testProbe.expectMsgClass(Messages.RepositoryDetails.class);
                JsonNode repositoryDetails = repositoryDetailsResponse.repositoryDetails.get("repositoryDetails");
                assertEquals("MockRepoName",repositoryDetails.get("name").asText());
            }
        };
    }

}
