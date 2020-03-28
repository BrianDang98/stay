package ca.georgebrown.comp3074.stay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
/**/

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ImageView listingImage;
    private TextView detail_title, detail_price,detail_address, detail_landlord, detail_description, detail_numbed, detail_numbath;
    private String ListingKey;

    /* Slider dots */
    ViewPager viewPager;
    LinearLayout sliderDotspanel;
    private int dotscount;
    private ImageView[] dots;

    /* Map */
    GoogleMap map;

    private DatabaseReference DetailListingRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detail_title = (TextView) findViewById(R.id.txtDetail_Title);
        detail_price = (TextView) findViewById(R.id.txtDetail_Price);
        detail_address = (TextView) findViewById(R.id.txtDetail_Address);
        detail_landlord = (TextView) findViewById(R.id.txtDetail_Landlord);
        detail_description = (TextView) findViewById(R.id.txtDetail_Description);
        detail_numbed = (TextView) findViewById(R.id.txtDetail_NumBed);
        detail_numbath = (TextView) findViewById(R.id.txtDetail_NumBath);

        ListingKey = getIntent().getExtras().get("ListingKey").toString();
        DetailListingRef = FirebaseDatabase.getInstance().getReference().child("Listing").child(ListingKey);

        DetailListingRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String title = dataSnapshot.child("title").getValue().toString();
                String price = dataSnapshot.child("price").getValue().toString();
                String address = dataSnapshot.child("address").getValue().toString();
                String landlord = dataSnapshot.child("userFirstName").getValue().toString();
                String description = dataSnapshot.child("description").getValue().toString();
                String numbed = dataSnapshot.child("numBed").getValue().toString();
                String numbath = dataSnapshot.child("numBath").getValue().toString();

                int intNumBed = Integer.parseInt(numbed);
                int intNumBath = Integer.parseInt(numbath);

                if(intNumBed < 2 && intNumBath < 2){
                    String strNumBed = String.valueOf(intNumBed);
                    detail_numbed.setText(strNumBed+" Bedroom");
                    String strNumBath = String.valueOf(intNumBath);
                    detail_numbath.setText(strNumBath+" Bathroom");
                }
                else{
                    String strNumBed = String.valueOf(intNumBed);
                    detail_numbed.setText(strNumBed+" Bedrooms");
                    String strNumBath = String.valueOf(intNumBath);
                    detail_numbath.setText(strNumBath+" Bathrooms");
                }

                detail_title.setText(title);
                detail_price.setText("$"+price);
                detail_address.setText(address);
                detail_landlord.setText("By "+landlord);
                detail_description.setText(description);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /* For slider dots*/
        viewPager = findViewById(R.id.viewPager);

        sliderDotspanel = findViewById(R.id.sliderDots);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);

        viewPager.setAdapter(viewPagerAdapter);
        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i <dotscount; i++){
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.interactive_dots_non) );

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);

            sliderDotspanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.interactive_dots));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                for(int i = 0; i <dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.interactive_dots_non));

                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.interactive_dots));
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        /* End of slider dots */

        /* Map */
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /* End of Map */
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng Listing = new LatLng(43.688101, -79.494282);
        map.addMarker(new MarkerOptions().position(Listing).title("Listing"));
        map.moveCamera(CameraUpdateFactory.newLatLng(Listing));
    }
}
