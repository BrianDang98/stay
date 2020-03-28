package ca.georgebrown.comp3074.stay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static ca.georgebrown.comp3074.stay.SetupActivity.Gallery_Pick;

public class AddListingActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private EditText listingTitle, listingPrice, listingDescription, listingRentalAddress, listingNumOfBedroom, listingNumOfBathroom;
    private ImageButton pictureToUpload;
    private Button btnUploadListing;
    private static final int Gallery_Pick = 1;
    private Uri ImageUri;
    StorageTask uploadTask;

    private String Title, Price, Description, Address, NumOfBedroom, NumOfBathroom;

    private StorageReference storageReference;
    private DatabaseReference userReference, listingReference;
    private FirebaseAuth mAuth;
    private String saveCurrentDate, saveCurrentTime, postRandomName, current_user_id, listingImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listing);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("Tenant");
        storageReference = FirebaseStorage.getInstance().getReference().child("Listing");

        listingTitle = (EditText) findViewById(R.id.txtListingTitle);
        listingPrice = (EditText) findViewById(R.id.txtListingPrice);
        listingDescription = (EditText) findViewById(R.id.txtListingDescription);
        listingRentalAddress = (EditText) findViewById(R.id.txtListingRentalAddress);
        listingNumOfBedroom = (EditText) findViewById(R.id.txtListingNumberBedroom);
        listingNumOfBathroom = (EditText) findViewById(R.id.txtListingNumberBathroom);
        pictureToUpload = (ImageButton) findViewById(R.id.imageButtonPicture);
        btnUploadListing = (Button) findViewById(R.id.btnUpload);

        mToolBar = (Toolbar) findViewById(R.id.add_listings_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Listing");


        pictureToUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        btnUploadListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateListingInfo();
            }
        });
    }

    // Open gallery
    private void OpenGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setType("image/");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(galleryIntent, Gallery_Pick);
    }

    // Pick an image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null && data.getData() != null){
            ImageUri = data.getData();
            pictureToUpload.setImageURI(ImageUri);
        }
    }

    // Get image file extension
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    // Validate listing information
    private void ValidateListingInfo() {
        Title = listingTitle.getText().toString();
        Price = listingPrice.getText().toString();
        Description = listingDescription.getText().toString();
        Address = listingRentalAddress.getText().toString();
        NumOfBedroom = listingNumOfBedroom.getText().toString();
        NumOfBathroom = listingNumOfBathroom.getText().toString();

        if(ImageUri == null){
            Toast.makeText(this, "Please select images of your rental", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(Title) && TextUtils.isEmpty(Price) && TextUtils.isEmpty(Description) && TextUtils.isEmpty(Address) && TextUtils.isEmpty(NumOfBedroom) && TextUtils.isEmpty(NumOfBathroom)){
            Toast.makeText(this, "Please fill all essential information about your rental", Toast.LENGTH_SHORT).show();
        }
        else{
            StoringImageToFirebaseStorage();
        }
    }

    // Store image to storage
    private void StoringImageToFirebaseStorage() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM-dd-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        if(ImageUri != null){
            final StorageReference filePath = storageReference.child(postRandomName + "."+getFileExtension(ImageUri));

            uploadTask = filePath.putFile(ImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    else{
                        return filePath.getDownloadUrl();
                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        listingImageUrl = downloadUri.toString();

                        listingReference = FirebaseDatabase.getInstance().getReference().child("Listing");
                        SavingListingInformationToDatabase();
                    }
                }
            });

        }
        else{
            Toast.makeText(AddListingActivity.this, "No image selected !!!", Toast.LENGTH_SHORT).show();
        }
    }

    // Save listing info to database
    private void SavingListingInformationToDatabase() {
        userReference.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userFirstname = dataSnapshot.child("firstName").getValue().toString();

                    HashMap listingMap = new HashMap();
                        listingMap.put("uid", current_user_id);
                        listingMap.put("date", saveCurrentDate);
                        listingMap.put("time", saveCurrentTime);
                        listingMap.put("title", Title);
                        listingMap.put("price", Price);
                        listingMap.put("description", Description);
                        listingMap.put("address", Address);
                        listingMap.put("numBed", NumOfBedroom);
                        listingMap.put("numBath", NumOfBathroom);
                        listingMap.put("listingImage", listingImageUrl);
                        listingMap.put("userFirstName", userFirstname);

                    listingReference.child(current_user_id + postRandomName).updateChildren(listingMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if(task.isSuccessful()){
                                        sendUserToMainActivity();
                                        Toast.makeText(AddListingActivity.this, "Your listing is updated successfully", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(AddListingActivity.this, "Error", Toast.LENGTH_SHORT).show();
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

    // Send user to Main activity
    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(AddListingActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
