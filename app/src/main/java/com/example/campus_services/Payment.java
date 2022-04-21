package com.example.campus_services;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class Payment extends AppCompatActivity implements PaymentResultListener {
    TextView paytext;
    Button paybtn;
    private TextView OrderNumber;
    private String OrderNo, Amount;
    Integer value;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Intent intent = getIntent();

        OrderNo = intent.getStringExtra("OrderNo");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getInt("amount");
            //The key argument here must match that used in the other activity
        }
        Amount = Integer.toString(value);
        Toast.makeText(getApplicationContext(), "Amount : "+value, Toast.LENGTH_SHORT).show();
        Checkout.preload(getApplicationContext());
        OrderNumber = (TextView) findViewById(R.id.ViewOrderNumber);
//        OrderNumber.setText("OrderNo: "+OrderNo);
        paybtn = (Button)findViewById(R.id.make_payment_btn);
        paytext = (TextView)findViewById(R.id.c31_c33_payment_amount);
        paytext.setText(" "+value);
        paybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makepayment();
            }
        });
    }

    private void makepayment() {

            Checkout checkout = new Checkout();
            checkout.setKeyID("rzp_test_xK7pKUklE5ovEt");

             checkout.setImage(R.drawable.foodify_logo);

            /**
             * Reference to current activity
             */
            final Activity activity = this;

            /**
             * Pass your payment options to the Razorpay Checkout as a JSONObject
             */
            try {
                JSONObject options = new JSONObject();

                options.put("name", "Foodify");
                options.put("description", "Reference No. #123456");
                options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                //options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
                options.put("theme.color", "#3399cc");
                options.put("currency", "INR");
                options.put("amount", value*100);//pass amount in currency subunits
                options.put("prefill.email", "dhairya9921@gmail.com");
                options.put("prefill.contact","7990352624");
                JSONObject retryObj = new JSONObject();
                retryObj.put("enabled", true);
                retryObj.put("max_count", 4);
                options.put("retry", retryObj);

                checkout.open(activity, options);

            } catch(Exception e) {
                Log.e("TAG", "Error in starting Razorpay Checkout", e);
            }
        }

    @Override
    public void onPaymentSuccess(String s) {

        paytext.setText("Succesfull payment ID: "+s);
        Intent i = new Intent(getApplicationContext(),Thanks.class);
        i.putExtra("OrderNo", OrderNo);
        startActivity(i);

    }

    @Override
    public void onPaymentError(int i, String s)
    {
        paytext.setText("Failed and cause: "+s);
        Intent i2 = new Intent(getApplicationContext(),PlaceOrder.class);
        startActivity(i2);
    }
}
