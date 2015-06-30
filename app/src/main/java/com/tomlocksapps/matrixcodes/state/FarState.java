package com.tomlocksapps.matrixcodes.state;

import android.content.Context;

import com.tomlocksapps.matrixcodes.R;

/**
 * Created by Tomasz on 2015-06-18.
 */
public class FarState implements UserState {

    private UserStateController stateController;
    private Context context;
    private int previewSizeId;
    private int minDistance;

    public FarState(UserStateController stateController, Context context, int previewSizeId, int minDistance) {
        this.stateController = stateController;
        this.context = context;
        this.previewSizeId = previewSizeId;
        this.minDistance = minDistance;
    }

    @Override
    public void isCloser() {
        stateController.setState(stateController.getMiddleState());
    }

    @Override
    public void isFurther() {

    }

    @Override
    public boolean canBeCloser() {
        return true;
    }

    @Override
    public boolean canBeFurther() {
        return false;
    }

    @Override
    public String getStringForCloser() {
        return context.getResources().getString(R.string.dialog_distance_distance_less, "" + minDistance);
    }

    @Override
    public String getStringForFurther() {
        return null;
    }

    @Override
    public int getPreviewSizeId() {
        return previewSizeId;
    }

}
