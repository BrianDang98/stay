package ca.georgebrown.comp3074.stay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private TextView ProfileUsername, btnLogOut;
    private CircleImageView profileImage;
    private Button btnToEditProfile, btnToListYourSpace, btnToManageYourSpace;
    private BottomNavigationView bottomNavigationView;

    private DatabaseReference ref;
    private StorageReference UserProfileImageRef;

    private FirebaseAuth auth;
    String currentUserId, profileImageUrl;
    private Uri ImageUri;
    final static int Gallery_Pick = 1;
    StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ProfileUsername = (TextView) findViewById(R.id.txtUserName);
        profileImage = (CircleImageView) findViewById(R.id.profileImage);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("profileImage");

        btnLogOut = (TextView) findViewById(R.id.btnLogOut);
        btnToListYourSpace = (Button) findViewById(R.id.btnToListYourSpace);

        btnToListYourSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ListMySpace = new Intent(ProfileActivity.this, AddListingActivity.class);
                startActivity(ListMySpace);
                finish();
            }
        });

        btnToEditProfile = (Button) findViewById(R.id.btnToYourProfile);
        btnToEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent EditProfile = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(EditProfile);
                finish();
            }
        });

        btnToManageYourSpace = (Button) findViewById(R.id.btnToManageYourSpace);
        btnToManageYourSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ManageYourSpace = new Intent(ProfileActivity.this, YourListingActivity.class);
                startActivity(ManageYourSpace);
                finish();
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference().child("Tenant");

        ref.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("firstName") && dataSnapshot.hasChild("lastName")) {
                        String fname = dataSnapshot.child("firstName").getValue(String.class);
                        String lname = dataSnapshot.child("lastName").getValue(String.class);
                        ProfileUsername.setText(fname + " " + lname);
                    }

                    if (dataSnapshot.hasChild("profileImage")) {
                        String image = dataSnapshot.child("profileImage").getValue().toString();
                        //Glide.with(ProfileActivity.this).load(image).into(profileImage);
                        Glide.with(ProfileActivity.this).load(image).centerCrop().into(profileImage);

                    } else {
                        Toast.makeText(ProfileActivity.this, "Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile_icon);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });

        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                SendUserToWelcomeActivity();
            }
        });

    }

    private void SendUserToWelcomeActivity() {
        Intent start = new Intent(getApplicationContext(), StartActivity.class);
        startActivity(start);
        finish();

    }

    // Open gallery
    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    // Get image file extension
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // Pick an image and save image to storage
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                final StorageReference filePath = UserProfileImageRef.child(currentUserId + "."+getFileExtension(ImageUri));

                uploadTask = filePath.putFile(ImageUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful()){
                            throw task.getException();
                        }
                        else{
                            return filePath.getDownloadUrl();
                        }                    }
                }).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Uri downloadUri = (Uri) task.getResult();
                            profileImageUrl = downloadUri.toString();

                            ref = FirebaseDatabase.getInstance().getReference().child("Tenant");
                            SavingProfileImageToDatabase();
                        }
                        else {
                            Toast.makeText(ProfileActivity.this, "Error Image cannot be cropped", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                /*
                filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Intent selfIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                            startActivity(selfIntent);
                            Toast.makeText(ProfileActivity.this, "Profile Image stored to Firebase storage successfully", Toast.LENGTH_SHORT).show();

                            filePath.getDownloadUrl();

                            ref.child("profileImage").setValue(filePath.getDownloadUrl())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProfileActivity.this, "Profile Image store to Firebase database successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(ProfileActivity.this, "Error Image cannot be cropped", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                 */
            }
        }
    }

    private void SavingProfileImageToDatabase() {
        ref.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    HashMap userMap = new HashMap();
                        userMap.put("listingImage", profileImageUrl);

                    ref.child(currentUserId).updateChildren(userMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(ProfileActivity.this, "Profile Image store to Firebase database successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(ProfileActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UserMenuSelector(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_icon:
                break;

            case R.id.fav_icon:
                Intent favourite = new Intent(getApplicationContext(), FavouriteActivity.class);
                startActivity(favourite);
                overridePendingTransition(0,0);
                break;

            case R.id.chat_icon:
                Intent chatbot = new Intent(getApplicationContext(), ChatListActivity.class);
                startActivity(chatbot);
                overridePendingTransition(0,0);
                break;

            case R.id.home_icon:
                Intent home = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(home);
                overridePendingTransition(0,0);
                break;

        }
    }
}
