package ftn.masterproba.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
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
import ftn.masterproba.databases.NaseljenoMestoTable;

public class NaseljenoMestoEditFragment extends AbstractInputFragment {

	public static NaseljenoMestoEditFragment newInstance(Long id) {
		NaseljenoMestoEditFragment fragment = new NaseljenoMestoEditFragment();
		Bundle args = new Bundle();
		args.putBoolean(ARG_KEY_IS_EDIT, true);
		args.putInt(ARG_KEY_LAYOUT_ID, R.layout.gen_fragment_naseljeno_mesto_edit);
		args.putInt(ARG_KEY_ENTITY_NAME_RES_ID, R.string.ent_naseljeno_mesto);
		args.putString(ARG_KEY_CONTENT_URI_STRING,
				NaseljenoMestoContentProvider.CONTENT_URI.toString());

		String[] projection = {
				NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_ID,
				NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_NAZIV,
				NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_POST_KOD,
				NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_ID_DRZAVA,
				NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_ID_DRUGA_DRZAVA};
		args.putStringArray(ARG_KEY_PROJECTION, projection);
		args.putLong(ARG_KEY_ID, id);

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
					DrzavaContentProvider.CONTENT_URI, projection, null,
					null, null);
			MatrixCursor cursorExtras = new MatrixCursor(new String[] {"_id", DrzavaTable.COLUMN_NAZIV});
			cursorExtras.addRow(new Object[] {BaseActivity.SPINNER_EMPTY_ID, getResources().getString(R.string.spinner_choose)});
			Cursor cursorExtended = new MergeCursor(new Cursor[] {cursorExtras, cursorEntity});
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
					android.R.layout.simple_spinner_dropdown_item, cursorExtended,
					new String[] { DrzavaTable.COLUMN_NAZIV },
					new int[] { android.R.id.text1 }, 0);
			Spinner spinner = (Spinner) getView().findViewById(R.id.spDrzava);
			spinner.setAdapter(adapter);
		}
		
		{
			String[] projection = {
					DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_ID,
					DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_NAZIV };
			Cursor cursorEntity = getActivity().getContentResolver().query(
					DrzavaContentProvider.CONTENT_URI, projection, null,
					null, null);
			MatrixCursor cursorExtras = new MatrixCursor(new String[] {"_id", DrzavaTable.COLUMN_NAZIV});
			cursorExtras.addRow(new Object[] {BaseActivity.SPINNER_EMPTY_ID, getResources().getString(R.string.spinner_none)});
			Cursor cursorExtended = new MergeCursor(new Cursor[] {cursorExtras, cursorEntity});
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
					android.R.layout.simple_spinner_dropdown_item, cursorExtended,
					new String[] { DrzavaTable.COLUMN_NAZIV },
					new int[] { android.R.id.text1 }, 0);
			Spinner spinner = (Spinner) getView().findViewById(R.id.spDrugaDrzava);
			spinner.setAdapter(adapter);
		}
	}

	@Override
	protected void putDataInViewControls(Cursor cursor) {
		cursor.moveToFirst();
		EditText etNaziv = ((EditText) getView().findViewById(R.id.etNaziv));
		EditText etPostKod = ((EditText) getView().findViewById(R.id.etPostKod));
		Spinner spDrzava = (Spinner) getView().findViewById(R.id.spDrzava);
		Spinner spDrugaDrzava = (Spinner) getView().findViewById(R.id.spDrugaDrzava);
		etNaziv.setText(cursor.getString(cursor
				.getColumnIndex(NaseljenoMestoTable.COLUMN_NAZIV)));
		etPostKod.setText(cursor.getString(cursor
				.getColumnIndex(NaseljenoMestoTable.COLUMN_POST_KOD)));
		boolean isSelected = ViewUtils.selectSpinnerItemById(spDrzava, cursor.getLong(cursor
				.getColumnIndex(NaseljenoMestoTable.COLUMN_ID_DRZAVA)));
		if(!isSelected){
			Log.e(this.getClass().getCanonicalName(), "Spinner item was not selected.");
			Toast.makeText(getActivity(), "Spinner item was not selected.", Toast.LENGTH_SHORT).show();
		}
		
		ViewUtils.selectSpinnerItemById(spDrugaDrzava, cursor.getLong(cursor
				.getColumnIndex(NaseljenoMestoTable.COLUMN_ID_DRUGA_DRZAVA)));
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
		Spinner spinnerDrugaDrzava = (Spinner) getView().findViewById(R.id.spDrugaDrzava);
		long idDrugaDrzava = spinnerDrugaDrzava.getSelectedItemId();
		cv.put(NaseljenoMestoTable.COLUMN_NAZIV, naziv);
		cv.put(NaseljenoMestoTable.COLUMN_POST_KOD, postKod);
		cv.put(NaseljenoMestoTable.COLUMN_ID_DRZAVA, idDrzava);
		if(idDrugaDrzava == BaseActivity.SPINNER_EMPTY_ID)
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
		Long drzavaId = ((Spinner) getView().findViewById(R.id.spDrzava)).getSelectedItemId();
		if(drzavaId == AdapterView.INVALID_ROW_ID || drzavaId == BaseActivity.SPINNER_EMPTY_ID){
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
	protected void setOnClickListeners() {
		// Do nothing.
	}

	@Override
	protected void initializeImageViews(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
	}

}
