package com.mongouniversity.m101j;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;

public class HelloFremarker {

	public static void main(String[] args) {
		
		Configuration configuration = new Configuration();
		configuration.setClassForTemplateLoading(HelloFremarker.class, "/");
		
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
