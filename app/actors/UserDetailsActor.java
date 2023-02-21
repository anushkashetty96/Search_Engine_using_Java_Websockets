package actors;

import akka.actor.AbstractActor;
import akka.actor.AbstractActorWithTimers;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.egit.github.core.Repository;
import scala.concurrent.duration.Duration;
import services.GithubService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

/**
 * Actor to fetch the user details for a given user
 * @author Sourav Uttam Sinha 40175660
 */
public class UserDetailsActor extends AbstractActorWithTimers {

    private ActorRef supervisorActor;
    private GithubService githubService;

    /**
     * Constructor needed in order create actor using Props method
     * @author Sourav Uttam Sinha 40175660
     * @param supervisorActor actor reference of the supervisor
     * @param githubService service used to fetch user details
     */
    public UserDetailsActor(ActorRef supervisorActor, GithubService githubService) {
        this.supervisorActor = supervisorActor;
        this.githubService = githubService;
    }

    /**
     * Props method of akka to create the actor
     * @author Sourav Uttam Sinha 40175660
     * @param supervisorActor actor reference of the supervisor
     * @param githubService service used to fetch user details
     * @return
     */
    public static Props props(ActorRef supervisorActor, GithubService githubService) {
        return Props.create(UserDetailsActor.class, supervisorActor, githubService);
    }

    /**
     * Runs on initialization of UserDetailsActor
     */
    @Override
    public void preStart() {
        System.out.println("UserDetails actor created.");
    }

    /**
     * Matches the incoming message for the UserDetailsActor
     * @author Sourav Uttam Sinha 40175660
     * @return Builder object after formation
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Messages.GetUserDetailsActor.class, userDetailsRequest -> {
                    getUserDetails(userDetailsRequest).thenAcceptAsync(this::processUserDetails);
//                    getTimers().startPeriodicTimer("userDetails",
//                            new Messages.GetUserDetailsActor(userDetailsRequest.username),
//                            Duration.create(10, TimeUnit.SECONDS));
                })
                .build();
    }

    /** calls the githubService and fetches the JsonNode result of the repository
     * @author Sourav Uttam Sinha 40175660
     * @param userDetailRequest request object consisting username
     * @return JsonNode of the user details searched
     * @throws Exception
     */
    private CompletionStage<JsonNode> getUserDetails(Messages.GetUserDetailsActor userDetailRequest) throws Exception {

        return githubService.getUserDetails(userDetailRequest.username)
                .thenApplyAsync(
                        userDetails -> {
                            ObjectMapper mapper = new ObjectMapper();
                            ObjectNode userData = mapper.createObjectNode();
                            JsonNode userJsonNode = mapper.convertValue(userDetails.getUser(), JsonNode.class);
                            List<Repository> repositories = userDetails.getRepository();
                            List<String> repositoryNames = new ArrayList<>();
                            for (Repository repository : repositories) {
                                repositoryNames.add(repository.getName());
                            }
                            ArrayNode arrayNode = mapper.createArrayNode();
                            repositoryNames.forEach(arrayNode::add);
                            userData.put("responseType", "userDetails");
                            userData.set("userDetails", userJsonNode);
                            userData.set("repositoryList", arrayNode);
                            return userData;
                        }
                );
    }

    /**
     * sends the user details JsonNode to the supervisorActor
     * @param userDetails JsonNode to be displayed on the page
     * @author Sourav Uttam Sinha 40175660
     */
    private void processUserDetails(JsonNode userDetails) {
        supervisorActor.tell(new Messages.UserDetails(userDetails), getSelf());
    }

}
