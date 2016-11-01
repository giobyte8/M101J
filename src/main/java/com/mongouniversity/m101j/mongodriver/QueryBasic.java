package com.mongouniversity.m101j.mongodriver;

import com.mongodb.client.MongoCursor;
import com.mongouniversity.m101j.mongodriver.tools.DBHelper;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by giovanni on 31/10/16.
 */
public class QueryBasic {

    /**
     * Retrieves a document from 'people' collection
     * through find first
     */
    private void findFirst() {
        Document firstPerson = DBHelper.getPeopleCollection().find().first();

        System.out.println("\nFound through find first:");
        System.out.println(firstPerson);
    }

    /**
     * Retrieves all documents from 'people' collection
     * and put them into an {@link java.util.ArrayList}
     */
    private void findInto() {
        List<Document> persons = new ArrayList<Document>();
        DBHelper.getPeopleCollection().find().into(persons);

        System.out.println("\nFound through find into");
        for (Document person : persons) {
            System.out.println(person.toJson());
        }
    }

    /**
     * Retrieves all documents from 'people' collection
     * through a cursors and iterates it printint each
     * found person
     */
    private void findIteration() {
        MongoCursor<Document> cursor = DBHelper.getPeopleCollection()
                .find()
                .iterator();
        try {
            System.out.println("\nFound through cursor iteration");
            while (cursor.hasNext()) {
                System.out.println(cursor.next());
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Retrieve the number of elements into 'people' collection
     * through a count over the collection
     */
    private void countPersons() {
        long count = DBHelper.getPeopleCollection().count();
        System.out.println("\nPersons in collection: " + count);
    }

    public static void main(String[] args) {
        QueryBasic queryBasic = new QueryBasic();
        queryBasic.findFirst();
        queryBasic.findInto();
        queryBasic.findIteration();
        queryBasic.countPersons();
    }
}
