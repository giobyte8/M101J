package com.mongouniversity.m101j.mongodriver;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongouniversity.m101j.mongodriver.tools.DBHelper;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by giovanni on 14/11/16.
 */
public class Aggregation {

    /**
     * Parse a raw string into json for the
     * aggregation framework
     */
    private void fromRawString() {

        String totalPop = "{ $group: { _id: \"$state\", totalPop: { $sum: \"$pop\"}}}";
        String gtOneMillion = "{ $match: { totalPop: {$gte: 1000000}}}";

        //
        // Compose aggregation pipeline
        List<Document> pipeline = Arrays.asList(
                Document.parse(totalPop),
                Document.parse(gtOneMillion)
        );

        //
        // Executes aggregation query
        List<Document> results = DBHelper.getZipCodesCollection()
                .aggregate(pipeline)
                .into(new ArrayList<Document>());

        //
        // Display aggregation results
        System.out.println("\nStates with population greater than one million");
        for (Document result : results) {
            System.out.println(result.toJson());
        }
    }

    /**
     * Creates each document for pipeline with instances
     * of Bson documents
     */
    private void fromBsonDocuments() {

        Document totalPop = new Document()
                .append(
                        "$group",
                        new Document()
                                .append("_id", "$state")
                                .append("totalPop", new Document("$sum", "$pop"))
                );

        Document gtOneMillion = new Document()
                .append(
                        "$match",
                        new Document("totalPop", new Document("$gte", 1000000))
                );

        //
        // Compose aggregation pipeline
        List<Document> pipeline = Arrays.asList(
                totalPop,
                gtOneMillion
        );

        //
        // Executes aggregation query
        List<Document> results = DBHelper.getZipCodesCollection()
                .aggregate(pipeline)
                .into(new ArrayList<Document>());

        //
        // Display aggregation results
        System.out.println("\nStates with population greater than one million");
        for (Document result : results) {
            System.out.println(result.toJson());
        }
    }

    /**
     * Creates each document for pipeline using
     * helper classes included in driver
     */
    private void fromHelperClasses() {

        Bson totalPop = Aggregates.group("$state", Accumulators.sum("totalPop", "$pop"));
        Bson gtOneMillion = Aggregates.match(Filters.gte("totalPop", 1000000));

        //
        // Compose aggregation pipeline
        List<Bson> pipeline = Arrays.asList(
                totalPop,
                gtOneMillion
        );

        //
        // Executes aggregation query
        List<Document> results = DBHelper.getZipCodesCollection()
                .aggregate(pipeline)
                .into(new ArrayList<Document>());

        //
        // Display aggregation results
        System.out.println("\nStates with population greater than one million");
        for (Document result : results) {
            System.out.println(result.toJson());
        }
    }

    public static void main(String[] args) {
        Aggregation aggregation = new Aggregation();
        aggregation.fromRawString();
        aggregation.fromBsonDocuments();
        aggregation.fromHelperClasses();
    }
}
