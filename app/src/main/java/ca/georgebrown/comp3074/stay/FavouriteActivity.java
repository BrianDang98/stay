package ca.georgebrown.comp3074.stay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    RecyclerView yourFavList;
    private List<String> myFav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);

        // Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.fav_icon);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fav_icon:
                break;

            case R.id.home_icon:
                Intent home = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(home);
                overridePendingTransition(0,0);
                break;

            case R.id.chat_icon:
                Intent chatbot = new Intent(getApplicationContext(), ChatListActivity.class);
                startActivity(chatbot);
                overridePendingTransition(0,0);
                break;

            case R.id.profile_icon:
                Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(profile);
                overridePendingTransition(0,0);
                break;
        }
    }
}
