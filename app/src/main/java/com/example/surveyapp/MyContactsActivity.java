package com.example.surveyapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MyContactsActivity extends AppCompatActivity {

    private EditText etName, etPhone;
    private Button btnSave;
    private ListView listView;

    // SQLite Database variables
    private SQLiteDatabase db;
    private ArrayList<String> displayList;
    private ArrayList<Integer> idList;
    private ArrayAdapter<String> adapter;

    private int editingId = -1; // Keeps track of which contact we are editing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_contacts);

        etName = findViewById(R.id.etContactName);
        etPhone = findViewById(R.id.etContactPhone);
        btnSave = findViewById(R.id.btnSaveContact);
        listView = findViewById(R.id.listViewContacts);

        // 1. Create or Open the Local SQLite Database
        db = openOrCreateDatabase("MyContactsDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS contacts(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, phone VARCHAR);");

        displayList = new ArrayList<>();
        idList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listView.setAdapter(adapter);

        // Load saved contacts when activity opens
        loadContacts();

        // 2. Save or Update Button Logic
        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please enter both name and phone", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editingId == -1) {
                // INSERT NEW
                db.execSQL("INSERT INTO contacts (name, phone) VALUES ('" + name + "', '" + phone + "')");
                Toast.makeText(this, "Contact Saved", Toast.LENGTH_SHORT).show();
            } else {
                // UPDATE EXISTING
                db.execSQL("UPDATE contacts SET name='" + name + "', phone='" + phone + "' WHERE id=" + editingId);
                Toast.makeText(this, "Contact Updated", Toast.LENGTH_SHORT).show();
                editingId = -1;
                btnSave.setText("Save Contact");
            }

            etName.setText("");
            etPhone.setText("");
            loadContacts(); // Refresh the list
        });

        // 3. Click a Contact to Call, Edit, or Delete
        listView.setOnItemClickListener((parent, view, position, id) -> {
            int contactId = idList.get(position);
            String[] parts = displayList.get(position).split("\n");
            String contactName = parts[0];
            String contactPhone = parts[1];

            String[] options = {"Call " + contactName, "Edit", "Delete"};

            new AlertDialog.Builder(this)
                    .setTitle(contactName)
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            // CALL LOGIC (ACTION_DIAL opens dialer with the number typed in)
                            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                            dialIntent.setData(Uri.parse("tel:" + contactPhone));
                            startActivity(dialIntent);

                        } else if (which == 1) {
                            // EDIT LOGIC
                            etName.setText(contactName);
                            etPhone.setText(contactPhone);
                            editingId = contactId;
                            btnSave.setText("Update Contact");

                        } else if (which == 2) {
                            // DELETE LOGIC
                            db.execSQL("DELETE FROM contacts WHERE id=" + contactId);
                            Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                            loadContacts();
                        }
                    }).show();
        });
    }

    // Fetches data from SQLite and puts it in the list
    private void loadContacts() {
        displayList.clear();
        idList.clear();

        Cursor cursor = db.rawQuery("SELECT * FROM contacts", null);
        int idIndex = cursor.getColumnIndex("id");
        int nameIndex = cursor.getColumnIndex("name");
        int phoneIndex = cursor.getColumnIndex("phone");

        if (cursor.moveToFirst()) {
            do {
                idList.add(cursor.getInt(idIndex));
                displayList.add(cursor.getString(nameIndex) + "\n" + cursor.getString(phoneIndex));
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }
}