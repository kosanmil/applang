package ftn.masterproba.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import applang.android.framework.fragments.AbstractDetailsFragment;
import applang.android.framework.utils.ImageUtils;

import com.nostra13.universalimageloader.core.ImageLoader;

import ftn.masterproba.R;
import ftn.masterproba.contentProviders.DrzavaContentProvider;
import ftn.masterproba.databases.DrzavaTable;
import ftn.masterproba.databases.NaseljenoMestoTable;
import ftn.masterproba.impl.fragments.DrzavaEditFragmentImpl;

public class DrzavaDetailsFragment extends AbstractDetailsFragment {

	public static final String STATE_KEY_ZASTAVA_URI = "zastavaUri";
	
	public static DrzavaDetailsFragment newInstance(Long id) {
		DrzavaDetailsFragment fragment = new DrzavaDetailsFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_KEY_LAYOUT_ID, R.layout.gen_fragment_drzava_details);
		args.putInt(ARG_KEY_ENTITY_NAME_RES_ID, R.string.ent_drzava);
		args.putString(ARG_KEY_CONTENT_URI_STRING,
				DrzavaContentProvider.CONTENT_URI.toString());

		String[] projection = {
				DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_ID,
				DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_SIFRA,
				DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_NAZIV,
				DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_ZASTAVA};
		args.putStringArray(ARG_KEY_PROJECTION, projection);
		args.putString(ARG_KEY_EDIT_FRAG_CLASS_NAME,
				DrzavaEditFragmentImpl.class.getName());
		args.putLong(ARG_KEY_ID, id);

		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected void putDataInViewControls(Cursor cursor) {
//		cursor.moveToFirst();
		TextView mTvSifra = ((TextView) getView().findViewById(R.id.tvSifra));
		TextView mTvNaziv = ((TextView) getView().findViewById(R.id.tvNaziv));
		String zastava = cursor.getString(cursor.getColumnIndex(DrzavaTable.COLUMN_ZASTAVA));
		ImageView ivZastava = (ImageView) getView().findViewById(R.id.ivZastava);
		ImageLoader.getInstance().displayImage(zastava, ivZastava, ImageUtils.DISPLAY_OPTIONS);
		mTvSifra.setText(cursor.getString(cursor
				.getColumnIndex(DrzavaTable.COLUMN_SIFRA)));
		mTvNaziv.setText(cursor.getString(cursor
				.getColumnIndex(DrzavaTable.COLUMN_NAZIV)));
		final long finalEntityId = mEntityId;
		final String finalEntityName = cursor.getString(cursor
				.getColumnIndex(DrzavaTable.COLUMN_NAZIV));
		Button btnNaseljenoMestoDrzava = (Button) getView().findViewById(
				R.id.btnNaseljenoMesto_drzava);
		btnNaseljenoMestoDrzava.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NaseljenoMestoListFragment fragment = NaseljenoMestoListFragment
						.newInstance(NaseljenoMestoTable.TABLE_NAME + "."
								+ NaseljenoMestoTable.COLUMN_ID_DRZAVA,
								finalEntityId,
								getResources().getString(R.string.ent_naseljeno_mesto_drzava) + ": " + finalEntityName);
				getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).addToBackStack(null)
				.commit();
			}
		});
		
		Button btnNaseljenoMestoDrugaDrzava = (Button) getView().findViewById(
				R.id.btnNaseljenoMesto_drugaDrzava);
		btnNaseljenoMestoDrugaDrzava.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NaseljenoMestoListFragment fragment = NaseljenoMestoListFragment
						.newInstance(NaseljenoMestoTable.TABLE_NAME + "."
								+ NaseljenoMestoTable.COLUMN_ID_DRUGA_DRZAVA,
								finalEntityId,
								getResources().getString(R.string.ent_naseljeno_mesto_druga_drzava) + ": " + finalEntityName);
				getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).addToBackStack(null)
				.commit();
			}
		});
	}

}
