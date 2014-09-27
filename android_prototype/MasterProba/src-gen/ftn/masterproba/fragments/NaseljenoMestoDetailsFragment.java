package ftn.masterproba.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import applang.android.framework.fragments.AbstractDetailsFragment;
import applang.android.framework.utils.ViewUtils;
import ftn.masterproba.R;
import ftn.masterproba.contentProviders.NaseljenoMestoContentProvider;
import ftn.masterproba.databases.DrzavaTable;
import ftn.masterproba.databases.FirmaTable;
import ftn.masterproba.databases.NaseljenoMestoTable;
import ftn.masterproba.impl.fragments.NaseljenoMestoEditFragmentImpl;

public class NaseljenoMestoDetailsFragment extends AbstractDetailsFragment{

	public static NaseljenoMestoDetailsFragment newInstance(Long id){
		NaseljenoMestoDetailsFragment fragment = new NaseljenoMestoDetailsFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_KEY_LAYOUT_ID, R.layout.gen_fragment_naseljeno_mesto_details);
		args.putInt(ARG_KEY_ENTITY_NAME_RES_ID, R.string.ent_naseljeno_mesto);
		args.putString(ARG_KEY_CONTENT_URI_STRING,
				NaseljenoMestoContentProvider.CONTENT_URI.toString());
		
		String[] projection = {
				NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_ID,
				NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_NAZIV, 
				NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_POST_KOD,
				"drzava" + "." + DrzavaTable.COLUMN_NAZIV + " as " + "drzava_" + DrzavaTable.COLUMN_NAZIV,
				"druga_drzava" + "." + DrzavaTable.COLUMN_NAZIV + " as " + "druga_drzava_" + DrzavaTable.COLUMN_NAZIV};
		args.putStringArray(ARG_KEY_PROJECTION, projection);
		args.putString(ARG_KEY_EDIT_FRAG_CLASS_NAME, 
				NaseljenoMestoEditFragmentImpl.class.getName());
		args.putLong(ARG_KEY_ID, id);

		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	protected void putDataInViewControls(Cursor cursor) {
		cursor.moveToFirst();
		TextView tvNaziv = ((TextView) getView().findViewById(R.id.tvNaziv));
		TextView tvPostKod = ((TextView) getView().findViewById(R.id.tvPostKod));
		TextView tvDrzava = (TextView) getView().findViewById(R.id.tvDrzava);
		TextView tvDrugaDrzava = (TextView) getView().findViewById(R.id.tvDrugaDrzava);
		tvNaziv.setText(cursor.getString(cursor
				.getColumnIndex(NaseljenoMestoTable.COLUMN_NAZIV)));
		tvPostKod.setText(cursor.getString(cursor
				.getColumnIndex(NaseljenoMestoTable.COLUMN_POST_KOD)));
		tvDrzava.setText(cursor.getString(cursor
				.getColumnIndex("drzava_" + DrzavaTable.COLUMN_NAZIV)));
		tvDrugaDrzava.setText(ViewUtils.getStringOrNoneFromCursor(getActivity(), cursor, "druga_drzava_" + DrzavaTable.COLUMN_NAZIV));
		final long finalEntityId = mEntityId;
		final String finalEntityName = cursor.getString(cursor
				.getColumnIndex(NaseljenoMestoTable.COLUMN_NAZIV));
		Button btnFirmaNaseljenoMesto = (Button) getView().findViewById(
				R.id.btnFirma_naseljenoMesto);
		btnFirmaNaseljenoMesto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FirmaListFragment fragment = FirmaListFragment
						.newInstance(FirmaTable.TABLE_NAME + "."
								+ FirmaTable.COLUMN_ID_NASELJENO_MESTO,
								finalEntityId,
								getResources().getString(R.string.ent_firma_naseljeno_mesto) + ": " + finalEntityName);
				getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).addToBackStack(null)
				.commit();
			}
		});
	}

}
