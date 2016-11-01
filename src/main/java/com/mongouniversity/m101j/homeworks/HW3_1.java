package com.mongouniversity.m101j.homeworks;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.mongouniversity.m101j.homeworks.tools.DBHelper;
import org.bson.Document;

import java.util.List;

public class HW3_1 {
	
	public static void main(String[] args) {

		MongoCollection<Document> students = DBHelper.getStudentsCollection();

		// Iterate over all students and get theirs scores
		MongoCursor<Document> cursor = students.find().iterator();
		while(cursor.hasNext()) {
			Document student = cursor.next();
			
			// Get all homework scores for this student
			List<Document> scores = (List<Document>) student.get("scores");
			
			// Get lowest score for this student
			Document lowestScore = null;
			for(Document score : scores) {
				if(score.getString("type").equals("homework")) {
					
					if(lowestScore == null) {
						lowestScore = score;
					}
					else if(lowestScore.getDouble("score") > score.getDouble("score")) {
						lowestScore = score;
					}
				}
			}
			
			// Remove lowest score from scores
			if(lowestScore != null) {
				scores.remove(lowestScore);
			}
			
			// Update student on database
			student.put("scores", scores);
			students.replaceOne(Filters.eq("_id", student.get("_id")), student);
			
		}
		
		System.out.println("Lowest homework scores removed");
		
		cursor.close();
	}

}
