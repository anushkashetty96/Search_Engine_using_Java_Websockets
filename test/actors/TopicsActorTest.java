package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.JsonNode;
import models.SearchResults;
import models.UserRepositoryTopics;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import play.cache.AsyncCacheApi;
import services.GithubService;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Class to test the TopicsActor
 * @author Trusha Patel
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class TopicsActorTest {

    static ActorSystem actorSystem;
    private static TestKit testProbe;
    private static GithubService githubServiceMock;
    private static AsyncCacheApi asyncCacheApi;

    /**
     * Overrides the API call with the Mock API Call class
     */

    @Before
    public  void setup() {
        actorSystem = ActorSystem.create();
        testProbe = new TestKit(actorSystem);
        githubServiceMock = Mockito.mock(GithubService.class);
        asyncCacheApi = Mockito.mock(AsyncCacheApi.class);
    }

    /**
     * Test case for TopicsActor
     */
    @Test
    public void TopicActorTest() {

        new TestKit(actorSystem) {
            {
                Mockito.when(asyncCacheApi.getOrElseUpdate(anyString(),any())).thenReturn(TopicStageSearch());
//                Mockito.when(githubServiceMock.searchResultsUsingActors(anyString())).thenReturn(completionStageSearch());
                final ActorRef topicActor = actorSystem.actorOf(
                        TopicsActor.props(testProbe.getRef(), githubServiceMock, asyncCacheApi));
                topicActor.tell(new Messages.GetRepositoryfromTopic("JAVA AI DL"), testProbe.getRef());
                Messages.TopicDetails actual = testProbe.expectMsgClass(Messages.TopicDetails.class);
                Messages.TopicDetails topicResponse = actual;
                assertEquals("topicsDetails",topicResponse.topicDetails.get("responseType").asText());
                List<String> repoNames = new ArrayList<>();
                for(JsonNode repository: topicResponse.topicDetails.get("searchProfile").get("repos")){
                    repoNames.add(repository.get("name").asText());
                    //System.out.println(repoNames);
                }
                assertEquals(Arrays.asList("name1","name2"),repoNames);
            }
        };
    }

    /**
     * Mock searchResult object
     * @return Future of object returned on calling GithubService's getReposByTopics function
     *
     */
    public CompletionStage<Object> TopicStageSearch()
    {
        return CompletableFuture.supplyAsync(() -> {
            SearchResults searchResults = new SearchResults();
            List<UserRepositoryTopics> searchItem = new ArrayList<>();
            UserRepositoryTopics userRepositoryTopics1 = new UserRepositoryTopics("owner1","name1");
            userRepositoryTopics1.setTopics(Arrays.asList("topic1","topic2"));
            UserRepositoryTopics userRepositoryTopics2 = new UserRepositoryTopics("owner2","name2");
            userRepositoryTopics2.setTopics(Arrays.asList("topic3","topic4"));
            searchItem.add(userRepositoryTopics1);
            searchItem.add(userRepositoryTopics2);
            searchResults.setKeyword("JAVA AI DL");
            searchResults.setRepos(searchItem);
            return searchResults;
        });
    }
}