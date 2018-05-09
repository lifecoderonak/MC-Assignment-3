package lifec.mc.a3_2015080;

// Android Libraries

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper_A3_2015080 extends SQLiteOpenHelper {

    // Database Values

    private static final String TABLE_NAME = "sensor_database";

    private static final String COLUMN1 = "ACCELEROMETER";
    private static final String COLUMN2 = "GYROSCOPE";
    private static final String COLUMN3 = "GPS";
    private static final String COLUMN4 = "NETWORK";
    private static final String COLUMN5 = "WIFI";
    private static final String COLUMN6 = "MICROPHONE";

    private SQLiteDatabase insertDataDatabase;
    private SQLiteDatabase readDataDatabase;

    // Constructor

    public DatabaseHelper_A3_2015080(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    // onCreate() Method

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, Timestamp DATETIME DEFAULT CURRENT_TIMESTAMP, " + this.COLUMN1 + " TEXT," + this.COLUMN2 + " TEXT," + this.COLUMN3 + " TEXT," + this.COLUMN4 + " TEXT," + this.COLUMN5 + " TEXT," + this.COLUMN6 + " TEXT)");
    }

    // onUpgrade() Method

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP IF TABLE EXIST ";
        db.execSQL(sql + TABLE_NAME);
        onCreate(db);
    }

    // insertData into Database Method

    public boolean insertData(String accelerometer, String gyroscope, String gps, String network, String wifi, String mic) {
        this.insertDataDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.COLUMN1, "\n" + accelerometer + "\n");
        contentValues.put(this.COLUMN2, gyroscope + "\n");
        contentValues.put(this.COLUMN3, gps + "\n");
        contentValues.put(this.COLUMN4, network + "\n");
        contentValues.put(this.COLUMN5, wifi + "\n");
        contentValues.put(this.COLUMN6, mic + "\n");
        if (insertDataDatabase.insert(TABLE_NAME, null, contentValues) == -1)
            return false;
        return true;
    }

    // For getting the data from the database

    public Cursor getData() {
        this.readDataDatabase = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = readDataDatabase.rawQuery(query, null);
        return cursor;
    }
}
