package com.narvaezfamily.lunchinator;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.WebClient;
import io.vertx.rxjava.ext.web.handler.BodyHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import rx.Observable;

/**
 * <p>Title: LunchinatorVerticle.java</p>
 * <p>Description: A {@link Verticle} which starts up the HTTP server for the web application UI.</p>
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

		FetchRestaurants fr = new FetchRestaurants(vertx);
		vertx.executeBlocking(future -> {
			fr.fetch().subscribe(data -> {
				future.complete(data);
			}, error -> {error.printStackTrace();});
		}, (AsyncResult<JsonArray> result) ->{
			if(!result.failed()) {
				JsonArray data = result.result();
				data.forEach(object ->{
					if(object instanceof JsonObject) {
						JsonObject jobj = (JsonObject)object;
						System.out.println(jobj.toString());
					}
				});
			}
		});

        setupRoutes(router);
        startServer(router);

        startFuture.complete();
    }

	private void setupRoutes(Router router) {
		router.route().handler(BodyHandler.create());
		router.route(HttpMethod.POST, "/api/create-ballot").blockingHandler(this::handleCreateBallot);
		router.route(HttpMethod.GET, "/api/ballot/:ballotId").blockingHandler(this::handleGetBallot);
    }

	private void startServer(Router router) {
		// prepare the SSL configuration off of the event bus to prevent blocking.
//		vertx.executeBlocking(future ->{
//			try {
//
//			}
//		}, (AsyncResult<HttpServerOptions> result) -> {
//        if (!result.failed()) {
//          vertx.createHttpServer(result.result()).requestHandler(router::accept).listen(LUNCHINATOR_PORT);
//          log.info("SSL Web server now listening");
//          startFuture.complete();
//        }
//      });
//        HttpServer server = vertx.createHttpServer(new HttpServerOptions().setSsl(true).setKeyStoreOptions(
//        new JksOptions().setPath("server-keystore.jks").setPassword("wibble")));
		HttpServer server = vertx.createHttpServer();
        server.requestHandler(router::accept).listen(LUNCHINATOR_PORT);
    }

	private void handleCreateBallot(RoutingContext context) {
		JsonObject body = context.getBodyAsJson();
		Ballot newBallot = new Ballot(body);
		ballotMap.put(newBallot.getBallotId(), newBallot);
		context.response().end(new JsonObject().put("ballotId", newBallot.getBallotId().toString()).toString());




//		FetchStories.fetchTopStoriesWithComments(client)
//                .subscribe(data -> {
//                    context.response().end(new JsonObject().put("topStories", data).toString());
//                }, error -> {
//                    error.printStackTrace();
//                }, () -> System.out.println("\nFinished fetching all the stories with comments!\n"));
    }

	private void handleGetBallot(RoutingContext context) {
		String ballotId = context.request().getParam("ballotId");
		if(ballotId == null || ballotMap.get(UUID.fromString(ballotId)) == null) {
			context.response().setStatusCode(400).end();
		} else {
			Ballot ballot = ballotMap.get(UUID.fromString(ballotId));
			context.response()
				.setStatusCode(201)
				.putHeader("content-type", "application/json;charset=utf-8")
				.end(Json.encodePrettily(ballot));
		}
	}
}
