package ftn.masterproba.fragments;

import java.util.Calendar;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import applang.android.framework.activities.BaseActivity;
import applang.android.framework.fragments.AbstractInputFragment;
import applang.android.framework.utils.DatePickerUtils;
import applang.android.framework.utils.ViewUtils;
import ftn.masterproba.R;
import ftn.masterproba.contentProviders.FirmaContentProvider;
import ftn.masterproba.contentProviders.NaseljenoMestoContentProvider;
import ftn.masterproba.databases.FirmaTable;
import ftn.masterproba.databases.NaseljenoMestoTable;

public class FirmaEditFragment extends AbstractInputFragment {

	public static FirmaEditFragment newInstance(Long id) {
		FirmaEditFragment fragment = new FirmaEditFragment();
		Bundle args = new Bundle();
		args.putBoolean(ARG_KEY_IS_EDIT, true);
		args.putInt(ARG_KEY_LAYOUT_ID, R.layout.gen_fragment_firma_edit);
		args.putInt(ARG_KEY_ENTITY_NAME_RES_ID, R.string.ent_firma);
		args.putString(ARG_KEY_CONTENT_URI_STRING,
				FirmaContentProvider.CONTENT_URI.toString());

		String[] projection = {
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_ID,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_NAZIV,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_PIB, 
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_DOMACA, 
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_OPIS,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_ID_NASELJENO_MESTO,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_ID_NADFIRMA,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_TELEFON,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_DATUM_OSNIVANJA,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_ADRESA};
		args.putStringArray(ARG_KEY_PROJECTION, projection);
		args.putLong(ARG_KEY_ID, id);

		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	protected void initializeSpinners() {
		//spNaseljenoMesto
		{
			String[] projection = {
					NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_ID,
					NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_NAZIV };
			Cursor cursorEntity = getActivity().getContentResolver().query(
					NaseljenoMestoContentProvider.CONTENT_URI, projection, null,
					null, null);
			MatrixCursor cursorExtras = new MatrixCursor(new String[] {"_id", NaseljenoMestoTable.COLUMN_NAZIV});
			cursorExtras.addRow(new Object[] {BaseActivity.SPINNER_EMPTY_ID, getResources().getString(R.string.spinner_choose)});
			Cursor cursorExtended = new MergeCursor(new Cursor[] {cursorExtras, cursorEntity});
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
					android.R.layout.simple_spinner_dropdown_item, cursorExtended,
					new String[] { NaseljenoMestoTable.COLUMN_NAZIV },
					new int[] { android.R.id.text1 }, 0);
			Spinner spinner = (Spinner) getView().findViewById(R.id.spNaseljenoMesto);
			spinner.setAdapter(adapter);
		}
		//spNadfirma
		{
			String[] projection = {
					FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_ID,
					FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_NAZIV };
			//Cannnot reference itself
			String selection = FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_ID + " != ?";
			String selectionArgs[] = {mEntityId.toString()};
			Cursor cursorEntity = getActivity().getContentResolver().query(
					FirmaContentProvider.CONTENT_URI, projection, selection,
					selectionArgs, null);
			MatrixCursor cursorExtras = new MatrixCursor(new String[] {"_id", FirmaTable.COLUMN_NAZIV});
			cursorExtras.addRow(new Object[] {BaseActivity.SPINNER_EMPTY_ID, getResources().getString(R.string.spinner_none)});
			Cursor cursorExtended = new MergeCursor(new Cursor[] {cursorExtras, cursorEntity});
			SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
					android.R.layout.simple_spinner_dropdown_item, cursorExtended,
					new String[] { FirmaTable.COLUMN_NAZIV },
					new int[] { android.R.id.text1 }, 0);
			Spinner spinner = (Spinner) getView().findViewById(R.id.spNadfirma);
			spinner.setAdapter(adapter);
		}
	}

	@Override
	protected void putDataInViewControls(Cursor cursor) {
		cursor.moveToFirst();
		EditText etNaziv = (EditText) getView().findViewById(R.id.etNaziv);
		EditText etPib = (EditText) getView().findViewById(R.id.etPib);
		EditText etOpis = (EditText) getView().findViewById(R.id.etOpis);
		EditText etTelefon = (EditText) getView().findViewById(R.id.etTelefon);
		Spinner spinnerNaseljenoMesto = (Spinner) getView().findViewById(R.id.spNaseljenoMesto);
		Spinner spinnerNadfirma = (Spinner) getView().findViewById(R.id.spNadfirma);
		CheckBox cbDomaca = (CheckBox) getView().findViewById(R.id.cbDomaca);
		DatePicker dpDatumOsnivanja = (DatePicker) getView().findViewById(R.id.dpDatumOsnivanja);
		EditText etAdresa = (EditText) getView().findViewById(R.id.etAdresa);
		etNaziv.setText(cursor.getString(cursor
				.getColumnIndex(FirmaTable.COLUMN_NAZIV)));
		etPib.setText(cursor.getString(cursor
				.getColumnIndex(FirmaTable.COLUMN_PIB)));
		etOpis.setText(cursor.getString(cursor
				.getColumnIndex(FirmaTable.COLUMN_OPIS)));
		etTelefon.setText(cursor.getString(cursor
				.getColumnIndex(FirmaTable.COLUMN_TELEFON)));
		etAdresa.setText(cursor.getString(
				cursor.getColumnIndex(FirmaTable.COLUMN_ADRESA)));
		boolean isSelectedNaseljenoMesto = ViewUtils.selectSpinnerItemById(spinnerNaseljenoMesto, cursor.getLong(cursor
				.getColumnIndex(FirmaTable.COLUMN_ID_NASELJENO_MESTO)));
		if(!cursor.isNull(cursor.getColumnIndex(FirmaTable.COLUMN_DATUM_OSNIVANJA)))
			DatePickerUtils.updateDatePicker(dpDatumOsnivanja, cursor.getLong(cursor.getColumnIndex(FirmaTable.COLUMN_DATUM_OSNIVANJA)));
		if(!isSelectedNaseljenoMesto){
			Log.w(this.getClass().getCanonicalName(), "Spinner item was not selected.");
			Toast.makeText(getActivity(), "Spinner item was not selected.", Toast.LENGTH_SHORT).show();
		}
		ViewUtils.selectSpinnerItemById(spinnerNadfirma, cursor.getLong(cursor
				.getColumnIndex(FirmaTable.COLUMN_ID_NADFIRMA)));
		cbDomaca.setChecked(cursor.getInt(cursor.getColumnIndex(FirmaTable.COLUMN_DOMACA)) != 0);
	}

	@Override
	protected ContentValues getDataFromInput() {
		ContentValues cv = new ContentValues();

		String naziv = ((EditText) getView().findViewById(R.id.etNaziv))
				.getText().toString();
		String pib = ((EditText) getView().findViewById(R.id.etPib))
				.getText().toString();
		String opis = ((EditText) getView().findViewById(R.id.etOpis))
				.getText().toString();
		String telefon = ((EditText) getView().findViewById(R.id.etTelefon))
				.getText().toString();
		String adresa = ((EditText) getView().findViewById(R.id.etAdresa))
				.getText().toString();
		Spinner spinnerNaseljenoMesto = (Spinner) getView().findViewById(R.id.spNaseljenoMesto);
		long idNaseljenoMesto = spinnerNaseljenoMesto.getSelectedItemId();
		Spinner spinnerNadfirma = (Spinner) getView().findViewById(R.id.spNadfirma);
		long idNadfirma = spinnerNadfirma.getSelectedItemId();
		Boolean domaca = ((CheckBox) getView().findViewById(R.id.cbDomaca)).isChecked();
		DatePicker dpDatumOsnivanja = (DatePicker) getView().findViewById(R.id.dpDatumOsnivanja);
		Calendar datumOsnivanja = DatePickerUtils.getCalendarFromDatePicker(dpDatumOsnivanja);
		cv.put(FirmaTable.COLUMN_NAZIV, naziv);
		cv.put(FirmaTable.COLUMN_PIB, pib);
		cv.put(FirmaTable.COLUMN_OPIS, opis);
		cv.put(FirmaTable.COLUMN_ID_NASELJENO_MESTO, idNaseljenoMesto);
		cv.put(FirmaTable.COLUMN_DOMACA, domaca);
		cv.put(FirmaTable.COLUMN_TELEFON, telefon);
		cv.put(FirmaTable.COLUMN_ADRESA, adresa);
		if(datumOsnivanja == null)
			cv.putNull(FirmaTable.COLUMN_DATUM_OSNIVANJA);
		else
			cv.put(FirmaTable.COLUMN_DATUM_OSNIVANJA, datumOsnivanja.getTimeInMillis());
		if(idNadfirma == BaseActivity.SPINNER_EMPTY_ID)
			cv.putNull(FirmaTable.COLUMN_ID_NADFIRMA);
		else
			cv.put(FirmaTable.COLUMN_ID_NADFIRMA, idNadfirma);
		return cv;
	}

	@Override
	protected boolean checkInput() {
		boolean retVal = true;
		StringBuilder toastMessageBuilder = new StringBuilder();
		String naziv = ((EditText) getView().findViewById(R.id.etNaziv))
				.getText().toString();
		String pib = ((EditText) getView().findViewById(R.id.etPib))
				.getText().toString();
		String newLine = "";
		if (TextUtils.isEmpty(naziv)) {
			retVal = false;
			toastMessageBuilder.append(newLine
					+ getResources().getString(
							R.string.toast_input_required_single,
							getResources().getString(
									R.string.ent_firma_naziv)));
			newLine = "\n";
		}
		if (TextUtils.isEmpty(pib)) {
			retVal = false;
			toastMessageBuilder.append(newLine
					+ getResources().getString(
							R.string.toast_input_required_single,
							getResources().getString(
									R.string.ent_firma_pib)));
			newLine = "\n";
		}
		Long naseljenoMestoId = ((Spinner) getView().findViewById(R.id.spNaseljenoMesto)).getSelectedItemId();
		if(naseljenoMestoId == AdapterView.INVALID_ROW_ID || naseljenoMestoId == BaseActivity.SPINNER_EMPTY_ID){
			retVal = false;
			toastMessageBuilder.append(newLine
					+ getResources().getString(
							R.string.toast_input_not_selected,
							getResources().getString(
									R.string.ent_firma_naseljeno_mesto)));
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
