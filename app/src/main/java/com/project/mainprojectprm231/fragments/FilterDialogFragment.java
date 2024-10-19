package com.project.mainprojectprm231.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.project.mainprojectprm231.R;

public class FilterDialogFragment extends DialogFragment {
    private EditText brandEditText, minPriceEditText, maxPriceEditText, minRatingEditText;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_filter);

        brandEditText = dialog.findViewById(R.id.edit_brand);
        minPriceEditText = dialog.findViewById(R.id.edit_min_price);
        maxPriceEditText = dialog.findViewById(R.id.edit_max_price);
        minRatingEditText = dialog.findViewById(R.id.edit_min_rating);

        dialog.findViewById(R.id.apply_button).setOnClickListener(v -> {
            String brand = brandEditText.getText().toString();
            double minPrice = Double.parseDouble(minPriceEditText.getText().toString());
            double maxPrice = Double.parseDouble(maxPriceEditText.getText().toString());
            int minRating = Integer.parseInt(minRatingEditText.getText().toString());

            ProductsFragment targetFragment = (ProductsFragment) getTargetFragment();
            if (targetFragment != null) {

            }
            dismiss();
        });

        return dialog;
    }
}
