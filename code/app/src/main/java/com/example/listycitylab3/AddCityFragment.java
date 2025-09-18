package com.example.listycitylab3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddCityFragment extends DialogFragment {
    interface AddCityDialogListener {
        void addCity(City city);
    }
    private AddCityDialogListener listener;

    public static AddCityFragment newInstance(int position) {
        AddCityFragment fragment = new AddCityFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddCityDialogListener) {
            listener = (AddCityDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement AddCityDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_city, null);

        EditText editCityName = view.findViewById(R.id.edit_text_city_text);
        EditText editProvinceName = view.findViewById(R.id.edit_text_province_text);

        // get position from args
        int position = -1;
        Bundle args = getArguments();
        if (args != null) position = args.getInt("position", -1);

        // if editing, get the actual City object from the activity and prefill
        City editingCity = null;
        if (position != -1) {
            // NOTE: requires MainActivity.getCityAt(int) to exist (see MainActivity changes above)
            editingCity = ((MainActivity) requireActivity()).getCityAt(position);
            if (editingCity != null) {
                editCityName.setText(editingCity.getName());
                editProvinceName.setText(editingCity.getProvince());
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        boolean isEdit = (editingCity != null);

        City finalEditingCity = editingCity; // for lambda
        int finalPosition = position;
        return builder
                .setView(view)
                .setTitle(isEdit ? "Edit City" : "Add a City")
                .setNegativeButton("Cancel", null)
                .setPositiveButton(isEdit ? "Save" : "Add", (dialog, which) -> {
                    String cityName = editCityName.getText().toString();
                    String provinceName = editProvinceName.getText().toString();

                    if (isEdit && finalEditingCity != null) {
                        // mutate the existing City using setters
                        finalEditingCity.setName(cityName);
                        finalEditingCity.setProvince(provinceName);
                        // tell MainActivity to refresh adapter
                        ((MainActivity) requireActivity()).notifyDataChanged();
                    } else {
                        // add a new city via listener
                        listener.addCity(new City(cityName, provinceName));
                    }
                })
                .create();
    }
}

