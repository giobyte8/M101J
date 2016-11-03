package com.mongouniversity.m101j.mongodriver;

import com.mongouniversity.m101j.mongodriver.tools.DBHelper;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates how to make queries using sort, skip
 * and limit
 *
 * @author giovanni
 */
public class QueryPagination {

    /**
     * Query all people collections applying sort, skip and
     * limit
     * @param sortAscBy  Field where to apply ascending ordering
     * @param sortDescBy Field where to apply descending ordering
     */
    private void findSorted(String sortAscBy, String sortDescBy, int skip, int limit) {
        List<Document> people = new ArrayList<Document>();

        Bson sortDocument = new Document()
                .append(sortAscBy, 1)
                .append(sortDescBy, -1);

        DBHelper.getPeopleCollection()
                .find()
                .sort(sortDocument)
                .skip(skip)
                .limit(limit)
                .into(people);

        System.out.println("\n" + limit + " next results after skip " + skip + " are:");
        for (Document person : people) {
            System.out.println(person);
        }
    }

    public static void main(String[] args) {
        QueryPagination qPagination = new QueryPagination();

        int pageSize = 40;
        for (int i = 0; i < DBHelper.getPeopleCollection().count(); i += pageSize) {
            qPagination.findSorted("name", "age", i, pageSize);
        }
    }
}
