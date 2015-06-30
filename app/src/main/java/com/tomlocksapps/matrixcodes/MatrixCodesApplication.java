package com.tomlocksapps.matrixcodes;

import android.app.Application;

/**
 * Created by Tomasz on 2015-03-03.
 */
public class MatrixCodesApplication extends Application {


    private boolean openCVLoaded = false;

    public boolean isOpenCVLoaded() {
        return openCVLoaded;
    }

    public void setOpenCVLoaded(boolean openCVLoaded) {
        this.openCVLoaded = openCVLoaded;
    }
}
