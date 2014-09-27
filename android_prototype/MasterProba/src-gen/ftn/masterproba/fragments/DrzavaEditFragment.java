package ftn.masterproba.fragments;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import applang.android.framework.fragments.AbstractInputFragment;
import applang.android.framework.utils.DialogUtils;
import applang.android.framework.utils.ImageUtils;
import ftn.masterproba.R;
import ftn.masterproba.contentProviders.DrzavaContentProvider;
import ftn.masterproba.databases.DrzavaTable;

public class DrzavaEditFragment extends AbstractInputFragment {

	public static final int REQ_CODE_ZASTAVA_TAKE_PHOTO = 100;
	public static final int REQ_CODE_ZASTAVA_CHOOSE_IMAGE = 101;
	
	public static final String STATE_KEY_ZASTAVA_URI = "zastavaUri";
	
	public static DrzavaEditFragment newInstance(Long id){
		DrzavaEditFragment fragment = new DrzavaEditFragment();
		Bundle args = new Bundle();
		args.putBoolean(ARG_KEY_IS_EDIT, true);
		args.putInt(ARG_KEY_LAYOUT_ID, R.layout.gen_fragment_drzava_edit);
		args.putInt(ARG_KEY_ENTITY_NAME_RES_ID, R.string.ent_drzava);
		args.putString(ARG_KEY_CONTENT_URI_STRING,
				DrzavaContentProvider.CONTENT_URI.toString());
		
		String[] projection = {
				DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_ID,
				DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_SIFRA, 
				DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_NAZIV,
				DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_ZASTAVA};
		args.putStringArray(ARG_KEY_PROJECTION, projection);
		args.putLong(ARG_KEY_ID, id);

		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	protected void initializeSpinners() {
		//No spinners. Do nothing.
	}
	
	
	@Override
	protected void putDataInViewControls(Cursor cursor) {
		cursor.moveToFirst();
		EditText mEtSifra = ((EditText) getView().findViewById(R.id.etSifra));
		EditText mEtNaziv = ((EditText) getView().findViewById(R.id.etNaziv));
		String zastava = cursor.getString(cursor.getColumnIndex(DrzavaTable.COLUMN_ZASTAVA));
		ImageView ivZastava = (ImageView) getView().findViewById(R.id.ivZastava);
		ImageLoader.getInstance().displayImage(zastava, ivZastava,  
				ImageUtils.DISPLAY_OPTIONS, new ImageUtils.TagImageViewLoadingListener());
		mEtSifra.setText(cursor.getString(cursor
				.getColumnIndex(DrzavaTable.COLUMN_SIFRA)));
		mEtNaziv.setText(cursor.getString(cursor
				.getColumnIndex(DrzavaTable.COLUMN_NAZIV)));
	}

	@Override
	protected ContentValues getDataFromInput() {
		ContentValues cv = new ContentValues();

		String sifra = ((EditText) getView().findViewById(R.id.etSifra))
				.getText().toString();
		String naziv = ((EditText) getView().findViewById(R.id.etNaziv))
				.getText().toString();
		ImageView ivZastava = (ImageView) getView().findViewById(R.id.ivZastava);
		String zastava = ivZastava.getTag() != null ? (String) ivZastava.getTag() : null;
		cv.put(DrzavaTable.COLUMN_SIFRA, sifra);
		cv.put(DrzavaTable.COLUMN_NAZIV, naziv);
		cv.put(DrzavaTable.COLUMN_ZASTAVA, zastava);
		return cv;
	}

	@Override
	protected boolean checkInput() {
		boolean retVal = true;
		StringBuilder toastMessageBuilder = new StringBuilder();
		String sifra = ((EditText) getView().findViewById(R.id.etSifra))
				.getText().toString();
		String naziv = ((EditText) getView().findViewById(R.id.etNaziv))
				.getText().toString();
		String newLine = "";
		if (TextUtils.isEmpty(sifra)) {
			retVal = false;
			toastMessageBuilder.append(newLine + getResources().getString(
					R.string.toast_input_required_single,
					getResources().getString(R.string.ent_drzava_sifra)));
			newLine = "\n";
		}
		if (TextUtils.isEmpty(naziv)) {
			retVal = false;
			toastMessageBuilder.append(newLine + getResources()
							.getString(
									R.string.toast_input_required_single,
									getResources().getString(
											R.string.ent_drzava_naziv)));
			newLine = "\n";
		}
		// Checking if there is a toast message to display
		if (toastMessageBuilder.length() > 0)
			Toast.makeText(getActivity(), toastMessageBuilder, Toast.LENGTH_LONG)
					.show();
		return retVal;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode != Activity.RESULT_OK){
			super.onActivityResult(requestCode, resultCode, data);
			return;
		}
		if(requestCode == REQ_CODE_ZASTAVA_TAKE_PHOTO || requestCode == REQ_CODE_ZASTAVA_CHOOSE_IMAGE){
			Uri selectedImage = data.getData();
			ImageView imageView = (ImageView) getView().findViewById(R.id.ivZastava);
			ImageLoader.getInstance().displayImage(selectedImage != null ? selectedImage.toString() : null, imageView,  
					ImageUtils.DISPLAY_OPTIONS, new ImageUtils.TagImageViewLoadingListener());
//			image = ImageUtils.downsampleBitmap(getActivity(), selectedImage, 140);
			Toast.makeText(getActivity(), "Decoding image successful", Toast.LENGTH_SHORT)
					.show();
		}
		else {
			super.onActivityResult(requestCode, resultCode, data);
		}
		
	}

	@Override
	protected void setOnClickListeners() {
		//Zastava btn
		{
			Button btn = (Button) getView().findViewById(R.id.btnZastava);
			btn.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					DialogUtils.showChooseImageLoadDialog(getFragmentManager(),
							DrzavaEditFragment.this, REQ_CODE_ZASTAVA_TAKE_PHOTO,
							REQ_CODE_ZASTAVA_CHOOSE_IMAGE);
				}
			});
		}
		//ZastavaDismiss btn
		{
			Button btn = (Button) getView().findViewById(R.id.btnZastavaDismiss);
			btn.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View v) {
					ImageLoader.getInstance().displayImage(null, (ImageView) getView().findViewById(R.id.ivZastava), 
							ImageUtils.DISPLAY_OPTIONS, new ImageUtils.TagImageViewLoadingListener());
				}
			});
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		if(getView() == null)
			return;
		ImageView ivZastava = (ImageView) getView().findViewById(R.id.ivZastava);
		if (ivZastava.getTag() != null && ivZastava.getTag() instanceof String)
			outState.putString(STATE_KEY_ZASTAVA_URI, (String) ivZastava.getTag());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void initializeImageViews(Bundle savedInstanceState) {
		if(savedInstanceState == null)
			return;
		String zastavaUriString = savedInstanceState.getString(STATE_KEY_ZASTAVA_URI);
		ImageView ivZastava = (ImageView) getView().findViewById(R.id.ivZastava);
		ImageLoader.getInstance().displayImage(zastavaUriString, ivZastava,  
				ImageUtils.DISPLAY_OPTIONS, new ImageUtils.TagImageViewLoadingListener());
	}

}
