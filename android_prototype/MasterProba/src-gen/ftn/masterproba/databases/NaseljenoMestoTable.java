package ftn.masterproba.databases;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class NaseljenoMestoTable {
	// Database table
	public static final String TABLE_NAME = "naseljeno_mesto";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAZIV = "naziv";
	public static final String COLUMN_POST_KOD = "post_kod";
	public static final String COLUMN_ID_DRZAVA = "id_drzava";
	public static final String COLUMN_ID_DRUGA_DRZAVA = "id_druga_drzava";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_NAZIV + " text not null, "
			+ COLUMN_POST_KOD + " text, "
			+ COLUMN_ID_DRZAVA + " integer not null, "
			+ COLUMN_ID_DRUGA_DRZAVA + " integer, "
			+ "FOREIGN KEY (" + COLUMN_ID_DRZAVA + ") REFERENCES " 
			+ DrzavaTable.TABLE_NAME + "(" + DrzavaTable.COLUMN_ID + "), "
			+ "FOREIGN KEY (" + COLUMN_ID_DRUGA_DRZAVA + ") REFERENCES " 
			+ DrzavaTable.TABLE_NAME + "(" + DrzavaTable.COLUMN_ID + "), "
			+ "UNIQUE (" + COLUMN_NAZIV + "," + COLUMN_ID_DRZAVA + ")"
			+ ");";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(NaseljenoMestoTable.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
	public static void deleteAll(SQLiteDatabase db){
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}
}
