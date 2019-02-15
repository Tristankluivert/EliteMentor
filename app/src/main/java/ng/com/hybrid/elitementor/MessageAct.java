package ng.com.hybrid.elitementor;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import ng.com.hybrid.elitementor.Adapter.MessageAdapter;
import ng.com.hybrid.elitementor.Model.Chat;
import ng.com.hybrid.elitementor.Model.User;

public class MessageAct extends AppCompatActivity {

    CircleImageView profilepic;
    TextView username;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
   Intent intent;
  MessageAdapter messageAdapter;
  List<Chat> mChat;
  RecyclerView recyclerView;
   ImageButton btn_send;
   EditText text_send;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.recycler_chat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profilepic = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        final String userid = intent.getStringExtra("userid");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = text_send.getText().toString();
                if(!msg.equals("")){
                    sendMessage(firebaseUser.getUid(),userid,msg);
                }else{
                    Toast.makeText(getApplicationContext(),"Write Something",Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

    //  userId = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getProfileimage().equals("default")){
                    profilepic.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(MessageAct.this).load(user.getProfileimage()).into(profilepic);
                }

                readMessage(firebaseUser.getUid(),userid, user.getProfileimage());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void sendMessage(String sender, String receiver, String message){

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("reciever",receiver);
        hashMap.put("message",message);

        databaseReference.child("Chats").push().setValue(hashMap);

    }

    private void readMessage(final String myid, final String userid, final String imgUrl){

        mChat = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (!Objects.equals(chat.getReceiver(), myid) && !Objects.equals(chat.getSender(), userid) ||
                            !Objects.equals(chat.getReceiver(), userid) && !Objects.equals(chat.getSender(), myid)){
                        mChat.add(chat);
                    }


                    messageAdapter = new MessageAdapter(MessageAct.this,mChat,imgUrl);
                    recyclerView.setAdapter(messageAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
