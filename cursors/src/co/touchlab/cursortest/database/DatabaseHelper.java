package co.touchlab.cursortest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/**
 * User: William Sanville
 * Date: 7/26/12
 * Time: 6:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "test.db";

    private static DatabaseHelper instance;

    private static final String CREATE_ARTICLE_TABLE =
            "create table " + Article.TABLE_NAME + "(" +
                    Article.ID_COLUMN + " integer primary key autoincrement, " +
                    Article.TEXT_COLUMN + " text not null, " +
                    Article.TITLE_COLUMN + " text not null, " +
                    Article.THUMBNAIL_COLUMN + " text, " +
                    Article.FULL_IMAGE_COLUMN + " text, " +
                    Article.CREATED_ON_COLUMN + " integer not null" +
                    ")";
    private static final String DROP_ARTICLE_TABLE = "drop table if exists " + Article.TABLE_NAME;

    //hold on to an instance of the actual database file handle, use only one
    private SQLiteDatabase database;

    public synchronized static DatabaseHelper getInstance(Context context)
    {
        if (instance == null)
            instance = new DatabaseHelper(context);
        return instance;
    }

    private DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
        this.database = this.getWritableDatabase();
    }

    public SQLiteDatabase getDatabase()
    {
        return database;
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
        //for API level 8 and up
        /*if (!db.isReadOnly())
            db.execSQL("PRAGMA foreign_keys=ON;");*/
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(CREATE_ARTICLE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.execSQL(DROP_ARTICLE_TABLE);
        onCreate(sqLiteDatabase);
    }
}
