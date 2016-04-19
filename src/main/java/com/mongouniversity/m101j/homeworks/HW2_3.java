package com.mongouniversity.m101j.homeworks;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;

public class HW2_3 {
	
	public static void main(String[] args) {
		
		MongoClient client  = new MongoClient("172.17.0.2");
		MongoDatabase dbase = client.getDatabase("students");
		MongoCollection<Document> grades = dbase.getCollection("grades");
		
		Bson filter  = Filters.eq("type", "homework");
		Bson sorting = new Document("student_id", 1).append("score", 1);
		List<Document> gradesOrdered = grades.find(filter).sort(sorting).into(new ArrayList<Document>());
		
		// Ids to delete
		List<ObjectId> idsToDelete = new ArrayList<ObjectId>();
		
		// Get all ids to delete
		double currentStudentId = -1;
		for(Document grade : gradesOrdered) {
			double studentId = grade.getDouble("student_id");
			if(studentId != currentStudentId) {
				idsToDelete.add(grade.getObjectId("_id"));
				currentStudentId = studentId;
			}
		}
		
		// Deletes ids with lowest score for type homework on each student (1 per student)
		System.out.println("To delete: " + idsToDelete.size());
		for(ObjectId objectId : idsToDelete) {
			Bson filterDel = Filters.eq("_id", objectId);
			grades.deleteOne(filterDel);
		}
		
		
		// Finally close connection
		client.close();
	}

}
