package com.example.demo.plugin;

import a3lib.SuperPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JiangNanRoll extends SuperPlugin {

    static String personListFileAddr = "data/jiangnan_list/pool.txt";

    class Persons {
        String name;
        Category category;
        double startPoint = 0.0, endPoint = 0.0;

        Persons(String name, String category) {
            this.name = name;
            this.category = Category.valueOf(category);
        }
    }

    enum Category {
        天, 侯, 卿, 物I, 物II;

        static double getPercentage(Category category) {
            if (category == 天)
                return 0.003;
            if (category == 侯)
                return 0.017;
            if (category == 卿)
                return 0.07;
            if (category == 物I)
                return 0.003;
            if (category == 物II)
                return 0.005;
            return -1.0;
        }
    }

    class RandomResultGenerator {
        List<Persons> personsList;
        double lowerBound = 0.0, upperBound = 1.0;
        Random rng = new Random();

        RandomResultGenerator(List<Persons> inPersonsList) {
            this.personsList = inPersonsList;
            if (inPersonsList.size() == 0) {
                upperBound = 0.0;
                return;
            }

            this.personsList.get(0).endPoint = Category.getPercentage(inPersonsList.get(0).category);
            for (int i = 1; i < personsList.size(); i++) {
                Persons currPersons = personsList.get(i);
                currPersons.startPoint = personsList.get(i - 1).endPoint;
                currPersons.endPoint = currPersons.startPoint + Category.getPercentage(currPersons.category);
            }
            upperBound = personsList.get(personsList.size() - 1).endPoint;
        }

        Persons nextPerson() {
            double rngRes;
            do {
                rngRes = rng.nextDouble();
            } while (rngRes > upperBound);

            for(Persons currPersons : personsList) {
                if(rngRes >= currPersons.startPoint && rngRes < currPersons.endPoint)
                    return currPersons;
            }
            return null;
        }
    }
}
