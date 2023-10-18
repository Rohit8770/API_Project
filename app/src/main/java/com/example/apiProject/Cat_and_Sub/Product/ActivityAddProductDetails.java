package com.example.apiProject.Cat_and_Sub.Product;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import android.Manifest;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.apiProject.Cat_and_Sub.Category.networkResponse.CatagoryListResponse;
import com.example.apiProject.Cat_and_Sub.Category.networkResponse.CommonResponse;
import com.example.apiProject.Cat_and_Sub.Sub_Category.SubNetworkRespose.SubCatagoryListResponse;
import com.example.apiProject.Utils.Tools;
import com.example.apiProject.Cat_and_Sub.User.UserNetwork.SharedPreference;
import com.example.apiProject.Cat_and_Sub.network.RestClient;
import com.example.apiProject.Cat_and_Sub.network.Restcall;

//import com.example.apiProject.Manifest;
import com.example.apiProject.R;
import com.example.apiProject.Utils.VariableBag;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ActivityAddProductDetails extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 101;
    String currentPhotoPath = "";
    AppCompatSpinner CatProSpinner,SubProSpinner;
    private File currentPhotoFile;
    ActivityResultLauncher<Intent> cameraLauncher = null;
    TextInputEditText etNamePro, etPrice, etDesc;
    Button btnCancel, btnSubmit;
    ImageView imageC, imgEdit;
    SwitchCompat switchVeg;
    Tools tools;
    ProductAdapter productAdapter;
    String selectedCategoryId,selectedSubCategoryId,selectedCategoryName,selectedSubCategoryName;
    int selectedPos = 0;
    boolean isEdit =false;
    int selectedsubPos=0;
    SharedPreference sharedPreference;
    Restcall restcall;
    String categoryId,subCategoryId,productId,productName,productPrice,oldImageProduct,productDesc,isVeg,productImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_details);

        etNamePro = findViewById(R.id.etNamePro);
        etPrice = findViewById(R.id.etPrice);
        etDesc = findViewById(R.id.etDesc);
        btnCancel = findViewById(R.id.btnCancel);
        btnSubmit = findViewById(R.id.btnSubmit);
        imageC = findViewById(R.id.imageC);
        imgEdit = findViewById(R.id.imgEdit);
        switchVeg = findViewById(R.id.switchVeg);
        CatProSpinner = findViewById(R.id.CatProSpinner);
        SubProSpinner = findViewById(R.id.SubProSpinner);
        sharedPreference = new SharedPreference(this);
     //   tools=new Tools();
        restcall = RestClient.createService(Restcall.class, VariableBag.BASE_URL, VariableBag.API_KEY);
        GetCatogary();

        btnCancel.setOnClickListener(v -> finish());

        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Tools.displayImage(ActivityAddProductDetails.this, imageC, currentPhotoPath);
            } else {
                Toast.makeText(this, "Not", Toast.LENGTH_SHORT).show();
            }
        });

        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    currentPhotoPath = "";
                    if (checkCameraPermission()) {
                        openCamera();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (getIntent() != null && getIntent().getStringExtra("category_id") != null) {
            isEdit = true;
            selectedCategoryId = getIntent().getStringExtra("category_id");
            selectedCategoryName = getIntent().getStringExtra("category_name");
            etNamePro.setText(productName);
            btnSubmit.setText("Edit");
        } else {
            isEdit = false;
            btnSubmit.setText("Add");
        }

        if (getIntent().getStringExtra("product_Id") != null){

            categoryId = getIntent().getStringExtra("category_Id");
            subCategoryId = getIntent().getStringExtra("subCat_id");
            productId = getIntent().getStringExtra("product_Id");
            productName = getIntent().getStringExtra("getProductName");
            productPrice = getIntent().getStringExtra("getProductPrice");
            productDesc = getIntent().getStringExtra("getProductDesc");
            oldImageProduct = getIntent().getStringExtra("getOldProductImage");
            productImage = getIntent().getStringExtra("getProductImage");
            isVeg = getIntent().getStringExtra("getIsVeg");

            Glide.with(this)
                    .load(productImage)
                    .placeholder(R.drawable.hourglass_wait)
                    .into(imgEdit);
            btnSubmit.setText("EDIT");
            etNamePro.setText(productName);
            etPrice.setText(productPrice);
            etDesc.setText(productDesc);
            isEdit = true;
        }else{
            isEdit = false;
            btnSubmit.setText("Submit");
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  SubProSpinner !=null &&

                if (etNamePro.getText().toString().trim().equalsIgnoreCase("")) {
                    etNamePro.setError("Enter product name");
                    etNamePro.requestFocus();

                } else if (etPrice.getText().toString().trim().equalsIgnoreCase("")) {
                    etPrice.setError("Enter product name");
                    etPrice.requestFocus();

                } else if (etDesc.getText().toString().trim().equalsIgnoreCase("")) {
                    etDesc.setError("Enter product name");
                    etDesc.requestFocus();

                }else {
                    if (isEdit) {
                        EditProduct();
                    } else {
                        AddProduct();
                    }
                }
            }
        });
            }
    private boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            return false;
        }
        return true;
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.apiProject",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                cameraLauncher.launch(takePictureIntent);
            }
        }
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoFile=image;
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void AddProduct() {
      //  tools.showLoading();
        RequestBody tag = RequestBody.create(MediaType.parse("text/plain"), "AddProduct");
        RequestBody rbCategory_id = RequestBody.create(MediaType.parse("text/plain"), selectedCategoryId);
        RequestBody rbSub_category_id = RequestBody.create(MediaType.parse("text/plain"), selectedSubCategoryId);
        RequestBody rbProduct_name = RequestBody.create(MediaType.parse("text/plain"), etNamePro.getText().toString().trim());
        RequestBody rbProduct_price = RequestBody.create(MediaType.parse("text/plain"), etPrice.getText().toString().trim());
        RequestBody rbProduct_desc = RequestBody.create(MediaType.parse("text/plain"), etDesc.getText().toString().trim());
        RequestBody rbIs_veg = RequestBody.create(MediaType.parse("text/plain"), switchVeg.isChecked() ? "1": "0");
        RequestBody rbUser_id = RequestBody.create(MediaType.parse("text/plain"), sharedPreference.getStringvalue("user_id"));
        MultipartBody.Part fileToUploadfile = null;


        if (fileToUploadfile == null && currentPhotoPath != "") {
            try {
                StrictMode.VmPolicy.Builder builder2 = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder2.build());
                File file = new File(currentPhotoPath);
                RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form_data"), file);
                fileToUploadfile = MultipartBody.Part.createFormData("product_image", file.getName(), requestBody);
            } catch (Exception e) {
                Toast.makeText(this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            restcall.AddProduct(tag, rbCategory_id, rbSub_category_id, rbProduct_name,fileToUploadfile, rbProduct_price,
                            rbProduct_desc, rbIs_veg, rbUser_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.newThread())
                    .subscribe(new Subscriber<CommonResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("##","run" +e.getLocalizedMessage());
                                    Toast.makeText(ActivityAddProductDetails.this, "Error while adding product", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                        @Override
                        public void onNext(CommonResponse commonResponse) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ActivityAddProductDetails.this, "" + commonResponse.getMessage(), Toast.LENGTH_SHORT).show();

                                    if (commonResponse.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE)) {
                                        if (currentPhotoFile != null && currentPhotoPath != null)
                                            currentPhotoFile.delete();
                                    }
                                    finish();

                                }
                            });
                        }
                    });
        }
    }
        public void GetCatogary(){
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
                                    Toast.makeText(ActivityAddProductDetails.this, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                        @Override
                        public void onNext(CatagoryListResponse categoryListResponce) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(categoryListResponce.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE)&&categoryListResponce.getCategoryList()!=null
                                            &&categoryListResponce.getCategoryList().size()>0){

                                        List<CatagoryListResponse.Category> activeCateGories=categoryListResponce.getCategoryList();
                                        String[] categoryNameArray = new String[activeCateGories.size() + 1];
                                        String[] categoryIdArray = new String[activeCateGories.size() + 1];

                                        categoryNameArray[0] = "Select Category";
                                        categoryIdArray[0] = "-1";

                                        for (int i = 0; i < activeCateGories.size(); i++) {

                                            categoryNameArray[i + 1] = activeCateGories.get(i).getCategoryName();
                                            categoryIdArray[i + 1] = activeCateGories.get(i).getCategoryId();
                                        }
                                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(ActivityAddProductDetails.this,
                                                android.R.layout.simple_spinner_dropdown_item, categoryNameArray);

                                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        CatProSpinner.setAdapter(arrayAdapter);

                                        CatProSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                                                selectedPos = position;
                                                if (selectedPos >= 0 && selectedPos < categoryIdArray.length) {
                                                    selectedCategoryId = categoryIdArray[selectedPos];
                                                    selectedCategoryName = categoryNameArray[selectedPos];
                                                    GetSubCategory(selectedCategoryId);

                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });

                                    }
                                    Toast.makeText(ActivityAddProductDetails.this, ""+categoryListResponce.getMessage(), Toast.LENGTH_SHORT).show();




                                }
                            });
                        }
                    });


        }

        public void GetSubCategory(String selectedCategoryId) {
            restcall.getSubCategory("getSubCategory", selectedCategoryId,sharedPreference.getStringvalue("user_id"))
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
                                    Log.e("##", e.getLocalizedMessage());
                                    Toast.makeText(ActivityAddProductDetails.this, "" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

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

                                        List<SubCatagoryListResponse.SubCategory> subactiveCateGories=subCategoryListResponce.getSubCategoryList();
                                        String[] subcategoryNameArray = new String[subactiveCateGories.size() + 1];
                                        String[] subcategoryIdArray = new String[subactiveCateGories.size() + 1];

                                        subcategoryNameArray[0] = "Select SubCategory";
                                        subcategoryIdArray[0] = "-1";
                                        for (int i = 0; i < subactiveCateGories.size(); i++) {

                                            subcategoryNameArray[i + 1] = subactiveCateGories.get(i).getSubcategoryName();
                                            subcategoryIdArray[i + 1] = subactiveCateGories.get(i).getSubCategoryId();
                                        }
                                        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(ActivityAddProductDetails.this,
                                                android.R.layout.simple_spinner_dropdown_item, subcategoryNameArray);

                                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        SubProSpinner.setAdapter(arrayAdapter2);
                                        SubProSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int subposition, long id) {
                                                selectedsubPos = subposition;
                                                if (selectedsubPos >= 0 && selectedsubPos < subcategoryIdArray.length) {
                                                    selectedSubCategoryId = subcategoryIdArray[selectedsubPos];
                                                    selectedSubCategoryName = subcategoryNameArray[selectedsubPos];


                                                }
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });



                                    } else {
                                        Toast.makeText(ActivityAddProductDetails.this, ""+subCategoryListResponce.getMessage(), Toast.LENGTH_SHORT).show();


                                    }
                                }
                            });

                        }
                    });

        }



    public  void  EditProduct(){

        restcall.EditProduct("EditProduct",categoryId,subCategoryId,productId,productName,productPrice,oldImageProduct,productDesc,isVeg,productImage,sharedPreference.getStringvalue("user_id"))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<CommonResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityAddProductDetails.this, "Error while editing", Toast.LENGTH_SHORT).show();

                            }
                        });

                    }

                    @Override
                    public void onNext(CommonResponse commonResponce) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ActivityAddProductDetails.this, "Select Category to Edit", Toast.LENGTH_SHORT).show();
                                if (commonResponce.getStatus().equalsIgnoreCase(VariableBag.SUCCESS_CODE)) {
                                    etNamePro.setText("");
                                    etPrice.setText("");
                                    etDesc.setText("");

                                    startActivity(new Intent(ActivityAddProductDetails.this, SubCatagoryListResponse.class));
                                    finish();
                                } else {
                                    Toast.makeText(ActivityAddProductDetails.this, commonResponce.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    }
                });

        }
    }
