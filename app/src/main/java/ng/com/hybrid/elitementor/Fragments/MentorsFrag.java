package ng.com.hybrid.elitementor.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ng.com.hybrid.elitementor.Adapter.UserAdapter;
import ng.com.hybrid.elitementor.Model.User;
import ng.com.hybrid.elitementor.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MentorsFrag extends Fragment {


    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    private List<User> users;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mentors,container,false);

        recyclerView = view.findViewById(R.id.recycler_mentor);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        users = new ArrayList<>();
        readUsers();
        return view;
    }

    private void readUsers() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               users.clear();
               for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                   User user = snapshot.getValue(User.class);


                   assert user != null;
                   if(!Objects.equals(user.getId(), firebaseUser != null ? Objects.requireNonNull(firebaseUser).getUid() : null)){
                       users.add(user);
                   }

                   }
                   userAdapter = new UserAdapter(getContext(),users);
                  recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
