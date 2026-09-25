package com.gu3sswe4k.app.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gu3sswe4k.app.R;
import com.gu3sswe4k.app.models.LabItem;

import java.util.ArrayList;
import java.util.List;

public class LabAdapter extends RecyclerView.Adapter<LabAdapter.LabViewHolder> {

    public interface OnLabClickListener {
        void onLabClick(LabItem lab);
    }

    private List<LabItem> labs = new ArrayList<>();
    private final OnLabClickListener listener;

    public LabAdapter(OnLabClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<LabItem> newLabs) {
        this.labs = newLabs;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lab, parent, false);
        return new LabViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabViewHolder holder, int position) {
        LabItem lab = labs.get(position);

        holder.tvId.setText(lab.id);
        holder.tvTitle.setText(lab.title);
        holder.tvCategory.setText(lab.category);
        holder.tvSeverityChip.setText(lab.severity.label);

        int color = Color.parseColor(lab.severity.colorHex);
        holder.severityBar.setBackgroundColor(color);
        holder.tvSeverityChip.setBackgroundColor(color);

        holder.itemView.setOnClickListener(v -> listener.onLabClick(lab));
    }

    @Override
    public int getItemCount() {
        return labs.size();
    }

    static class LabViewHolder extends RecyclerView.ViewHolder {
        View severityBar;
        TextView tvId, tvTitle, tvCategory, tvSeverityChip;

        LabViewHolder(@NonNull View itemView) {
            super(itemView);
            severityBar = itemView.findViewById(R.id.severity_bar);
            tvId = itemView.findViewById(R.id.tv_lab_id);
            tvTitle = itemView.findViewById(R.id.tv_lab_title);
            tvCategory = itemView.findViewById(R.id.tv_lab_category);
            tvSeverityChip = itemView.findViewById(R.id.tv_severity_chip);
        }
    }
}
