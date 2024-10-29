package com.project.mainprojectprm231.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.project.mainprojectprm231.LoginActivity;
import com.project.mainprojectprm231.R;

import static android.content.Context.MODE_PRIVATE;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private static final String PREF_NAME = "UserPrefs";
    private TextView profileName, profileEmail, profilePhone, profileAddress;
    private ImageView profileImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImage = view.findViewById(R.id.profile_image);
//        profileName = view.findViewById(R.id.profile_name);
        profileEmail = view.findViewById(R.id.profile_email);
//        profilePhone = view.findViewById(R.id.profile_phone);
//        profileAddress = view.findViewById(R.id.profile_address);

        Button editProfileButton = view.findViewById(R.id.edit_profile_button);
        Button logoutButton = view.findViewById(R.id.logout_button);

        if (editProfileButton != null) {
            editProfileButton.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Edit Profile Clicked", Toast.LENGTH_SHORT).show();
                // Implement edit profile functionality
            });
        } else {
            Log.e(TAG, "Edit profile button not found in layout");
        }

        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> logout());
        } else {
            Log.e(TAG, "Logout button not found in layout");
        }

        loadUserProfile();

        return view;
    }

    private void loadUserProfile() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");
        String username = sharedPreferences.getString("username", "");
        String phone = sharedPreferences.getString("phone", "");
        String address = sharedPreferences.getString("address", "");

        if (profileName != null) profileName.setText(username);
        else Log.e(TAG, "profileName TextView not found");

        if (profileEmail != null) profileEmail.setText(email);
        else Log.e(TAG, "profileEmail TextView not found");

        if (profilePhone != null) profilePhone.setText(phone);
        else Log.e(TAG, "profilePhone TextView not found");

        if (profileAddress != null) profileAddress.setText(address);
        else Log.e(TAG, "profileAddress TextView not found");

        if (profileImage != null) {
            profileImage.setImageResource(R.drawable.default_profile_image);
        } else {
            Log.e(TAG, "profileImage ImageView not found");
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all saved user data
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}