package com.example.apiProject.Cat_and_Sub.Sub_Category.Outpotsub2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.apiProject.Cat_and_Sub.Category.Output.AddActivity;
import com.example.apiProject.Cat_and_Sub.Category.Output.ReturnDataActivity;
import com.example.apiProject.Cat_and_Sub.Category.networkResponse.CatagoryListResponse;
import com.example.apiProject.Cat_and_Sub.Sub_Category.SubNetworkRespose.SubCatagoryListResponse;
import com.example.apiProject.Cat_and_Sub.Sub_Category.SubNetworkRespose.SubCommonResponse;
import com.example.apiProject.Cat_and_Sub.User.UserNetwork.SharedPreference;
import com.example.apiProject.Cat_and_Sub.network.Restcall;
import com.example.apiProject.Cat_and_Sub.network.RestClient;
import com.example.apiProject.R;
import com.example.apiProject.Utils.VariableBag;

import java.util.ArrayList;
import java.util.List;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ResultSubActivity extends AppCompatActivity {

    EditText subsearchbar;
    AppCompatSpinner subspinnerDropdown;
    RecyclerView subrcv;
    Button subbtnpluse;
    Restcall restCall;
    int selectedPos = 0;

    SwipeRefreshLayout subSwap;


    SharedPreference sharedPreference;
    SubAdapter subAdapter;
    String selectedCategoryId;
    String selectedCategoryName;
    List<CatagoryListResponse.Category> categoryListResponceList;

    @SuppressLint("MissingInflatedId")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        subbtnpluse = findViewById(R.id.subbtnpluse);
        subrcv = findViewById(R.id.subrcv);
        subSwap = findViewById(R.id.subSwap);
        subsearchbar = findViewById(R.id.subsearchbar);
        subspinnerDropdown = findViewById(R.id.subspinnerDropdown);
        sharedPreference = new SharedPreference(this);

        restCall = RestClient.createService(Restcall.class, VariableBag.BASE_URL, VariableBag.API_KEY);
        subrcv.setLayoutManager(new LinearLayoutManager(ResultSubActivity.this));
        subAdapter = new SubAdapter(ResultSubActivity.this, new ArrayList<>());

        GetCatogary();

        subSwap.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                GetSubCategory();
            }
        });

        subbtnpluse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResultSubActivity.this, SubAddActivity.class);
                startActivity(intent);
            }
        });

        subsearchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                subAdapter.Search(charSequence,subrcv);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }




    public void GetCatogary(){
        restCall.getCategory("getCategory",sharedPreference.getStringvalue("user_id"))
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
                                Toast.makeText(ResultSubActivity.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                    @Override
                    public void onNext(CatagoryListResponse catagoryListResponse) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(catagoryListResponse.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE)
                                        &&catagoryListResponse.getCategoryList()!=null
                                        &&catagoryListResponse.getCategoryList().size()>0){


                                    List<CatagoryListResponse.Category> activeCateGories= catagoryListResponse.getCategoryList();
                                    String[] categoryNameArray = new String[activeCateGories.size() + 1];
                                    String[] categoryIdArray = new String[activeCateGories.size() + 1];

                                    categoryNameArray[0] = "Select Category";
                                    categoryIdArray[0] = "-1";

                                    for (int i = 0; i < activeCateGories.size(); i++)
                                    {

                                        categoryNameArray[i + 1] = activeCateGories.get(i).getCategoryName();
                                        categoryIdArray[i + 1] = activeCateGories.get(i).getCategoryId();
                                    }

                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ResultSubActivity.this,
                                            android.R.layout.simple_spinner_dropdown_item, categoryNameArray);

                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    subspinnerDropdown.setAdapter(arrayAdapter);

                                    subspinnerDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                            selectedPos = position;
                                            if (selectedPos >= 0 && selectedPos < categoryIdArray.length) {
                                                selectedCategoryId = categoryIdArray[selectedPos];
                                                selectedCategoryName = categoryNameArray[selectedPos];

                                                GetSubCategory();
                                            }
                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });

                                }
                                subAdapter = new SubAdapter(ResultSubActivity.this, new ArrayList<>());




                            }
                        });
                    }
                });
    }
    public void GetSubCategory() {
        if (selectedCategoryId == null || selectedCategoryId.equals("-1")) {
            subAdapter.updateDate(new ArrayList<>());
        } else {
            restCall.getSubCategory("getSubCategory", selectedCategoryId, sharedPreference.getStringvalue("user_id"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<SubCatagoryListResponse>() {
                        @Override
                        public void onCompleted() {

                        }
                        @Override
                        public void onError(Throwable e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("##", e.getLocalizedMessage());
                                    Toast.makeText(ResultSubActivity.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                        }

                        @Override
                        public void onNext(SubCatagoryListResponse subCatagoryListResponse) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (subCatagoryListResponse != null
                                            && subCatagoryListResponse.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE)
                                            && subCatagoryListResponse.getSubCategoryList() != null
                                            && subCatagoryListResponse.getSubCategoryList().size() > 0) {

                                        subAdapter.updateDate(subCatagoryListResponse.getSubCategoryList());

                                        subAdapter = new SubAdapter(ResultSubActivity.this, subCatagoryListResponse.getSubCategoryList());


                                        LinearLayoutManager layoutManager = new LinearLayoutManager(ResultSubActivity.this);
                                        subrcv.setLayoutManager(layoutManager);
                                        subAdapter = new SubAdapter(ResultSubActivity.this, subCatagoryListResponse.getSubCategoryList());
                                        subrcv.setAdapter(subAdapter);


                                        subAdapter.SetUpInterface(new SubAdapter.SubCatagoryClick() {

                                            public void SubEditClick(SubCatagoryListResponse.SubCategory subCategory) {
                                                String categoryId = subCategory.getCategoryId();
                                                String selectedSubCategoryId =subCategory.getSubcategoryName();
                                                String subCategoryName =subCategory.getSubcategoryName();

                                                Intent i = new Intent(ResultSubActivity.this, SubAddActivity.class);
                                                Bundle bundle =new Bundle();

                                                bundle.putString("category_id", categoryId);
                                                bundle.putString("subCategoryId", selectedSubCategoryId);
                                                bundle.putString("subcategoryName", subCategoryName);
                                                i.putExtras(bundle);
                                                startActivity(i);
                                            }
                                            @Override
                                            public void DeleteSubCategory(SubCatagoryListResponse.SubCategory subCategory) {

                                                String subCategoryId = subCategory.getSubCategoryId();

                                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResultSubActivity.this);
                                                alertDialog.setTitle("Alert");
                                                alertDialog.setMessage("Are you sure, you want to delete" + subCategoryId + "->" + sharedPreference.getStringvalue("used-id"));

                                                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        DeleteClick1(subCategoryId);
                                                        dialog.dismiss();
                                                    }
                                                });
                                                alertDialog.show();
                                                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                });
                                            }
                                        });


                                    } else {

                                        subAdapter.updateDate(new ArrayList<>());
                                    }
                                }
                            });

                        }
                    });


        }
    }
    public void DeleteClick1(String subCategoryId) {
        restCall.DeleteSubCategory("DeleteSubCategory", sharedPreference.getStringvalue("user_id"), subCategoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<SubCommonResponse>() {
                    @Override
                    public void onCompleted() {
                        // Handle completion if needed.
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Handle the error.
                        Log.e("DeleteSubCategory Error", e.getMessage());
                        Toast.makeText(ResultSubActivity.this, "Error while deleting the product", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(SubCommonResponse commonResponse) {
                        if (commonResponse != null) {
                            if (commonResponse.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE)) {
                                Toast.makeText(ResultSubActivity.this, "Subcategory deleted", Toast.LENGTH_SHORT).show();
                                GetSubCategory();
                            } else {
                                Log.e("DeleteSubCategory Error", commonResponse.getMessage());
                                Toast.makeText(ResultSubActivity.this, "Failed to delete: " + commonResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }




}
































