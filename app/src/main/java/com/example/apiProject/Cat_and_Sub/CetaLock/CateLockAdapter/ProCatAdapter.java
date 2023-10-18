package com.example.apiProject.Cat_and_Sub.CetaLock.CateLockAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apiProject.Cat_and_Sub.Product.ResponseProduct.ProductCategoryListResponse;
import com.example.apiProject.R;

import java.util.List;

public class ProCatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    Context context;
    List<ProductCategoryListResponse.Product> productList;

    public ProCatAdapter(Context context, List<ProductCategoryListResponse.Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.productcat_item,parent,false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView txPro;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            txPro=itemView.findViewById(R.id.txPro);
        }
    }
}
