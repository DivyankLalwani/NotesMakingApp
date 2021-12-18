package com.example.makenotes.activities.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.makenotes.activities.entity.note;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface NoteDao {

    @Query("Select* from notes ORDER BY id DESC")
    List<note> getAllNotes();

    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNode(note Note);
     @Delete
    void delete(note Note);
}
