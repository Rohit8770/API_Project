package com.example.apiProject.Cat_and_Sub.CetaLock.CateLockAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apiProject.Cat_and_Sub.Category.networkResponse.CatagoryListResponse;
import com.example.apiProject.Cat_and_Sub.Sub_Category.SubNetworkRespose.SubCatagoryListResponse;
import com.example.apiProject.R;

import java.util.List;

public class CatAdapter extends RecyclerView.Adapter<CatAdapter.CategoryViewHolder>{

    Context context;
    List<CatagoryListResponse.Category> categoryList;
    List<SubCatagoryListResponse.SubCategory> subCategoryList;

    public CatAdapter(Context context, List<CatagoryListResponse.Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.categorycat_item,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CatagoryListResponse.Category category=categoryList.get(position);

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView txCategory;
        RecyclerView SubCatRecyclerView;


        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);


            }
        }

}
