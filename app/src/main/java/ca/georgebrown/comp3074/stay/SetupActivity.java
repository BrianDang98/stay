package ca.georgebrown.comp3074.stay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.drm.DrmManagerClient;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {

    private EditText firstName, lastName, gender, email, phone;
    private Button saveUserInfo;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    String currentUserId;
    final static int Gallery_Pick = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Tenant").child(currentUserId);

        firstName = findViewById(R.id.txtFirstName);
        lastName = findViewById(R.id.txtLastName);

        saveUserInfo = findViewById(R.id.btnSaveInfo);

        saveUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccountSetupInformation();
            }
        });
    }


    private void saveAccountSetupInformation() {
        String fname = firstName.getText().toString();
        String lname = lastName.getText().toString();
        if(TextUtils.isEmpty(fname)){
            Toast.makeText(this, "First name", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(lname)){
            Toast.makeText(this, "Last name", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap userMap = new HashMap();
            userMap.put("firstName", fname);
            userMap.put("lastName", lname);
            userRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        sendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        String message = task.getException().getMessage();
                        Toast.makeText(SetupActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }



    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
