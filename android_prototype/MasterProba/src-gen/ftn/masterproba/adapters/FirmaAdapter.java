package ftn.masterproba.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import applang.android.framework.utils.ViewUtils;
import ftn.masterproba.R;
import ftn.masterproba.databases.DrzavaTable;
import ftn.masterproba.databases.FirmaTable;
import ftn.masterproba.databases.NaseljenoMestoTable;

public class FirmaAdapter extends CursorAdapter {

	private LayoutInflater mInflater;

	public FirmaAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.gen_list_item_firma, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView tvNaziv = (TextView) view.findViewById(R.id.tvNaziv);
		TextView tvPib = (TextView) view.findViewById(R.id.tvPib);
		TextView tvNaseljenoMesto = (TextView) view
				.findViewById(R.id.tvNaseljenoMesto);
		TextView tvNadfirma = (TextView) view.findViewById(R.id.tvNadfirma);
		tvNaziv.setText(cursor.getString(cursor
				.getColumnIndex(FirmaTable.COLUMN_NAZIV)));
		tvPib.setText(cursor.getString(cursor
				.getColumnIndex(FirmaTable.COLUMN_PIB)));
		tvNaseljenoMesto.setText(cursor.getString(cursor
				.getColumnIndex("naseljeno_mesto_" + NaseljenoMestoTable.COLUMN_NAZIV)));
		tvNadfirma.setText(ViewUtils.getStringOrNoneFromCursor(context, cursor, "nadfirma_" + FirmaTable.COLUMN_NAZIV));
	}
}
