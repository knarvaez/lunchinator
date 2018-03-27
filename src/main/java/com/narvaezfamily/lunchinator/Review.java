package com.narvaezfamily.lunchinator;

import io.vertx.core.json.JsonObject;

/**
 * <p>Title: Review.java</p>
 * <p>Description: Class that represents a single restaurant review.</p>
 * @author Kevin Narvaez <knarvaez@storyrock.com>
 * Created on Mar 27, 2018
 */
public class Review {
	private final int id;
	private final String restaurantName;
	private final String reviewer;
	private int rating;
	private String review;
	private String reviewerImage;

	public Review(JsonObject reviewJson) {
		id = reviewJson.getInteger("Id");
		restaurantName = reviewJson.getString("restaurant");
		reviewer = reviewJson.getString("reviewer");
		String rtStr = reviewJson.getString("rating");
		rating = rtStr != null ? Integer.parseInt(rtStr) : 0;
		review = reviewJson.getString("review");
		reviewerImage = reviewJson.getString("reviewerImage");
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public String getReview() {
		return review;
	}

	public void setReview(String review) {
		this.review = review;
	}

	public String getReviewerImage() {
		return reviewerImage;
	}

	public void setReviewerImage(String reviewerImage) {
		this.reviewerImage = reviewerImage;
	}

	public int getId() {
		return id;
	}

	public String getRestaurantName() {
		return restaurantName;
	}

	public String getReviewer() {
		return reviewer;
	}


}
