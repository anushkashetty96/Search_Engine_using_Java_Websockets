package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.cache.AsyncCacheApi;
import services.GithubService;


/**
 * Actor to fetch the list of repositories for a given phrase on the main search page
 * @author Ashwin Raghunath, Trusha Patel, Anushka Shetty, Sourav Sinha, Anmol Malhotra
 */
public class SupervisorActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    private final ActorRef wsOut;
    private GithubService githubService;
    private final AsyncCacheApi asyncCacheApi;
    private ActorRef repositoryDetailsActor = null;
    private ActorRef issueStatisticsActor = null;
    private ActorRef searchPageActor = null;
    private ActorRef topicsSearchActor = null;
    private ActorRef userDetailsActor = null;
    private ActorRef commitStatisticsActor = null;

    public SupervisorActor(final ActorRef wsOut, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        this.wsOut =  wsOut;
        this.githubService = githubService;
        this.asyncCacheApi = asyncCacheApi;
    }

    public static Props props(final ActorRef wsout, GithubService githubService, AsyncCacheApi asyncCacheApi) {
        return Props.create(SupervisorActor.class, wsout, githubService, asyncCacheApi);
    }


    @Override
    public void preStart() {
        System.out.println("Supervisor actor created.");
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(JsonNode.class, this::processRequest)
                .match(Messages.RepositoryDetails.class, repositoryDetails -> wsOut.tell(repositoryDetails.repositoryDetails, self()))
                .match(Messages.IssueStatistics.class, issueStatistics -> wsOut.tell(issueStatistics.issueStatistics, self()))
                .match(Messages.SearchResult.class, searchResult -> wsOut.tell(searchResult.searchResult, self()))
                .match(Messages.TopicDetails.class,topicSearchInfo->wsOut.tell(topicSearchInfo.topicDetails,self()))
                .match(Messages.UserDetails.class, userDetails -> wsOut.tell(userDetails.userDetails, self()))
                .match(Messages.CommitStatistics.class, commitStatistics -> wsOut.tell(commitStatistics.commitStatistics, self()))
                .matchAny(other -> log.error("Unknown message received: " + other.getClass()))
                .build();
    }


    private void processRequest(JsonNode receivedJson) throws JsonProcessingException {
    	log.info(receivedJson.asText());
        ObjectMapper mapper = new ObjectMapper();
        if(receivedJson.has("searchPage")) {

            if(searchPageActor==null)
            {
                log.info("Creating a search page actor.");
                searchPageActor = getContext().actorOf(SearchPageActor.props(self(), githubService, asyncCacheApi));
            }
            String phrase = receivedJson.get("searchPage").asText();
            searchPageActor.tell(new Messages.SearchPageActor(phrase), getSelf());

        } else if(receivedJson.has("repositoryDetails")) {
            String repositoryName = receivedJson.get("repositoryDetails").asText();
            String username = receivedJson.get("username").asText();
            if(repositoryDetailsActor == null) {
                log.info("Creating a repository profile actor.");
                repositoryDetailsActor = getContext().actorOf(RepositoryDetailsActor.props(self(), githubService));
            }
            repositoryDetailsActor.tell(new Messages.GetRepositoryDetailsActor(username, repositoryName), getSelf());
        }
        else if(receivedJson.has("topicsDetails")){
            String topic_name = receivedJson.get("topicsDetails").asText();
            if(topicsSearchActor == null){
                topicsSearchActor = getContext().actorOf(TopicsActor.props(self(),githubService,asyncCacheApi));
            }
            topicsSearchActor.tell(new Messages.GetRepositoryfromTopic(topic_name),getSelf());
        }
        else if(receivedJson.has("userDetails")) {
            String username = receivedJson.get("username").asText();
            if(userDetailsActor == null) {
                log.info("Creating a user profile actor.");
                userDetailsActor = getContext().actorOf(UserDetailsActor.props(self(), githubService));
            }
            userDetailsActor.tell(new Messages.GetUserDetailsActor(username), getSelf());
        }
        else if(receivedJson.has("issueStatisticsPage")) {
            String repositoryName = receivedJson.get("repositoryName").asText();
            String userName = receivedJson.get("userName").asText();
            if(issueStatisticsActor == null) {
                log.info("Creating a issue statistics actor.");
                issueStatisticsActor = getContext().actorOf(IssueStatisticsActor.props(self(), githubService));
            }
            issueStatisticsActor.tell(new Messages.GetIssueStatisticsActor(userName, repositoryName), getSelf());
        }
        else if(receivedJson.has("commitStatisticsPage")) {
            String repositoryName = receivedJson.get("repositoryName").asText();
            String userName = receivedJson.get("userName").asText();
            if(commitStatisticsActor == null) {
                System.out.println("Creating a commit statistics actor.");
                commitStatisticsActor = getContext().actorOf(CommitStatisticsActor.props(self(), githubService));
            }
            commitStatisticsActor.tell(new Messages.GetCommitStatisticsActor(userName, repositoryName), getSelf());
        }
    }
}