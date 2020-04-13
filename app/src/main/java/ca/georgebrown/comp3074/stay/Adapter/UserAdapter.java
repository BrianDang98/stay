package ca.georgebrown.comp3074.stay.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ca.georgebrown.comp3074.stay.R;
import ca.georgebrown.comp3074.stay.Tenant;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<Tenant> mUser;

    public UserAdapter(Context context, List<Tenant> mUsers){
        this.mContext = context;
        this.mUser = mUsers;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.txtUserChatName);
            profile_image = itemView.findViewById(R.id.userReceiverImage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_chatbot, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tenant user = mUser.get(position);
        holder.username.setText(user.getFirstName());
        if(user.getImageUrl().equals("default")){
            holder.profile_image.setImageResource(R.mipmap.ic_launcher);
        }
        else{
            Glide.with(mContext).load(user.getImageUrl()).centerCrop().into(holder.profile_image);
        }
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }


}
