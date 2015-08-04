package accountoperations.clientsqliteversion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
    protected static final String COLUMN_USERNAME = "_username";
    protected static final String COLUMN_PASSWORD = "_password";
    protected static final String COLUMN_NAME = "_name";
    protected static final String COLUMN_SURNAME = "_surname";
    protected static final String COLUMN_GRADUATED_FROM = "_graduated_from";
    protected static final String COLUMN_GRADUATED_IN = "_graduated_in";
    protected static final String COLUMN_BORN_PLACE = "_born_place";
    protected static final String COLUMN_BIRTHDAY = "_birthday";
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "_id";
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


    public boolean onUpdate(String json) {
        // parse json string
        try {
            ArrayList<Long> _id = new ArrayList<>();
            ArrayList<String> _username = new ArrayList<>();
            ArrayList<String> _password = new ArrayList<>();
            ArrayList<String> _name = new ArrayList<>();
            ArrayList<String> _surname = new ArrayList<>();
            ArrayList<String> _graduated_from = new ArrayList<>();
            ArrayList<String> _graduated_in = new ArrayList<>();
            ArrayList<String> _born_place = new ArrayList<>();
            ArrayList<String> _birthday = new ArrayList<>();

            JSONArray ja = new JSONArray(json);
            for(int i = 0; i < ja.length(); i++) {
                _id.add(ja.getJSONObject(i).getLong("_id"));
                _username.add(ja.getJSONObject(i).getString("_username"));
                _password.add(ja.getJSONObject(i).getString("_password"));
                _name.add(ja.getJSONObject(i).getString("_name"));
                _surname.add(ja.getJSONObject(i).getString("_surname"));
                _graduated_from.add(ja.getJSONObject(i).getString("_graduated_from"));
                _graduated_in.add(ja.getJSONObject(i).getString("_graduated_in"));
                _born_place.add(ja.getJSONObject(i).getString("_born_place"));
                _birthday.add(ja.getJSONObject(i).getString("_birthday"));
            }

            // testing if ArrayLists has filled w/ data or not
//            for (int i = 0; i < _id.size(); i++) {
//                Log.v("_id", String.valueOf(_id.get(i)) + "\n\n\n");
//                Log.v("_username", _username.get(i) + "\n\n\n");
//                Log.v("_password", _password.get(i) + "\n\n\n");
//                Log.v("_name", _name.get(i) + "\n\n\n");
//                Log.v("_surname", _surname.get(i) + "\n\n\n");
//                Log.v("_graduated_from", _graduated_from.get(i) + "\n\n\n");
//                Log.v("_graduated_in", _graduated_in.get(i) + "\n\n\n");
//                Log.v("_born_place", _born_place.get(i) + "\n\n\n");
//                Log.v("_birthday", _birthday.get(i) + "\n\n\n\nNext one\n\n\n");
//            }

            SQLiteDatabase db = this.getReadableDatabase();
            db.execSQL("DELETE FROM " + TABLE_USERS);

            for (int i = 0; i < _id.size(); i++) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_ID, _id.get(i));
                contentValues.put(COLUMN_USERNAME, _username.get(i));
                contentValues.put(COLUMN_PASSWORD, _password.get(i));
                contentValues.put(COLUMN_NAME, _name.get(i));
                contentValues.put(COLUMN_SURNAME, _surname.get(i));
                contentValues.put(COLUMN_GRADUATED_FROM, _graduated_from.get(i));
                contentValues.put(COLUMN_GRADUATED_IN, _graduated_in.get(i));
                contentValues.put(COLUMN_BORN_PLACE, _born_place.get(i));
                contentValues.put(COLUMN_BIRTHDAY, _birthday.get(i));
                db.insert(TABLE_USERS, null, contentValues);
            }

            return true;
        } catch (Exception e) {
            Log.wtf("json error", e.toString());
            AccountOperations ac = new AccountOperations();
            Toast.makeText(ac.getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            return false;
        }

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
        try {
            String selection = "_username = ? AND _password = ?";
            String[] selectionArgs = {username, password};
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
        } catch (Exception e) { Log.wtf("LOGIN ERROR ", e.toString()); }

        return _id;
    }


    public int delete(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts", "id = ? ", new String[]{Integer.toString(id)});
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
