package com.narvaezfamily.lunchinator;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.WebClient;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;

/**
 * <p>Title: Restaurant.java</p>
 * <p>Description: Class that represents single lunchinator restaurant choice.</p>
 * @author Kevin Narvaez <knarvaez@storyrock.com>
 * Created on Mar 26, 2018
 */
public class Restaurant {
	private final WebClient webClient;
	private final Vertx vertx;
	private final int id;
	private final String name;
	private int waitTimeMinutes;
	private List<String> types;
	private String image;
	private String description;
	private List<Review> reviews;
	private int averageRating = 0;
	private int highestRating = 0;

	public Restaurant(JsonObject restaurantJson, WebClient client, Vertx pVertx) {
		webClient = client;
		vertx = pVertx;
		id = restaurantJson.getInteger("id");
		name = restaurantJson.getString("name");
		types = new LinkedList<>();
		JsonArray cuisineTypes = restaurantJson.getJsonArray("types");
		if(cuisineTypes != null) {
			types.addAll(cuisineTypes.getList());
		}
		Object val = restaurantJson.getValue("waitTimeMinutes");
		if(val != null && val instanceof Integer) {
			waitTimeMinutes = (Integer)val;
		} else if(val != null && val instanceof String) {
			waitTimeMinutes = Integer.parseInt((String)val);
		}
		image = restaurantJson.getString("image");
		description = restaurantJson.getString("description");
		reviews = new ArrayList<>();
		fetchReviews().subscribe(jsonArray -> {
			int sum = 0;
			for(Object obj:jsonArray) {
				if(obj instanceof JsonObject) {
					JsonObject jobj = (JsonObject)obj;
					Review review = new Review(jobj);
					reviews.add(review);
					sum += review.getRating();
					highestRating = review.getRating() > highestRating ? review.getRating() : highestRating;
				}
			}
			averageRating = Math.round(sum / (float)reviews.size());
			vertx.eventBus().publish("REVIEWS_LOADED", null);
		});
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getWaitTimeMinutes() {
		return waitTimeMinutes;
	}

	public void setWaitTimeMinutes(int waitTimeMinutes) {
		this.waitTimeMinutes = waitTimeMinutes;
	}

	public List<String> getTypes() {
		return types;
	}

	public void setTypes(List<String> types) {
		this.types = types;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private Observable<JsonArray> fetchReviews() {
		String encodedName  = name.replace(" ", "%20");
		return webClient
			.get(String.format("/api/reviews/%s", encodedName))
			.rxSend()
			.toObservable()
			.map(response -> response.bodyAsJsonArray());
	}

	public int getAverageRating() {
		return averageRating;
	}

	public Review getTopReview() {
		Review topReview = null;
		for(Review review:reviews) {
			topReview = review.getRating() == highestRating ? review : null;
			if(topReview != null) {
				break;
			}
		}
		return topReview;
	}
}
