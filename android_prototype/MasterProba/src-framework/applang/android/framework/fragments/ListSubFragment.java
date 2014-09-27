package applang.android.framework.fragments;

import java.lang.reflect.InvocationTargetException;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import applang.android.framework.activities.BaseActivity;
import applang.android.framework.utils.DialogUtils;
import ftn.masterproba.R;

public class ListSubFragment extends AbstractFragment implements
		LoaderCallbacks<Cursor> {

	public static final String ARG_KEY_PROJECTION = "arg_projection";
	public static final String ARG_KEY_LIST_ADAPTER_CLASS_NAME = "arg_listAdapterClassName";
	public static final String ARG_KEY_EDIT_FRAG_CLASS_NAME = "arg_editFragClassName";
	public static final String ARG_KEY_DETAILS_FRAG_CLASS_NAME = "arg_detailsFragClassName";
	
	//Loader manager args
	public static final String LOADER_ARG_KEY_SELECTION = "loader_selection";
	public static final String LOADER_ARG_KEY_SELECTION_ARGS = "loader_selection_args";
	
	//Filter args that aren't in the usual newInstance method
	public static final String ARG_KEY_FILTER_COLUMN_NAME = "filter_column_name";
	public static final String ARG_KEY_FILTER_VALUE = "filter_column_value";
	
//	public static final String ARG_KEY_LISTVIEW_STATE = "listViewState";
	
	protected ListView mListView;
	protected CursorAdapter mAdapter;

	public static ListSubFragment newInstance(int entityNameResId, 
			String[] projection, String contentUri, String adapterClassName, 
			String editFragClassName, String detailsFragClassName){
		ListSubFragment fragment = new ListSubFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_KEY_ENTITY_NAME_RES_ID, entityNameResId);
		args.putStringArray(ARG_KEY_PROJECTION, projection);
		args.putString(ARG_KEY_CONTENT_URI_STRING, contentUri);
		args.putString(ARG_KEY_LIST_ADAPTER_CLASS_NAME, adapterClassName);
		args.putString(ARG_KEY_EDIT_FRAG_CLASS_NAME, editFragClassName);
		args.putString(ARG_KEY_DETAILS_FRAG_CLASS_NAME, detailsFragClassName);
		fragment.setArguments(args);
		return fragment;
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fw_subfragment_list_layout,
				container, false);
		return view;
	}
	

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		mListView = (ListView) view.findViewById(R.id.listEntity);
		mListView.setEmptyView(view.findViewById(android.R.id.empty));
		mListView.setOnItemClickListener(new ListItemClickListener());
		mListView.setOnItemLongClickListener(new ListItemLongClickListener());
		String adapterClassName = getArguments().getString(
				ARG_KEY_LIST_ADAPTER_CLASS_NAME);
		try {
			mAdapter = (CursorAdapter) Class.forName(adapterClassName)
					.getConstructor(Context.class, Cursor.class, Integer.TYPE)
					.newInstance(getActivity(), null, 0);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | java.lang.InstantiationException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException e) {
			Log.e(this.getClass().getCanonicalName(),
					"An error has occured while creating the fragment from the view tag.", e);
			Toast.makeText(getActivity(), "An error has occured!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		mListView.setAdapter(mAdapter);
//		if(getArguments().getParcelable(ARG_KEY_LISTVIEW_STATE) != null)
//			mListView.onRestoreInstanceState(getArguments().getParcelable(ARG_KEY_LISTVIEW_STATE));
//		if(savedInstanceState != null) {
//			
//			listView.setSelectionFromTop(savedInstanceState.getInt(STATE_KEY_LISTVIEW_INDEX), 
//					savedInstanceState.getInt(STATE_KEY_LISTVIEW_TOP));
//		}
		fillData(null, null);
	}
	
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		ListView listView = (ListView) getView().findViewById(R.id.listEntity);
//		int index = listView.getFirstVisiblePosition();
//		View v = listView.getChildAt(0);
//		int top = (v == null) ? 0 : v.getTop();
//		outState.putInt(STATE_KEY_LISTVIEW_INDEX, index);
//		outState.putInt(STATE_KEY_LISTVIEW_TOP, top);
//		super.onSaveInstanceState(outState);
//	}

	protected void fillData(String selection, String[] selectionArgs) {
		String filterColumnName = getArguments().getString(ARG_KEY_FILTER_COLUMN_NAME);
		Long filterValue = getArguments().getLong(ARG_KEY_FILTER_VALUE);
		if(!TextUtils.isEmpty(filterColumnName) && filterValue != 0L ){
			String selectionToAppend = filterColumnName + " = '" + filterValue.toString() + "'";
			if(selection == null)
				selection = selectionToAppend;
			else
				selection = "(" + selection + ")" + " AND " + selectionToAppend;
		}
		
		Bundle args = new Bundle();
		args.putString(ListSubFragment.LOADER_ARG_KEY_SELECTION, selection);
		args.putStringArray(ListSubFragment.LOADER_ARG_KEY_SELECTION_ARGS, selectionArgs);
		getLoaderManager().restartLoader(0, args, this);
	}

	protected class ListItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			openDetails(id);
		}
	}

	protected class ListItemLongClickListener implements
			OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, final long id) {

			DialogUtils.showListLongClickDialog(getParentFragment().getFragmentManager(),
					ListSubFragment.this,
					getResources().getString(mEntityNameResId), id);

			return true;
		}

	}

	public void openDetails(Long id) {
		String fragmentClassName = getArguments().getString(
				ARG_KEY_DETAILS_FRAG_CLASS_NAME);

		Fragment fragment;
		try {
			fragment = (Fragment) Class
					.forName(fragmentClassName)
					.getMethod(BaseActivity.NEW_INSTANCE_METHOD_NAME,
							Long.class).invoke(null, id);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException e) {
			Log.e(this.getClass().getCanonicalName(),
					"An error has occured with the instantiation of the Details Fragment: "
							, e);
			Toast.makeText(getActivity(), "An error has occured!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		getParentFragment().getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).addToBackStack(null)
				.commit();
	}

	public void openEdit(Long id) {
		String fragmentClassName = getArguments().getString(
				ARG_KEY_EDIT_FRAG_CLASS_NAME);

		Fragment fragment;
		try {
			fragment = (Fragment) Class
					.forName(fragmentClassName)
					.getMethod(BaseActivity.NEW_INSTANCE_METHOD_NAME,
							Long.class).invoke(null, id);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException e) {
			Log.e(this.getClass().getCanonicalName(),
					"An error has occured with the instantiation of the Edit Fragment: "
							,e );
			Toast.makeText(getActivity(), "An error has occured!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		getParentFragment().getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).addToBackStack(null)
				.commit();
	}
	
	public void confirmDelete(Long id) {
		DialogUtils.showConfirmDeleteFromListDialog(getParentFragment().getFragmentManager(), this, 
				getResources().getString(mEntityNameResId), id);
	}

	public void delete(Long id) {
		try {
			getActivity().getContentResolver().delete(getContentUri(id), null,
					null);
			Toast.makeText(
					getActivity(),
					getResources().getString(R.string.toast_deleted,
							getResources().getString(mEntityNameResId)),
					Toast.LENGTH_SHORT).show();
		} catch (SQLiteConstraintException e) {
			Log.e(this.getClass().getCanonicalName(),
					"Could not delete due to constraint.", e);
			DialogUtils
			.showSimpleMessageDialog(
					getParentFragment().getFragmentManager(),
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

	// CursorLoader methods
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
		String[] selectionArgs = args.getStringArray(LOADER_ARG_KEY_SELECTION_ARGS);
		String selection;
//		if(selectionArgs == null)
//			selection = null;
//		else
			selection = args.getString(LOADER_ARG_KEY_SELECTION);
		
		String[] projection = getArguments().getStringArray(ARG_KEY_PROJECTION);
		CursorLoader cursorLoader = new CursorLoader(getActivity(),
				getContentUri(), projection, selection, selectionArgs, null);
		return cursorLoader;
	}


	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// data is not available anymore, delete reference
		mAdapter.swapCursor(null);

	}
	
	/**
	 * Returns the state of the ListView as a Parcelable. Returns null if the ListView is null, or if there is nothing interesting to save.
	 * @return
	 */
	public Parcelable getListViewState(){
		return mListView != null ? mListView.onSaveInstanceState() : null;
	}
	

}
