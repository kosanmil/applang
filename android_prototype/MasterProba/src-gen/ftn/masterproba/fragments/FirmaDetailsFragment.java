package ftn.masterproba.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import applang.android.framework.fragments.AbstractDetailsFragment;
import applang.android.framework.utils.ButtonMethodsUtils;
import applang.android.framework.utils.DatePickerUtils;
import applang.android.framework.utils.ViewUtils;
import ftn.masterproba.R;
import ftn.masterproba.contentProviders.FirmaContentProvider;
import ftn.masterproba.databases.FirmaTable;
import ftn.masterproba.databases.NaseljenoMestoTable;
import ftn.masterproba.impl.fragments.FirmaEditFragmentImpl;

public class FirmaDetailsFragment extends AbstractDetailsFragment {

	public static FirmaDetailsFragment newInstance(Long id) {
		FirmaDetailsFragment fragment = new FirmaDetailsFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_KEY_LAYOUT_ID,
				R.layout.gen_fragment_firma_details);
		args.putInt(ARG_KEY_ENTITY_NAME_RES_ID, R.string.ent_firma);
		args.putString(ARG_KEY_CONTENT_URI_STRING,
				FirmaContentProvider.CONTENT_URI.toString());

		String[] projection = {
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_ID,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_NAZIV,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_PIB,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_DOMACA,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_OPIS,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_TELEFON,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_DATUM_OSNIVANJA,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_ADRESA,
				"naseljeno_mesto" + "." + NaseljenoMestoTable.COLUMN_NAZIV + " as " + "naseljeno_mesto_" + NaseljenoMestoTable.COLUMN_NAZIV,
				"nadfirma" + "." + FirmaTable.COLUMN_NAZIV + " as " + "nadfirma_" + FirmaTable.COLUMN_NAZIV};
		args.putStringArray(ARG_KEY_PROJECTION, projection);
		args.putString(ARG_KEY_EDIT_FRAG_CLASS_NAME,
				FirmaEditFragmentImpl.class.getName());
		args.putLong(ARG_KEY_ID, id);

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected void putDataInViewControls(Cursor cursor) {
		cursor.moveToFirst();
		TextView tvNaziv = (TextView) getView().findViewById(R.id.tvNaziv);
		TextView tvPib = (TextView) getView().findViewById(R.id.tvPib);
		TextView tvOpis = (TextView) getView().findViewById(R.id.tvOpis);
		TextView tvNaseljenoMesto = (TextView) getView().findViewById(
				R.id.tvNaseljenoMesto);
		TextView tvDomaca = (TextView) getView().findViewById(R.id.tvDomaca);
		TextView tvTelefon = (TextView) getView().findViewById(R.id.tvTelefon);
		TextView tvNadfirma = (TextView) getView().findViewById(R.id.tvNadfirma);
		TextView tvDatumOsnivanja = (TextView) getView().findViewById(R.id.tvDatumOsnivanja);
		TextView tvAdresa = (TextView) getView().findViewById(R.id.tvAdresa);
		tvNaziv.setText(cursor.getString(cursor
				.getColumnIndex(FirmaTable.COLUMN_NAZIV)));
		tvPib.setText(cursor.getString(cursor
				.getColumnIndex(FirmaTable.COLUMN_PIB)));
		tvOpis.setText(cursor.getString(cursor
				.getColumnIndex(FirmaTable.COLUMN_OPIS)));
		tvNaseljenoMesto.setText(cursor.getString(cursor
				.getColumnIndex("naseljeno_mesto_" + NaseljenoMestoTable.COLUMN_NAZIV)));
		final String telefon = cursor.getString(cursor
				.getColumnIndex(FirmaTable.COLUMN_TELEFON));
		tvTelefon.setText(telefon);
		final String adresa = cursor.getString(cursor.getColumnIndex(FirmaTable.COLUMN_ADRESA));
		tvAdresa.setText(adresa);
		tvDomaca.setText(ViewUtils
				.getYesNoStringByBoolean(getResources(), 
						cursor.getInt(cursor.getColumnIndex(FirmaTable.COLUMN_DOMACA)) != 0));
		tvNadfirma.setText(ViewUtils.getStringOrNoneFromCursor(getActivity(), cursor, "nadfirma_" + FirmaTable.COLUMN_NAZIV));
		if(!cursor.isNull(cursor.getColumnIndex(FirmaTable.COLUMN_DATUM_OSNIVANJA)))
			tvDatumOsnivanja.setText(DatePickerUtils.formatDateByLocale(getActivity(), 
					cursor.getLong(cursor.getColumnIndex(FirmaTable.COLUMN_DATUM_OSNIVANJA))));
		final long finalEntityId = mEntityId;
		final String finalEntityName = cursor.getString(cursor
				.getColumnIndex(FirmaTable.COLUMN_NAZIV));
		Button btnFirmaNadfirma = (Button) getView().findViewById(
				R.id.btnFirma_nadfirma);
		btnFirmaNadfirma.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FirmaListFragment fragment = FirmaListFragment
						.newInstance(FirmaTable.TABLE_NAME + "."
								+ FirmaTable.COLUMN_ID_NADFIRMA,
								finalEntityId,
								getResources().getString(R.string.ent_firma_nadfirma) + ": " + finalEntityName);
				getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).addToBackStack(null)
				.commit();
			}
		});
		
		ImageButton btnTelefonCall = (ImageButton) getView().findViewById(R.id.btnTelefonCall);
		ImageButton btnTelefonMessage = (ImageButton) getView().findViewById(R.id.btnTelefonMessage);
		if(TextUtils.isEmpty(telefon)){
			btnTelefonCall.setVisibility(View.GONE);
			btnTelefonMessage.setVisibility(View.GONE);
		}
		else{
			//Telefon Call onClickListener
			btnTelefonCall.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ButtonMethodsUtils.dialTelephoneNumber(getActivity(), telefon);
				}
			});
			//Telefon Message onClickListener
			btnTelefonMessage.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ButtonMethodsUtils.messageTelephoneNumber(getActivity(), telefon);
				}
			});
		}
		
		ImageButton btnAdresaMap = (ImageButton) getView().findViewById(R.id.btnAdresaMap);
		if(TextUtils.isEmpty(adresa)){
			btnAdresaMap.setVisibility(View.GONE);
		}
		else{
			//Telefon Call onClickListener
			btnAdresaMap.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					ButtonMethodsUtils.openMapForAddress(getActivity(), adresa);
				}
			});
		}
	}

}
