package com.example.testbutton;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.DialogFragment;

public class DialogNameAudio extends DialogFragment {

    private Datable datable;
    private EditText userInput;
    private View promptsView;
    private TextView textView_ok;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        datable = (Datable) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.name_audio, container, false);
        userInput = (EditText) view.findViewById(R.id.input_text);
        textView_ok = (TextView) view.findViewById(R.id.ok_button);

        textView_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datable.addText(userInput.getText().toString());
                getDialog().dismiss();
            }
        });

        return view;

    }
}
