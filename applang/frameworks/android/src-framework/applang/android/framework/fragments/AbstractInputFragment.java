package applang.android.framework.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import applang.android.framework.activities.BaseActivity;
import applang.android.framework.utils.ViewUtils;
import ftn.masterproba.R;
import ftn.masterproba.databases.DrzavaTable;

public abstract class AbstractInputFragment extends AbstractFragment {

	public static final String ARG_KEY_ID = "id";
	public static final String ARG_KEY_PROJECTION = "arg_projection";
	public static final String ARG_KEY_IS_EDIT = "is_edit";
	public static final String ARG_KEY_NEW_INIT_COLUMN_NAMES = "new_init_column_names";
	public static final String ARG_KEY_NEW_INIT_LONG_VALUES = "new_init_long_values";

	protected Long mEntityId = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		String title;
		if(isEdit())
			title = getResources()
					.getString(R.string.title_edit,
							getResources().getString(mEntityNameResId));
		else
			title = getResources()
			.getString(R.string.title_new,
					getResources().getString(mEntityNameResId));
			
		((BaseActivity) getActivity()).setTitle(title);
		((BaseActivity) getActivity()).setSubtitle(null);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if(isEdit()){
			mEntityId = getArguments().getLong(ARG_KEY_ID);
			if (mEntityId == null) {
				printNotFoundToastMessage();
				getFragmentManager().popBackStackImmediate();
			}
		}
		initializeSpinners();
		if(savedInstanceState == null)
			populateFields();
		setOnClickListeners();
		initializeImageViews(savedInstanceState);
		super.onViewCreated(view, savedInstanceState);
	}


	/**
	 * Initializes spinners with the Adapters that are tied to those spinners and selects the value tied to the entity. 
	 * If the fragment does not contain spinners, this method does nothing.
	 */
	protected abstract void initializeSpinners();
	
	protected abstract void setOnClickListeners();
	
	protected abstract void initializeImageViews(Bundle savedInstanceState);
	
	protected void populateFields() {
		Cursor cursor;
		if(isEdit())
		{
			String[] projection = getArguments().getStringArray(ARG_KEY_PROJECTION);
			cursor = getActivity().getContentResolver().query(
					getContentUri(mEntityId), projection, null, null, null);
			if (cursor == null || cursor.getCount() == 0) {
				printNotFoundToastMessage();
				if (cursor != null)
					cursor.close();
				getFragmentManager().popBackStackImmediate();
				return;
			}
				
		} else {
			String[] newInitColumnNames = getArguments().getStringArray(ARG_KEY_NEW_INIT_COLUMN_NAMES);
			long[] newInitValuesLong = getArguments().getLongArray(ARG_KEY_NEW_INIT_LONG_VALUES);
			if(newInitColumnNames == null || newInitValuesLong == null || newInitColumnNames.length != newInitValuesLong.length){
				Log.i(this.getClass().getCanonicalName(), "Not calling the putDataInViewControls(), because the init values are non existant or invalid.");
				return;
			}
			//Have to do this due to not being able to cast int[] to Object[]
			Object[] newInitValues = new Object[newInitValuesLong.length];
			for (int i = 0; i < newInitValuesLong.length; i++)
				newInitValues[i] = newInitValuesLong[i];
			cursor = new MatrixCursor(newInitColumnNames);
			((MatrixCursor) cursor).addRow(newInitValues);
		}
		
		putDataInViewControls(cursor);
		if (cursor != null)
			cursor.close();
	}
	

	/**
	 * Fills the view's controls with the data from the cursor. <br/>
	 * If no data is loaded (for example, if it's a 'new' input), leave the method blank when overriding it. <br/>
	 * No need to close the cursor, it is automatically closed by the caller of
	 * this method.
	 * @param cursor
	 */
	protected abstract void putDataInViewControls(Cursor cursor);

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		inflater.inflate(R.menu.input_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_confirm:
			if(isEdit())
				edit();
			else
				create();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected void edit() {
		if (checkInput()) {
			ContentValues cv = getDataFromInput();
			try{
				getActivity().getContentResolver().update(getContentUri(mEntityId),
						cv, null, null);
				Toast.makeText(
						getActivity(),
						getResources().getString(R.string.toast_edited,
								getResources().getString(mEntityNameResId)),
						Toast.LENGTH_SHORT).show();
				ViewUtils.closeKeyboard(getActivity());
				getFragmentManager().popBackStackImmediate();
			} catch (SQLException e) {
				showDatabaseErrorDialog(e);
			}
		}
	}
	
	protected void create() {
		if (checkInput()) {
			ContentValues cv = getDataFromInput();
			try{
				getActivity().getContentResolver().insert(
						getContentUri(), cv);
				Toast.makeText(
						getActivity(),
						getResources().getString(R.string.toast_created,
								getResources().getString(mEntityNameResId)),
						Toast.LENGTH_SHORT).show();
				ViewUtils.closeKeyboard(getActivity());
				getFragmentManager().popBackStackImmediate();
			} catch (SQLException e) {
				showDatabaseErrorDialog(e);
			}
		}
	}

	/**
	 * Gets the data from all the inputs in the fragment and puts them in the
	 * content provider.
	 * 
	 * @return
	 */
	protected abstract ContentValues getDataFromInput();

	/**
	 * Checks the input for errors, returns true if there are no errors, or
	 * false if there are errors. <br/>
	 * If there are errors, a Toast message should appear explaining the errors.
	 * 
	 * @return
	 */
	protected abstract boolean checkInput();

	private void printNotFoundToastMessage() {
		Toast.makeText(
				getActivity(),
				getResources().getString(R.string.toast_not_found,
						getResources().getString(mEntityNameResId)),
				Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Returns true if this input is in edit mode. False if it's in new mode.
	 * @return
	 */
	protected boolean isEdit(){
		return getArguments().getBoolean(ARG_KEY_IS_EDIT);
	}
}
