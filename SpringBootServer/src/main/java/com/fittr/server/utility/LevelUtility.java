package com.fittr.server.utility;

import java.util.HashMap;
import java.util.Map;

public class LevelUtility {

    public static Map<String, Double> levelCalculator(Double distance) {
        Map<String, Double> levelmap = new HashMap<>();
        if (distance >= 1.0 && distance < 2000.0) {
            levelmap.put("CURRENT_LEVEL",1.0);
            levelmap.put("DISTANCE_FOR_NEXT_LEVEL", 1999.0-distance);
        }

        else if (distance >= 2000.0 && distance < 4000.0) {
            levelmap.put("CURRENT_LEVEL",2.0);
            levelmap.put("DISTANCE_FOR_NEXT_LEVEL", 3999.0-distance);
        }

        else if (distance >= 4000.0 && distance < 7000.0) {
            levelmap.put("CURRENT_LEVEL",3.0);
            levelmap.put("DISTANCE_FOR_NEXT_LEVEL", 6999.0-distance);
        }
        else if (distance >= 7000.0 && distance < 9999.0) {
            levelmap.put("CURRENT_LEVEL",4.0);
            levelmap.put("DISTANCE_FOR_NEXT_LEVEL", 10000.0-distance);
        }
        else if (distance >= 10000.0 && distance < 15000.0) {
            levelmap.put("CURRENT_LEVEL",5.0);
            levelmap.put("DISTANCE_FOR_NEXT_LEVEL", 14999-distance);
        }
        else  {
            levelmap.put("CURRENT_LEVEL",6.0);
            levelmap.put("DISTANCE_FOR_NEXT_LEVEL", Math.abs(14999-distance));
        }
        return levelmap;
    }
}
