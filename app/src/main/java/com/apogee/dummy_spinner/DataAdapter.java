package com.apogee.dummy_spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    private List<DataModel> dataList;
    private Context context;

    public DataAdapter(List<DataModel> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataModel data = dataList.get(position);
        holder.bind(data);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView column_name, column_value;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            column_name = itemView.findViewById(R.id.column_name);
            column_value = itemView.findViewById(R.id.column_value);
        }

        public void bind(DataModel data) {
//            column_name.setText("ID: " + data.getId() + "\nName: " + data.getName() + "\nAge: " + data.getAge());
            column_name.setText(data.getColumn_name());
            column_value.setText(data.getColumn_value());

        }
    }
}
