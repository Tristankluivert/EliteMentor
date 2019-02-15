package ng.com.hybrid.elitementor.Fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.BreakIterator;

import de.hdodenhof.circleimageview.CircleImageView;
import ng.com.hybrid.elitementor.EditProfile;
import ng.com.hybrid.elitementor.Login;
import ng.com.hybrid.elitementor.R;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

/**
 * A simple {@link Fragment} subclass.
 */

public class ProfileFrag extends Fragment {


    private CircleImageView userimage;
    private TextView username,status,location,discipline,gender;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private StorageReference UserProfileImageRef;
    String currentUserId;
    private Button edit,logout;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mAuth = FirebaseAuth.getInstance();

        //  currentUserId = mAuth.getCurrentUser().getUid();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {
            currentUserId = firebaseUser.getUid();
        }

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        userimage = view.findViewById(R.id.userimage);
        username = view.findViewById(R.id.username);
        location = view.findViewById(R.id.location);
        status = view.findViewById(R.id.status);
        discipline = view.findViewById(R.id.discipline);
        gender = view.findViewById(R.id.gender);
        edit = view.findViewById(R.id.edit);
    logout = view.findViewById(R.id.logout);

    logout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAuth.signOut();
            Intent ui = new Intent(getContext(),Login.class);
          ui.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(ui);
          getActivity().finish();


        }
    });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(),EditProfile.class);
                startActivity(intent);

            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myName = dataSnapshot.child("username").getValue().toString();
                    String myStatus = dataSnapshot.child("status").getValue().toString();
                    String myDis = dataSnapshot.child("discipline").getValue().toString();
                    String myLoc = dataSnapshot.child("location").getValue().toString();
                    String myGen = dataSnapshot.child("gender").getValue().toString();
                    Picasso.get().load(myProfileImage).placeholder(R.mipmap.ic_launcher).into(userimage);
                    username.setText(myName);
                    location.setText(myLoc);
                    gender.setText(myGen);
                    discipline.setText(myDis);
                    status.setText(myStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }
}
