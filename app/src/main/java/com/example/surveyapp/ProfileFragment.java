package com.example.surveyapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class ProfileFragment extends Fragment {

    private ImageView imgProfile;
    private TextView tvName, tvEmail, tvPoints, tvLocation;
    private Button btnTakePhoto, btnGetLocation, btnLogout;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private String currentUserId;
    private FusedLocationProviderClient fusedLocationClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 1. UI Initialization
        imgProfile = view.findViewById(R.id.imgProfile);
        tvName = view.findViewById(R.id.tvName);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvPoints = view.findViewById(R.id.tvPoints);
        tvLocation = view.findViewById(R.id.tvLocation);
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        btnGetLocation = view.findViewById(R.id.btnGetLocation);
        btnLogout = view.findViewById(R.id.btnLogout);

        // 2. Critical: Session & Path Management
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();

            // This path must match exactly where SurveyActivity writes
            // Paste your exact URL inside the quotes
            mDatabase = FirebaseDatabase.getInstance("https://surveyapp-f1065-default-rtdb.asia-southeast1.firebasedatabase.app").getReference("users").child(currentUserId);
            mStorage = FirebaseStorage.getInstance().getReference("profile_images").child(currentUserId + ".jpg");

            // Safety Check: If DB is empty, initialize it and create the "users" folder
            initializeUserIfNull(mAuth.getCurrentUser().getEmail());
        } else {
            Toast.makeText(getContext(), "User session expired. Please Login.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
            return view;
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // 3. Start fetching data
        loadUserData();

        // 4. Button Actions
        btnTakePhoto.setOnClickListener(v -> checkCameraPermission());
        btnGetLocation.setOnClickListener(v -> checkLocationPermission());

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences prefs = requireActivity().getSharedPreferences("SurveyPrefs", Context.MODE_PRIVATE);
            prefs.edit().putBoolean("isLoggedIn", false).apply();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }

    // Fix for the "null" database state - forces the database to create your data
    private void initializeUserIfNull(String email) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    HashMap<String, Object> userMap = new HashMap<>();
                    userMap.put("email", email);
                    userMap.put("points", 0);
                    userMap.put("latitude", 0.0);
                    userMap.put("longitude", 0.0);

                    mDatabase.setValue(userMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "New Profile Created in Cloud!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "DB Write Blocked: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Init Blocked by Firebase Rules!", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Fetches and updates the UI
    private void loadUserData() {
        if (currentUserId == null) return;

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // 1. Force fetch email directly from the Auth session (Guaranteed to work)
                    String email = snapshot.child("email").getValue(String.class);
                    String savedName = snapshot.child("name").getValue(String.class);

                    String base64Image = snapshot.child("profileImageBase64").getValue(String.class);

                    if (base64Image != null && !base64Image.isEmpty() && isAdded()) {
                        try {
                            // Convert the text back into bytes
                            byte[] decodedString = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
                            // Convert the bytes back into a Bitmap picture
                            Bitmap decodedByte = android.graphics.BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            // Set the picture to your ImageView
                            imgProfile.setImageBitmap(decodedByte);
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Error loading image", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (email != null) {
                        tvEmail.setText("Email: " + email);
                    }


                    if (savedName != null) {
                        tvName.setText("Hello, " + savedName + "!");
                    } else {
                        tvName.setText("Hello, User!");
                    }

                    // 2. Load Points & Location
                    Object pts = snapshot.child("points").getValue();
                    Double lat = snapshot.child("latitude").getValue(Double.class);
                    Double lng = snapshot.child("longitude").getValue(Double.class);

                    tvPoints.setText("Points: " + (pts != null ? pts.toString() : "0"));

                    if (lat != null && lng != null && (lat != 0.0 || lng != 0.0)) {
                        tvLocation.setText(String.format("Location:\nLat: %.4f\nLng: %.4f", lat, lng));
                    } else {
                        tvLocation.setText("Location: Not Set");
                    }

                    // 3. Load Image
                    String imgUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    if (imgUrl != null && !imgUrl.isEmpty() && isAdded()) {
                        Glide.with(requireContext()).load(imgUrl).into(imgProfile);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) Toast.makeText(getContext(), "Sync Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Permission and Hardware Logic
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    imgProfile.setImageBitmap(bitmap);
                    uploadImage(bitmap);
                }
            });

    private void uploadImage(Bitmap bitmap) {
        Toast.makeText(getContext(), "Converting and saving photo...", Toast.LENGTH_SHORT).show();

        // 1. Compress the image so the database doesn't choke
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos); // 50% quality reduces size heavily
        byte[] imageBytes = baos.toByteArray();

        // 2. Convert the byte array to a Base64 String
        String base64Image = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

        // 3. Save the giant string directly to your Realtime Database (NO Storage needed)
        mDatabase.child("profileImageBase64").setValue(base64Image)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Photo Saved to Database!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Save Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(loc -> {
                if (loc != null) {
                    mDatabase.child("latitude").setValue(loc.getLatitude());
                    mDatabase.child("longitude").setValue(loc.getLongitude());
                } else {
                    Toast.makeText(getContext(), "GPS signal weak. Try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {});
}