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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import ng.com.hybrid.elitementor.MessageAct;
import ng.com.hybrid.elitementor.Model.Chat;
import ng.com.hybrid.elitementor.Model.User;
import ng.com.hybrid.elitementor.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mcontext;
    private List<Chat> mChat;
   private String imgUrl;
    FirebaseUser firebaseUser;

    public MessageAdapter (Context mContext, List<Chat> mChat, String imgUrl){
        this.mChat = mChat;
        this.imgUrl = imgUrl;
        this.mcontext = mContext;

    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message;
        public CircleImageView profileimage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            show_message = itemView.findViewById(R.id.show_message);
            profileimage = itemView.findViewById(R.id.profile_image);
        }
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if(i == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_right,viewGroup,false);
            return new MessageAdapter.ViewHolder(view);
        }else{
            View view = LayoutInflater.from(mcontext).inflate(R.layout.chat_item_left,viewGroup,false);
            return new MessageAdapter.ViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder viewHolder, final int i) {

        Chat chat = mChat.get(i);
        viewHolder.show_message.setText(chat.getMessage());
                if(imgUrl.equals("default")){
                    viewHolder.profileimage.setImageResource(R.mipmap.ic_launcher);
                }else{
                    Glide.with(mcontext).load(imgUrl).into(viewHolder.profileimage);
                }

    }


    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}
