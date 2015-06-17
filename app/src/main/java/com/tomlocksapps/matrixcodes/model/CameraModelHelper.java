package com.tomlocksapps.matrixcodes.model;

import android.hardware.Camera;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tomasz on 2015-05-12.
 */
public class CameraModelHelper {

    //PHONEMODEL_HEIGHTxWIDTH
    private static final Map<CameraModel, CameraModelParameters> CAMERA_MODELS = new HashMap<CameraModel, CameraModelParameters>() {{
        //put("XT1032_480x864", new CameraModelParameters(14618,-0.977));
        put(new CameraModel(960, 1280 ,"XT1032"), new CameraModelParameters(24523,-1.007));
        put(new CameraModel(720, 1280,"XT1032"), new CameraModelParameters(23048,-0.993));
        put(new CameraModel(720, 960 ,"XT1032"), new CameraModelParameters(18152,-1.005));
        put(new CameraModel(480, 864 ,"XT1032"), new CameraModelParameters(15465,-0.991));
        put(new CameraModel(432, 768 ,"XT1032"), new CameraModelParameters(13513,-0.985));
        put(new CameraModel(480, 720 ,"XT1032"), new CameraModelParameters(12219,-0.976));
        put(new CameraModel(480, 640,"XT1032"), new CameraModelParameters(10813, -0.975));
        put(new CameraModel(240, 320 ,"XT1032"), new CameraModelParameters(5299,-0.967));
        put(new CameraModel(144, 176 ,"XT1032"), new CameraModelParameters(3680.2,-1.019));
    }
    };

    static public CameraModelParameters getFactor(String model, Camera.Size size) {
//        return CAMERA_MODELS.get(model+"_"+size.height+"x"+size.width);
        return CAMERA_MODELS.get(new CameraModel(size.height,size.width, model));
    }

    public static class CameraModelParameters {
        private double factor;
        private double powerOf;

        public CameraModelParameters(double factor, double powerOf) {
            this.factor = factor;
            this.powerOf = powerOf;
        }

        public double getFactor() {
            return factor;
        }

        public double getPowerOf() {
            return powerOf;
        }
    }

    public static class CameraModel {
        private int width;
        private int height;
        private String model;

        public CameraModel(int height, int width, String model) {
            this.width = width;
            this.height = height;
            this.model = model;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CameraModel that = (CameraModel) o;

            if (height != that.height) return false;
            if (width != that.width) return false;
            if (!model.equals(that.model)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = width;
            result = 31 * result + height;
            result = 31 * result + model.hashCode();
            return result;
        }
    }

}
