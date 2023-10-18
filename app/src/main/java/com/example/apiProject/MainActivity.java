package com.example.apiProject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.apiProject.Cat_and_Sub.MargeActivity;
import com.example.apiProject.Cat_and_Sub.Product.ActivityAddProductDetails;
import com.example.apiProject.Cat_and_Sub.Product.ProductFileActivity;
import com.example.apiProject.Cat_and_Sub.User.UserFlashActivity;

public class MainActivity extends AppCompatActivity {

    Button btn1,btn2,btn3;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1=findViewById(R.id.btn1);
        btn2=findViewById(R.id.btn2);
     //   btn3=findViewById(R.id.btn3);



        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, MargeActivity.class);
                startActivity(i);
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, UserFlashActivity.class);
                startActivity(i);
            }
        });
       /* btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, ProductFileActivity.class);
                startActivity(i);
            }
        });*/
    }
}