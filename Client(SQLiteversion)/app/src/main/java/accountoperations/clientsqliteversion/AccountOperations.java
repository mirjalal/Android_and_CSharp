package accountoperations.clientsqliteversion;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import static android.widget.Toast.LENGTH_LONG;

public class AccountOperations extends AppCompatActivity {

    Button register, updateInfo, export, importdb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_operations);

        File filesDir = new File("/storage/emulated/0/Account operations(SQLite version)");
        if (!filesDir.exists()){
            filesDir.mkdirs();
        } else {
            filesDir.delete();
            filesDir.mkdirs();
        }

        register = (Button) findViewById(R.id.register);
        updateInfo = (Button) findViewById(R.id.updateInfo);
        export = (Button) findViewById(R.id.export);
        importdb = (Button) findViewById(R.id.importdb);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.register:
                        Intent intent = new Intent(getApplicationContext(), User.class);
                        intent.putExtra("id", -1);
                        startActivity(intent);
                        break;
                    case R.id.updateInfo:
                        startActivity(new Intent(AccountOperations.this, Login.class));
                        break;
                    case R.id.export:
                        File export_db_file = new File(getApplicationContext().getDatabasePath("users.db").getPath());
                        if (export_db_file.exists()) {
                            export_db(getResults().toString());
                        } else {
                            Toast.makeText(getApplicationContext(), "No entries found in database.", LENGTH_LONG).show();
                        }
                        break;
                    case R.id.importdb:
                        File import_db_file = new File("/storage/emulated/0/Account operations(SQLite version)/export.im");
                        if (import_db_file.exists()) {
                            import_db();
                        } else {
                            Toast.makeText(getApplicationContext(), "Required file's not found.", LENGTH_LONG).show();
                        }
                        break;
                }
            }
        };

        register.setOnClickListener(clickListener);
        updateInfo.setOnClickListener(clickListener);
        export.setOnClickListener(clickListener);
        importdb.setOnClickListener(clickListener);
    }


    private void export_db(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getApplicationContext().openFileOutput("android_export.im", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();

            copyFile(new File(getApplicationContext().getFilesDir().getPath()), new File("/storage/emulated/0/Account operations(SQLite version)"));
            Toast.makeText(getApplicationContext(), "Database exported to: /storage/emulated/0/Account operations(SQLite version)", LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            Toast.makeText(getApplicationContext(), "", LENGTH_LONG).show();
        }
    }


    private void import_db() {
        try {
            File filesDir = new File(getApplicationContext().getFilesDir().getPath());
            if (filesDir.exists()){
                copyFile(new File("/storage/emulated/0/Account operations(SQLite version)"), new File(getApplicationContext().getFilesDir().getPath()));
            }
            else {
                filesDir.mkdirs();
                copyFile(new File("/storage/emulated/0/Account operations(SQLite version)"), new File(getApplicationContext().getFilesDir().getPath()));
            }

            import_data();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Error occured.", LENGTH_LONG).show();
        }
    }


    private void import_data() {
        File file = new File(getApplicationContext().getFilesDir().getPath(), "export.im");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();

//            Toast.makeText(getApplicationContext(), String.valueOf(text), LENGTH_LONG).show();

            if (Integer.valueOf(String.valueOf(text.length())) == 0) {
                Toast.makeText(getApplicationContext(), "Nothing found to import.", LENGTH_LONG).show();
            } else {
                DBHelper helper = new DBHelper(this);
                if(helper.onUpdate(String.valueOf(text))){
                    Toast.makeText(getApplicationContext(), "Database updated.", LENGTH_LONG).show();
                }
            }
        }
        catch (Exception e) {
            // We'll need to add proper error handling here
            Log.wtf("EXCEPTION: ", e.toString());
            Toast.makeText(getApplicationContext(), "Database not updated. ERROR: " + e.toString(), LENGTH_LONG).show();
        }
    }


    public void copyFile(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists() && !targetLocation.mkdirs())
                Toast.makeText(getApplicationContext(), "Cannot create directory.", LENGTH_LONG).show();

            String[] children = sourceLocation.list();
            for (String aChildren : children) {
                copyFile(new File(sourceLocation, aChildren),
                        new File(targetLocation, aChildren));
            }
        } else {
            // make sure the directory we plan to store the recording in exists
            File directory = targetLocation.getParentFile();
            if (directory != null && !directory.exists() && !directory.mkdirs())
                Toast.makeText(getApplicationContext(), "Cannot create directory.", LENGTH_LONG).show();

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }


    private JSONArray getResults() {
        String dbPath = getApplicationContext().getDatabasePath("users.db").getPath();// Set path to your database
        String tableName = "users"; //Set name of your table

        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY);
        String searchQuery = "SELECT _id, _username, _password, _name, _surname, _graduated_from, _graduated_in, _born_place, _birthday FROM " + tableName;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null);

        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            Log.d("TAG_NAME", cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        Log.d("TAG_NAME", resultSet.toString());

        return resultSet;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        // Take care of calling this method on earlier versions of
        // the platform where it doesn't exist.
//            onBackPressed();

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        // This will be called either automatically for you on 2.0
        // or later, or by the code above on earlier versions of the
        // platform.
//        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_account_operations, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
