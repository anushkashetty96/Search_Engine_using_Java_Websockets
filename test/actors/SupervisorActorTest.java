package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.User;
import org.junit.Assert;
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

/**
 * Test class for SupervisorActor
 * @author Ashwin Raghunath
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class SupervisorActorTest {

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
     * Test case for RepositoryDetailsActor flow in SupervisorActor
     */
    @Test
    public void supervisorActorTestForRepositoryDetailsFlow() {

        new TestKit(actorSystem) {
            {
                Mockito.when(githubServiceMock.getRepositoryDetails(anyString(), anyString())).thenReturn(repositoryDetailsCompletionStage());
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode repositoryData = mapper.createObjectNode();
                repositoryData.put("repositoryDetails", "KP_G05");
                repositoryData.put("username", "ashraghunath");
                final ActorRef supervisorActor = actorSystem.actorOf(
                        SupervisorActor.props(testProbe.getRef(), githubServiceMock,asyncCacheApi));
                supervisorActor.tell(repositoryData, testProbe.getRef());
                ObjectNode repositoryDetailsJsonNode = testProbe.expectMsgClass(ObjectNode.class);
                assertEquals("repositoryDetails",repositoryDetailsJsonNode.get("responseType").asText());
                assertEquals("KP_G05",repositoryDetailsJsonNode.get("repositoryDetails").get("name").asText());
            }
        };
    }

    /**
     * Test case for UserDetailsActor flow in SupervisorActor
     */
    @Test
    public void supervisorActorTestForUserDetailsFlow() {

        new TestKit(actorSystem) {
            {
                Mockito.when(githubServiceMock.getUserDetails(anyString())).thenReturn(userDetailsCompletionStage());
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode userData = mapper.createObjectNode();
                userData.put("userDetails", "sauravus");
                userData.put("username", "sauravus");
                final ActorRef supervisorActor = actorSystem.actorOf(
                        SupervisorActor.props(testProbe.getRef(), githubServiceMock,asyncCacheApi));
                supervisorActor.tell(userData, testProbe.getRef());
                ObjectNode userDetailsJsonNode = testProbe.expectMsgClass(ObjectNode.class);
                assertEquals("userDetails",userDetailsJsonNode.get("responseType").asText());
                assertEquals("sauravus",userDetailsJsonNode.get("userDetails").get("name").asText());
            }
        };
    }

    /**
     * Test case for SearchPageActor flow in SupervisorActor
     */
    @Test
    public void supervisorActorTestForSearchPageFlow() {

        new TestKit(actorSystem) {
            {
                Mockito.when(githubServiceMock.searchResultsUsingActors(anyString(),anyString())).thenReturn(searchResultsMockObject());
//                Mockito.when(asyncCacheApi.getOrElseUpdate(anyString(),any())).thenReturn(completionStageSearch());
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode repositoryData = mapper.createObjectNode();
                repositoryData.put("searchPage", "JAVA AI DL");
                final ActorRef supervisorActor = actorSystem.actorOf(
                        SupervisorActor.props(testProbe.getRef(), githubServiceMock,asyncCacheApi));
                supervisorActor.tell(repositoryData, testProbe.getRef());
                ObjectNode searchPageJsonNode = testProbe.expectMsgClass(ObjectNode.class);
                assertEquals("searchResults",searchPageJsonNode.get("responseType").asText());
                assertEquals("owner",searchPageJsonNode.get("searchMap").get("JAVA AI DL").get(0).get("owner").asText());
            }
        };
    }


    /**
     * Mock RepositoryDetails object
     * @return future of RepositoryDetails
     */
    public CompletionStage<RepositoryDetails> repositoryDetailsCompletionStage()
    {
        return CompletableFuture.supplyAsync(() -> {

            Repository repository = new Repository();
            repository.setName("KP_G05");
            Issue issue = new Issue();
            issue.setTitle("title");
            List<Issue> issues = Arrays.asList(issue);
            RepositoryDetails repositoryDetails = new RepositoryDetails();
            repositoryDetails.setRepository(repository);
            repositoryDetails.setIssues(issues);
            return repositoryDetails;

        });
    }

    /**
     * Mock UserDetails object
     * @return future of UserDetails
     */
    public CompletionStage<UserDetails> userDetailsCompletionStage()
    {
        return CompletableFuture.supplyAsync(() -> {

            User user = new User();
            user.setName("sauravus");
            Repository repository = new Repository();
            repository.setName("title");
            List<Repository> repositories = Arrays.asList(repository);
            UserDetails userDetails = new UserDetails();
            userDetails.setUser(user);
            userDetails.setRepository(repositories);
            return userDetails;

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
     * Test case for IssueStatisticsActor flow in SupervisorActor
     */
    @Test
    public void supervisorActorTestForIssueWordLevelStats() {

        new TestKit(actorSystem) {
            {
                Mockito.when(githubServiceMock.getAllIssues(anyString(), anyString())).thenReturn(issueStatsCompletionStage());
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode issueStatData = mapper.createObjectNode();

                issueStatData.put("issueStatisticsPage", "");
                issueStatData.put("repositoryName", "KP_G05");
                issueStatData.put("userName", "anushkashetty96");

                //issueStatData.put("issueStatList", "{\"exception\":1,\"reference\":1,\"pointer\":1,\"null\":2,\"bound\":1,\"index\":1,\"out\":1}\n");
                final ActorRef supervisorActor = actorSystem.actorOf(
                        SupervisorActor.props(testProbe.getRef(), githubServiceMock,asyncCacheApi));
                supervisorActor.tell(issueStatData, testProbe.getRef());
                ObjectNode issueStatsJsonNode = testProbe.expectMsgClass(ObjectNode.class);
                JsonNode issueStatistics = issueStatsJsonNode.get("issueStatList");
                assertEquals("issueStatisticsPage",issueStatsJsonNode.get("responseType").asText());
                assertEquals(2,issueStatistics.get("wordfrequency").get("null").asInt());
                assertEquals("KP_G05",issueStatsJsonNode.get("respositoryName").asText());
            }
        };
    }

    /**
     * Mock IssueWordStatistics object
     * @return future of IssueWordStatistics
     */
    public CompletionStage<IssueWordStatistics> issueStatsCompletionStage()
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
     * Test case for TopicsActor flow in SupervisorActor
     */
    @Test
    public void supervisorActorTestForTopicsSearchFlow() {

        new TestKit(actorSystem) {
            {
                Mockito.when(asyncCacheApi.getOrElseUpdate(anyString(),any())).thenReturn(topicsSearchCompletionStage());
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode topicData = mapper.createObjectNode();
                topicData.put("topicsDetails", "java");
                final ActorRef supervisor = actorSystem.actorOf(
                        SupervisorActor.props(testProbe.getRef(), githubServiceMock, asyncCacheApi));
                supervisor.tell(topicData, testProbe.getRef());
                ObjectNode topicsInfoNode = testProbe.expectMsgClass(ObjectNode.class);
                //System.out.println("Topics INfo: "+ topicsInfoNode);
                assertEquals("topicsDetails", topicsInfoNode.get("responseType").asText());
                List<String> owners = new ArrayList<>();
                for(JsonNode repository:topicsInfoNode.get("searchProfile").get("repos"))
                {
                    owners.add(repository.get("owner").asText());
                }
                assertEquals(Arrays.asList("owner1","owner2"),owners);
            }
        };
    }

    public CompletionStage<Object> topicsSearchCompletionStage()
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
            searchResults.setKeyword("java");
            searchResults.setRepos(searchItem);
            return searchResults;
        });
    }

    /**
     * Test case for CommitStatisticsActor flow in SupervisorActor
     */
    @Test
    public void supervisorActorTestForCommitStats() {

        new TestKit(actorSystem) {
            {
                Mockito.when(githubServiceMock.getCommitStatisticsForRepository(anyString(), anyString())).thenReturn(commitStatisticsCompletionStage());
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode commitStatsData = mapper.createObjectNode();

                commitStatsData.put("commitStatisticsPage", "");
                commitStatsData.put("repositoryName", "KP_G05");
                commitStatsData.put("userName", "anmolMalhotra97");

                final ActorRef supervisorActor = actorSystem.actorOf(
                        SupervisorActor.props(testProbe.getRef(), githubServiceMock,asyncCacheApi));
                supervisorActor.tell(commitStatsData, testProbe.getRef());
                ObjectNode commitStatsJsonNode = testProbe.expectMsgClass(ObjectNode.class);
                JsonNode commitStatistics = commitStatsJsonNode.get("commitStatsList");
                assertEquals("commitStatisticsPage",commitStatsJsonNode.get("responseType").asText());
                Assert.assertEquals(1, commitStatistics.get("mapOfUserAndCommits").size());
            }
        };
    }

    /**
     * Mock CommitStatistics object
     * @return future of CommitDetails
     */
    public CompletionStage<CommitDetails> commitStatisticsCompletionStage() {
        return CompletableFuture.supplyAsync(() -> {
            models.CommitDetails commitDetails = new models.CommitDetails();
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

}
