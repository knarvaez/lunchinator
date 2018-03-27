package com.narvaezfamily.lunchinator;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Vertx;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * <p>Title: Ballot.java</p>
 * <p>Description: Description Here.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: StoryRock, Inc.</p>
 * <p>Revision: $Id$</p>
 * @author Kevin Narvaez <knarvaez@storyrock.com>
 * Created on Mar 24, 2018
 */
public class Ballot {
	private static final String DATE_PATTERN = "M/d/yy H:mm";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
	private final LocalDateTime endTime;
	private final UUID ballotId;
	private final Set<Voter> validVoters;
	private List<Restaurant> choices;
	// Vote map key = restaurant id, value is set of voter email
	private final Map<Integer, Set<String>> voteMap;
	private FetchRestaurants fetchRestaurants;

    public Ballot(Vertx vertx, JsonObject ballotJson) {
		validVoters = new HashSet<>();
		voteMap = new HashMap<>();
		ballotId = UUID.randomUUID();
		String endTimeJson = ballotJson.getString("endTime");
		endTime = LocalDateTime.parse(endTimeJson, DATE_FORMATTER);
		JsonArray voterArray = ballotJson.getJsonArray("voters");
		for(Object obj:voterArray) {
			if(obj instanceof JsonObject) {
				JsonObject jobj = (JsonObject)obj;
				Voter voter = new Voter(jobj);
				validVoters.add(voter);
			}
		}

		// register for restaurants loaded message
		vertx.eventBus().consumer(FetchRestaurants.RESTAURANTS_LOADED, message -> {
			choices = fetchRestaurants.pickRandom();
		});
		fetchRestaurants = new FetchRestaurants(vertx);
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public UUID getBallotId() {
		return ballotId;
	}

	public Set<Voter> getValidVoters() {
		return validVoters;
	}

	/**
	 * To be allowed to vote on this ballot the voter must be in the set of valid voters and the
	 * restaurant id must be in the list of choices as well as the system time is before the ballot
	 * end time.
	 * @param restaurantId
	 * @param voter
	 * @return http return code to be used in the response; 204 for success vote with no content, 409
	 * for vote cast past deadline or invalid params.
	 */
	public int castVote(int restaurantId, Voter voter) {
		if(LocalDateTime.now().isBefore(endTime)) {
			boolean voteOk = false;
			if(validVoters.contains(voter)) {
				for(Restaurant restaurant:choices) {
					if(restaurant.getId() == restaurantId) {
						voteOk = true;
					}
				}
			}
			return voteOk ? 204 : 409;
		} else {
			return 409;
		}
	}
}
