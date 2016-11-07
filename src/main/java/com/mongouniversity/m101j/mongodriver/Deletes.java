package com.mongouniversity.m101j.mongodriver;

import com.mongodb.client.model.Filters;
import com.mongouniversity.m101j.mongodriver.tools.DBHelper;
import com.mongouniversity.m101j.mongodriver.tools.RandomData;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates the use of delete many and delete one
 * functions
 *
 * @author giovanni
 */
public class Deletes {

    /**
     * Delete one person with given name
     * @param name First person found in collection with
     *             this name will be deleted
     */
    private void deleteOneByName(String name) {
        DBHelper.getPeopleCollection().deleteOne(Filters.eq("name", name));

        List<Document> people = new ArrayList<Document>();
        DBHelper.getPeopleCollection().find().into(people);

        System.out.println("\nPersons after delete one with name " + name + ": ");
        for (Document person : people) {
            System.out.println(person);
        }
    }

    /**
     * Deletes all people with age greater than given
     * @param maxAge People with age greater than this will be deleted
     */
    private void deleteManyAfterAge(int maxAge) {
        DBHelper.getPeopleCollection().deleteMany(Filters.gt("age", maxAge));

        List<Document> people = new ArrayList<Document>();
        DBHelper.getPeopleCollection().find().into(people);

        System.out.println("\nPersons after delete all with age major than " + maxAge + ": ");
        for (Document person : people) {
            System.out.println(person);
        }
    }

    public static void main(String[] args) {
        Deletes deletes = new Deletes();
        deletes.deleteOneByName(RandomData.randomName());
        deletes.deleteManyAfterAge(RandomData.randomAge());
    }
}
