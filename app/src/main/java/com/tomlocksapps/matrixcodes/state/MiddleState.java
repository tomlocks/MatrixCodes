package com.tomlocksapps.matrixcodes.state;

import android.content.Context;

import com.tomlocksapps.matrixcodes.R;

/**
 * Created by Tomasz on 2015-06-18.
 */
public class MiddleState implements UserState {

    private UserStateController stateController;
    private Context context;
    private int previewSizeId;
    private int minDistance, maxDistance;

    public MiddleState(UserStateController stateController, Context context, int previewSizeId, int minDistance, int maxDistance) {
        this.stateController = stateController;
        this.context = context;
        this.previewSizeId = previewSizeId;
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
    }

    @Override
    public void isCloser() {
        stateController.setState(stateController.getCloseState());
    }

    @Override
    public void isFurther() {
        stateController.setState(stateController.getFarState());
    }

    @Override
    public boolean canBeCloser() {
        return true;
    }

    @Override
    public boolean canBeFurther() {
        return true;
    }

    @Override
    public String getStringForCloser() {
        return context.getResources().getString(R.string.dialog_distance_distance_less, "" + minDistance);
    }

    @Override
    public String getStringForFurther() {
        return context.getResources().getString(R.string.dialog_distance_distance_more, "" + maxDistance);
    }

    @Override
    public int getPreviewSizeId() {
        return previewSizeId;
    }

}
