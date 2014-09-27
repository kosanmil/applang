package ftn.masterproba.contentProviders;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import ftn.masterproba.databases.DatabaseOpenHelper;
import ftn.masterproba.databases.DrzavaTable;
import ftn.masterproba.impl.databases.DatabaseOpenHelperImpl;

public class DrzavaContentProvider extends ContentProvider {

	// database
	private DatabaseOpenHelperImpl database;

	// used for the UriMacher
	private static final int MULTIPLE = 10;
	private static final int SINGLE = 20;

	private static final String AUTHORITY = "ftn.masterProba.drzavaContentProvider";
	private static final String BASE_PATH = "drzava";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
			+ "/" + BASE_PATH);

	public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
			+ "/todos";
	public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
			+ "/todo";

	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	static {
		sURIMatcher.addURI(AUTHORITY, BASE_PATH, MULTIPLE);
		sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", SINGLE);
	}

	@Override
	public boolean onCreate() {
		database = new DatabaseOpenHelperImpl(getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

		// checkColumns(projection);

		queryBuilder.setTables(DrzavaTable.TABLE_NAME);
		// Proverava da li se URI zavrsava sa "/#", ako se zavrsava,
		// dodaje u Where klauzu taj Id.
		int uriType = sURIMatcher.match(uri);
		switch (uriType) {
		case MULTIPLE:
			break;
		case SINGLE:
			// adding the ID to the original query
			queryBuilder.appendWhere(DrzavaTable.COLUMN_ID + "="
					+ uri.getLastPathSegment());
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		SQLiteDatabase db = database.getWritableDatabase();

		Cursor cursor = queryBuilder.query(db, projection, selection,
				selectionArgs, null, null, sortOrder);
		// make sure that potential listeners are getting notified
		cursor.setNotificationUri(getContext().getContentResolver(), uri);

		return cursor;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase db = database.getWritableDatabase();
		Long id = (long) 0;
		switch (uriType) {
		case MULTIPLE:
			id = db.insertOrThrow(DrzavaTable.TABLE_NAME, null, values);
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(BASE_PATH + "/" + id);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase db = database.getWritableDatabase();
		int rowsDeleted = 0;
		switch (uriType) {
		case MULTIPLE:
			rowsDeleted = db.delete(DrzavaTable.TABLE_NAME, selection,
					selectionArgs);
			break;
		case SINGLE:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(DrzavaTable.TABLE_NAME,
						DrzavaTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsDeleted = db.delete(DrzavaTable.TABLE_NAME,
						DrzavaTable.COLUMN_ID + "=" + id + " and " + selection,
						selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int uriType = sURIMatcher.match(uri);
		SQLiteDatabase sqlDB = database.getWritableDatabase();
		int rowsUpdated = 0;
		switch (uriType) {
		case MULTIPLE:
			rowsUpdated = sqlDB.update(DrzavaTable.TABLE_NAME,
					values, selection, selectionArgs);
			break;
		case SINGLE:
			String id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = sqlDB.update(
						DrzavaTable.TABLE_NAME, values,
						DrzavaTable.COLUMN_ID + "=" + id, null);
			} else {
				rowsUpdated = sqlDB.update(
						DrzavaTable.TABLE_NAME, values,
						DrzavaTable.COLUMN_ID + "=" + id
								+ " and " + selection, selectionArgs);
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	// private void checkColumns(String[] projection) {
	// String[] available = {
	// ZanrTable.TABLE_NAME + "." + ZanrTable.COLUMN_ID,
	// ZanrTable.TABLE_NAME + "." + ZanrTable.COLUMN_NAZIV };
	// if (projection != null) {
	// HashSet<String> requestedColumns = new HashSet<String>(
	// Arrays.asList(projection));
	// HashSet<String> availableColumns = new HashSet<String>(
	// Arrays.asList(available));
	// // check if all columns which are requested are available
	// if (!availableColumns.containsAll(requestedColumns)) {
	// throw new IllegalArgumentException(
	// "Unknown columns in projection");
	// }
	// }
	// }

}
