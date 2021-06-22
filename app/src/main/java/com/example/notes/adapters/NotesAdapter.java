package com.example.notes.adapters;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notes.R;
import com.example.notes.activities.MainActivity;
import com.example.notes.models.NotesData;
import com.example.notes.roomDatabase.RoomDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    MainActivity mMainActivity;
    private List<NotesData> list;
    private RoomDB database;
    private int lastPosition=-1;
    String options[];

    public NotesAdapter(Activity context, List<NotesData> list) {
        this.mMainActivity = (MainActivity) context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_notes_adapter, parent, false);
        return new ViewHolder(itemView);    }

    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHolder holder, int position) {
        if (position>lastPosition) {
            lastPosition=position;
        }
        database=RoomDB.getInstance(mMainActivity);
        holder.setNote(list.get(position));
        holder.deleteIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mMainActivity);
                builder.setTitle("Are you sure you want to delete this note?");
                options = new String[]{"Yes", "Cancel"};
                builder.setItems(options, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            NotesData d=list.get(holder.getAdapterPosition());
                            database.dao().delete(d);
                            int position=holder.getAdapterPosition();
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,list.size());
                        case 1:
                            dialogInterface.dismiss();
                            break;
                    }
                });
                androidx.appcompat.app.AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.titleET)
        TextView titleET;
        @BindView(R.id.descET)
        TextView descET;
        @BindView(R.id.dateET)
        TextView dateET;
        @BindView(R.id.deleteIV)
        TextView deleteIV;
        @BindView(R.id.layoutNote)
        ConstraintLayout layoutNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

        public void setNote(NotesData mainData) {
            titleET.setText(mainData.getTitle());
            descET.setText(mainData.getDescription());
            dateET.setText(mainData.getDate());

            layoutNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog viewNotesDialog = new Dialog(mMainActivity);
                    viewNotesDialog.setContentView(R.layout.view_notes);
                    int width = WindowManager.LayoutParams.MATCH_PARENT;
                    int height = WindowManager.LayoutParams.WRAP_CONTENT;
                    viewNotesDialog.getWindow().setLayout(width, height);
                    viewNotesDialog.show();

                    ConstraintLayout viewNoteLayout  = viewNotesDialog.findViewById(R.id.viewNoteLayout);
                    TextView titleAdd = viewNotesDialog.findViewById(R.id.noteTitle);
                    TextView descAdd = viewNotesDialog.findViewById(R.id.noteDescription);
                    TextView dateAdd = viewNotesDialog.findViewById(R.id.noteDate);
                    LinearLayout btnEdit = viewNotesDialog.findViewById(R.id.editNote);
                    String sTitle=mainData.getTitle();
                    String sDesc=mainData.getDescription();
                    String sDate=mainData.getDate();
                    titleAdd.setText(sTitle);
                    descAdd.setText(sDesc);
                    dateAdd.setText(sDate);

                    btnEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewNotesDialog.dismiss();
                            int sID=mainData.getID();
                            Dialog dialog =new Dialog(mMainActivity);
                            dialog.setContentView(R.layout.edit_dialog);
                            int width= WindowManager.LayoutParams.MATCH_PARENT;
                            int height=WindowManager.LayoutParams.WRAP_CONTENT;
                            dialog.getWindow().setLayout(width,height);
                            dialog.show();

                            EditText editTitle =dialog.findViewById(R.id.titleUpdate);
                            EditText editDescription=dialog.findViewById(R.id.descUpdate);
                            TextView editDate =dialog.findViewById(R.id.dateUpdate);
                            Button btnUpdate=dialog.findViewById(R.id.btnUpdate);

                            Calendar myCalendar= Calendar.getInstance();
                            final DatePickerDialog.OnDateSetListener dateSetListener= (view, year, month, dayOfMonth) -> {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, month);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String myFormat = "dd/MMM/yyyy";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.UK);
                                editDate.setText(sdf.format(myCalendar.getTime()));
                            };

                            editDate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new DatePickerDialog(mMainActivity, dateSetListener, myCalendar
                                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                            myCalendar.get(Calendar.DAY_OF_MONTH)).show(); }
                            });

                            editTitle.setText(sTitle);
                            editDescription.setText(sDesc);

                            btnUpdate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String newTitle =editTitle.getText().toString().trim();
                                    String newDescription =editDescription.getText().toString().trim();
                                    String newDate =editDate.getText().toString().trim();

                                    database.dao().update(sID,newTitle,newDescription,newDate);

                                    list.clear();
                                    list.addAll(database.dao().getAll());
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                            });
                        }
                    });
                }

            });

            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();

            if (mainData.getColor() != null){
                gradientDrawable.setColor(Color.parseColor(mainData.getColor()));
            }else {
                gradientDrawable.setColor(Color.parseColor("#e6f2ff"));
            }

        }
    }
}