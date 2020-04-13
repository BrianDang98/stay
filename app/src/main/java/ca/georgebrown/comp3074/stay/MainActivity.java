package ca.georgebrown.comp3074.stay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView userFirstName, userGreeting;
    private DatabaseReference ref, listingRef, likeRef;
    private FirebaseAuth auth;
    String currentUserId;

    private BottomNavigationView bottomNavigationView;
    private Toolbar mToolbar;

    private RecyclerView postList;

    Boolean likeChecker;
    String greeting = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        postList = (RecyclerView) findViewById(R.id.all_listings);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        userFirstName = (TextView) findViewById(R.id.txtProfileName);
        userGreeting = (TextView) findViewById(R.id.greeting);

        // Set time-based greeting
        //Get the time of the day
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        //Set greeting
        if(hour>= 12 && hour < 17){
            greeting = "Good afternoon";
        } else if(hour >= 17 && hour < 21){
            greeting = "Good evening";
        } else if(hour >= 21 && hour < 24){
            greeting = "Good night";
        } else {
            greeting = "Good morning";
        }
        userGreeting.setText(greeting);




        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Tenant");
        listingRef = FirebaseDatabase.getInstance().getReference().child("Listing");

        likeRef = FirebaseDatabase.getInstance().getReference().child("Like");


        // Greeting + Username
        ref.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("firstName"))
                    {
                        String fullname = dataSnapshot.child("firstName").getValue(String.class);
                        userFirstName.setText(fullname);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.home_icon);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        DisplayAllListings();
    }

    private void DisplayAllListings() {

        FirebaseRecyclerOptions<Listing> options =
                new FirebaseRecyclerOptions.Builder<Listing>()
                        .setQuery(listingRef, Listing.class)
                        .build();


        FirebaseRecyclerAdapter<Listing, ListingViewHolder> adapter =
                new FirebaseRecyclerAdapter<Listing, ListingViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ListingViewHolder holder, int position, @NonNull Listing model) {
                        final String ListingKey = getRef(position).getKey();

                        holder.listingTitle.setText(model.getTitle());
                        holder.listingPrice.setText("$"+model.getPrice()+"/month");
                        Glide.with(MainActivity.this).load(model.getListingImage()).centerCrop().into(holder.listingImageView);


                        holder.setLikeButtonStatus(ListingKey);
                        holder.listingTitle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent detailListing = new Intent(MainActivity.this, DetailActivity.class);
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

                    }


                    @NonNull
                    @Override
                    public ListingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_listing_layout, parent, false);
                        ListingViewHolder viewHolder = new ListingViewHolder(view);
                        return viewHolder;
                    }
                };

        postList.setAdapter(adapter);

        adapter.startListening();
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

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_icon:
                break;

            case R.id.fav_icon:
                Intent favourite = new Intent(getApplicationContext(), FavouriteActivity.class);
                startActivity(favourite);
                overridePendingTransition(0,0);
                break;

            case R.id.chat_icon:
                Intent chatlist = new Intent(getApplicationContext(), ChatListActivity.class);
                startActivity(chatlist);
                overridePendingTransition(0,0);
                break;

            case R.id.profile_icon:
                Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profile);
                overridePendingTransition(0,0);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
