package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.JsonNode;
import models.UserDetails;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import services.GithubService;
//import views.html.repository;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;


/**
 * Test class for UserDetailsActor
 * @author Sourav Uttam Sinha 40175660
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class UserDetailsActorTest {

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
     * Mock UserDetails object
     * @return future of UserDetails
     */
    public CompletionStage<UserDetails> userDetailsCompletionStage()
    {
        return CompletableFuture.supplyAsync(() -> {

            UserDetails userDetails = new UserDetails();
            userDetails.setUser(user());
            userDetails.setRepository(repositories());
            return userDetails;

        });
    }

    /**
     * mock repository to be added to UserDetails
     * @return User with name set
     */
    private User user()
    {
        User user = new User();
        user.setName("MockUserName");
        return user;
    }

    /**
     * mock issues to be added to RepositoryDetails
     * @return issues list with title set
     */
    private List<Repository> repositories()
    {
        Repository repository = new Repository();
        repository.setName("title");
        return Arrays.asList(repository);
    }

    /**
     * Test case for RepositoryDetailsActor
     */
    @Test
    public void actorTest() {

        new TestKit(actorSystem) {
            {
                Mockito.when(githubServiceMock.getUserDetails(anyString())).thenReturn(userDetailsCompletionStage());
                final ActorRef userActor = actorSystem.actorOf(
                        UserDetailsActor.props(testProbe.getRef(), githubServiceMock));
                userActor.tell(new Messages.GetUserDetailsActor("username"), testProbe.getRef());
                Messages.UserDetails userDetailsResponse = testProbe.expectMsgClass(Messages.UserDetails.class);
                JsonNode userDetails = userDetailsResponse.userDetails.get("userDetails");
                assertEquals("MockUserName",userDetails.get("name").asText());
            }
        };
    }

}
