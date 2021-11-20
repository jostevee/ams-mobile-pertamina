package com.example.basicapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AssetConfirmAdapter extends RecyclerView.Adapter<AssetConfirmAdapter.ViewHolder> {
    Context context;
    List<AssetConfirmModel> list;

    public AssetConfirmAdapter(Context context, List<AssetConfirmModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_confirm_layout, parent, false);
        //LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        //view = layoutInflater.inflate(R.layout.item_confirm_layout, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list != null && list.size() > 0){
            AssetConfirmModel assetModel = list.get(position);
            holder.assetid_tv.setText(assetModel.getAssetId());
            holder.assetname_tv.setText(assetModel.getAssetName());
            holder.unit_tv.setText(assetModel.getKodeUnit());
            holder.ruangan_tv.setText(assetModel.getKodeRuangan());
            holder.keterangan_tv.setText(assetModel.getKeterangan());
            holder.status_tv.setText(assetModel.getStatus());
            if (assetModel.getStatus().equalsIgnoreCase("Compared")) {
                holder.status_tv.setTextColor(Color.parseColor("#8BC34A"));
                holder.status_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else {
                holder.status_tv.setTextColor(Color.parseColor("#F44336"));
                holder.status_tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                //holder.status_tv.setTextColor(Color.parseColor("red"));
                //holder.status_tv.setBackgroundColor(Color.parseColor("red"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView assetid_tv, assetname_tv, keterangan_tv, unit_tv, ruangan_tv, status_tv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            assetid_tv=itemView.findViewById(R.id.tvc_assetid);
            assetname_tv=itemView.findViewById(R.id.tvc_assetname);
            unit_tv=itemView.findViewById(R.id.tvc_kodeunit);
            ruangan_tv=itemView.findViewById(R.id.tvc_koderuangan);
            keterangan_tv=itemView.findViewById(R.id.tvc_keterangan);
            status_tv=itemView.findViewById(R.id.tvc_status);
        }
    }
}
