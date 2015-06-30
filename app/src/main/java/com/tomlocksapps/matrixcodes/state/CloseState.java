package com.tomlocksapps.matrixcodes.state;

import android.content.Context;

import com.tomlocksapps.matrixcodes.R;

/**
 * Created by Tomasz on 2015-06-17.
 */
public class CloseState implements UserState {

    private UserStateController stateController;
    private Context context;
    private int previewSizeId;
    private int maxDistance;

    public CloseState(UserStateController stateController, Context context, int previewSizeId, int maxDistance) {
        this.stateController = stateController;
        this.context = context;
        this.previewSizeId = previewSizeId;
        this.maxDistance = maxDistance;
    }

    @Override
    public void isCloser() {

    }

    @Override
    public void isFurther() {
        stateController.setState(stateController.getMiddleState());
    }

    @Override
    public boolean canBeCloser() {
        return false;
    }

    @Override
    public boolean canBeFurther() {
        return true;
    }

    @Override
    public String getStringForCloser() {
        return null;
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
