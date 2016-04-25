package doubled.greenrivertech.net.sqlitechat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.PortUnreachableException;

public class ChatWindow extends AppCompatActivity {
    private TextView messages;
    private EditText message;
    private EditText name;
    private MessageDBHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        helper = new MessageDBHelper(getApplicationContext());
        name = (EditText) findViewById(R.id.name);
        message = (EditText) findViewById(R.id.message);
        messages = (TextView) findViewById(R.id.messages);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_window, menu);
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

    public void submitData(View v)
    {
        String givenName = name.getText().toString();
        String givenMessage = message.getText().toString();

        if (givenName.equals("") || givenMessage.equals(""))
        {
            Toast.makeText(ChatWindow.this, "Please enter a name and message.", Toast.LENGTH_SHORT)
                    .show();
        }
        else
        {
            MessageContract contract = new MessageContract();
            contract.putData(givenName, givenMessage);

            name.setText("");
            message.setText("");

            displayData();
        }
    }

    public void displayData()
    {
        MessageContract contract = new MessageContract();
        String data = "";
        Cursor c = contract.collectData();
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            data += c.getString(c.getColumnIndex("name")) + ": " +
                    c.getString(c.getColumnIndex("message")) + "\n";
            c.moveToNext();
        }
        messages.setText(data);
    }

    public class MessageContract {

        public MessageContract() {}

        public abstract class ChatSchema implements BaseColumns
        {
            public static final String TABLE_NAME = "chat";
            public static final String MESSAGE_ID = "messId";
            public static final String NAME = "name";
            public static final String MESSAGE = "message";
        }

        private static final String TEXT_TYPE = " TEXT";
        private static final String COMMA_SEP = ", ";

        public  static final String CREATE_TABLE =
                "CREATE TABLE " + ChatSchema.TABLE_NAME + " (" +
                ChatSchema._ID + " Integer Primary Key, " +
                ChatSchema.NAME + TEXT_TYPE + COMMA_SEP +
                ChatSchema.MESSAGE + TEXT_TYPE  + " )";

        public static final String DELETE_TABLE =
                "DROP TABLE IF EXISTS " +
                ChatSchema.TABLE_NAME;


        public void putData(String givenName, String givenMessage)
        {
            SQLiteDatabase db = helper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(ChatSchema.NAME, givenName);
            values.put(ChatSchema.MESSAGE, givenMessage);

            long rowId;
            rowId = db.insert(ChatSchema.TABLE_NAME, "null", values);
        }

        public Cursor collectData()
        {
            SQLiteDatabase db = helper.getReadableDatabase();

            String[] projection = {ChatSchema._ID, ChatSchema.NAME, ChatSchema.MESSAGE};

            String sort = ChatSchema._ID + " DESC";

            return db.query(ChatSchema.TABLE_NAME, projection, null, null, null, null, sort);
        }
    }

    public class MessageDBHelper extends SQLiteOpenHelper {
        public static final int VERSION = 1;
        public static final String DB_NAME = "ChatWindow.db";

        public MessageDBHelper(Context cont) {
            super(cont, DB_NAME, null, VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(MessageContract.CREATE_TABLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(MessageContract.DELETE_TABLE);
            onCreate(db);
        }
    }
}
