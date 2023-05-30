package com.apogee.dummy_spinner;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;

import java.util.List;

public class ColumnDataAdapter extends RecyclerView.Adapter<ColumnDataAdapter.ViewHolder> {
    private List<ColumnData> columnDataList;
    private List<String> subTypeList;
    Context context;

    public ColumnDataAdapter(List<ColumnData> columnDataList, Context context, List<String> subTypeList) {
        this.columnDataList = columnDataList;
        this.subTypeList = subTypeList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_complete_data, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ColumnData columnData = columnDataList.get(position);
        holder.columnNameTextView.setText(columnData.getColumnName());

        String value = columnData.getColumnValue();

        String subtypeListValue = subTypeList.get(position);

      if ((subtypeListValue.equals("IMAGE") || subtypeListValue.equals("PICTURE") ) && value.contains("[")) {

          holder.columnValueTextView.setVisibility(View.GONE);

            holder.img.setVisibility(View.VISIBLE);
          holder.pdfView.setVisibility(View.GONE);


            byte[] imageByteArray = convertStringToByteArray(value); // your byte array here

            Bitmap bmp = convertByteArrayToBitmap(imageByteArray);

            holder.img.setImageBitmap(bmp);


        } else if((subtypeListValue.equals("PDF") || subtypeListValue.equals("EXCEL") ) && value.contains("[")){

          holder.columnValueTextView.setVisibility(View.GONE);

          holder.img.setVisibility(View.GONE);
          holder.pdfView.setVisibility(View.VISIBLE);

          byte[] pdfByteArray = convertStringToByteArray(value);
          holder.pdfView.fromBytes(pdfByteArray)
                  .defaultPage(0)
                  .enableSwipe(true)
                  .swipeHorizontal(false)
                  .enableDoubletap(true)
                  .load();

      }else {
            holder.img.setVisibility(View.GONE);
          holder.pdfView.setVisibility(View.GONE);

          Log.d(TAG, "onBindViewHolder: " + value);
            holder.columnValueTextView.setText(columnData.getColumnValue());

        }

    }

    public Bitmap convertByteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }

    public byte[] convertStringToByteArray(String byteString) {
        String[] byteValues = byteString.substring(1, byteString.length() - 1).split(",");
        byte[] byteArray = new byte[byteValues.length];

        for (int i = 0; i < byteValues.length; i++) {
            byteArray[i] = Byte.parseByte(byteValues[i].trim());
        }

        return byteArray;
    }


    @Override
    public int getItemCount() {
        return columnDataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView columnNameTextView;
        public TextView columnValueTextView;
        public ImageView img;
        public PDFView pdfView;

        public ViewHolder(View itemView) {
            super(itemView);
            columnNameTextView = itemView.findViewById(R.id.columnNameTextView);
            columnValueTextView = itemView.findViewById(R.id.columnValueTextView);
            img = itemView.findViewById(R.id.img);
            pdfView = itemView.findViewById(R.id.pdfView);
        }
    }
}