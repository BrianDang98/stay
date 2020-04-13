package ca.georgebrown.comp3074.stay.Adapter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ca.georgebrown.comp3074.stay.DetailActivity;
import ca.georgebrown.comp3074.stay.Listing;
import ca.georgebrown.comp3074.stay.MainActivity;
import ca.georgebrown.comp3074.stay.R;

public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingViewHolder> {
    private Context context;
    private List<Listing> mListings;

    public ListingAdapter(Context context, List<Listing> mListings) {
        this.context = context;
        this.mListings = mListings;
    }

    @NonNull
    @Override
    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_listing_layout, parent, false);
        ListingViewHolder viewHolder = new ListingViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListingViewHolder holder, int position) {
        // final String ListingKey = getRef(position).getKey();
        Listing listing = mListings.get(position);
        holder.listingTitle.setText(listing.getTitle());
        holder.listingPrice.setText("$"+listing.getPrice()+"/month");
        Glide.with(context).load(listing.getListingImage()).centerCrop().into(holder.listingImageView);

        // holder.setLikeButtonStatus(ListingKey);

        /*
        holder.listingTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailListing = new Intent(context, DetailActivity.class);
                detailListing.putExtra("ListingKey", ListingKey);
                startActivity(detailListing);
            }
        });

        holder.likeListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeChecker = true;

                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(likeChecker.equals(true)){
                            if(dataSnapshot.child(ListingKey).hasChild(currentUserId)){
                                likeRef.child(ListingKey).child(currentUserId).removeValue();
                                likeChecker = false;
                                // FirebaseDatabase.getInstance().getReference().child("Like").child(currentUserId).child()
                            }
                            else{
                                likeRef.child(ListingKey).child(currentUserId).setValue(true);
                                likeChecker = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

         */
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ListingViewHolder extends RecyclerView.ViewHolder{

        TextView listingTitle, listingPrice;
        ImageView listingImageView;
        ImageButton likeListing;
        int countLikes;
        String currentUserId;
        DatabaseReference LikesRef;

        public ListingViewHolder(@NonNull View itemView) {
            super(itemView);
            listingTitle = itemView.findViewById(R.id.txtTitle);
            listingPrice = itemView.findViewById(R.id.txtPrice);
            listingImageView = itemView.findViewById(R.id.imgListing);
            likeListing = itemView.findViewById(R.id.btnLikeListing);

            LikesRef = FirebaseDatabase.getInstance().getReference().child("Like");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }

        public void setLikeButtonStatus(final String ListingKey) {
            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child(ListingKey).hasChild(currentUserId)){
                        countLikes = (int) dataSnapshot.child(ListingKey).getChildrenCount();
                        likeListing.setImageResource(R.drawable.icon_like);
                    }
                    else{
                        countLikes = (int) dataSnapshot.child(ListingKey).getChildrenCount();
                        likeListing.setImageResource(R.drawable.icon_unlike);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
