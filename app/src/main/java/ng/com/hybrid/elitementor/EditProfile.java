package ng.com.hybrid.elitementor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfile extends AppCompatActivity {

    EditText newname,newstatus;
    CardView cdname,cdstatus;
    Animation animation;
    Button btnsave;
    FirebaseAuth mAuth;
    private String currentUserId;
    private DatabaseReference edRef;
    CircleImageView newImage;
    ProgressDialog progressDialog;
    final static int Gallery_Pick = 1;
    private StorageReference UserProfileImageRef;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);


  //Checking current user
     if(firebaseUser != null){
      currentUserId = firebaseUser.getUid();
     }

        newname = findViewById(R.id.newname);
        newstatus = findViewById(R.id.newstatus);
        cdname = findViewById(R.id.cdname);
        cdstatus = findViewById(R.id.cdstatus);
        newImage =  findViewById(R.id.newImage);
       btnsave = findViewById(R.id.btnsave);

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
     edRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);



        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              validateUser();
            }
        });

        newImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,Gallery_Pick);

            }
        });

        //Retrieving user data

        edRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myName = dataSnapshot.child("username").getValue().toString();
                    String myStatus = dataSnapshot.child("status").getValue().toString();


                    Picasso.get().load(myProfileImage).placeholder(R.mipmap.ic_launcher).into(newImage);
                    newname.setText(myName);
                    newstatus.setText(myStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
                progressDialog.setTitle("Profile Image");
                progressDialog.setMessage("Please wait, while we are updating your new profile image...");
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.show();
                Uri resultUri = result.getUri();

                final StorageReference ref = UserProfileImageRef.child(currentUserId + ".jpg");
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
                            edRef.child("profileimage").setValue(link).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Intent selfIntent = new Intent(getApplicationContext(), EditProfile.class);
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
                progressDialog.dismiss();
            }
        }
    }



    private void validateUser() {

        String username = newname.getText().toString();
        String status = newstatus.getText().toString();


        if(TextUtils.isEmpty(username)){
            Toast.makeText(getApplicationContext(),"Name please", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(status)){
            Toast.makeText(getApplicationContext(),"Status please", Toast.LENGTH_SHORT).show();
        }else {
            progressDialog.setTitle("Saving Information");
            progressDialog.setMessage("Please wait, while we are updating your data...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
            updateUser(username,status);

        }


    }

    public void updateUser(String username, String status){

        HashMap userMap = new HashMap();
        userMap.put("username",username);
        userMap.put("status",status);

        edRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Details updated",Toast.LENGTH_SHORT).show();
                    sendUserToMain();
                }else{
                    progressDialog.dismiss();
                   Toast.makeText(getApplicationContext(),"Error occured,Please try again",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    private void sendUserToMain() {

        Intent send = new Intent(getApplicationContext(),MainActivity.class);
        send.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(send);
        finish();

    }

}
