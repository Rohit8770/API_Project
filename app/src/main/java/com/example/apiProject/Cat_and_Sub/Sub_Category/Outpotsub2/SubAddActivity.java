package com.example.apiProject.Cat_and_Sub.Sub_Category.Outpotsub2;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apiProject.Cat_and_Sub.Category.networkResponse.CatagoryListResponse;
import com.example.apiProject.Cat_and_Sub.Sub_Category.SubNetworkRespose.SubCommonResponse;
import com.example.apiProject.Cat_and_Sub.User.UserNetwork.SharedPreference;
import com.example.apiProject.Cat_and_Sub.network.RestClient;
import com.example.apiProject.Cat_and_Sub.network.Restcall;
import com.example.apiProject.R;
import com.example.apiProject.Utils.Tools;
import com.example.apiProject.Utils.VariableBag;

import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SubAddActivity extends AppCompatActivity {

    EditText etName1;
    Button btnAddon;
    Restcall restcall;
    AppCompatSpinner spinnerAdd;
    String selectedCategoryId,selectedSubCategoryId;
    String selectedCategoryName;
    int selectedpos = 0;
    Tools tools;
    boolean isEditMode = false;
    SharedPreference sharedPreference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_add);
        etName1=findViewById(R.id.etvName1);
        btnAddon=findViewById(R.id.btnAddon);
        spinnerAdd=findViewById(R.id.spinnerAdd);
        sharedPreference=new SharedPreference(this);
        tools=new Tools();
        restcall= RestClient.createService(Restcall.class, VariableBag.BASE_URL, VariableBag.API_KEY);
        GetCategory();


        Bundle bundle =getIntent().getExtras();
        if (bundle !=null && bundle.getString("categoryId")!=null){

            isEditMode =true;
            selectedCategoryId=bundle.getString("category_id");
            selectedSubCategoryId=bundle.getString("selectedSubCategoryId");
            selectedCategoryName=bundle.getString("selectedCategoryName");
            btnAddon.setText("Edit");
            etName1.setText(selectedCategoryName);
            subCategoryEdit();
        }else {
            isEditMode =false;
            btnAddon.setText("");
            AddSubCategory(selectedCategoryId,etName1.getText().toString().trim());
        }


        btnAddon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (spinnerAdd!=null && etName1.getText().toString().trim().equalsIgnoreCase("")){
                    etName1.setError("Enter sub category name");
                    etName1.requestFocus();
                }else{
                    if (isEditMode){
                        subCategoryEdit();
                    } else{
                        AddSubCategory(selectedCategoryId,etName1.getText().toString().trim());
                    }
                }
            }
        });
    }
    public void AddSubCategory (String category_id,String subcategory_name) {
        restcall.AddSubCategory("AddSubCategory",category_id,subcategory_name,sharedPreference.getStringvalue("user_id"))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<SubCommonResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("API Error", e.getMessage());
                                Toast.makeText(SubAddActivity.this, "This is Wrong Tag", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNext(SubCommonResponse subCommonResponse) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (subCommonResponse.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE)){
                                    etName1.setText("");
                                    finish();
                                }
                                Toast.makeText(SubAddActivity.this, ""+subCommonResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

    }

    public void GetCategory(){
        restcall.getCategory("getCategory",sharedPreference.getStringvalue("user_id"))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CatagoryListResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("##",e.getLocalizedMessage());
                                Toast.makeText(SubAddActivity.this, "" +e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNext(CatagoryListResponse catagoryListResponse) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (catagoryListResponse.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE)
                                        && catagoryListResponse.getCategoryList()!=null
                                        && catagoryListResponse.getCategoryList().size()>0){

                                    List<CatagoryListResponse.Category> activeCateGories =catagoryListResponse.getCategoryList();
                                    String[] categoryNameArray=new String[activeCateGories.size() +1];
                                    String[] categoryIdArray=new String[activeCateGories.size() +1];

                                    categoryNameArray[0] = "Select Category";
                                    categoryIdArray[0] ="-1";

                                    for (int i =0; i< activeCateGories.size(); i++){
                                        categoryNameArray[i + 1] =activeCateGories.get(i).getCategoryName();
                                        categoryIdArray[i + 1] =activeCateGories.get(i).getCategoryId();
                                    }
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(SubAddActivity.this,
                                            android.R.layout.simple_spinner_item,categoryNameArray);


                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    spinnerAdd.setAdapter(arrayAdapter);

                                    spinnerAdd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            selectedpos=position;
                                            if (selectedpos >=0 && selectedpos < categoryIdArray.length){
                                                selectedCategoryId = categoryIdArray[selectedpos];
                                                //     selectedCategoryName = categoryNameArray[selectedpos];

                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });
                                }
                                Toast.makeText(SubAddActivity.this, ""+catagoryListResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });


    }

    public  void subCategoryEdit(){
        tools.showLoading();
        restcall.EditSubCategory("EditSubCategory", selectedCategoryId,etName1.getText().toString(),selectedSubCategoryId, sharedPreference.getStringvalue("user_id"))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<SubCommonResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SubAddActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNext(SubCommonResponse commonSubCategoryResponse) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SubAddActivity.this, commonSubCategoryResponse.getMessage(), Toast.LENGTH_SHORT).show();

                                if (commonSubCategoryResponse.getStatus().equals(VariableBag.SUCCESS_CODE)){
                                    etName1.setText("");
                                    startActivity(new Intent(SubAddActivity.this, ResultSubActivity.class));
                                    finish();
                                }
                                else {
                                    Toast.makeText(SubAddActivity.this, "Not able to edit", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

}
