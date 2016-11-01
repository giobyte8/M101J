package com.mongouniversity.m101j.mongodriver.tools;

import java.util.Random;

/**
 *
 * Created by giovanni on 31/10/16.
 */
public class RandomData {

    private static Random random = new Random();

    private static String[] names = new String[]{
            "Giovanni",
            "Omar",
            "Jezel",
            "Wendy",
            "Celina",
            "Jesus",
            "Kate",
            "Brown",
            "Jones",
            "Another",
            "Other name"
    };

    private static String[] lastNames = new String[] {
            "Aguirre",
            "Alvarez",
            "Salgado",
            "Barrera",
            "Red",
            "Brown",
            "Enders",
            "Skywalker"
    };


    public static int randomAge() {
        int max = 100;
        int min = 1;

        return random.nextInt(max - min + 1) + min;
    }

    public static String randomLastname() {
        int max = lastNames.length - 1;
        int min = 0;

        int position = random.nextInt(max - min + 1) + min;
        return lastNames[position];
    }

    public static String randomName() {
        int max = names.length - 1;
        int min = 0;

        int position = random.nextInt(max - min + 1) + min;
        return names[position];
    }
}
