package com.example.campus_services;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class Login_Activity extends AppCompatActivity {

    private EditText vEmail,vPassword;
    private Button vbtnLogin;
    private TextView vSignup,vForgotPassword;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        vEmail = (EditText) findViewById(R.id.Email);
        vPassword = (EditText) findViewById(R.id.Password);
        vbtnLogin = (Button) findViewById(R.id.btnLogin);
        vSignup = (TextView) findViewById(R.id.Signup);
        vForgotPassword = (TextView) findViewById(R.id.ForgotPassword);
        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        vbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = vEmail.getText().toString().trim();
                String password = vPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    vEmail.setError("Please Enter Email");
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    vPassword.setError("Password is required");
                    return;
                }

                progressDialog.setMessage("Logging In...");
                progressDialog.show();

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()){
                            final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            final String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                            final String student_id = email.substring(0,9);

//                            if(!(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified())){
//                                FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if(task.isSuccessful()){
//                                            Toast.makeText(Login_Activity.this,"Email verification link is sent",Toast.LENGTH_LONG).show();
//                                            FirebaseAuth.getInstance().signOut();
//                                        } else{
//                                            Toast.makeText(Login_Activity.this,"Error!!!" + task.getException().toString(),Toast.LENGTH_LONG).show();
//                                        }
//                                    }
//                                });
//                                return;
//                            }

                            databaseReference.child("Users").child("Canteen").child(uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        // forward to canteen's home activity
                                        if((!(boolean)dataSnapshot.child("ban").getValue())) {
                                            Canteen canteen = dataSnapshot.getValue(Canteen.class);
                                            Intent intent = new Intent(Login_Activity.this, CanteenManager.class);
                                            intent.putExtra("CanteenName", canteen.getName());
                                            intent.putExtra("CanteenAvailable", canteen.getAvailable());
                                            finish();
                                            startActivity(intent);
                                        }else{
                                            showThatUserIsBanned();
                                        }
                                    } else{
                                        databaseReference.child("Users").child("Doctor").child(uid).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if(dataSnapshot.exists()){
                                                    // forward to doctor's home activity
                                                    if((!(boolean)dataSnapshot.child("ban").getValue())){
                                                        Intent intent = new Intent(Login_Activity.this,Doctor_Profile.class);
                                                        finish();
                                                        startActivity(intent);
                                                    }else{
                                                        showThatUserIsBanned();
                                                    }
                                                } else{
                                                    databaseReference.child("Users").child("Supervisor").child(uid).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                        {
                                                            if(dataSnapshot.exists())
                                                            {
                                                                // forward to supervisor's home activity
                                                                Intent intent = new Intent(Login_Activity.this,SupervisiorMain.class);
                                                                finish();
                                                                startActivity(intent);
                                                            }
                                                            else
                                                            {
                                                                databaseReference.child("Users").child("Admin").child(uid).addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                        if(dataSnapshot.exists()){
                                                                            // forward to admin home activity
                                                                            Intent intent = new Intent(Login_Activity.this,AdminHome.class);
                                                                            finish();
                                                                            startActivity(intent);
                                                                        } else{
                                                                            databaseReference.child("Users").child("Professor").child(uid).addValueEventListener(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                    if(dataSnapshot.exists()){
                                                                                        // forward to professor home activity
                                                                                        if((!(boolean)dataSnapshot.child("ban").getValue())) {
                                                                                            Intent intent = new Intent(Login_Activity.this, CanteenOrderActivity.class);
                                                                                            finish();
                                                                                            startActivity(intent);
                                                                                        }else{
                                                                                            showThatUserIsBanned();
                                                                                        }
                                                                                    } else{
                                                                                        databaseReference.child("Users").child("Student").child(student_id).addValueEventListener(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                if(dataSnapshot.exists()){
                                                                                                    if((!(boolean)dataSnapshot.child("ban").getValue())) {
                                                                                                        Intent intent = new Intent(Login_Activity.this, Splashscreen.class);
                                                                                                        finish();
                                                                                                        startActivity(intent);
                                                                                                    }else{
                                                                                                        showThatUserIsBanned();
                                                                                                    }
                                                                                                }
                                                                                                else{
                                                                                                    //Log.d("hello! = ", "1" );
                                                                                                    FirebaseAuth.getInstance().signOut();
                                                                                                }
                                                                                            }

                                                                                            @Override
                                                                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                            }
                                                                                        });
                                                                                    }

                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                }
                                                                            });
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });



                        }
                        else{
                            Toast.makeText(Login_Activity.this,"Incorrect Credentials!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        vSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_Activity.this,SignUpActivity.class);
                finish();
                startActivity(intent);
            }
        });
    }

    public void showThatUserIsBanned(){
        Toast.makeText(Login_Activity.this,"Your account is banned by the Admin.",Toast.LENGTH_LONG).show();
        FirebaseAuth.getInstance().signOut();
    }
}