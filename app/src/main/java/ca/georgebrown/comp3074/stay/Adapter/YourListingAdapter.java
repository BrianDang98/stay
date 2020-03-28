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

import ca.georgebrown.comp3074.stay.Listing;
import ca.georgebrown.comp3074.stay.R;
import ca.georgebrown.comp3074.stay.YourListingActivity;

public class YourListingAdapter extends RecyclerView.Adapter<YourListingAdapter.ViewHolder> {
    private Context context;
    private List<Listing> mListings;

    public YourListingAdapter(Context context, List<Listing> mListings) {
        this.context = context;
        this.mListings = mListings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.your_listing, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Listing listing = mListings.get(position);
        holder.YourListingTitle.setText(listing.getTitle());
        holder.YourListingPrice.setText("$"+listing.getPrice()+"/month");
        Glide.with(context).load(listing.getListingImage()).centerCrop().into(holder.YourListingImageView);
    }

    @Override
    public int getItemCount() {
        return mListings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView YourListingTitle, YourListingPrice, YourListingStatus;
        ImageView YourListingImageView;

        public ViewHolder(@NonNull View ItemView) {
            super(ItemView);
            YourListingTitle = ItemView.findViewById(R.id.txtYourListing_Title);
            YourListingPrice = ItemView.findViewById(R.id.txtYourListing_Price);
            YourListingStatus = ItemView.findViewById(R.id.txtYourListing_Status);
            YourListingImageView = ItemView.findViewById(R.id.imgYourListing);
        }
    }
}
