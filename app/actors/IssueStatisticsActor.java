package actors;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import models.IssueWordStatistics;
import play.cache.AsyncCacheApi;
import services.GithubService;
/**
 * Actor to fetch the  for a given repository and userName
 * @author Anushka R Shetty
 */
public class IssueStatisticsActor extends AbstractActor {

	private ActorRef sessionActor;
	private GithubService githubService;

	/**
	 * Constructor needed in order create actor using Props method
	 * @author Anushka R Shetty
	 * @param sessionActor reference of the supervisor
	 * @param githubService service used to fetch issue word level statistics
	 */
	public IssueStatisticsActor(ActorRef sessionActor, GithubService githubService) {
		this.sessionActor = sessionActor;
		this.githubService = githubService;
	}
	/**
	 * Matches the incoming message for the IssueStatisticsActor
	 * @author Anushka R Shetty
	 * @return Builder object after formation
	 */
	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return receiveBuilder().match(Messages.GetIssueStatisticsActor.class, issueWordLevelStats -> {
			onGetIssueStatistics(issueWordLevelStats).thenAcceptAsync(this::processIssueStatisticsResult);
		}).build();
	}

	/**
	 * Props method of akka to create the actor
	 * @author Anushka R Shetty
	 * @param supervisorActor actor reference of the supervisor
	 * @param githubService service used to fetch issue word level statistics
	 * @return
	 */
	public static Props props(ActorRef  supervisorActor, GithubService githubService) {
		return Props.create(IssueStatisticsActor.class, supervisorActor, githubService);
	}

	/**
	 * Runs on initialization of IssueStatisticsActor
	 */
	@Override
	public void preStart() {
		System.out.println("Issue Statistics actor created.");
	}

	/** calls the githubService and fetches the JsonNode result of the issue word level statistics
	 * @author Anushka R Shetty
	 * @param issueWordLevelStats request object consisting username and repositoryname
	 * @return JsonNode of the issue word level statistics
	 * @throws Exception
	 */
	private CompletionStage<JsonNode> onGetIssueStatistics(Messages.GetIssueStatisticsActor issueWordLevelStats)
			throws Exception {


		return githubService.getAllIssues(issueWordLevelStats.username, issueWordLevelStats.repositoryName)
				.thenApplyAsync(
						issueStats -> {
							ObjectMapper mapper = new ObjectMapper();
							ObjectNode issueStatData = mapper.createObjectNode();
							JsonNode issueStatJsonNode = mapper.convertValue(issueStats, JsonNode.class);


							issueStatData.put("responseType", "issueStatisticsPage");
							issueStatData.set("issueStatList", issueStatJsonNode);
							issueStatData.put("respositoryName",issueWordLevelStats.repositoryName);
							return issueStatData;
						}
				);
	}

	/**
	 * sends the issue word level statistics JsonNode to the supervisorActor
	 * @param issueWordLevelStats JsonNode to be displayed on the page
	 * @author Anushka R Shetty
	 */
	private void processIssueStatisticsResult(JsonNode issueWordLevelStats) {
		sessionActor.tell(new Messages.IssueStatistics(issueWordLevelStats), getSelf());
	}

}
