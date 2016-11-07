package com.mongouniversity.m101j.mongodriver;

import com.mongodb.client.model.Filters;
import com.mongouniversity.m101j.mongodriver.tools.DBHelper;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates how to make replaces, updates
 * and upserts
 *
 * @author giovanni
 */
public class Updates {

    /**
     * Replace all people which name to be equals to <code>name</code>
     * param, with a new document with name equal to <code>updatedName</code>.
     *
     * @param name Al people with this name will be replaced
     * @param updatedName Name to use in replacement documents
     */
    private void replace(String name, String updatedName) {
        DBHelper.getPeopleCollection().replaceOne(
                Filters.eq("name", name),
                new Document("name", updatedName)

                        .append("updated", true)
        );

        List<Document> people = new ArrayList<Document>();
        DBHelper.getPeopleCollection().find().into(people);
        for (Document document : people) {
            System.out.println(document);
        }
    }

    /**
     * Update all people with name equals to <code>name</code>
     * param with given name
     * @param name People with this name will be updated
     * @param updatedName This name will be assigned to updated people
     */
    private void update(String name, String updatedName) {
        DBHelper.getPeopleCollection().updateMany(
                Filters.eq("name", name),
                com.mongodb.client.model.Updates.set("name", updatedName)
        );

        List<Document> people = new ArrayList<Document>();
        DBHelper.getPeopleCollection().find().into(people);
        for (Document document : people) {
            System.out.println(document);
        }
    }

    public static void main(String[] args) {
        Updates updates = new Updates();
        updates.replace("Giovanni", "Updated name");
        updates.update("Giovanni", "Updated name");
    }
}
