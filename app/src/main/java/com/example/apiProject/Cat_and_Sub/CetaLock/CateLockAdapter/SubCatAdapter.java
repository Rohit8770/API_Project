package com.example.apiProject.Cat_and_Sub.CetaLock.CateLockAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apiProject.Cat_and_Sub.Product.ResponseProduct.ProductCategoryListResponse;
import com.example.apiProject.Cat_and_Sub.Sub_Category.SubNetworkRespose.SubCatagoryListResponse;
import com.example.apiProject.R;

import java.util.List;

public class SubCatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<SubCatagoryListResponse.SubCategory> subCategoryList;
    List<ProductCategoryListResponse.Product> productList;

    public SubCatAdapter(Context context, List<SubCatagoryListResponse.SubCategory> subCategoryList) {
        this.context = context;
        this.subCategoryList = subCategoryList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.subcategorycat_item,parent,false);
        return new SubCateLockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return subCategoryList.size();
    }

    public class SubCateLockViewHolder extends RecyclerView.ViewHolder {
        TextView txSubCategory;
        RecyclerView ProCatRecyclerView;
        public SubCateLockViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
