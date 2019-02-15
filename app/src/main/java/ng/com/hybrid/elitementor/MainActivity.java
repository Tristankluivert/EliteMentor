package ng.com.hybrid.elitementor;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import ng.com.hybrid.elitementor.Fragments.ChatFragment;
import ng.com.hybrid.elitementor.Fragments.MentorsFrag;
import ng.com.hybrid.elitementor.Fragments.ProfileFrag;


public class MainActivity extends AppCompatActivity {

    private RelativeLayout mlinId;
    PathModel outline;

    private FirebaseAuth mAuth;
    private DatabaseReference UserRef;
    String currentUserID;

    private StorageReference UserProfileImageRef;
    Toolbar toolbar;
    TextView currentname;
    CircleImageView currentpic;
    private DatabaseReference edRef;
    private String currentUserId;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                new ChatFragment()).commit();

        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");


          currentname = findViewById(R.id.currentname);
          currentpic = findViewById(R.id.currentpic);

     BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
     bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            currentUserId = firebaseUser.getUid();


        edRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        edRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myName = dataSnapshot.child("username").getValue().toString();


                    Picasso.get().load(myProfileImage).placeholder(R.mipmap.ic_launcher).into(currentpic);
                   currentname.setText(myName);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

          }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            mAuth.signOut();
            SendUserToLogin();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private  BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Fragment selectedFrag = null;

        switch (menuItem.getItemId()){
            case R.id.action_chats:
                selectedFrag = new ChatFragment();
                break;

            case R.id.action_profile:
                selectedFrag = new ProfileFrag();
                break;

            case R.id.action_mentors:
                selectedFrag = new MentorsFrag();
                break;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frag_container,
                selectedFrag).commit();
        return true;
    }
};

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentuser  = mAuth.getCurrentUser();

        if(currentuser == null){
            SendUserToLogin();
        }else{
            CheckUserData();
        }



    }

    private void CheckUserData() {

        final String current_user_id = mAuth.getCurrentUser().getUid();
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!dataSnapshot.hasChild(current_user_id)){
                    sendUserToDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendUserToDetails() {

        Intent userintent = new Intent(getApplicationContext(),UserDetails.class);
        userintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(userintent);
        finish();

    }

    private void SendUserToLogin() {

        Intent loginintent = new Intent(getApplicationContext(),Login.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();
    }



}
