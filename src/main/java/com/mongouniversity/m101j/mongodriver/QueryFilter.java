package com.mongouniversity.m101j.mongodriver;

import com.mongodb.client.model.Filters;
import com.mongouniversity.m101j.mongodriver.tools.DBHelper;
import com.mongouniversity.m101j.mongodriver.tools.RandomData;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates how to make queries using filters
 *
 * @author giovanni
 */
public class QueryFilter {

    /**
     * Find all people with name equal to given parameter
     * using a filter
     *
     * @param name The name to use into query filter
     */
    private void filterEquals(String name) {

        Bson filter = Filters.eq("name", name);

        List<Document> persons = new ArrayList<Document>();
        DBHelper.getPeopleCollection().find(filter).into(persons);

        System.out.print("\nThere are " + persons.size());
        System.out.println(" persons with name: '" + name + "'");
        for (Document person : persons) {
            System.out.println(person.toJson());
        }
    }

    /**
     * Find all people with age greather than or equals to
     * given parameter using a filter
     *
     * @param minAge The min age to use into query filter
     */
    private void findGreaterThanEquals(int minAge) {

        Bson filter = Filters.gte("age", minAge);

        List<Document> persons = new ArrayList<Document>();
        DBHelper.getPeopleCollection().find(filter).into(persons);

        System.out.print("\nThere are " + persons.size());
        System.out.println(" persons with minimal required age of: '" + minAge + "'");
        for (Document person : persons) {
            System.out.println(person.toJson());
        }
    }

    /**
     * Find all people that match with a query that implements
     * multiple filters
     */
    private void findUsingMultipleFilters() {
        String searchedName = RandomData.randomName();
        String excludedLastName = RandomData.randomLastname();
        int minimalRequiredAge = RandomData.randomAge();

        //
        // Note on next query that you can embbed multiple documents
        // into filter, the filter structure is very similar to mongo
        // shell client
        Bson filter = new Document()
                .append("name", searchedName)
                .append("lastName", new Document("$ne", excludedLastName))
                .append("age", new Document("$gte", minimalRequiredAge));

        // Executes query
        List<Document> persons = new ArrayList<Document>();
        DBHelper.getPeopleCollection().find(filter).into(persons);

        // Print results
        String header = "\nPersons with name equals to: " + searchedName;
        header += ", and last name different from: " + excludedLastName;
        header += ", and age greater than or equals to: " + minimalRequiredAge;
        System.out.println(header);
        for (Document person : persons) {
            System.out.println(person.toJson());
        }
    }

    public static void main(String[] args) {
        QueryFilter queryFilter = new QueryFilter();
        queryFilter.filterEquals(RandomData.randomName());
        queryFilter.findGreaterThanEquals(RandomData.randomAge());
        queryFilter.findUsingMultipleFilters();
    }
}
