package ftn.masterproba.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "masterProba.db";
	private static final int DATABASE_VERSION = 16;

	public DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		DrzavaTable.onCreate(db);
		NaseljenoMestoTable.onCreate(db);
		FirmaTable.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		if (!db.isReadOnly()) {
//	        // Disable foreign key constraints, for easier table dropping during upgrade 
//		    db.execSQL("PRAGMA foreign_keys = OFF;"); 
//	    } 
		FirmaTable.onUpgrade(db, oldVersion, newVersion);
		NaseljenoMestoTable.onUpgrade(db, oldVersion, newVersion);
		DrzavaTable.onUpgrade(db, oldVersion, newVersion);
//		if (!db.isReadOnly()) {
//	        // Re-enable foreign key constraints 
//		    db.execSQL("PRAGMA foreign_keys = ON;"); 
//	    } 
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
	        // Enable foreign key constraints 
		    db.execSQL("PRAGMA foreign_keys = ON;"); 
	    } 
	}
	
	


}
