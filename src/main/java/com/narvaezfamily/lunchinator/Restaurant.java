package com.narvaezfamily.lunchinator;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.client.WebClient;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
	private final int id;
	private final String name;
	private int waitTimeMinutes;
	private List<String> types;
	private String image;
	private String description;
	private List<Review> reviews;

	public Restaurant(JsonObject restaurantJson, WebClient client) {
		webClient = client;
		id = restaurantJson.getInteger("id");
		name = restaurantJson.getString("name");
		types = new LinkedList<>();
		JsonArray cuisineTypes = restaurantJson.getJsonArray("types");
		if(cuisineTypes != null) {
			types.addAll(cuisineTypes.getList());
		}
		Integer wtm = restaurantJson.getInteger("waitTimeMinutes");
		waitTimeMinutes = wtm != null ? wtm : 0;
		image = restaurantJson.getString("image");
		description = restaurantJson.getString("description");
		reviews = new ArrayList<>();
		fetchReviews().subscribe(jsonArray -> {
			for(Object obj:jsonArray) {
				if(obj instanceof JsonObject) {
					JsonObject jobj = (JsonObject)obj;
					Review review = new Review(jobj);
					reviews.add(review);
				}
			}
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
		String encodedName  = null;

		try	{
			encodedName = URLEncoder.encode(name, "UTF-8");
		} catch(UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}

		if(encodedName != null) {
			return webClient
				.get(String.format("/api/reviews/%s", encodedName))
				.rxSend()
				.toObservable()
				.map(response -> response.bodyAsJsonArray());
		} else {
			return null;
		}
	}
}
