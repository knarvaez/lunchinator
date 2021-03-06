package com.narvaezfamily.lunchinator;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * <p>Title: LunchinatorVerticle.java</p>
 * <p>Description: A vertx {@link Verticle} which starts up the HTTP server for the web application UI.</p>
 * @author Kevin Narvaez <knarvaez@storyrock.com>
 * Created on Mar 24, 2018
 */
public class LunchinatorVerticle extends AbstractVerticle {
	public static final int LUNCHINATOR_PORT = 8080;
	private Map<UUID, Ballot> ballotMap;

	@Override
    public void start(Future<Void> startFuture) {
		ballotMap = new HashMap<>();
        Router router = Router.router(vertx);

		setupRoutes(router);
        startServer(router);

        startFuture.complete();
    }

	private void setupRoutes(Router router) {
		router.route().handler(BodyHandler.create());
		router.route(HttpMethod.POST, "/api/create-ballot").blockingHandler(this::handleCreateBallot);
		router.route(HttpMethod.GET, "/api/ballot/:ballotId").blockingHandler(this::handleGetBallot);
		router.route(HttpMethod.POST, "/api/vote").blockingHandler(this::handleCastVote);
    }

	private void startServer(Router router) {
		HttpServer server = vertx.createHttpServer();
        server.requestHandler(router::accept).listen(LUNCHINATOR_PORT);
    }

	private void handleCreateBallot(RoutingContext context) {
		JsonObject body = context.getBodyAsJson();
		Ballot newBallot = new Ballot(vertx,body);
		ballotMap.put(newBallot.getBallotId(), newBallot);
		context.response().end(new JsonObject().put("ballotId", newBallot.getBallotId().toString()).toString());
    }

	private void handleGetBallot(RoutingContext context) {
		String ballotId = context.request().getParam("ballotId");
		if(ballotId == null || ballotMap.get(UUID.fromString(ballotId)) == null) {
			context.response().setStatusCode(404).end();
		} else {
			Ballot ballot = ballotMap.get(UUID.fromString(ballotId));
			context.response()
				.setStatusCode(201)
				.putHeader("content-type", "application/json;charset=utf-8")
				.end(ballot.getBallotJson().encodePrettily());
		}
	}

	private void handleCastVote(RoutingContext context) {
		String ballotId = context.request().getParam("ballotId");
		if(ballotId == null || ballotMap.get(UUID.fromString(ballotId)) == null) {
			context.response().setStatusCode(404).end();
		} else {
			Ballot ballot = ballotMap.get(UUID.fromString(ballotId));
			int restaurantId = Integer.parseInt(context.request().getParam("id"));
			String voterName = context.request().getParam("voterName");
			String email = context.request().getParam("emailAddress");
			JsonObject voterJson = new JsonObject().put("name", voterName).put("emailAddress", email);
			Voter voter = new Voter(voterJson);
			int code = ballot.castVote(restaurantId, voter);
			context.response().setStatusCode(code).end();
		}
	}
}
