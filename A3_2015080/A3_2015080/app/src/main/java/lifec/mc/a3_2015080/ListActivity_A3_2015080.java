package lifec.mc.a3_2015080;

// Android Libraries

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

// OpenCSV Library

import com.opencsv.CSVWriter;

// Java Libraries

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListActivity_A3_2015080 extends AppCompatActivity {

    // Private Variables

    private ListView listView;
    private List<String> listData;
    private CSVWriter writer;
    private String tableValue;
    private DatabaseHelper_A3_2015080 databaseHelper;
    private ListAdapter listAdapter;

    // onCreate() Method

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list__a3_2015080);
        this.databaseHelper = new DatabaseHelper_A3_2015080(this);
        listView = findViewById(R.id.listview);
        this.writer = null;

        try {
            Cursor cursor = databaseHelper.getData();
            listData = new ArrayList<>();
            while (cursor.moveToNext()) {
                try {
                    helperLog("");
                    tableValue = cursor.getString(1) + "#" + cursor.getString(2) + "#" + cursor.getString(3) + "#" + cursor.getString(4) + "#" + cursor.getString(5) + "#" + cursor.getString(6) + "#" + cursor.getString(7);
                    helperLog("");
                    writer = new CSVWriter(new FileWriter(getExternalFilesDir(null) + "/SensorValues.csv", true), ',');
                    String[] entries = tableValue.split("#");
                    helperLog("");
                    writer.writeNext(entries);
                    helperLog("");
                    listData.add(cursor.getString(1) + cursor.getString(2) + cursor.getString(3) + cursor.getString(4) + cursor.getString(5) + cursor.getString(6) + cursor.getString(7));
                    helperLog("");

                } catch (IOException exception) {
                    Toast.makeText(this, "Error in List View", Toast.LENGTH_SHORT).show();
                }
            }
            helperLog("");
            closeAndSetAdapter(listData);
        } catch (IOException exception) {
            Toast.makeText(this, "Error in List View", Toast.LENGTH_SHORT).show();
        }
    }

    public void closeAndSetAdapter(List<String> listData) throws IOException {
        writer.close();
        listAdapter = new ArrayAdapter<>(ListActivity_A3_2015080.this, android.R.layout.simple_list_item_1, listData);
        listView.setAdapter(listAdapter);
    }

    public void helperLog(String logMsg) {
        Log.d("CHECK", logMsg);
    }
}
