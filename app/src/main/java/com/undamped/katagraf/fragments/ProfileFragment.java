package com.undamped.katagraf.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.undamped.katagraf.LoginActivity;
import com.undamped.katagraf.R;
import com.undamped.katagraf.StartActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @BindView(R.id.logoutBtn) Button logoutBtn;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, root);

        logoutBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Snackbar.make(view, "Successfully Signed out", Snackbar.LENGTH_LONG).show();
            Intent logoutIntent = new Intent(getContext(), StartActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            getActivity().finish();
        });

        return root;
    }
}