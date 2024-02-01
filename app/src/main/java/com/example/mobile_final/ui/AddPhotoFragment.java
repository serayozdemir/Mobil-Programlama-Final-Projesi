package com.example.mobile_final.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mobile_final.R;
import com.example.mobile_final.models.LabelModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddPhotoFragment extends Fragment {

    public static final int CAMERA_REQUEST_CODE=1;

    ImageView photo_imageView;
    ScrollView photo_scrollView;

    Button cameraButton, saveButton;
    LinearLayout linearLayout;

    List<String> selectedLabels;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference labelsRef = db.collection("labels");

    @SuppressLint("MissingInflatedId")
    public View onCreate(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater(R.layout.fragment_add_photo, container, false);

        photo_imageView = view.findViewById(R.id.photo_imageView);
        cameraButton = view.findViewById(R.id.cameraButton);
        saveButton = view.findViewById(R.id.saveButton);
        photo_scrollView = view.findViewById(R.id.photo_scrollView);
        linearLayout = view.findViewById(R.id.photo_linearLayout);

        selectedLabels = new ArrayList<>();

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhotoToFirestore();
            }
        });

        loadLabelsFromFirestore();

        return view;

    }
    private void loadLabelsFromFirestore(){
        labelsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<LabelModel> labelList = task.getResult().toObjects(LabelModel.class);

                    for(LabelModel label: labelList){
                        displayLabel(label);
                    }
                }else{
                    Toast.makeText(getActivity(), "Error getting labels", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayLabel(LabelModel label){
        LinearLayout horizontalLayout = new LinearLayout(getActivity());
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);

        CheckBox checkBox = new CheckBox(getActivity());
        checkBox.setText(label.getLabel());
        checkBox.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if(isChecked){
                selectedLabels.add(label.getLabel());
            }else{
                selectedLabels.remove(label.getLabel());
            }
        }));

        TextView labelTextView = new TextView(getActivity());

         horizontalLayout.addView(checkBox);
         horizontalLayout.addView(labelTextView);

         linearLayout.addView(horizontalLayout);
    }

    private void openCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if(requestCode == CAMERA_REQUEST_CODE && requestCode == Activity.RESULT_OK){
            Bitmap photo = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
            photo_imageView.setImageBitmap(photo);
        }
    }

    private void savePhotoToFirestore(){
        Bitmap photoBitmap = getBitmapFromImageView(photo_imageView);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] photoData = baos.toByteArray();

        String base64Photo = android.util.Base64.encodeToString(photoData, Base64.DEFAULT);

        Map<String , Object> data = new HashMap<>();
        data.put("photo", base64Photo);
        data.put("labels", selectedLabels);

        FirebaseFirestore.getInstance().collection("photos").add(data).addOnSuccessListener(documentReference -> {
            Toast.makeText(getActivity(), "The photo was succesfully saved to Firestore", Toast.LENGTH_SHORT).show();

            String documentId = documentReference.getId();

            StorageReference storageRef = FirebaseFirestore.getInstance().getReference().child("photos").child(documentId+ "jpg");

                storageRef.putBytes(photoData)
                        .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(getActivity(), "P", Toast.LENGTH_SHORT).show();
                })      .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Error uploading to Storage", + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        }).addOnFailureListener(e -> {
            Toast.makeText(getActivity(), "Error adding to Firestore", + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
    private Bitmap getBitmapFromImageView(ImageView imageView){
        if(imageView.getDrawable() instanceof BitmapDrawable){
            return( (BitmapDrawable) ((BitmapDrawable) imageView.getDrawable())).getBitmap();
        }else{
            return null;
        }
    }


}
