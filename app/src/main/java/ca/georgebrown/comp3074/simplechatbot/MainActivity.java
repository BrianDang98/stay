package ca.georgebrown.comp3074.simplechatbot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    final Fragment fragmentHome = new HomeFragment();
    final Fragment fragmentFavourite = new FavouriteFragment();
    final Fragment fragmentChat = new ChatFragment();
    final Fragment fragmentProfile = new ProfileFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragmentHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        fm.beginTransaction().add(R.id.main_container, fragmentProfile, "3").hide(fragmentProfile).commit();
        fm.beginTransaction().add(R.id.main_container, fragmentChat, "3").hide(fragmentChat).commit();
        fm.beginTransaction().add(R.id.main_container, fragmentFavourite, "2").hide(fragmentFavourite).commit();
        fm.beginTransaction().add(R.id.main_container, fragmentHome, "1").commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.home_icon:
                            fm.beginTransaction().hide(active).show(fragmentHome).commit();
                            active = fragmentHome;
                            return true;

                        case R.id.fav_icon:
                            fm.beginTransaction().hide(active).show(fragmentFavourite).commit();
                            active = fragmentFavourite;
                            return true;

                        case R.id.chat_icon:
                            fm.beginTransaction().hide(active).show(fragmentChat).commit();
                            active = fragmentChat;
                            return true;

                        case R.id.profile_icon:
                            fm.beginTransaction().hide(active).show(fragmentProfile).commit();
                            active = fragmentProfile;
                            return true;
                    }
                    return false;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_navigation, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
