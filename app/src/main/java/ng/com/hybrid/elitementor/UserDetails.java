package ng.com.hybrid.elitementor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

import java.util.HashMap;


import de.hdodenhof.circleimageview.CircleImageView;



public class UserDetails extends AppCompatActivity {
    private EditText discipline, sex,location;
    EditText UserName;
    private CardView SaveInformationbutton;
    private CircleImageView ProfileImage;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private StorageReference UserProfileImageRef;
    String currentUserID;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);



        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");


        UserName =  findViewById(R.id.UserName);
        discipline =  findViewById(R.id.discipline);
        location = findViewById(R.id.location);
        sex =  findViewById(R.id.sex);
        SaveInformationbutton =  findViewById(R.id.saveInformationbutton);
        ProfileImage =  findViewById(R.id.setup_profile_image);
        loadingBar = new ProgressDialog(this);






        SaveInformationbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveAccountSetupInformation();
            }
        });


        ProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,Gallery_Pick);

            }
        });

        //Storing data
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    if (dataSnapshot.hasChild("profileimage")) {

                        String image = dataSnapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.escalator).into(ProfileImage);
                    } else {
                        Toast.makeText(getApplicationContext(), "Please select profile image first.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            if(requestCode == Gallery_Pick){
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we are updating your profile image...");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                Uri resultUri = result.getUri();

                final StorageReference ref = UserProfileImageRef.child(currentUserID + ".jpg");
                UploadTask uploadTask = ref.putFile(resultUri);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            Uri downloadUri = task.getResult();
                            String link = downloadUri.toString();
                            UsersRef.child("profileimage").setValue(link).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent selfIntent = new Intent(getApplicationContext(), UserDetails.class);
                                        startActivity(selfIntent);
                                        Toast.makeText(getApplicationContext(),"Profile Image Uploaded",Toast.LENGTH_SHORT).show();
                                    }else{
                                        String message = task.getException().getMessage();
                                        Toast.makeText(getApplicationContext(),"Error occured: "+message,Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            // Handle failures
                            // ...
                            String message = task.getException().getMessage();
                            Toast.makeText(getApplicationContext(),"Error occured: "+message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                //
            } else {
                Toast.makeText(getApplicationContext(), "Error Occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }


    private void SaveAccountSetupInformation() {
        String username = UserName.getText().toString();
        String disi = discipline.getText().toString();
        String six = sex.getText().toString();
        String loc = location.getText().toString();



        if (TextUtils.isEmpty(username)) {

            Toast.makeText(getApplicationContext(), "Enter your name please", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(disi)) {
            Toast.makeText(getApplicationContext(), "Enter your discipline please", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(six)) {
            Toast.makeText(getApplicationContext(), "Enter your gender please", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(loc)){
            Toast.makeText(getApplicationContext(), "Enter your location please", Toast.LENGTH_SHORT).show();
        } else {

                loadingBar.setTitle("Saving Information");
                loadingBar.setMessage("Please wait, while we are creating your new Account...");
                loadingBar.setCanceledOnTouchOutside(true);
                 loadingBar.show();
                HashMap userMap = new HashMap();
                userMap.put("username", username);
                userMap.put("discipline",disi);
                userMap.put("gender", six);
                userMap.put("location",loc);
                userMap.put("id",currentUserID);
                userMap.put("status", "Hey there, i am using Yearonesuite developed by Team Hybrid.");
                UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            SendUserToMainActivity();
                            Toast.makeText(getApplicationContext(), "Details Stored.", Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        } else {
                            String message = task.getException().getMessage();
                            Toast.makeText(getApplicationContext(), "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(),MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        finish();
    }
}
