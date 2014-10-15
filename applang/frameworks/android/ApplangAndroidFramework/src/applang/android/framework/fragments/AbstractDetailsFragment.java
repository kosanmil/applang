package applang.android.framework.fragments;

import java.lang.reflect.InvocationTargetException;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import applang.android.framework.R;
import applang.android.framework.activities.BaseActivity;
import applang.android.framework.utils.DialogUtils;

public abstract class AbstractDetailsFragment extends AbstractFragment {

	public static final String ARG_KEY_ID = "id";
	public static final String ARG_KEY_PROJECTION = "arg_projection";
	public static final String ARG_KEY_EDIT_FRAG_CLASS_NAME = "arg_editFragClassName";

	public static final String ARG_KEY_ACTION_EDIT_ENABLED = "action_edit_enabled";
	public static final String ARG_KEY_ACTION_DELETE_ENABLED = "action_delete_enabled";
	
	protected Long mEntityId;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		((BaseActivity) getActivity()).setTitle(getResources().getString(
				R.string.title_details,
				getResources().getString(mEntityNameResId)));
		((BaseActivity) getActivity()).setSubtitle(null);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mEntityId = getArguments().getLong(ARG_KEY_ID);
		if (mEntityId == null) {
			printNotFoundToastMessage();
			getFragmentManager().popBackStackImmediate();
		}
		populateFields();
		super.onViewCreated(view, savedInstanceState);
	}
	
	protected void populateFields() {

		String[] projection = getArguments().getStringArray(ARG_KEY_PROJECTION);
		Cursor cursor = getActivity().getContentResolver().query(
				getContentUri(mEntityId), projection, null, null, null);
		if (cursor == null || cursor.getCount() == 0) {
			printNotFoundToastMessage();
			if (cursor != null)
				cursor.close();
			getFragmentManager().popBackStackImmediate();
		} else {
			cursor.moveToFirst();
			putDataInViewControls(cursor);
		}
		if (cursor != null)
			cursor.close();
	}

	/**
	 * Fills the view's controls with the data from the cursor. <br/>
	 * No need to close the cursor, it is automatically closed by the caller of
	 * this method.
	 * 
	 * @param cursor
	 */
	protected abstract void putDataInViewControls(Cursor cursor);

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.details_menu, menu);
		if (!this.getArguments().getBoolean(ARG_KEY_ACTION_EDIT_ENABLED, true)){
			MenuItem actionEdit = menu.findItem(R.id.action_edit);
			actionEdit.setVisible(false);
			actionEdit.setEnabled(false);
		}
		if (!this.getArguments().getBoolean(ARG_KEY_ACTION_DELETE_ENABLED, true)){
			MenuItem actionDelete = menu.findItem(R.id.action_delete);
			actionDelete.setVisible(false);
			actionDelete.setEnabled(false);
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_edit) {
			openEdit();
			return true;
		} else if (item.getItemId() == R.id.action_delete) {
			confirmDelete();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	protected void openEdit() {
		String fragmentClassName = getArguments().getString(
				ARG_KEY_EDIT_FRAG_CLASS_NAME);

		Fragment fragment;
		try {
			fragment = (Fragment) Class
					.forName(fragmentClassName)
					.getMethod(BaseActivity.NEW_INSTANCE_METHOD_NAME,
							Long.class).invoke(null, mEntityId);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException e) {
			Log.e(this.getClass().getCanonicalName(),
					"An error has occured with the instantiation of the New Fragment: "
							, e);
			Toast.makeText(getActivity(), "An error has occured!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).addToBackStack(null)
				.commit();
	}

	public void confirmDelete() {
		DialogUtils.showConfirmDeleteFromDetailsDialog(getFragmentManager(),
				this, getResources().getString(mEntityNameResId));
	}

	public void delete() {
		try {
			getActivity().getContentResolver().delete(getContentUri(mEntityId),
					null, null);
			Toast.makeText(
					getActivity(),
					getResources().getString(R.string.toast_deleted,
							getResources().getString(mEntityNameResId)),
					Toast.LENGTH_SHORT).show();
			getFragmentManager().popBackStackImmediate();
		} catch (SQLiteConstraintException e) {
			Log.e(this.getClass().getCanonicalName(),
					"Could not delete due to constraint.", e);
			DialogUtils
					.showSimpleMessageDialog(
							getFragmentManager(),
							getResources()
									.getString(
											R.string.dialog_message_delete_failed_sql_constraint),
							getResources().getString(
									R.string.dialog_title_attention),
							R.drawable.ic_action_warning);

		} catch (SQLException e) {
			showDatabaseErrorDialog(e);
		}
	}

	protected void printNotFoundToastMessage() {
		Toast.makeText(
				getActivity(),
				getResources().getString(R.string.toast_not_found,
						getResources().getString(mEntityNameResId)),
				Toast.LENGTH_LONG).show();
	}

}
