package com.example.apiProject.Cat_and_Sub.Sub_Category.Outpotsub2;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apiProject.Cat_and_Sub.Product.ResponseProduct.ProductCategoryListResponse;
import com.example.apiProject.Cat_and_Sub.Sub_Category.SubNetworkRespose.SubCatagoryListResponse;
import com.example.apiProject.R;

import java.util.ArrayList;
import java.util.List;

public class SubAdapter extends RecyclerView.Adapter<SubAdapter.SubViewHolder> {

    Context context;
    List<SubCatagoryListResponse.SubCategory> subCatagories;
    List<SubCatagoryListResponse.SubCategory> subSearchList;

    SubCatagoryClick subCatagoryClick;

    public interface SubCatagoryClick{
        void DeleteSubCategory(SubCatagoryListResponse.SubCategory subCategory);
        void SubEditClick(SubCatagoryListResponse.SubCategory subCategory);
    }
    public void SetUpInterface(SubCatagoryClick subCatagoryClick){
        this.subCatagoryClick = subCatagoryClick;
    }
    public void updateDate(List<SubCatagoryListResponse.SubCategory> newSubCategoryList){
        subCatagories.clear();
        subCatagories.addAll(newSubCategoryList);
        notifyDataSetChanged();
    }
    public SubAdapter(Context context, List<SubCatagoryListResponse.SubCategory> subCatagories) {
        this.context = context;
        this.subCatagories = subCatagories;
        this.subSearchList = subCatagories;
    }

    @NonNull
    @Override
    public SubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.sub_item,parent,false);
        return new SubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubViewHolder holder, int position) {
        SubCatagoryListResponse.SubCategory subCategory=subSearchList.get(position);
        holder.Textview1.setText(subCategory.getSubCategoryId());
        holder.Textview2.setText(subCategory.getSubcategoryName());
        holder.Textview3.setText(subCategory.getCategoryId());

        holder.btnEdit11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subCatagoryClick.SubEditClick(subCategory);
            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    subCatagoryClick.DeleteSubCategory(subCategory);

            }
        });

    }

    @Override
    public int getItemCount() {
        return subSearchList.size();
    }

    public class SubViewHolder extends RecyclerView.ViewHolder {

        TextView  Textview1,Textview2,Textview3;
        ImageView btnEdit11,btnDelete;

        public SubViewHolder(@NonNull View itemView) {
            super(itemView);
            Textview1=itemView.findViewById(R.id.Textview1);
            Textview2=itemView.findViewById(R.id.Textview2);
            Textview3=itemView.findViewById(R.id.Textview3);
            btnEdit11=itemView.findViewById(R.id.btnEdit11);
            btnDelete=itemView.findViewById(R.id.btnDelete);
        }
    }
    public void Search(CharSequence charSequence, RecyclerView rcv) {
        try {
            String charString = charSequence.toString().toLowerCase().trim();
            if (charString.isEmpty()) {
                subSearchList = subCatagories;
                rcv.setVisibility(View.VISIBLE);
            } else {
                int flag = 0;
                List<SubCatagoryListResponse.SubCategory> filterlist = new ArrayList<>();
                for (SubCatagoryListResponse.SubCategory Row : subCatagories) {
                    if (Row.getSubcategoryName().toString().toLowerCase().contains(charString.toLowerCase())) {
                        filterlist.add(Row);
                        flag = 1;
                    }
                }
                if (flag == 1) {
                    subSearchList = filterlist;
                    rcv.setVisibility(View.VISIBLE);
                } else {
                    rcv.setVisibility(View.GONE);
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
