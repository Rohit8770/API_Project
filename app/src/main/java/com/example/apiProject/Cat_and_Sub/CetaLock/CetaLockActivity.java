package com.example.apiProject.Cat_and_Sub.CetaLock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apiProject.Cat_and_Sub.Category.networkResponse.CatagoryListResponse;
import com.example.apiProject.Cat_and_Sub.Category.networkResponse.CommonResponse;
import com.example.apiProject.Cat_and_Sub.CetaLock.CateLockAdapter.CatAdapter;
import com.example.apiProject.Cat_and_Sub.User.UserNetwork.SharedPreference;
import com.example.apiProject.Cat_and_Sub.network.RestClient;
import com.example.apiProject.Cat_and_Sub.network.Restcall;
import com.example.apiProject.R;
import com.example.apiProject.Utils.VariableBag;

import java.util.List;

import rx.Subscriber;
import rx.schedulers.Schedulers;

public class CetaLockActivity extends AppCompatActivity {

    TextView txt;
    RecyclerView CategoryRecyclerView;

    private CatAdapter catAdapter;
    private Restcall restcall;
    SharedPreference sharedPreference;
    List<CatagoryListResponse.Category> categoryList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ceta_lock);

        txt = findViewById(R.id.txt);
        CategoryRecyclerView = findViewById(R.id.CategoryRecyclerView);

        sharedPreference = new SharedPreference(this);
        restcall = RestClient.createService(Restcall.class, VariableBag.BASE_URL, VariableBag.API_KEY);

        CategoryRecyclerView.setLayoutManager(new LinearLayoutManager(CetaLockActivity.this));

    }

    protected void onResume() {
        super.onResume();
        GetCategoryCall();

    }


    private void GetCategoryCall() {
        restcall.getCategory("getCategory", sharedPreference.getStringvalue("user_id"))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<CatagoryListResponse>() {
                    @Override
                    public void onCompleted() {
                        // This is called when the data is retrieved successfully
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(CetaLockActivity.this, "No internet", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNext(CatagoryListResponse catagoryListResponse) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (catagoryListResponse.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE) && catagoryListResponse.getCategoryList() != null
                                        && catagoryListResponse.getCategoryList().size() > 0) {

                                    // Display categories in your RecyclerView
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(CetaLockActivity.this);
                                    CategoryRecyclerView.setLayoutManager(layoutManager);
                                    catAdapter = new CatAdapter(CetaLockActivity.this, catagoryListResponse.getCategoryList());
                                    CategoryRecyclerView.setAdapter(catAdapter);

                                    // After displaying categories, you can make the GetCatalog API call
                                    // For example, if you want to get the catalog for the first category:
                                    if (catagoryListResponse.getCategoryList().size() > 0) {
                                        String categoryId = catagoryListResponse.getCategoryList().get(0).getCategoryId();
                                        // Make the GetCatalog API call
                                        CallGetCatalog(categoryId);
                                    }
                                } else {
                                    Toast.makeText(CetaLockActivity.this, catagoryListResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
    }

    private void CallGetCatalog(String categoryId) {
        // Make the GetCatalog API call using Retrofit
        restcall.GetCatalog("GetCatalog", sharedPreference.getStringvalue("user_id"), categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<CommonResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        // This method is called if there's an error in the network request
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Handle network errors, e.g., display an error message
                                // For example, you can show a Toast with an error message
                                Toast.makeText(CetaLockActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNext(CommonResponse commonResponse) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (commonResponse.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE)) {
                                    // The API request was successful, and you can use the data in commonResponse
                                } else {
                                    // Handle the case where the API request did not succeed
                                    // You can show an error message or perform other error handling here
                                }
                            }
                        });
                    }
                });
    }


}