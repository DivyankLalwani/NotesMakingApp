package com.example.makenotes.activities.database;
import android.content.Context;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.makenotes.activities.dao.NoteDao;
import com.example.makenotes.activities.entity.note;


@androidx.room.Database(entities = note.class, version = 1, exportSchema = false)
public abstract class Database extends RoomDatabase {

    public static Database notesDatabase;

    public static synchronized Database getDatabase(Context context) {

        if (notesDatabase == null) {
            notesDatabase = Room.databaseBuilder(
                    context,
                    Database.class,
                    "notes_db"
            ).build();
        }
        return notesDatabase;
    }
    public abstract NoteDao noteDao();
}