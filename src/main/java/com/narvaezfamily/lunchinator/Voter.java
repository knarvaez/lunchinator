package com.narvaezfamily.lunchinator;

import io.vertx.core.json.JsonObject;

/**
 * <p>Title: Voter.java</p>
 * <p>Description: Class that represents an individual voter that will participate in the Lunchinator
 * ballot.</p>
 * @author Kevin Narvaez <knarvaez@storyrock.com>
 * Created on Mar 25, 2018
 */
public class Voter {
	private static final String NAME_KEY = "name";
	private static final String EMAIL_KEY = "emailAddress";
	private final String name;
	private final String email;

	public Voter(JsonObject voter) {
		name = voter.getString(NAME_KEY);
		email = voter.getString(EMAIL_KEY);
	}

	/**
	 * Retrieve voter name.
	 * @return String of voter name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieve voter email address.
	 * @return String of email address.
	 */
	public String getEmail() {
		return email;
	}
}
