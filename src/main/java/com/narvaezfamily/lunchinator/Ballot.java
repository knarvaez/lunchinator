package com.narvaezfamily.lunchinator;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
	private final List<Voter> voters;

    public Ballot(JsonObject ballotJson) {
		voters = new ArrayList<>();
		ballotId = UUID.randomUUID();
		String endTimeJson = ballotJson.getString("endTime");
		endTime = LocalDateTime.parse(endTimeJson, DATE_FORMATTER);
		JsonArray voterArray = ballotJson.getJsonArray("voters");
		for(Object obj:voterArray) {
			if(obj instanceof JsonObject) {
				JsonObject jobj = (JsonObject)obj;
				Voter voter = new Voter(jobj);
				voters.add(voter);
			}
		}
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public UUID getBallotId() {
		return ballotId;
	}

	public List<Voter> getVoters() {
		return voters;
	}
}
