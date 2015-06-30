package com.tomlocksapps.matrixcodes.utils;


/**
 * Created by Tomasz on 2015-05-05.
 */
public class Log {

    private static final String APP_TAG = "MatrixCodes";


    public static void d(String message, LogType logType, Object o) {
        android.util.Log.d(APP_TAG , logType + " "+ o.getClass().getName() + " " + message);
    }

    public enum LogType {
        LIFECYCLE("Lifecycle: "),
        OPENCV("OpenCV: "),
        CAMERA("Camera: "),
        OTHER("Other: ")
        ;

        private final String text;

        /**
         * @param text
         */
        private LogType(final String text) {
            this.text = text;
        }


        @Override
        public String toString() {
            return text;
        }
    }

}
