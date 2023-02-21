package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import services.GithubService;

import java.util.concurrent.CompletionStage;
/**
 * Actor to fetch the commit Stats for a given repository
 * @author Anmol Malhotra
 **/

public class CommitStatisticsActor extends AbstractActor {

    private ActorRef supervisorActor;
    private GithubService githubService;

    /**
     * Constructor needed in order create actor using Props method
     * @author Anmol Malhotra
     * @param supervisorActor reference of the supervisor
     * @param githubService service used to fetch commit statistics
     */

    public CommitStatisticsActor(ActorRef supervisorActor, GithubService githubService) {
        this.supervisorActor = supervisorActor;
        this.githubService = githubService;
    }

    /**
     * Props method of akka to create the actor
     * @author Anmol Malhotra
     * @param supervisorActor actor reference of the supervisor
     * @param githubService service used to fetch commit statistics
     */

    public static Props props(ActorRef supervisorActor, GithubService githubService) {
        return Props.create(CommitStatisticsActor.class, supervisorActor, githubService);
    }

    /**
     * Runs on initialization of CommitStatisticsActor
     */
    @Override
    public void preStart() {
        System.out.println("Commit Statistics Actor created");
    }

    /**
     * Matches the incoming message for the CommitStatisticsActor
     * @author Anmol Malhotra
     * @return Builder object after formation
     */

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Messages.GetCommitStatisticsActor.class, commitStats -> getCommitStatistics(commitStats).thenAcceptAsync(this::processCommitStatisticsResult)).build();
    }

    /** calls the githubService and fetches the JsonNode result of the commit statistics
     * @author Anmol Malhotra
     * @param commitStatsActor request object consisting username and repositoryName
     * @return JsonNode of the commit statistics
     */
    private CompletionStage<JsonNode> getCommitStatistics(Messages.GetCommitStatisticsActor commitStatsActor) {
        return githubService.getCommitStatisticsForRepository(commitStatsActor.username, commitStatsActor.repositoryName)
                .thenApplyAsync(commitStatsData -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    ObjectNode commitStatObjectNode = objectMapper.createObjectNode();
                    JsonNode commitStatsJsonNode = objectMapper.convertValue(commitStatsData, JsonNode.class);

                    commitStatObjectNode.put("responseType", "commitStatisticsPage");
                    commitStatObjectNode.set("commitStatsList", commitStatsJsonNode);
                    commitStatObjectNode.put("repositoryName", commitStatsActor.repositoryName);
                    return commitStatObjectNode;
                });
    }

    /**
     * sends the commit statistics JsonNode to the supervisorActor
     * @param commitStatistics JsonNode to be displayed on the page
     * @author Anmol Malhotra
     */

    private void processCommitStatisticsResult(JsonNode commitStatistics) {
        supervisorActor.tell(new Messages.CommitStatistics(commitStatistics), getSelf());
    }
}
