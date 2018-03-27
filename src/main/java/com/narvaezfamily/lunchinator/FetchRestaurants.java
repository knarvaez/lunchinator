package com.narvaezfamily.lunchinator;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.WebClient;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

/**
 * <p>Title: FetchRestaurants.java</p>
 * <p>Description: Description Here.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: StoryRock, Inc.</p>
 * <p>Revision: $Id$</p>
 * @author Kevin Narvaez <knarvaez@storyrock.com>
 * Created on Mar 24, 2018
 */
public class FetchRestaurants {
	public static final int DEFAULT_LIMIT = 5;
	// web client to retrieve retaurants from interview api
	private WebClient client;
	private final WebClientOptions options = new WebClientOptions()
            .setDefaultHost("interview-project-17987.herokuapp.com")
            .setDefaultPort(443)
            .setSsl(true);

	public FetchRestaurants(Vertx vertx) {
		client = WebClient.create(vertx, options);
	}

	public Observable<JsonArray> fetch() {
		return client
			.get("/api/restaurants")
			.rxSend()
			.toObservable()
			.map(response -> response.bodyAsJsonArray());
	}
}
