package com.tomlocksapps.matrixcodes.state;

/**
 * Created by Tomasz on 2015-06-17.
 */
public interface UserState {
    void isCloser();
    void isFurther();
    boolean canBeCloser();
    boolean canBeFurther();
    String getStringForCloser();
    String getStringForFurther();
    int getPreviewSizeId();
}
