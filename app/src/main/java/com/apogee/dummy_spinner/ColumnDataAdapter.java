package com.apogee.dummy_spinner;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

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
            holder.doc.setVisibility(View.GONE);

            holder.img.setVisibility(View.VISIBLE);


            byte[] imageByteArray = convertStringToByteArray(value); // your byte array here

            Bitmap bmp = convertByteArrayToBitmap(imageByteArray);

            holder.img.setImageBitmap(bmp);
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // Get the drawable from the ImageView
                    BitmapDrawable drawable = (BitmapDrawable) holder.img.getDrawable();
                    Bitmap imageBitmap = drawable.getBitmap();


                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
                    int screenWidth = displayMetrics.widthPixels;
                    int screenHeight = displayMetrics.heightPixels;
                    int desiredWidth = (int) (screenWidth * 0.8);
                    int desiredHeight = (int) (screenHeight * 0.8);

                    // Create an AlertDialog with a custom view containing an ImageView
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);


                    ImageView alertDialogImageView = new ImageView(context);

                    alertDialogImageView.setImageBitmap(imageBitmap);
                    alertDialogImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    builder.setView(alertDialogImageView)
                            .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.getWindow().setLayout(desiredWidth, desiredHeight);
                    alertDialog.show();
                }
            });


        } else if (subtypeListValue.equals("PDF")) {

            holder.columnValueTextView.setVisibility(View.GONE);

            holder.img.setVisibility(View.GONE);
            holder.doc.setVisibility(View.VISIBLE);

            Uri uri = Uri.parse(value);

            holder.doc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.pdf_viewer, null);


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


        } else if (subtypeListValue.equals("EXCEL")) {


            holder.columnValueTextView.setVisibility(View.GONE);

            holder.img.setVisibility(View.GONE);
            holder.doc.setVisibility(View.VISIBLE);

            holder.doc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogView = inflater.inflate(R.layout.excel_viewer, null);


                    // Build the custom AlertDialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();
                    Uri uri = Uri.parse(value);

                    TextView excel_viewer = dialogView.findViewById(R.id.excel_viewer);


                    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

                    int screenWidth = displayMetrics.widthPixels;
                    int screenHeight = displayMetrics.heightPixels;

                    int buttonWidth = (int) (screenWidth * 0.85);
                    int buttonHeight = (int) (screenHeight * 0.85);

                    // Set the button width and height to match the screen dimensions
                    excel_viewer.getLayoutParams().width = buttonWidth;
                    excel_viewer.getLayoutParams().height = buttonHeight;

                    String excelContent = readExcelFile(context, uri);

                    excel_viewer.setText(excelContent);

                    Log.d(TAG, "onClick: " + excelContent);


                    dialog.show();
                }
            });


        } else if (subtypeListValue.equals("AUDIO")) {


            holder.columnValueTextView.setVisibility(View.GONE);

            holder.img.setVisibility(View.GONE);
            holder.doc.setVisibility(View.GONE);
            holder.playButton.setVisibility(View.VISIBLE);

            holder.playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(value);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Play the recording
                    mediaPlayer.start();
                }
            });


        } else {
            holder.img.setVisibility(View.GONE);
            holder.doc.setVisibility(View.GONE);


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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView columnNameTextView;
        public TextView columnValueTextView;
        public ImageView img;
        public Button doc;
        public GifImageView playButton;


        public ViewHolder(View itemView) {
            super(itemView);
            columnNameTextView = itemView.findViewById(R.id.columnNameTextView);
            columnValueTextView = itemView.findViewById(R.id.columnValueTextView);
            img = itemView.findViewById(R.id.img);
            doc = itemView.findViewById(R.id.pdf);
            playButton = itemView.findViewById(R.id.playButton);


        }
    }

    private String readExcelFile(Context context, Uri excelUri) {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(excelUri);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Assuming the first sheet

            for (Row row : sheet) {
                for (Cell cell : row) {
                    CellType cellType = cell.getCellType();
                    if (cellType == CellType.STRING) {
                        contentBuilder.append(cell.getStringCellValue());
                    } else if (cellType == CellType.NUMERIC) {
                        contentBuilder.append(cell.getNumericCellValue());
                    } else if (cellType == CellType.BOOLEAN) {
                        contentBuilder.append(cell.getBooleanCellValue());
                    }
                    contentBuilder.append("\t"); // Separate cells with a tab
                }
                contentBuilder.append("\n"); // Separate rows with a new line
            }

            workbook.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }


}