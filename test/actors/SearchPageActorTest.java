package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import models.UserRepositoryTopics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import play.cache.AsyncCacheApi;
import services.GithubService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * Test class for SearchPageActor
 * @author Ashwin Raghunath
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SearchPageActorTest {

    static ActorSystem actorSystem;
    private static TestKit testProbe;
    private static GithubService githubServiceMock;
    private static AsyncCacheApi asyncCacheApi;

    @Before
    public  void setup() {
        actorSystem = ActorSystem.create();
        testProbe = new TestKit(actorSystem);
        githubServiceMock = Mockito.mock(GithubService.class);
        asyncCacheApi = Mockito.mock(AsyncCacheApi.class);
    }

    /**
     * Test case for RepositoryDetailsActor
     */
    @Test
    public void actorTest() {

        new TestKit(actorSystem) {
            {
                Mockito.when(githubServiceMock.searchResultsUsingActors(anyString(),anyString())).thenReturn(searchResultsMockObject());
//                Mockito.when(asyncCacheApi.getOrElseUpdate(anyString(),any())).thenReturn(completionStageSearch());
                final ActorRef searchPageActor = actorSystem.actorOf(
                        SearchPageActor.props(testProbe.getRef(), githubServiceMock, asyncCacheApi));
                searchPageActor.tell(new Messages.SearchPageActor("phrase"), testProbe.getRef());
                Messages.SearchResult searchResultResponse = testProbe.expectMsgClass(Messages.SearchResult.class);
                assertEquals("searchResults",searchResultResponse.searchResult.get("responseType").asText());
                assertEquals("name",searchResultResponse.searchResult.get("searchMap").get("JAVA AI DL").get(0).get("name").asText());
            }
        };
    }

    public CompletionStage<Map<String,List<UserRepositoryTopics>>> searchResultsMockObject()
    {
        return CompletableFuture.supplyAsync(() -> {

            Map<String,List<UserRepositoryTopics>> map = new HashMap<>();
            UserRepositoryTopics userRepositoryTopics = new UserRepositoryTopics("owner","name");
            map.put("JAVA AI DL",Arrays.asList(userRepositoryTopics));
            return map;

        });
    }

    /**
     * Mock searchResult object
     * @return future of Object that gets return on calling search function in github service
     */
    public CompletionStage<Object> completionStageSearch()
    {
        return CompletableFuture.supplyAsync(() -> {
            UserRepositoryTopics userRepositoryTopics = new UserRepositoryTopics("owner","name");
            Map<String,List<UserRepositoryTopics>> map = new HashMap<>();
            map.put("JAVA AI DL", Arrays.asList(userRepositoryTopics));
            return map;
        });
    }
}
