package com.mongouniversity.m101j;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import freemarker.template.Configuration;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class HelloMongoDBSparkFremarker {
	
	public static void main(String[] args) {
		
		final Configuration configuration = new Configuration();
		configuration.setClassForTemplateLoading(HelloMongoDBSparkFremarker.class, "/mongodb");
		
		// MongoDB Connection and collection
		MongoClient client  = new MongoClient("172.17.0.1");
		MongoDatabase dbase = client.getDatabase("jmongo");
		final MongoCollection<Document> people = dbase.getCollection("people");
		
		Spark.get("/oneperson", new Route() {
			
			public Object handle(Request request, Response response) throws Exception {
				
				Document firstPerson = people.find().first();
				return "First person found was: " + firstPerson.getString("first_name");
			}
		});
	}

}
