package com.mongouniversity.m101j;

import freemarker.template.Configuration;
import freemarker.template.Template;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class HelloSparkFreemarker {
	
	public static void main( String[] args ) {
		final Configuration configuration = new Configuration();
		configuration.setClassForTemplateLoading(HelloSparkFreemarker.class, "/");
		
        Spark.get("/", new Route() {
        	public Object handle(Request request, Response response) throws Exception {
				try {
					Template template = configuration.getTemplate("hello.ftl");
					StringWriter writer = new StringWriter();
					
					Map<String, Object> helloMap = new HashMap<String, Object>();
					helloMap.put("name", "Giovanni");
					
					template.process(helloMap, writer);
					return writer;
					
				} catch (Exception e) {
					e.printStackTrace();
					return "Error 500";
				}
			}
        });
    }
}
