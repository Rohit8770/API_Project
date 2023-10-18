package com.example.apiProject.Cat_and_Sub.Product;

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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.apiProject.Cat_and_Sub.Category.Output.AddActivity;
import com.example.apiProject.Cat_and_Sub.Category.Output.ReturnDataActivity;
import com.example.apiProject.Cat_and_Sub.Category.networkResponse.CatagoryListResponse;
import com.example.apiProject.Cat_and_Sub.Category.networkResponse.CommonResponse;
import com.example.apiProject.Cat_and_Sub.Product.ResponseProduct.ProductCategoryListResponse;
import com.example.apiProject.Cat_and_Sub.Sub_Category.SubNetworkRespose.SubCatagoryListResponse;
import com.example.apiProject.Cat_and_Sub.User.UserNetwork.SharedPreference;
import com.example.apiProject.Cat_and_Sub.network.RestClient;
import com.example.apiProject.Cat_and_Sub.network.Restcall;
import com.example.apiProject.R;
import com.example.apiProject.Utils.VariableBag;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ProductFileActivity extends AppCompatActivity {

    FloatingActionButton btnAddition;
    AppCompatSpinner CatSpinner, SubSpinner;
    EditText etSearchProduct;
    RecyclerView rcvProduct;
    SharedPreference sharedPreference;
    Restcall restCall;
    int selectedPos = 0;
    String product_id;
    SwipeRefreshLayout ProSearchbar;
    ProductAdapter productAdapter;
    LinearLayout noDataFoundView;
    int selectedsubPos = 0;
 //   ProductAdapter productAdapter = new ProductAdapter(new ArrayList<>(), ProductFileActivity.this);
    String selectedCategoryId, selectedSubCategoryId,selectedSubCategoryName,selectedCategoryName;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_file);

        btnAddition = findViewById(R.id.btnAddition);
        CatSpinner = findViewById(R.id.CatSpinner);
        SubSpinner = findViewById(R.id.SubSpinner);
        etSearchProduct = findViewById(R.id.etSearchProduct);
        rcvProduct = findViewById(R.id.rcvProduct);
        noDataFoundView=findViewById(R.id.noDataFoundView);
        btnAddition = findViewById(R.id.btnAddition);
        ProSearchbar=findViewById(R.id.ProSearchbar);
        sharedPreference = new SharedPreference(this);

        rcvProduct.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter( new ArrayList<>(),ProductFileActivity.this);
        rcvProduct.setAdapter(productAdapter);

        restCall = RestClient.createService(Restcall.class, VariableBag.BASE_URL, VariableBag.API_KEY);

        GetCatogary();

   //     GetProduct();

       /* ProSearchbar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            }
        });*/


        btnAddition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ActivityAddProductDetails.class);
                startActivity(i);
            }
        });
        etSearchProduct.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                productAdapter.Search(charSequence,rcvProduct);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }



    @Override
    protected void  onResume(){
        super.onResume();
        GetCatogary();
    }

    public void GetCatogary() {
        restCall.getCategory("getCategory", sharedPreference.getStringvalue("user_id"))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<CatagoryListResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                             //   Log.e("##", e.getLocalizedMessage());
                                Toast.makeText(ProductFileActivity.this, "No Internet" , Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                    @Override
                    public void onNext(CatagoryListResponse catagoryListResponse) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (catagoryListResponse.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE) &&
                                        catagoryListResponse.getCategoryList() != null
                                        && catagoryListResponse.getCategoryList().size() > 0) {
                                    List<CatagoryListResponse.Category> categories = catagoryListResponse.getCategoryList();
                                    String[] categoryNameArray = new String[categories.size() + 1];
                                    String[] categoryIdArray = new String[categories.size() + 1];

                                    categoryNameArray[0] = "Select Category";
                                    categoryIdArray[0] = "-1";

                                    for (int i = 0; i < categories.size(); i++) {
                                        categoryNameArray[i + 1] = categories.get(i).getCategoryName();
                                        categoryIdArray[i + 1] = categories.get(i).getCategoryId();
                                    }

                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ProductFileActivity.this,
                                            android.R.layout.simple_spinner_dropdown_item, categoryNameArray);

                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    CatSpinner.setAdapter(arrayAdapter);
                                    CatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                            selectedPos = position;
                                            if (selectedPos >= 0 && selectedPos < categoryIdArray.length) {
                                                selectedCategoryId = categoryIdArray[selectedPos];
                                                selectedCategoryName = categoryNameArray[selectedPos];
                                                if (selectedCategoryId.equalsIgnoreCase("-1")) {
                                                    Toast.makeText(ProductFileActivity.this, "Select Category", Toast.LENGTH_SHORT).show();
                                                }
                                                else {
                                                    GetSubCategory();
                                                }

                                            }
                                        }
                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });

                                }
                           }
                        });
                    }
                });

    }


    public void GetSubCategory() {
        restCall.getSubCategory("getSubCategory", selectedCategoryId,sharedPreference.getStringvalue("user_id"))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<SubCatagoryListResponse>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                              //  Log.e("##", e.getLocalizedMessage());
                                Toast.makeText(ProductFileActivity.this, "No Internet" , Toast.LENGTH_SHORT).show();

                            }
                        });

                    }

                    @Override
                    public void onNext(SubCatagoryListResponse subCategoryListResponce) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (subCategoryListResponce != null
                                        && subCategoryListResponce.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE)
                                        && subCategoryListResponce.getSubCategoryList() != null
                                        && subCategoryListResponce.getSubCategoryList().size() > 0) {

                                    List<SubCatagoryListResponse.SubCategory> subCategories=subCategoryListResponce.getSubCategoryList();
                                    String[] subcategoryNameArray = new String[subCategories.size() + 1];
                                    String[] subcategoryIdArray = new String[subCategories.size() + 1];

                                    subcategoryNameArray[0] = "Select SubCategory";
                                    subcategoryIdArray[0] = "-1";
                                    for (int i = 0; i < subCategories.size(); i++) {

                                        subcategoryNameArray[i + 1] = subCategories.get(i).getSubcategoryName();
                                        subcategoryIdArray[i + 1] = subCategories.get(i).getSubCategoryId();
                                    }
                                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ProductFileActivity.this,
                                            android.R.layout.simple_spinner_dropdown_item, subcategoryNameArray);

                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    SubSpinner.setAdapter(arrayAdapter);

                                    SubSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> adapterView, View view, int subposition, long id) {
                                            selectedPos = subposition;
                                            if (selectedPos >= 0 && selectedPos < subcategoryIdArray.length) {
                                                selectedSubCategoryId = subcategoryIdArray[selectedPos];
                                                selectedSubCategoryName = subcategoryNameArray[selectedPos];
                                                if (selectedCategoryId.equalsIgnoreCase("-1") &&
                                                selectedCategoryId.equalsIgnoreCase("-1")){
                                                    Toast.makeText(ProductFileActivity.this, "select subcategory", Toast.LENGTH_SHORT).show();
                                                }
                                                else{
                                                    GetProduct();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onNothingSelected(AdapterView<?> adapterView) {

                                        }
                                    });

                                } /*else {
                                    Toast.makeText(ProductFileActivity.this, ""+subCategoryListResponce.getMessage(), Toast.LENGTH_SHORT).show();
                                }*/
                            }
                        });

                    }
                });
        //productAdapter=new ProductAdapter( new ArrayList<>(),ProductFileActivity.this);

    }
    public void GetProduct() {
      restCall.getProduct("getProduct", selectedCategoryId, selectedSubCategoryId,sharedPreference.getStringvalue("user_id"))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<ProductCategoryListResponse>() {
                    @Override
                    public void onCompleted() {
                        // Handle completion if needed
                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ProductFileActivity.this, "Error while fetching products: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                              //  Log.e("##", "run: " + e.getLocalizedMessage());
                            }
                        });
                    }

                    @Override
                    public void onNext(ProductCategoryListResponse productCategoryListResponse) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (productCategoryListResponse.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE)
                                        && productCategoryListResponse.getProductList() != null
                                        && productCategoryListResponse.getProductList().size()>0) {

                                    LinearLayoutManager layoutManager= new LinearLayoutManager(ProductFileActivity.this);
                                    rcvProduct.setLayoutManager(layoutManager );
                                    productAdapter = new ProductAdapter(productCategoryListResponse.getProductList(),ProductFileActivity.this);
                                    rcvProduct.setAdapter(productAdapter);


                                    productAdapter.SetUpInterface(new ProductAdapter.ProductClick() {
                                        @Override
                                        public void DeleteClick(ProductCategoryListResponse.Product product) {

                                            String productId =product.getProductId();

                                            AlertDialog.Builder alertDialog =new AlertDialog.Builder(ProductFileActivity.this);
                                            alertDialog.setTitle("Alert!!");
                                            alertDialog.setMessage("Are you sure, you want to delete" +productId +"->" +sharedPreference.getStringvalue("used-id"));

                                            alertDialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                DeleteProduct(productId);
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
                                        @Override
                                        public void EditClick(ProductCategoryListResponse.Product product) {

                                                Intent intent = new Intent(ProductFileActivity.this, ActivityAddProductDetails.class);
                                                intent.putExtra("category_Id", selectedCategoryId);
                                                intent.putExtra("subCat_id", selectedSubCategoryId);
                                                intent.putExtra("product_Id", product.getProductId());
                                                intent.putExtra("getProductName", product.getProductName());
                                                intent.putExtra("getProductPrice", product.getProductPrice());
                                                intent.putExtra("getProductDesc", product.getProductDesc());
                                                intent.putExtra("getOldProductImage", product.getOldProductImage());
                                                intent.putExtra("getIsVeg", product.getIsVeg());
                                                intent.putExtra("getProductImage", product.getProductImage());
                                                startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });
                    }
                });


    }
    public void DeleteProduct(String product_id) {
        restCall.DeleteProduct("DeleteProduct", product_id, sharedPreference.getStringvalue("user_id"))
                .subscribeOn(Schedulers.io())
                .observeOn((Schedulers.newThread()))
                .subscribe(new Subscriber<CommonResponse>() {
                    @Override
                    public void onCompleted() {
                    }
                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ProductFileActivity.this, "Error while deleting the product", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    @Override
                    public void onNext(CommonResponse productCategoryListResponse) {
                    //    tools.stopLoading();
                        if (productCategoryListResponse.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE)) {
                            GetProduct();
                        }
                        Toast.makeText(ProductFileActivity.this, productCategoryListResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
}
