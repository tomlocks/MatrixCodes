package com.tomlocksapps.matrixcodes.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tomlocksapps.matrixcodes.R;
import com.tomlocksapps.matrixcodes.state.UserState;
import com.tomlocksapps.matrixcodes.state.UserStateController;

/**
 * Created by Tomasz on 2015-06-17.
 */
public class DistanceDialogFragment extends DialogFragment {

    private UserStateController userStateController;

//    public DistanceDialogFragment(UserStateController userStateController) {
//        this.userStateController = userStateController;
//    }



    OnDistanceDialogListener onDistanceDialogListener;

    public UserStateController getUserStateController() {
        return userStateController;
    }

    public OnDistanceDialogListener getOnDistanceDialogListener() {
        return onDistanceDialogListener;
    }

    public void setOnDistanceDialogListener(OnDistanceDialogListener onDistanceDialogListener) {
        this.onDistanceDialogListener = onDistanceDialogListener;
    }

    public interface OnDistanceDialogListener {
        void onUserClick(int previewId);
        void onUserCancel();
    }

    public void setUserStateController(UserStateController userStateController) {
        this.userStateController = userStateController;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        UserState state = userStateController.getState();

        builder.setTitle(R.string.dialog_distance_distance_question_title);
        builder.setMessage(R.string.dialog_distance_distance_question_description);

        if (state.canBeCloser())
            builder.setNegativeButton(state.getStringForCloser(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userStateController.isCloser();
                    onDistanceDialogListener.onUserClick(userStateController.getState().getPreviewSizeId());
                }
            });

        if (state.canBeFurther())
            builder.setPositiveButton(state.getStringForFurther(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userStateController.isFurther();
                    onDistanceDialogListener.onUserClick(userStateController.getState().getPreviewSizeId());
                }
            });


        return builder.create();
    }

    @Override
    public void  onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        onDistanceDialogListener.onUserCancel();
    }
}
