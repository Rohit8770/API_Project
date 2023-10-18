package com.example.apiProject.Cat_and_Sub.Product;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.apiProject.Cat_and_Sub.Product.ResponseProduct.ProductCategoryListResponse;
import com.example.apiProject.R;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    List<ProductCategoryListResponse.Product> productListResponces;
    List<ProductCategoryListResponse.Product> Searchlist;
    Context context;
    ProductClick productClick;

    public interface ProductClick{
        void DeleteClick(ProductCategoryListResponse.Product product);
        void EditClick(ProductCategoryListResponse.Product product);
    }

    public void SetUpInterface(ProductClick productClick){
        this.productClick= productClick;
    }

    public ProductAdapter(List<ProductCategoryListResponse.Product> productListResponces,Context context) {
        this.productListResponces = productListResponces;
        this.Searchlist =productListResponces;
        this.context = context;
    }
    public void setData(List<ProductCategoryListResponse.Product> productListResponces) {
        this.productListResponces = productListResponces;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.product_item_file, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ProductCategoryListResponse.Product product = Searchlist.get(position);
        try {
        Glide.with(context)
                        .load(productListResponces.get(position).getProductImage())
                        .placeholder(R.drawable.background)
                        .error(R.drawable.ic_launcher_foreground)
                        .into(holder.imgProduct);
        } catch (Exception e) {
            e.printStackTrace();
        }



        holder.txProName.setText(product.getProductName());
        holder.txPrice.setText(product.getProductPrice());
        holder.txDescription.setText(product.getProductDesc());
        holder.txVeg.setText(product.getIsVeg());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Toast.makeText(context, "Edit Clicked", Toast.LENGTH_SHORT).show();
                productClick.EditClick(productListResponces.get(position));
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productClick !=null){
                    productClick.DeleteClick(product);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return Searchlist.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct, btnEdit, btnDelete;
        TextView txProName, txPrice, txDescription, txVeg;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txProName = itemView.findViewById(R.id.txProName);
            txPrice = itemView.findViewById(R.id.txPrice);
            txDescription = itemView.findViewById(R.id.txDescription);
            txVeg = itemView.findViewById(R.id.txVeg);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    public void Search(CharSequence charSequence, RecyclerView rcv) {
        try {
            String charString = charSequence.toString().toLowerCase().trim();
            if (charString.isEmpty()) {
                Searchlist = productListResponces;
                rcv.setVisibility(View.VISIBLE);
            } else {
                int flag = 0;
                List<ProductCategoryListResponse.Product> filterlist = new ArrayList<>();
                for (ProductCategoryListResponse.Product Row : productListResponces) {
                    if (Row.getProductName().toString().toLowerCase().contains(charString.toLowerCase())) {
                        filterlist.add(Row);
                        flag = 1;
                    }
                }
                if (flag == 1) {
                    Searchlist = filterlist;
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
