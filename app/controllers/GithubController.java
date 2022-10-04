package controllers;

import Helper.SessionHelper;
import actors.SupervisorActor;
import play.cache.AsyncCacheApi;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.streams.ActorFlow;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import services.GithubService;

import java.util.concurrent.CompletionStage;

import static play.mvc.Results.ok;
import akka.actor.ActorSystem;
import akka.stream.Materializer;

import javax.inject.Inject;

/**
 * Handles all incoming http requests and maps it to a particular method
 */
public class GithubController {

	private final FormFactory formFactory;
	private final GithubService githubService;
	private final SessionHelper sessionHelper;
	private AsyncCacheApi cache;
	private final AssetsFinder assetsFinder;
	private HttpExecutionContext httpExecutionContext;
	@Inject
	private Materializer materializer;
	@Inject
	private ActorSystem actorSystem;

	@Inject
	public GithubController(HttpExecutionContext httpExecutionContext, AssetsFinder assetsFinder, FormFactory formFactory, GithubService githubService, SessionHelper sessionHelper, AsyncCacheApi cache, Materializer materializer, ActorSystem actorSystem) {
		this.formFactory = formFactory;
		this.githubService = githubService;
		this.sessionHelper = sessionHelper;
		this.cache=cache;
		this.actorSystem=actorSystem;
		this.materializer=materializer;
		this.assetsFinder=assetsFinder;
		this.httpExecutionContext=httpExecutionContext;
	}
	
	/**
	 * Returns Result View for the Search page along with the session
	 * 
	 * @author Anushka Shetty 40192371
	 * @param request object
	 * @return Result View for the search page
	 */
	public Result index(Http.Request request) {
		if(sessionHelper.checkSessionExist(request))
		return ok(views.html.index.render(request, sessionHelper.getSearchResultsForCurrentSession(request, null, null)));
		else
	    return ok(views.html.index.render(request,null)).addingToSession(request, sessionHelper.getSessionKey(), sessionHelper.generateSessionValue());
	}

	/**
	 * Establishes socket connection with supervisor actor
	 * @return Websocket after initialization
	 * @author Ashwin Raghunath
	 */
	public WebSocket ws() {
		return WebSocket.Json.accept(request -> ActorFlow.actorRef(out -> SupervisorActor.props(out, githubService, cache), actorSystem, materializer));
	}

}
