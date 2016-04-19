package com.mongouniversity.m101j;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class HelloSparkFremarker {
	
	public static void main( String[] args )
    {
		final Configuration configuration = new Configuration();
		configuration.setClassForTemplateLoading(HelloFremarker.class, "/");
		
        Spark.get("/", new Route() 
        { 
        	public Object handle(Request request, Response response) throws Exception 
			{
				
				try 
				{
					Template template = configuration.getTemplate("hello.ftl");
					StringWriter writer = new StringWriter();
					
					Map<String, Object> helloMap = new HashMap<String, Object>();
					helloMap.put("name", "Giovanni");
					
					template.process(helloMap, writer);
					return writer;
					
				} 
				catch (Exception e) 
				{ 
					e.printStackTrace();
					return "Error 500";
				}
				
				
			}
        		
        });
    }

}
