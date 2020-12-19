package com.abdullah;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.abdullah.R;

class Insert_Book extends DialogFragment {

    Insert_Book newInstance() {
        return new Insert_Book();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_insert__book, container, true);


        return v;
    }
}