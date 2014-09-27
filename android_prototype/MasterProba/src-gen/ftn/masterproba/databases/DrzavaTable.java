package ftn.masterproba.databases;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DrzavaTable {
	// Database table
	public static final String TABLE_NAME = "drzava";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SIFRA = "sifra";
	public static final String COLUMN_NAZIV = "naziv";
	public static final String COLUMN_ZASTAVA = "zastava";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SIFRA + " text not null unique, "
			+ COLUMN_NAZIV + " text not null unique, " 
			+ COLUMN_ZASTAVA + " text"
			+ ");";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(DrzavaTable.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
	public static void deleteAll(SQLiteDatabase db){
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}
}
