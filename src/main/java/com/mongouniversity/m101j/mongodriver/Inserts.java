package com.mongouniversity.m101j.mongodriver;

import com.mongouniversity.m101j.mongodriver.tools.DBHelper;
import com.mongouniversity.m101j.mongodriver.tools.RandomData;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by giovanni on 31/10/16.
 */
public class Inserts {

    /**
     * Insert one hundred random people into
     * 'people' collection
     */
    private void insertPeople() {

        //
        // Using insertOne method
        for (int i = 0; i < 10; i++) {
            Document person = new Document();
            person
                    .append("name", RandomData.randomName())
                    .append("lastName", RandomData.randomLastname())
                    .append("age", RandomData.randomAge());

            DBHelper.getPeopleCollection().insertOne(person);
        }

        //
        // Using insertMany method
        List<Document> persons = new ArrayList<Document>();
        for (int i = 0; i < 90; i++) {
            Document person = new Document();
            person
                    .append("name", RandomData.randomName())
                    .append("lastName", RandomData.randomLastname())
                    .append("age", RandomData.randomAge());

            persons.add(person);
        }
        DBHelper.getPeopleCollection().insertMany(persons);
    }

    public static void main(String[] args) {
        Inserts inserts = new Inserts();
        inserts.insertPeople();
    }
}
