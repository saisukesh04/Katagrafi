package com.undamped.katagraf.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.undamped.katagraf.LoginActivity;
import com.undamped.katagraf.R;
import com.undamped.katagraf.StartActivity;
import com.undamped.katagraf.database.ItemDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    @BindView(R.id.profile_progress) ProgressBar profile_progress;
    @BindView(R.id.profile_image) CircleImageView profile_image;
    @BindView(R.id.profile_image_edit) ImageView profile_image_edit;
    @BindView(R.id.profile_mail) TextView profile_mail;
    @BindView(R.id.profile_name) TextView profile_name;
    @BindView(R.id.allergiesText) TextView allergiesText;
    @BindView(R.id.update_button) Button update_button;
    @BindView(R.id.logoutBtn) Button logoutBtn;

    final public int IMAGE_CODE = 1;
    private Uri profileImageURI;
    private SharedPreferences pref;
    private SharedPreferences.Editor Ed;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        ButterKnife.bind(this, root);

        pref = getContext().getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE);
        Ed = pref.edit();

        String image = pref.getString("Image","null");
        profile_name.setText(pref.getString("Name","Sai Sukesh"));
        allergiesText.setText(pref.getString("Allergy","-"));

        if (!image.equals("null"))
            Glide.with(getContext()).load(Uri.parse(image)).into(profile_image);

        profile_mail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        profile_image_edit.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},101);
            }else{
                bringImageSelection();
            }
        });

        update_button.setOnClickListener(v -> {
            update_button.setEnabled(false);
            update_button.setAlpha(0.75f);
            profile_progress.setVisibility(View.VISIBLE);
            uploadProfileImageToStorage(v);
        });

        logoutBtn.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            ItemDatabase.getInstance(getContext()).ItemDao().removeAllItems();
            Snackbar.make(view, "Successfully Signed out", Snackbar.LENGTH_LONG).show();
            Intent logoutIntent = new Intent(getContext(), StartActivity.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(logoutIntent);
            getActivity().finish();
        });

        Ed.putString("Allergy","Groundnuts, Glucose, Sugar, Fructose");
        Ed.apply();
        return root;
    }

    private void bringImageSelection() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_CODE);
    }

    private void cropImage(){
        CropImage.activity(profileImageURI)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(getContext(),this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            profileImageURI = data.getData();
            cropImage();
        } else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                Glide.with(getContext()).load(result.getUri()).into(profile_image);
                profileImageURI = result.getUri();
                update_button.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(getContext(), "Please select a image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 101) {
            if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bringImageSelection();
            } else {
                Toast.makeText(getContext(), "Please provide the permissions!",Toast.LENGTH_LONG).show();
            }
        }
    }

    private void uploadProfileImageToStorage(View v) {
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference storagePath = mStorageRef.child("User Profile Photos").child(FirebaseAuth.getInstance().getUid());
        storagePath.putFile(profileImageURI)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return storagePath.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String downloadUrl = task.getResult().toString();

                        Ed.putString("Image", downloadUrl);
                        Ed.apply();

                        //TODO: Add the imageURI to shared preferences also
                        //TODO: Add the image url to mongodb
                    }else{
                        update_button.setEnabled(true);
                        update_button.setAlpha(1);
                        profile_progress.setVisibility(View.GONE);
                    }
                });

    }
}