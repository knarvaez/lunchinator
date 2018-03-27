/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.narvaezfamily.lunchinator;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.WebClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.runner.RunWith;

/**
 *
 * @author Kevin Narvaez <knarvaez@storyrock.com>
 */
@RunWith(VertxUnitRunner.class)
public class LunchinatorVerticleTest {

	@Rule
    public RunTestOnContext rule = new RunTestOnContext();

    Vertx vertx;
    WebClient client;

	@Before
	public void setUp(TestContext context) {
		vertx = new Vertx(rule.vertx());
        client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(LunchinatorVerticle.LUNCHINATOR_PORT));
        Async async = context.async();

		vertx.rxDeployVerticle(LunchinatorVerticle.class.getName())
                .subscribe(deployID -> {
                            context.assertEquals(true, deployID != null);
                            async.complete();
                        },
                        error -> {
                            error.printStackTrace();
                            context.fail();
                        });
	}

	/**
	 * Test of start method, of class LunchinatorVerticle.
	 * @param context
	 */
	@Test
	public void start(TestContext context) {
		System.out.println("start");
		Async async = context.async();

		JsonObject ballot = new JsonObject();
		ballot.put("endTime", "3/25/18 11:45");
		JsonArray voters = new JsonArray();
		voters.add(new JsonObject().put("name", "Bob").put("emailAddress", "bob@gmail.com"));
		voters.add(new JsonObject().put("name", "Jim").put("emailAddress", "jim@gmail.com"));
		ballot.put("voters", voters);

        client.post("/api/create-ballot")
			.sendJsonObject(ballot, ar -> {
				if(ar.succeeded()) {
					async.complete();
				} else {
					context.fail();
				}
			});
//                .rxSend()
//                .subscribe(stories -> {
//                    System.out.println(new JsonObject(stories.bodyAsString()).encodePrettily());
//                    async.complete();
//                }, err -> {
//                    err.printStackTrace();
//                    context.fail();
//                });
	}

}
