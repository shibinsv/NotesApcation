package com.example.notes.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notes.BaseActivity;
import com.example.notes.R;
import com.example.notes.adapters.NotesAdapter;
import com.example.notes.models.NotesData;
import com.example.notes.roomDatabase.RoomDB;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private Activity mActivity;
    private MainActivity mMainActivity;
    NotesAdapter adapter;
    List<NotesData> list = new ArrayList<>();
    RoomDB database;
    int sID;

    @BindView(R.id.addNotes)
    TextView addNotes;
    @BindView(R.id.deleteAllNotes)
    TextView deleteAllNotes;
    @BindView(R.id.recyclerNotes)
    RecyclerView recyclerNotes;

    //Note colors
    private String selectedNoteColor;
    private View viewSubtitleIndicator;
    private NotesData alreadyAvailableNote;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        ButterKnife.bind(this);
        initView();

    }

    private void initView() {
        database = RoomDB.getInstance(this);
        NotesData data =new NotesData();
        data.setColor(selectedNoteColor);
        list = database.dao().getAll();
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        recyclerNotes.setLayoutManager(layoutManager);
        adapter = new NotesAdapter(mActivity, list);
        recyclerNotes.setAdapter(adapter);

        addNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.add_dialog);
                int width = WindowManager.LayoutParams.MATCH_PARENT;
                int height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog.getWindow().setLayout(width, height);
                dialog.show();

                Button btnSave = dialog.findViewById(R.id.btnSave);
                EditText titleAdd = dialog.findViewById(R.id.titleAdd);
                EditText descAdd = dialog.findViewById(R.id.descAdd);
                TextView dateAdd = dialog.findViewById(R.id.dateAdd);
                LinearLayout addNoteLayout=dialog.findViewById(R.id.addNoteLayout);
                LinearLayout bottomTaskLayout=dialog.findViewById(R.id.layout_bottom_notes);
                viewSubtitleIndicator = dialog.findViewById(R.id.viewSubtitleIndicator);


                final ImageView imageColor1 = bottomTaskLayout.findViewById(R.id.imageColor1);
                final ImageView imageColor2 = bottomTaskLayout.findViewById(R.id.imageColor2);
                final ImageView imageColor3 = bottomTaskLayout.findViewById(R.id.imageColor3);


                //white
                bottomTaskLayout.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedNoteColor = "#ffffff";
                        imageColor1.setImageResource(R.drawable.ic_done);
                        imageColor2.setImageResource(0);
                        imageColor3.setImageResource(0);
                        setSubtitleIndicatorColor();
                    }
                });

                //yellow
                bottomTaskLayout.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedNoteColor = "#ffff33";
                        imageColor1.setImageResource(0);
                        imageColor2.setImageResource(R.drawable.ic_done);
                        imageColor3.setImageResource(0);
                        setSubtitleIndicatorColor();
                    }
                });

                //red
                bottomTaskLayout.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedNoteColor = "#ff9999";
                        imageColor1.setImageResource(0);
                        imageColor2.setImageResource(0);
                        imageColor3.setImageResource(R.drawable.ic_done);
                        setSubtitleIndicatorColor();
                    }
                });


                if (alreadyAvailableNote != null &&
                        alreadyAvailableNote.getColor() != null
                        && !alreadyAvailableNote.getColor().trim().isEmpty()){
                    switch (alreadyAvailableNote.getColor()){
                        case "#FDBE3B":
                            bottomTaskLayout.findViewById(R.id.viewColor2).performClick();
                            break;
                        case "#FF4842":
                            bottomTaskLayout.findViewById(R.id.viewColor3).performClick();
                            break;
                    }
                }


                Calendar myCalendar= Calendar.getInstance();
                final DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, month);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String myFormat = "dd/MMM/yyyy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                        dateAdd.setText(sdf.format(myCalendar.getTime()));
                    }};

                dateAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DatePickerDialog(MainActivity.this, dateSetListener, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show(); }
                });
                
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String sNewTitle = titleAdd.getText().toString().trim();
                        String sNewDesc = descAdd.getText().toString().trim();
                        String sNewDate = dateAdd.getText().toString().trim();

                        if (!sNewTitle.equals("") && !sNewDesc.equals("") && !sNewDate.equals("")) {
                            NotesData data = new NotesData();
                            data.setTitle(sNewTitle);
                            data.setDescription(sNewDesc);
                            data.setDate(sNewDate);
                            data.setColor(selectedNoteColor);
                            database.dao().insert(data);
                            //clear editText
                            titleAdd.setText("");
                            descAdd.setText("");
                            dateAdd.setText("");
                            list.clear();
                            list.addAll(database.dao().getAll());
                            adapter.notifyDataSetChanged();
                        }else {
                            if (sNewTitle.isEmpty() && sNewDesc.isEmpty()) {
                                Toast.makeText(dialog.getContext(),
                                        "Note title can't be empty!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        dialog.dismiss();

                    }
                });
            }
        });
        
    }
    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));

    }
}