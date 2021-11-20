package com.example.basicapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ViewHolder> {
    Context context;
    List<AssetModel> asset_list;

    public AssetAdapter(Context context, List<AssetModel> asset_list) {
        this.context = context;
        this.asset_list = asset_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent,false);

        //View view= LayoutInflater.from(context).inflate(R.layout.item_layout, parent,false);
        //return new ViewHolder(view);

        // click listener here
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (asset_list != null && asset_list.size() > 0){
            AssetModel assetModel = asset_list.get(position);
            holder.koderuangan_tv.setText(assetModel.getKodeRuangan());
            holder.assetid_tv.setText(assetModel.getAssetid());
            holder.barcode_tv.setText(assetModel.getBarcode());
            holder.assetname_tv.setText(assetModel.getAssetname());
            holder.tahun_tv.setText(assetModel.getTahunperolehan());
        }
    }

    @Override
    public int getItemCount() {
        return asset_list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView koderuangan_tv, assetid_tv, barcode_tv, assetname_tv, tahun_tv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            koderuangan_tv=itemView.findViewById(R.id.tv_koderuangan);
            assetid_tv=itemView.findViewById(R.id.tv_assetid);
            barcode_tv=itemView.findViewById(R.id.tv_barcode);
            assetname_tv=itemView.findViewById(R.id.tv_assetname);
            tahun_tv=itemView.findViewById(R.id.tv_tahun);
        }
    }
}
