package com.apogee.dummy_spinner;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

public class ColumnDataAdapter extends RecyclerView.Adapter<ColumnDataAdapter.ViewHolder> implements OnLoadCompleteListener, OnPageChangeListener {
    private List<ColumnData> columnDataList;
    private List<String> subTypeList;
    Context context;
    private static final int REQUEST_OPEN_PDF = 1;
    private Activity activity;


    public ColumnDataAdapter(List<ColumnData> columnDataList, Activity activity, Context context, List<String> subTypeList) {
        this.columnDataList = columnDataList;
        this.subTypeList = subTypeList;
        this.context = context;
        this.activity = activity;

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

        if ((subtypeListValue.equals("IMAGE") || subtypeListValue.equals("PICTURE")) && value.contains("[")) {

            holder.columnValueTextView.setVisibility(View.GONE);
            holder.pdf.setVisibility(View.GONE);

            holder.img.setVisibility(View.VISIBLE);


            byte[] imageByteArray = convertStringToByteArray(value); // your byte array here

            Bitmap bmp = convertByteArrayToBitmap(imageByteArray);

            holder.img.setImageBitmap(bmp);


        } else if (subtypeListValue.equals("PDF")) {

            holder.columnValueTextView.setVisibility(View.GONE);

            holder.img.setVisibility(View.GONE);
            holder.pdf.setVisibility(View.VISIBLE);

            Uri uri = Uri.parse(value);

            holder.pdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.webview, null);


                    // Build the custom AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();


                    PDFView pdf = dialogView.findViewById(R.id.pdf);
                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

// Calculate the screen width and height
                    int screenWidth = displayMetrics.widthPixels;
                    int screenHeight = displayMetrics.heightPixels;

                    int buttonWidth = (int) (screenWidth * 0.85);
                    int buttonHeight = (int) (screenHeight * 0.85);

                    // Set the button width and height to match the screen dimensions
                    pdf.getLayoutParams().width = buttonWidth;
                    pdf.getLayoutParams().height = buttonHeight;


                    byte[] pdfBytes = Base64.decode(value, Base64.DEFAULT);

                    File pdfFile = createPdfFile(pdfBytes);
                    if (pdfFile != null) {

                        pdf.fromFile(pdfFile)

                                .enableSwipe(true) // allows to block changing pages using swipe
                                .swipeHorizontal(false)
                                .enableDoubletap(true)
                                .defaultPage(0)
                                .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
                                .password(null)
                                .scrollHandle(null)
                                .enableAntialiasing(true) // improve rendering a little bit on low-res screens
                                // spacing between pages in dp. To define spacing color, set view background
                                .spacing(0)
                                .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen

                                .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
                                .fitEachPage(false) // fit each page to the view, else smaller pages are scaled relative to largest page.
                                .pageSnap(false) // snap pages to screen boundaries
                                .pageFling(false) // make a fling change only a single page like ViewPager
                                .nightMode(false)
                                .load();
                    }
                    // Show the custom dialog
                    dialog.show();
                }
            });


//            WebSettings webSettings = holder.pdf.getSettings();
//            webSettings.setJavaScriptEnabled(true);
//
//            String pdfData = "data:application/pdf;base64," + value;
//            holder.pdf.loadData(pdfData, "application/pdf", "base64");


        } else {
            holder.img.setVisibility(View.GONE);
            holder.pdf.setVisibility(View.GONE);


            Log.d(TAG, "onBindViewHolder: " + value);
            holder.columnValueTextView.setText(columnData.getColumnValue());

        }

    }

    private File createPdfFile(byte[] pdfBytes) {
        try {
            File pdfFile = new File(context.getCacheDir(), "temp_pdf.pdf");
            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            outputStream.write(pdfBytes);
            outputStream.close();
            return pdfFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

    @Override
    public void loadComplete(int nbPages) {

    }

    @Override
    public void onPageChanged(int page, int pageCount) {

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView columnNameTextView;
        public TextView columnValueTextView;
        public ImageView img;
        public Button pdf;


        public ViewHolder(View itemView) {
            super(itemView);
            columnNameTextView = itemView.findViewById(R.id.columnNameTextView);
            columnValueTextView = itemView.findViewById(R.id.columnValueTextView);
            img = itemView.findViewById(R.id.img);
            pdf = itemView.findViewById(R.id.pdf);

        }
    }

    private byte[] decompressByteArray(byte[] compressedBytes) {
        byte[] decompressedByteArray = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedBytes);
            ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream);
            zipInputStream.getNextEntry();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            zipInputStream.close();
            byteArrayOutputStream.close();
            decompressedByteArray = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            Log.e("MainActivity", "Error decompressing byte array: " + e.getMessage());
        }
        return decompressedByteArray;
    }


}