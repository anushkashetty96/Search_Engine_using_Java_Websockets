package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.cache.AsyncCacheApi;
import services.GithubService;

import java.util.concurrent.CompletionStage;

/**
 * Actor to find the repositories based on the topic queried
 * @author Trusha Patel
 */

public class TopicsActor extends AbstractActor {

    private ActorRef sessionActor;
    private AsyncCacheApi asyncCacheApi;
    private GithubService githubService;

    /**
     * @param sessionActor Actor reference for the supervisor actor
     * @param githubService Instance of <code>GithubService</code> inteface, used to make external API calls to GitHub
     * @param asyncCacheApi to utilize the asynchronous chache
     */

    public TopicsActor(ActorRef sessionActor, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        this.sessionActor = sessionActor;
        this.githubService = githubService;
        this.asyncCacheApi = asyncCacheApi;
    }

    /**
     * Creates a topic-actor with properties passed in parameters
     * @param sessionActor Actor reference to the supervisor actor
     * @param githubService Instance of <code>GitHubAPI</code> inteface, used to make external API calls to GitHub
     * @param asyncCacheApi to utilize asynchronous cache
     * @return A <code>Props</code> with the actor's configuration
     */
    public static Props props(ActorRef sessionActor, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        return Props.create(TopicsActor.class, sessionActor, githubService , asyncCacheApi);
    }

    /**
     * Method called once directly during the initialization of the first instance of this actor
     */
    @Override
    public void preStart() {
        System.out.println("Topics actor created.");
    }

    /**
     * Match the class of an incoming message and take the appropriate action
     * @return Topics Search Response defined in an <code>AbstractActor.Receive</code>
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.GetRepositoryfromTopic.class, topicsInfo -> {
                    onGetTopicsSearch(topicsInfo).thenAcceptAsync(this::processTopicsResult);
                })
                .build();
    }
    /**
     * Gets search result information for provided query and calls <code>processTopicsResult</code> to process it
     * @param topicsRequest <code>GetRepositoryfromTopic</code> request to retrieve the information for
     * @throws Exception If the call cannot be completed due to an error
     */

    private CompletionStage<JsonNode> onGetTopicsSearch(Messages.GetRepositoryfromTopic topicsRequest) throws Exception {

        return asyncCacheApi.getOrElseUpdate(topicsRequest.topic_name ,
                        () -> githubService.getReposByTopics(topicsRequest.topic_name))
                .thenApplyAsync(
                        searchDetails -> {
                            asyncCacheApi.set(topicsRequest.topic_name,searchDetails,60*20);
                            ObjectMapper mapper = new ObjectMapper();
                            ObjectNode repositoryData = mapper.createObjectNode();
                            JsonNode repositoryJsonNode = mapper.convertValue(searchDetails, JsonNode.class);
                            repositoryData.put("responseType", "topicsDetails");
                            repositoryData.set("searchProfile", repositoryJsonNode);
                            return repositoryData;
                        }
                );
    }

    /**
     * Based on provides search response, creates and sends a JSON response
     * Sends the search result to be forwarded to the client
     * @param topicInfo Search result containing information about 10 repositories based on the queried topic
     */

    private void processTopicsResult(JsonNode topicInfo) {
        sessionActor.tell(new Messages.TopicDetails(topicInfo), getSelf());
    }
}
