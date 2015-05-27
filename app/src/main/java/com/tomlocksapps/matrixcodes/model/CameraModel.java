package com.tomlocksapps.matrixcodes.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tomasz on 2015-05-12.
 */
public class CameraModel {

    private static final Map<String, Double> CAMERA_MODELS = new HashMap<String, Double>() {{
        put("XT1032", 10800d);
    }
    };

    static public Double getFactor(String model) {
        return CAMERA_MODELS.get(model);
    }

}
