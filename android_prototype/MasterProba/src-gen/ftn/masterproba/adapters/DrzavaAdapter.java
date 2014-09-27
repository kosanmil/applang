package ftn.masterproba.adapters;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import applang.android.framework.utils.ImageUtils;
import ftn.masterproba.R;
import ftn.masterproba.databases.DrzavaTable;
import ftn.masterproba.fragments.DrzavaDetailsFragment;

public class DrzavaAdapter extends CursorAdapter {

	private LayoutInflater mInflater;

	public DrzavaAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return mInflater.inflate(R.layout.gen_list_item_drzava, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		TextView tvSifra = (TextView) view.findViewById(R.id.tvSifra);
		TextView tvNaziv = (TextView) view.findViewById(R.id.tvNaziv);
		tvSifra.setText(cursor.getString(cursor
				.getColumnIndex(DrzavaTable.COLUMN_SIFRA)));
		tvNaziv.setText(cursor.getString(cursor
				.getColumnIndex(DrzavaTable.COLUMN_NAZIV)));
		String zastava = cursor.getString(cursor.getColumnIndex(DrzavaTable.COLUMN_ZASTAVA));
		ImageView ivZastava = (ImageView) view.findViewById(R.id.ivZastava);
		ImageLoader.getInstance().displayImage(zastava, ivZastava, ImageUtils.DISPLAY_OPTIONS);

	}

}
