package com.example.mobile_final.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobile_final.R;
import com.example.mobile_final.models.LabelModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class AddLabelFragment extends Fragment {
    EditText editTextLabel, editTextDescription;
    Button addButton;
    LinearLayout linearLayout;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference labelsRef = db.collection("labels");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_label, container, false);

        editTextLabel = view.findViewById(R.id.editTextLabel);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        addButton = view.findViewById(R.id.addButton);
        linearLayout = view.findViewById(R.id.linearLayout);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLabelToFirestore();
            }
        });

        getLabelsFromFirestore();

        return  view;
    }

    private void addLabelToFirestore(){
        String label = editTextLabel.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if(label.isEmpty()){
            Toast.makeText(getActivity(), "Label can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        labelsRef.add(newLabel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity(), "Label added successfully!", Toast.LENGTH_SHORT).show();
                    clearInputFields();
                    displayLabel(newLabel);
                }else{
                    Toast.makeText(getActivity(), "Error add label", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLabelsFromFirestore(){
        labelsRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    List<LabelModel> labelList = task.getResult().toObjects(LabelModel.class);

                    for(LabelModel label : labelList){
                        displayLabel(label);
                    }
                }else{
                    Toast.makeText(getActivity(), "Error get label", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void displayLabel(LabelModel label){
        TextView labelTextView = new TextView(getActivity());
        labelTextView.setText(label.getLabel());

        linearLayout.addView(labelTextView);
    }

    private void clearInputFields(){
        editTextLabel.setText("");
        editTextDescription.setText("");
    }
}
