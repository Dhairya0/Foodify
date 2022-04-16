package com.example.campus_services;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class HomeActivity extends AppCompatActivity {

    private TextView tvDisplayName;
    private Button btnCanteenOrder;

    private FirebaseAuth mAuth;
    private String userID;
    private DatabaseReference table_user;
    private ValueEventListener listener1;
    CarouselView carouselView;
    int[] sampleImages = {R.drawable.images1, R.drawable.images2, R.drawable.images3, R.drawable.images4};
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        carouselView = findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);
        carouselView.setImageListener(imageListener);
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() == null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }

        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getEmail();
        int i=0;
        while(userID.charAt(i) != '@')
            i++;
        userID = userID.substring(0,i);
        tvDisplayName = findViewById(R.id.tvDisplayName);

        btnCanteenOrder = findViewById(R.id.btnCanteenOrder);


//        c11_constrainLayout.setVisibility(View.GONE);
//        splashScreenConstrain.setVisibility(View.VISIBLE);
//        if(!isConnected()){
//            internetLayout.setVisibility(View.VISIBLE);
//            c11_constrainLayout.setVisibility(View.GONE);
//            splashScreenConstrain.setVisibility(View.GONE);
//        }else {
//            internetLayout.setVisibility(View.GONE);
//            c11_constrainLayout.setVisibility(View.VISIBLE);
//
//
//
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    c11_constrainLayout.setVisibility(View.VISIBLE);
//                    splashScreenConstrain.setVisibility(View.GONE);
//                }
//
//            }, 5000);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        table_user = db.getReference("Users").child("Student");
        listener1 = table_user.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                 User user = dataSnapshot.child(userID).getValue(User.class);
                 tvDisplayName.setText("Welcome: " + user.getName().toUpperCase()+"\n");
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnCanteenOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CanteenOrderActivity.class));
            }
        });


    }
    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home_page,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

        switch (item.getItemId())
        {
            case R.id.menuLogout:
                table_user.removeEventListener(listener1);
                mAuth.signOut();
                Intent intent = new Intent(this, Login_Activity.class);
                finish();
                startActivity(intent);
                return true;

            case R.id.menuProfile:
                Intent intent1 = new Intent(this,Student_Profile.class);
                startActivity(intent1);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
