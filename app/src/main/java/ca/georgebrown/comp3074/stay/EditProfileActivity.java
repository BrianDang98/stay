package ca.georgebrown.comp3074.stay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {

    private EditText editFirstName, editLastName, editEmail, editGender, editPhone;
    private Button btnEditProfile;
    private String currentUserId;
    private DatabaseReference editProfileRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        editProfileRef = FirebaseDatabase.getInstance().getReference().child("Tenant");

        editFirstName = (EditText) findViewById(R.id.txtYourFirstName);
        editLastName = (EditText) findViewById(R.id.txtYourLastName);
        editEmail = (EditText) findViewById(R.id.txtYourEmail);
        editGender = (EditText) findViewById(R.id.txtYourGender);
        editPhone = (EditText) findViewById(R.id.txtYourPhone);


        btnEditProfile = (Button) findViewById(R.id.btnEditYourProfile);

        editProfileRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.hasChild("firstName")){
                        String yourFirstName = dataSnapshot.child("firstName").getValue().toString();
                        editFirstName.setText(yourFirstName);
                    }
                    if(dataSnapshot.hasChild("lastName")){
                        String yourLastName = dataSnapshot.child("lastName").getValue().toString();
                        editLastName.setText(yourLastName);
                    }
                    if(dataSnapshot.hasChild("gender")){
                        String yourGender = dataSnapshot.child("gender").getValue().toString();
                        editGender.setText(yourGender);
                    }
                    if(dataSnapshot.hasChild("phone")){
                        String yourPhone = dataSnapshot.child("phone").getValue().toString();
                        editPhone.setText(yourPhone);
                    }
                    if(dataSnapshot.hasChild("email")){
                        String yourEmail = dataSnapshot.child("email").getValue().toString();
                        editEmail.setText(yourEmail);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });
    }

    private void ValidateAccountInfo() {
        String yourFirstName = editFirstName.getText().toString();
        String yourLastName = editLastName.getText().toString();
        String yourGender = editGender.getText().toString();
        String yourEmail = editEmail.getText().toString();
        String yourPhone = editPhone.getText().toString();

        if(TextUtils.isEmpty(yourFirstName)){
            Toast.makeText(this, "Please add your first name...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(yourLastName)){
            Toast.makeText(this, "Please add your last name...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(yourGender)){
            Toast.makeText(this, "Please add your gender...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(yourEmail)){
            Toast.makeText(this, "Please add your email...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(yourPhone)){
            Toast.makeText(this, "Please add your phone...", Toast.LENGTH_SHORT).show();
        }
        else{
            UpdateInfoAccount(yourFirstName, yourLastName, yourGender, yourEmail, yourPhone);
        }
    }

    private void UpdateInfoAccount(String yourFirstName, String yourLastName, String yourGender, String yourEmail, String yourPhone) {
        HashMap userMap = new HashMap();
        userMap.put("firstName", yourFirstName);
        userMap.put("lastName", yourLastName);
        userMap.put("gender", yourGender);
        userMap.put("email", yourEmail);
        userMap.put("phone", yourPhone);
        editProfileRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Toast.makeText(EditProfileActivity.this, "Your beautiful profile has been updated...", Toast.LENGTH_SHORT).show();
                    sendUserToMainActivity();
                }
                else{
                    Toast.makeText(EditProfileActivity.this, "Error!!! Our beautiful profile cannot be updated...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(EditProfileActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
