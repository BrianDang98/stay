package ca.georgebrown.comp3074.stay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private TextView btnSignIn;
    private EditText email, password, firstName, lastName;
    private Button btnSignUp;

    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private DatabaseReference ref;

    FirebaseAuth mAuth;
    DatabaseReference userRef;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // *** Variable declaration *** //
        auth = FirebaseAuth.getInstance();

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        firstName = findViewById(R.id.txtFirstName);
        lastName = findViewById(R.id.txtLastName);
        progressBar = findViewById(R.id.progressBar);

        //*** Start of Sign Up Button ***//
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_firstname = firstName.getText().toString().trim();
                String txt_lastname = lastName.getText().toString().trim();
                String txt_email = email.getText().toString().trim();
                String txt_password = password.getText().toString().trim();

                if (TextUtils.isEmpty(txt_firstname)) {
                    email.setError("Enter first name!");
                    email.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(txt_lastname)) {
                    email.setError("Enter last name!");
                    email.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(txt_email)) {
                    email.setError("Enter email address!");
                    email.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(txt_password)) {
                    password.setError("Enter password!");
                    password.requestFocus();
                    return;
                }

                if (txt_password.length() < 6) {
                    password.setError("Password too short, enter minimum 6 characters!");
                    password.requestFocus();
                    return;
                }

                else{
                    register(txt_email, txt_password);
                }
            }
        });
        //*** End of Sign Up Code ***//

        //*** Start of Sign In Button ***//
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });
        //*** End of Sign In Button ***//

    }

    private void register(String email, String password){

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(getApplicationContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();
                            String userEmail = firebaseUser.getEmail();

                            ref = FirebaseDatabase.getInstance().getReference("Tenant").child(userId);

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("email", userEmail);
                            hashMap.put("profileImage", "default");

                            ref.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        saveFirstNameLastName();
                                        StoreFirstNameLastName();
                                        Toast.makeText(SignupActivity.this, "Registration succeeded !", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        }

                    }
                });
    }

    private void StoreFirstNameLastName() {
        String fname = firstName.getText().toString();
        String lname = lastName.getText().toString();
        HashMap userMap = new HashMap();
        userMap.put("firstName", fname);
        userMap.put("lastName", lname);
        userRef.updateChildren(userMap);
    }

    private void saveFirstNameLastName() {
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Tenant").child(currentUserId);

        firstName = findViewById(R.id.txtFirstName);
        lastName = findViewById(R.id.txtLastName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
