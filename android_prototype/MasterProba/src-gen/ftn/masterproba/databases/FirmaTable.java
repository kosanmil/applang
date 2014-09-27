package ftn.masterproba.databases;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class FirmaTable {
	// Database table
	public static final String TABLE_NAME = "firma";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_PIB = "pib";
	public static final String COLUMN_NAZIV = "naziv";
	public static final String COLUMN_DOMACA = "domaca";
	public static final String COLUMN_OPIS = "opis";
	public static final String COLUMN_ID_NASELJENO_MESTO = "id_naseljeno_mesto";
	public static final String COLUMN_ID_NADFIRMA = "id_nadfirma";
	public static final String COLUMN_TELEFON = "telefon";
	public static final String COLUMN_DATUM_OSNIVANJA = "datumOsnivanja";
	public static final String COLUMN_ADRESA = "adresa";
	
	// Database creation SQL statement
	private static final String DATABASE_CREATE = 
			"CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_PIB + " text not null unique, "
			+ COLUMN_NAZIV + " text not null, "
			+ COLUMN_DOMACA + " integer not null, "
			+ COLUMN_OPIS + " text, " 
			+ COLUMN_ID_NASELJENO_MESTO + " integer not null, "
			+ COLUMN_ID_NADFIRMA + " integer, "
			+ COLUMN_TELEFON + " text, "
			+ COLUMN_DATUM_OSNIVANJA + " integer, "
			+ COLUMN_ADRESA + " text, "
			+ "FOREIGN KEY (" + COLUMN_ID_NASELJENO_MESTO + ") REFERENCES " 
			+ NaseljenoMestoTable.TABLE_NAME + "(" + NaseljenoMestoTable.COLUMN_ID + "), "
			+ "FOREIGN KEY (" + COLUMN_ID_NADFIRMA + ") REFERENCES " 
			+ FirmaTable.TABLE_NAME + "(" + FirmaTable.COLUMN_ID + ") "
			+ ");";

	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion) {
		Log.w(FirmaTable.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
	public static void deleteAll(SQLiteDatabase db){
		db.execSQL("DELETE FROM " + TABLE_NAME);
	}
}
