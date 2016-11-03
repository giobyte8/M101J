package com.mongouniversity.m101j.mongodriver;

import com.mongodb.client.model.Projections;
import com.mongouniversity.m101j.mongodriver.tools.DBHelper;
import com.mongouniversity.m101j.mongodriver.tools.RandomData;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates how to make queries applying projections
 * to retrieved results
 *
 * Created by giovanni on 3/11/16.
 * @author giovanni
 */
public class QueryProjection {

    /**
     * Find all people with given name, but uses projection to
     * only display its age
     * @param name Name to apply as filter
     */
    private void queryAges(String name) {
        List<Document> people = new ArrayList<Document>();

        Bson projectionDoc = new Document()
                .append("age", 1)
                .append("_id", 0);

        DBHelper.getPeopleCollection()
                .find(new Document("name", name))
                .projection(projectionDoc)
                .into(people);

        System.out.println("\nAges with name '" + name + "'are:");
        for (Document person : people) {
            System.out.println(person);
        }
    }

    /**
     * Find people with given name, but only retrieve
     * name and last name through projection
     * @param name Name to apply as filter to query
     */
    private void queryOnlyNames(String name) {
        List<Document> people = new ArrayList<Document>();
        DBHelper.getPeopleCollection()
                .find(new Document("name", name))
                .projection(Projections.fields(
                        Projections.include("name"),
                        Projections.include("lastName"),
                        Projections.excludeId()
                ))
                .into(people);

        System.out.println("\nQuery results with name '" + name + "'are:");
        for (Document person : people) {
            System.out.println(person);
        }
    }

    public static void main(String[] args) {
        QueryProjection qProjection = new QueryProjection();
        qProjection.queryOnlyNames(RandomData.randomName());
        qProjection.queryAges(RandomData.randomName());
    }
}
