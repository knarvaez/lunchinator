package com.narvaezfamily.lunchinator;

import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.WebClient;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;

/**
 * <p>Title: FetchRestaurants.java</p>
 * <p>Description: Class to asynchronously contact the interview service
 * to retrieve restaurant and review data.</p>
 * @author Kevin Narvaez <knarvaez@storyrock.com>
 * Created on Mar 24, 2018
 */
public class FetchRestaurants {
	public static final int DEFAULT_LIMIT = 5;
	public static final String RESTAURANTS_LOADED = "restaurantsLoaded";

	// web client to retrieve retaurants from interview api
	private WebClient client;
	private final WebClientOptions options = new WebClientOptions()
            .setDefaultHost("interview-project-17987.herokuapp.com")
            .setDefaultPort(443)
            .setSsl(true);
	private List<Restaurant> restaurants;
	private Vertx vertx;

	/**
	 * 
	 * @param vertx
	 */
	public FetchRestaurants(Vertx pVertx) {
		vertx = pVertx;
		client = WebClient.create(vertx, options);
		vertx.executeBlocking(future -> {
			fetch().subscribe(data -> {
				future.complete(data);
			}, error -> {error.printStackTrace();});
		}, (AsyncResult<JsonArray> result) ->{
			if(!result.failed()) {
				restaurants = new LinkedList<>();
				JsonArray data = result.result();
				data.forEach(object ->{
					if(object instanceof JsonObject) {
						JsonObject jobj = (JsonObject)object;
						Restaurant restaurant = new Restaurant(jobj, client);
						restaurants.add(restaurant);
					}
				});
				vertx.eventBus().publish(RESTAURANTS_LOADED, null);
			}
		});
	}

	private Observable<JsonArray> fetch() {
		return client
			.get("/api/restaurants")
			.rxSend()
			.toObservable()
			.map(response -> response.bodyAsJsonArray());
	}

	/**
	 * Picks a random list of <code>DEFAULT_LIMIT<code> restaurants with no duplicate items.
	 * @return List of random order resataurants
	 */
	public List<Restaurant> pickRandom() {
		List<Restaurant> copy = new LinkedList<>(restaurants);
		Collections.shuffle(copy);
		return copy.subList(0, DEFAULT_LIMIT);
	}
}
