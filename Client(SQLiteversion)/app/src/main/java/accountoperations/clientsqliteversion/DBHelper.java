package accountoperations.clientsqliteversion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * <p>
 * This class is responsible for creating the database.
 * The <code>onUpgrade()</code> method will simply
 * delete all existing data and re-create the table.
 * It also defines several constant for the table name
 * and the table columns.
 * </p>
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "_id";
    protected static final String COLUMN_USERNAME = "_username";
    protected static final String COLUMN_PASSWORD = "_password";
    protected static final String COLUMN_NAME = "_name";
    protected static final String COLUMN_SURNAME = "_surname";
    protected static final String COLUMN_GRADUATED_FROM = "_graduated_from";
    protected static final String COLUMN_GRADUATED_IN = "_graduated_in";
    protected static final String COLUMN_BORN_PLACE = "_born_place";
    protected static final String COLUMN_BIRTHDAY = "_birthday";

    private static final String DATABASE_NAME = "users.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "CREATE TABLE `"
            + TABLE_USERS + "` (`"
            + COLUMN_ID + "` INTEGER PRIMARY KEY AUTOINCREMENT, `"
            + COLUMN_USERNAME + "` TEXT, `"
            + COLUMN_PASSWORD + "` TEXT, `"
            + COLUMN_NAME + "` TEXT, `"
            + COLUMN_SURNAME + "` TEXT, `"
            + COLUMN_GRADUATED_FROM + "` TEXT, `"
            + COLUMN_GRADUATED_IN + "` TEXT, `"
            + COLUMN_BORN_PLACE + "` TEXT, `"
            + COLUMN_BIRTHDAY + "` TEXT);";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT _username, _password, _name, _surname, _graduated_from, _graduated_in, _born_place, _birthday FROM " + TABLE_USERS + " WHERE _id = " + id + ";", null);
    }

    public boolean register(String username, String password, String name, String surname, String graduated_from, String graduated_in, String born_place, String birthday) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_SURNAME, surname);
        contentValues.put(COLUMN_GRADUATED_FROM, graduated_from);
        contentValues.put(COLUMN_GRADUATED_IN, graduated_in);
        contentValues.put(COLUMN_BORN_PLACE, born_place);
        contentValues.put(COLUMN_BIRTHDAY, birthday);
        db.insert(TABLE_USERS, null, contentValues);

        return true;
    }

    public boolean update(Integer id, String username, String password, String name, String surname, String graduated_from, String graduated_in, String born_place, String birthday) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_USERNAME, username);
        contentValues.put(COLUMN_PASSWORD, password);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_SURNAME, surname);
        contentValues.put(COLUMN_GRADUATED_FROM, graduated_from);
        contentValues.put(COLUMN_GRADUATED_IN, graduated_in);
        contentValues.put(COLUMN_BORN_PLACE, born_place);
        contentValues.put(COLUMN_BIRTHDAY, birthday);
        db.update(TABLE_USERS, contentValues, "_id = ? ", new String[]{Integer.toString(id)});

        return true;
    }

    public int OAuth(String username, String password) {
        int _id = 0;
        String selection = "_username = ? AND _password = ?";
        String[] selectionArgs = { username, password };
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(
                TABLE_USERS,                                // The table to query
                new String[]{COLUMN_ID},                    // The columns to return
                selection,                                  // The columns for the WHERE clause
                selectionArgs,                              // The values for the WHERE clause
                null,                                       // don't group the rows
                null,                                       // don't filter by row groups
                null                                        // The sort order
        );

        cursor.moveToFirst();
        _id = cursor.getInt(0);
        cursor.close();
        Log.e("_id of user", String.valueOf(_id));
        return _id;
    }

    public Integer delete(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts", "id = ? ", new String[] { Integer.toString(id) });
    }

//    public ArrayList<String> getAllCotacts() {
//        ArrayList<String> array_list = new ArrayList<String>();
//
//        //hp = new HashMap();
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery("SELECT _name, _surname, _graduated_from, _graduated_in, _born_place, _birthday FROM " + TABLE_USERS, null);
//        res.moveToFirst();
//
//        while(!res.isAfterLast()){
//            array_list.add(res.getString(res.getColumnIndex(COLUMN_NAME)));
//            res.moveToNext();
//        }
//
//        res.close();
//
//        return array_list;
//    }


}
