package com.tomlocksapps.matrixcodes.state;

import android.content.Context;

/**
 * Created by Tomasz on 2015-06-17.
 */
public class UserStateController {
    private UserState state;
    private UserState closeState;
    private UserState middleState;
    private UserState farState;

    private static final int MIN_DISTANCE = 1;
    private static final int MAX_DISTANCE = 2;

    public UserStateController(Context context) {
        closeState = new CloseState(this, context,  7, MIN_DISTANCE);
        middleState = new MiddleState(this, context,  5, MIN_DISTANCE, MAX_DISTANCE);
        farState = new FarState(this, context,  3, MAX_DISTANCE);

        state = middleState;
    }

    public void isCloser() {
        state.isCloser();
    }

    public void isFurther() {
        state.isFurther();
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public UserState getCloseState() {
        return closeState;
    }

    public UserState getMiddleState() {
        return middleState;
    }

    public UserState getFarState() {
        return farState;
    }
}
