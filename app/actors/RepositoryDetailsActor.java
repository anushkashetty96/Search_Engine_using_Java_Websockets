package actors;

import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.egit.github.core.Issue;
import services.GithubService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;


/**
 * Actor to fetch the repository details for a given repository
 * @author Ashwin Raghunath
 */
public class RepositoryDetailsActor extends AbstractActorWithTimers {

    private ActorRef supervisorActor;
    private GithubService githubService;


    /**
     * Constructor needed in order create actor using Props method
     * @author Ashwin Raghunath
     * @param supervisorActor actor reference of the supervisor
     * @param githubService service used to fetch repository details
     */
    public RepositoryDetailsActor(ActorRef supervisorActor, GithubService githubService) {
        this.supervisorActor = supervisorActor;
        this.githubService = githubService;
    }

    /**
     * Props method of akka to create the actor
     * @author Ashwin Raghunath
     * @param supervisorActor actor reference of the supervisor
     * @param githubService service used to fetch repository details
     * @return
     */
    public static Props props(ActorRef supervisorActor, GithubService githubService) {
        return Props.create(RepositoryDetailsActor.class, supervisorActor, githubService);
    }

    /**
     * Runs on initialization of RepositoryDetailsActor
     */
    @Override
    public void preStart() {
        System.out.println("RepositoryDetails actor created.");
    }

    /**
     * Matches the incoming message for the RepositoryDetailsActor
     * @author Ashwin Raghunath
     * @return Builder object after formation
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.GetRepositoryDetailsActor.class, repositoryDetailsRequest -> {
                    getRepositoryDetails(repositoryDetailsRequest).thenAcceptAsync(this::processRepositoryDetails);
//                    getTimers().startPeriodicTimer("repositoryDetails",
//                            new Messages.GetRepositoryDetailsActor(repositoryDetailsRequest.username,repositoryDetailsRequest.repositoryName),
//                            Duration.create(15, TimeUnit.SECONDS));
                })
                .build();
    }

    /** calls the githubService and fetches the JsonNode result of the repository
     * @author Ashwin Raghunath
     * @param repositoryDetailRequest request object consisting username and repositoryname
     * @return JsonNode of the repository details searched
     * @throws Exception
     */
    private CompletionStage<JsonNode> getRepositoryDetails(Messages.GetRepositoryDetailsActor repositoryDetailRequest) throws Exception {
        return githubService.getRepositoryDetails(repositoryDetailRequest.username, repositoryDetailRequest.repositoryName)
                .thenApplyAsync(
                        repositoryDetails -> {
                            ObjectMapper mapper = new ObjectMapper();
                            ObjectNode repositoryData = mapper.createObjectNode();
                            JsonNode repositoryJsonNode = mapper.convertValue(repositoryDetails.getRepository(), JsonNode.class);
                            List<Issue> issues = repositoryDetails.getIssues();
                            List<String> issueNames = new ArrayList<>();
                            for (Issue issue : issues) {
                                issueNames.add(issue.getTitle());
                            }
                            ArrayNode arrayNode = mapper.createArrayNode();
                            issueNames.forEach(arrayNode::add);
                            repositoryData.put("responseType", "repositoryDetails");
                            repositoryData.set("repositoryDetails", repositoryJsonNode);
                            JsonNode issuesJsonNode = mapper.convertValue(repositoryDetails.getIssues(), JsonNode.class);
                            repositoryData.set("issueListNode", issuesJsonNode);

                            return repositoryData;
                        }
                );
    }

    /**
     * sends the repository details JsonNode to the supervisorActor
     * @param repositoryDetails JsonNode to be displayed on the page
     * @author Ashwin Raghunath
     */
    private void processRepositoryDetails(JsonNode repositoryDetails) {
        supervisorActor.tell(new Messages.RepositoryDetails(repositoryDetails), getSelf());
    }
}
