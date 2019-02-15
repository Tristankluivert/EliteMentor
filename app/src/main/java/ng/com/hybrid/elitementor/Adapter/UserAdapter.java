package ng.com.hybrid.elitementor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ng.com.hybrid.elitementor.MessageAct;
import ng.com.hybrid.elitementor.Model.User;
import ng.com.hybrid.elitementor.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mcontext;
    private List<User> mUsers;

    public UserAdapter(Context mContext, List<User> mUsers){
        this.mUsers = mUsers;
        this.mcontext = mContext;

    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public CircleImageView profileimage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profileimage = itemView.findViewById(R.id.profile_image);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_mentor,viewGroup,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        final User user = mUsers.get(i);
        viewHolder.username.setText(user.getUsername());

        if(user.getProfileimage().equals("default")){
            viewHolder.profileimage.setImageResource(R.mipmap.ic_launcher);
        }else{
            Glide.with(mcontext).load(user.getProfileimage()).into(viewHolder.profileimage);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mcontext,MessageAct.class);
                intent.putExtra("userid",user.getId());
                mcontext.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }
}
