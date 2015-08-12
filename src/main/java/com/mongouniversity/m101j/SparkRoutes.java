package com.mongouniversity.m101j;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class SparkRoutes 
{
	public static void main( String[] args )
    {
		final Configuration configuration = new Configuration();
		configuration.setClassForTemplateLoading(HelloFremarker.class, "/");
		
        Spark.get("/", new Route() 
        { 
        	public Object handle(Request request, Response response) throws Exception 
			{
				return "Hello Spark routes";
			}	
        });
        
        Spark.get("/test", new Route() 
        { 
        	public Object handle(Request request, Response response) throws Exception 
			{
				return "Hello test Spark route";
			}	
        });
        
        Spark.get("/echo/:thing", new Route() 
        { 
        	public Object handle(Request request, Response response) throws Exception 
			{
				return "Echoing: " + request.params(":thing");
			}
        });
        
        Spark.get("/favorite_fruit", new Route() 
        { 
        	public Object handle(Request request, Response response) throws Exception 
			{
				Map<String, Object> fruits = new HashMap<String, Object>();
				fruits.put("fruits", Arrays.asList("apple", "orange", "banana", "melon"));
				
				Template fruitPicker = configuration.getTemplate("fruitsForm.ftl");
				StringWriter writer = new StringWriter();
				
				fruitPicker.process(fruits, writer);
				return writer;
			}
        });
        
        Spark.post("/favorite_fruit", new Route() 
        { 
        	public Object handle(Request request, Response response) throws Exception 
			{
				final String fruit = request.queryParams("fruit");
				if (fruit == null) {
					return "Why you not choose a fruit?";
				}
				else {
					return "Your favorite fruit is: " + fruit;
				}
			}
        });
        
    }

}
