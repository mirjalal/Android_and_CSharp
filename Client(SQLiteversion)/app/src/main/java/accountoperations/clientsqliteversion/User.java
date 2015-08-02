package accountoperations.clientsqliteversion;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class User extends AppCompatActivity {

    EditText username, password, name, surname, graduated_from, graduated_in, born_place;
    String _username, _password, _name, _surname, _graduated_from, _graduated_in, _born_place, _birthday;
    TextView birthday;
    Button register;
    int id = 0;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        name = (EditText) findViewById(R.id.name);
        surname = (EditText) findViewById(R.id.surname);
        graduated_from = (EditText) findViewById(R.id.graduated_from);
        graduated_in = (EditText) findViewById(R.id.graduated_in);
        born_place = (EditText) findViewById(R.id.born_place);
        birthday = (TextView) findViewById(R.id.birthday);
        register = (Button) findViewById(R.id.register);

        dbHelper = new DBHelper(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int _id = extras.getInt("id");

            if (_id > 0) {
                //means this is the view part not the add contact part.
                Cursor cursor = dbHelper.getData(_id);
            //    id_To_Update = Value;
                cursor.moveToFirst();

                String _username = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USERNAME));
                String _password = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PASSWORD));
                String _name = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_NAME));
                String _surname = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_SURNAME));
                String _graduated_from = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_GRADUATED_FROM));
                String _graduated_in = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_GRADUATED_IN));
                String _born_place = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_BORN_PLACE));
                String _birthday = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_BIRTHDAY));

                if (!cursor.isClosed())
                    cursor.close();

                register.setVisibility(View.INVISIBLE);

                username.setText(_username);
                username.setFocusable(true);
                username.setClickable(true);
                username.setEnabled(false);

                password.setText(_password);
                password.setFocusable(true);
                password.setClickable(true);
                password.setEnabled(false);
//                password.setTransformationMethod(new PasswordTransformationMethod()); // you can show password characters with this method.

                name.setText(_name);
                name.setFocusable(true);
                name.setClickable(true);

                surname.setText(_surname);
                surname.setFocusable(true);
                surname.setClickable(true);

                graduated_from.setText(_graduated_from);
                graduated_from.setFocusable(true);
                graduated_from.setClickable(true);

                graduated_in.setText(_graduated_in);
                graduated_in.setFocusable(true);
                graduated_in.setClickable(true);

                born_place.setText(_born_place);
                born_place.setFocusable(true);
                born_place.setClickable(true);

                birthday.setText(_birthday);
                birthday.setFocusable(true);
                birthday.setClickable(true);
            }
        }

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.register:
                        _name = name.getText().toString().trim();
                        _surname = surname.getText().toString().trim();
                        _graduated_from = graduated_from.getText().toString().trim();
                        _graduated_in = graduated_in.getText().toString().trim();
                        _born_place = born_place.getText().toString().trim();

                        if (birthday.getText().equals("Birthday"))
                            _birthday = "";
                        else
                            _birthday = birthday.getText().toString();

                        if (username.getText().toString().equals("") || password.getText().toString().equals(""))
                            Toast.makeText(getApplicationContext(), "Username and password required.", Toast.LENGTH_LONG).show();
                        else {
                            _username = username.getText().toString().trim();
                            _password = password.getText().toString(); // don't trim this value; user should be enter whitespace

                            Bundle extras = getIntent().getExtras();
                            if (extras != null) {
                                int Value = extras.getInt("id");

                                if (Value > 0) {
                                    if (dbHelper.update(id, _username, _password, _name, _surname, _graduated_from, _graduated_in, _born_place, _birthday)) {
                                        Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), AccountOperations.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    if (dbHelper.register(_username, _password, _name, _surname, _graduated_from, _graduated_in, _born_place, _birthday)) {
                                        Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "not done", Toast.LENGTH_SHORT).show();
                                    }
                                    Intent intent = new Intent(getApplicationContext(), AccountOperations.class);
                                    startActivity(intent);
                                }
                            }
                        }
                }
            }
        };

        register.setOnClickListener(clickListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Bundle extras = getIntent().getExtras();
        // Inflate the menu; this adds items to the action bar if it is present.

        if (extras != null) {
            int Value = extras.getInt("id");
            if (Value > 0)
                getMenuInflater().inflate(R.menu.menu_user, menu);
            else
                getMenuInflater().inflate(R.menu.menu_user, menu);
        }

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
