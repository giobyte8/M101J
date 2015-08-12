package com.mongouniversity.m101j;

import spark.Spark;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Hello world!
 *
 */
public class HelloSpark 
{
    public static void main( String[] args )
    {
        Spark.get("/", new Route(){

			public Object handle(Request request, Response response) throws Exception {
				return "Hello world from Spark";
			}
        		
        });
    }
}
