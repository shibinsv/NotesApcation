package com.example.notes.roomDatabase;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.notes.models.NotesData;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@androidx.room.Dao
public interface Dao {
    @Insert(onConflict = REPLACE)
    void insert(NotesData mainData);

    @Delete
    void delete(NotesData mainData);

    @Query("UPDATE notes_table SET title =:sTitle,description =:sDescription,date =:sDate WHERE ID =:sID")
    void update(int sID,String sTitle,String sDescription,String sDate);

    @Query("SELECT *FROM notes_table")
    List<NotesData> getAll();

}
