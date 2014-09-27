package ftn.masterproba.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.provider.SyncStateContract.Columns;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import applang.android.framework.activities.BaseActivity;
import applang.android.framework.fragments.AbstractInputFragment;
import applang.android.framework.utils.ViewUtils;
import ftn.masterproba.R;
import ftn.masterproba.contentProviders.DrzavaContentProvider;
import ftn.masterproba.contentProviders.NaseljenoMestoContentProvider;
import ftn.masterproba.databases.DrzavaTable;
import ftn.masterproba.databases.FirmaTable;
import ftn.masterproba.databases.NaseljenoMestoTable;

public class NaseljenoMestoNewFragment extends AbstractInputFragment {

	public static NaseljenoMestoNewFragment newInstance() {
		NaseljenoMestoNewFragment fragment = new NaseljenoMestoNewFragment();
		Bundle args = new Bundle();
		args.putBoolean(ARG_KEY_IS_EDIT, false);
		args.putInt(ARG_KEY_LAYOUT_ID, R.layout.gen_fragment_naseljeno_mesto_new);
		args.putInt(ARG_KEY_ENTITY_NAME_RES_ID, R.string.ent_naseljeno_mesto);
		args.putString(ARG_KEY_CONTENT_URI_STRING,
				NaseljenoMestoContentProvider.CONTENT_URI.toString());
		fragment.setArguments(args);
		return fragment;
	}

	public static NaseljenoMestoNewFragment newInstance(String initColumnName,
			long initValue) {
		NaseljenoMestoNewFragment fragment = newInstance();
		Bundle args = fragment.getArguments();
		args.putStringArray(ARG_KEY_NEW_INIT_COLUMN_NAMES,
				new String[] { initColumnName });
		args.putLongArray(ARG_KEY_NEW_INIT_LONG_VALUES,
				new long[] { initValue });
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected void initializeSpinners() {
		{
			String[] projection = {
					DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_ID,
					DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_NAZIV };
			Cursor cursorEntity = getActivity().getContentResolver().query(
					DrzavaContentProvider.CONTENT_URI, projection, null, null,
					null);
			MatrixCursor cursorExtras = new MatrixCursor(new String[] { "_id",
					DrzavaTable.COLUMN_NAZIV });
			cursorExtras.addRow(new Object[] { BaseActivity.SPINNER_EMPTY_ID,
					getResources().getString(R.string.spinner_choose) });
			Cursor cursorExtended = new MergeCursor(new Cursor[] {
					cursorExtras, cursorEntity });
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(
					getActivity(),
					android.R.layout.simple_spinner_dropdown_item,
					cursorExtended, new String[] { DrzavaTable.COLUMN_NAZIV },
					new int[] { android.R.id.text1 }, 0);
			Spinner spinner = (Spinner) getView().findViewById(R.id.spDrzava);
			spinner.setAdapter(adapter);
		}

		{
			String[] projection = {
					DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_ID,
					DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_NAZIV };
			Cursor cursorEntity = getActivity().getContentResolver().query(
					DrzavaContentProvider.CONTENT_URI, projection, null, null,
					null);
			MatrixCursor cursorExtras = new MatrixCursor(new String[] { "_id",
					DrzavaTable.COLUMN_NAZIV });
			cursorExtras.addRow(new Object[] { BaseActivity.SPINNER_EMPTY_ID,
					getResources().getString(R.string.spinner_none) });
			Cursor cursorExtended = new MergeCursor(new Cursor[] {
					cursorExtras, cursorEntity });
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(
					getActivity(),
					android.R.layout.simple_spinner_dropdown_item,
					cursorExtended, new String[] { DrzavaTable.COLUMN_NAZIV },
					new int[] { android.R.id.text1 }, 0);
			Spinner spinner = (Spinner) getView().findViewById(
					R.id.spDrugaDrzava);
			spinner.setAdapter(adapter);
		}
	}

	@Override
	protected ContentValues getDataFromInput() {
		ContentValues cv = new ContentValues();

		String naziv = ((EditText) getView().findViewById(R.id.etNaziv))
				.getText().toString();
		String postKod = ((EditText) getView().findViewById(R.id.etPostKod))
				.getText().toString();
		Spinner spinnerDrzava = (Spinner) getView().findViewById(R.id.spDrzava);
		long idDrzava = spinnerDrzava.getSelectedItemId();
		Spinner spinnerDrugaDrzava = (Spinner) getView().findViewById(
				R.id.spDrugaDrzava);
		long idDrugaDrzava = spinnerDrugaDrzava.getSelectedItemId();
		cv.put(NaseljenoMestoTable.COLUMN_NAZIV, naziv);
		cv.put(NaseljenoMestoTable.COLUMN_POST_KOD, postKod);
		cv.put(NaseljenoMestoTable.COLUMN_ID_DRZAVA, idDrzava);
		if (idDrugaDrzava == BaseActivity.SPINNER_EMPTY_ID)
			cv.putNull(NaseljenoMestoTable.COLUMN_ID_DRUGA_DRZAVA);
		else
			cv.put(NaseljenoMestoTable.COLUMN_ID_DRUGA_DRZAVA, idDrugaDrzava);
		return cv;
	}

	@Override
	protected boolean checkInput() {
		boolean retVal = true;
		StringBuilder toastMessageBuilder = new StringBuilder();
		String naziv = ((EditText) getView().findViewById(R.id.etNaziv))
				.getText().toString();
		String postKod = ((EditText) getView().findViewById(R.id.etPostKod))
				.getText().toString();
		String newLine = "";
		if (TextUtils.isEmpty(naziv)) {
			retVal = false;
			toastMessageBuilder.append(newLine
					+ getResources().getString(
							R.string.toast_input_required_single,
							getResources().getString(
									R.string.ent_naseljeno_mesto_naziv)));
			newLine = "\n";
		}
		if (TextUtils.isEmpty(postKod)) {
			retVal = false;
			toastMessageBuilder.append(newLine
					+ getResources().getString(
							R.string.toast_input_required_single,
							getResources().getString(
									R.string.ent_naseljeno_mesto_post_code)));
			newLine = "\n";
		}
		Long drzavaId = ((Spinner) getView().findViewById(R.id.spDrzava))
				.getSelectedItemId();
		if (drzavaId == AdapterView.INVALID_ROW_ID
				|| drzavaId == BaseActivity.SPINNER_EMPTY_ID) {
			retVal = false;
			toastMessageBuilder.append(newLine
					+ getResources().getString(
							R.string.toast_input_not_selected,
							getResources().getString(
									R.string.ent_naseljeno_mesto_drzava)));
			newLine = "\n";
		}
		// Checking if there is a toast message to display
		if (toastMessageBuilder.length() > 0)
			Toast.makeText(getActivity(), toastMessageBuilder,
					Toast.LENGTH_LONG).show();
		return retVal;
	}

	@Override
	protected void putDataInViewControls(Cursor cursor) {
		cursor.moveToFirst();
		{
			int columnIndex = cursor
					.getColumnIndex(NaseljenoMestoTable.COLUMN_ID_DRZAVA);
			if (columnIndex != -1) {
				Spinner spinner = (Spinner) getView().findViewById(
						R.id.spDrzava);
				ViewUtils.selectSpinnerItemById(spinner,
						cursor.getLong(columnIndex));
			}
		}
		{
			int columnIndex = cursor
					.getColumnIndex(NaseljenoMestoTable.COLUMN_ID_DRUGA_DRZAVA);
			if (columnIndex != -1) {
				Spinner spinner = (Spinner) getView().findViewById(
						R.id.spDrugaDrzava);
				ViewUtils.selectSpinnerItemById(spinner,
						cursor.getLong(columnIndex));
			}
		}
	}
 
	@Override
	protected void setOnClickListeners() {
		// Do nothing.
	}

	@Override
	protected void initializeImageViews(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

	}

}
