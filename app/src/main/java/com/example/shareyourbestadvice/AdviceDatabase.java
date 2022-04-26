package com.example.shareyourbestadvice;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Advice.class}, version = 1, exportSchema = false)
public abstract class AdviceDatabase extends RoomDatabase {

    private static volatile AdviceDatabase dbInstance;
    private final static String ADVICES_DB_NAME = "my_db";

    public static AdviceDatabase getInstance(Context context) {
        if (dbInstance == null) {
            synchronized (AdviceDatabase.class) {
                if (dbInstance == null) {
                    dbInstance =
                            Room.databaseBuilder(context.getApplicationContext(),
                                    AdviceDatabase.class,
                                    ADVICES_DB_NAME)
                                    .fallbackToDestructiveMigration()
                                    .build();
                }
            }
        }
        return dbInstance;
    }

    public abstract AdviceDao adviceDao();
}
