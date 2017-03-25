package com.example.mohibhero.lab1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

public class ChatWindow extends AppCompatActivity {

    final ArrayList<ChatDataOptions> chatArray = new ArrayList<>();

    private ChatDatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private String[] allColumns = { ChatDatabaseHelper.COLUMN_ID,
            ChatDatabaseHelper.COLUMN_MESSAGE };
    private boolean isFrameLoadedExists;
    private FrameLayout chatFrameLayout;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        databaseHelper = new ChatDatabaseHelper(this);

        database = databaseHelper.getWritableDatabase();

        readMessages();

        Resources resources = getResources();
        final ListView listViewChatWindow = (ListView) findViewById(R.id.listViewChatWindow);
        chatAdapter = new ChatAdapter(this);
        listViewChatWindow.setAdapter(chatAdapter);
        final EditText editTextChatBox = (EditText) findViewById(R.id.textChatBox);
        Button buttonSend = (Button) findViewById(R.id.sendChatButton);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chatString = editTextChatBox.getText().toString();
                writeMessages(chatString);

                editTextChatBox.setText("");
            }
        });

        chatFrameLayout = (FrameLayout)findViewById(R.id.chatFrameLayout);

        isFrameLoadedExists = (chatFrameLayout != null);

        listViewChatWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {

                Object object = listViewChatWindow.getItemAtPosition(position);
                ChatDataOptions cdo =(ChatDataOptions)object;
                Bundle dataBundle = new Bundle();

                dataBundle.putString("messageText", cdo.getMessage());
                dataBundle.putString("messageID", Integer.toString(position));

                if(!isFrameLoadedExists) {        // phone
                    Intent intent = new Intent(ChatWindow.this, MessageDetails.class);
                    intent.putExtras(dataBundle);
                    startActivityForResult(intent, 5);
                } else {                    // tablet
                    FragmentTransaction ftm = getSupportFragmentManager().beginTransaction();
                    MessageFragment mf = new MessageFragment(1);
                    mf.setArguments(dataBundle);

                    ftm.replace(R.id.chatFrameLayout, mf);
                    ftm.commit();
                }
            }
        });




    }

    private void readMessages() {
        // read database and save messages into the array list
        Cursor cursor = database.query(ChatDatabaseHelper.TABLE_MESSAGES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String message = cursor.getString(cursor.getColumnIndex( ChatDatabaseHelper.COLUMN_MESSAGE));
            long id = cursor.getLong(cursor.getColumnIndex(ChatDatabaseHelper.COLUMN_ID));

            Log.i("Chat Window", "ID: " + cursor.getString( cursor.getColumnIndex(ChatDatabaseHelper.COLUMN_ID)) + " SQL MESSAGE:" + cursor.getString( cursor.getColumnIndex(ChatDatabaseHelper.COLUMN_MESSAGE)));

            ChatDataOptions data = new ChatDataOptions(message, id);

            chatArray.add(data);
            cursor.moveToNext();
        }

        Log.i("Chat Window", "Cursor’s column count =" + cursor.getColumnCount());

        for(int i = 0; i < cursor.getColumnCount(); i++) {
            Log.i("Chat Window", "Column Name: " + cursor.getColumnName(i));
        }

        // close the cursor
        cursor.close();
    }

    private void writeMessages(String message) {
        ContentValues values = new ContentValues();

        values.put(ChatDatabaseHelper.COLUMN_MESSAGE, message);
        long id = database.insert(ChatDatabaseHelper.TABLE_MESSAGES, null,
                values);

        ChatDataOptions data = new ChatDataOptions(message, id);
        chatAdapter.notifyDataSetChanged();
        chatArray.add(data);

    }

    public void deleteMessage(int id) {
        database.delete(ChatDatabaseHelper.TABLE_MESSAGES, "_id=?",
                new String[]{Long.toString(chatArray.get(id).get_id())});

        chatArray.remove(id);
        chatAdapter.notifyDataSetChanged();

        FragmentTransaction ftm = getSupportFragmentManager().beginTransaction();
        MessageFragment mf = (MessageFragment) getSupportFragmentManager().findFragmentById(R.id.chatFrameLayout);

        ftm.remove(mf);
        ftm.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 5) {
            Bundle extras = data.getExtras();
            int id = Integer.parseInt(extras.getString("id"));

            database.delete(ChatDatabaseHelper.TABLE_MESSAGES, "_id=?",
                    new String[]{Long.toString(chatArray.get(id).get_id())});

            chatArray.remove(id);
            chatAdapter.notifyDataSetChanged();
        }
    }

    private class ChatAdapter extends ArrayAdapter<ChatDataOptions> {

        public ChatAdapter(Context context) {
            super(context, 0);
        }

        public int getCount() {
            return chatArray.size();
        }

        public long getItemId(int position) { return chatArray.get(position).get_id();}
        public ChatDataOptions getItem(int position) {
            return chatArray.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null;
            if (position%2 == 0) {
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            } else {
                result = inflater.inflate(R.layout.chat_row_outgoing, null);
            }

            TextView message = (TextView) result.findViewById((R.id.textChat));
            message.setText(getItem(position).getMessage());
            return result;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        databaseHelper.close();
    }
}
