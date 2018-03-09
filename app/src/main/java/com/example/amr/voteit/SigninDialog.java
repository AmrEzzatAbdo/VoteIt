package com.example.amr.voteit;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by Amr on 3/4/2018.
 */

public class SigninDialog extends DialogFragment {
    Button DGSigninBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment, container);
        DGSigninBtn = (Button) view.findViewById(R.id.DGSignIn);
        DGSigninBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                MainActivity activity = new MainActivity();
                activity.GoogleSignIn(view);
                dismiss();
            }
        });
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setTitle("Signin to continue...");
        return view;

    }

    public void show(FragmentManager fragmentManager, String h) {

    }
}
