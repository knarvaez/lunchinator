/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.narvaezfamily.lunchinator;

import io.vertx.rxjava.core.Vertx;

/**
 *
 * @author Kevin Narvaez <knarvaez@storyrock.com>
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(LunchinatorVerticle.class.getName())
                .subscribe(deployID -> {
//                            vertx.close();
							System.out.println("Lunchinator is running...");
                        },
                        error -> {
                            error.printStackTrace();
                            vertx.close();
                        });
	}

}
