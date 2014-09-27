package ftn.masterproba.adapters;

import ftn.masterproba.R;
import ftn.masterproba.databases.DrzavaTable;
import ftn.masterproba.databases.FirmaTable;
import ftn.masterproba.databases.NaseljenoMestoTable;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import applang.android.framework.utils.ViewUtils;

public class NaseljenoMestoAdapter extends CursorAdapter {

	private LayoutInflater mInflater;

	public NaseljenoMestoAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.gen_list_item_naseljeno_mesto, parent, false);
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView tvNaziv = (TextView) view.findViewById(R.id.tvNaziv);
		TextView tvPostKod = (TextView) view.findViewById(R.id.tvPostKod);
		TextView tvDrzava = (TextView) view.findViewById(R.id.tvDrzava);
		TextView tvDrugaDrzava = (TextView) view.findViewById(R.id.tvDrugaDrzava);
		tvNaziv.setText(cursor.getString(cursor
				.getColumnIndex(NaseljenoMestoTable.COLUMN_NAZIV)));
		tvPostKod.setText(cursor.getString(cursor
				.getColumnIndex(NaseljenoMestoTable.COLUMN_POST_KOD)));
		tvDrzava.setText(cursor.getString(cursor
				.getColumnIndex("drzava_" + DrzavaTable.COLUMN_NAZIV)));
		tvDrugaDrzava.setText(ViewUtils.getStringOrNoneFromCursor(context, cursor, "druga_drzava_" + DrzavaTable.COLUMN_NAZIV));
	}


}
