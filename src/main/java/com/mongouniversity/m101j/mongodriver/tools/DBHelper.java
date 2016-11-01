package com.mongouniversity.m101j.mongodriver.tools;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Holds global useful variables
 *
 * Created by giovanni on 31/10/16.
 */
public class DBHelper {

    private static final String DB_HOST = "localhost";
    private static MongoDatabase database;

    private static MongoDatabase getDBInstance() {
        if (database == null) {
            MongoClient mClient = new MongoClient(DB_HOST);
            database = mClient.getDatabase("m101j");
        }

        return database;
    }

    public static MongoCollection<Document> getPeopleCollection() {
        return getDBInstance().getCollection("people");
    }
}
