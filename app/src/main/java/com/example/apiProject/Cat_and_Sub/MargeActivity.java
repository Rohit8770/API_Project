package com.example.apiProject.Cat_and_Sub;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.apiProject.Cat_and_Sub.Category.Output.ReturnDataActivity;
import com.example.apiProject.Cat_and_Sub.CetaLock.CetaLockActivity;
import com.example.apiProject.Cat_and_Sub.Product.ProductFileActivity;
import com.example.apiProject.Cat_and_Sub.Sub_Category.Outpotsub2.ResultSubActivity;
import com.example.apiProject.Cat_and_Sub.User.UserNetwork.SharedPreference;
import com.example.apiProject.R;


public class MargeActivity extends AppCompatActivity {
    Button cat1,sub1,product1,ceta1;
    TextView textUser;
    SharedPreference sharedPreference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marge);

        cat1=findViewById(R.id.cat1);
        ceta1=findViewById(R.id.ceta1);
        sub1=findViewById(R.id.sub1);
        product1=findViewById(R.id.product1);
        textUser=findViewById(R.id.textUser);
        sharedPreference=new SharedPreference(MargeActivity.this);

        textUser.setText(sharedPreference.getStringvalue("first_name")+"........");

        cat1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MargeActivity.this, ReturnDataActivity.class);
                startActivity(i);
            }
        });
        sub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MargeActivity.this, ResultSubActivity.class);
                startActivity(i);
            }
        });
        product1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MargeActivity.this, ProductFileActivity.class);
                startActivity(i);
            }
        });
        ceta1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MargeActivity.this, CetaLockActivity.class);
                startActivity(i);
            }
        });
    }
}