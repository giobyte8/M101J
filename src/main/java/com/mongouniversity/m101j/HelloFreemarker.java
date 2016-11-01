package com.mongouniversity.m101j;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class HelloFreemarker {

	public static void main(String[] args) {
		
		Configuration configuration = new Configuration();
		configuration.setClassForTemplateLoading(HelloFreemarker.class, "/");
		
		try {
			Template template = configuration.getTemplate("hello.ftl");
			StringWriter writer = new StringWriter();
			
			Map<String, Object> helloMap = new HashMap<String, Object>();
			helloMap.put("name", "Giovanni");
			
			template.process(helloMap, writer);
			System.out.println(writer);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
