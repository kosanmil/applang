package applang.android.framework.fragments;

import java.lang.reflect.InvocationTargetException;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import applang.android.framework.R;
import applang.android.framework.activities.BaseActivity;
import applang.android.framework.utils.ViewUtils;

public abstract class AbstractListFragment extends AbstractFragment implements
    OnQueryTextListener {

	public static final String ARG_KEY_NEW_FRAG_CLASS_NAME = "arg_newFragClassName";
	public static final String ARG_KEY_SUBTITLE = "subtitle";
	
	public static final String ARG_KEY_FILTER_COLUMN_NAME = "filter_column_name";
	public static final String ARG_KEY_FILTER_VALUE = "filter_column_value";
	
//	public static final String STATE_KEY_LISTVIEW_STATE = "stateListViewState";

	protected ListView mListView;
	protected CursorAdapter mAdapter;
	
	protected ListSubFragment listSubFragment;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fw_fragment_list_layout,
				container, false);
		((BaseActivity) getActivity()).setTitle(getResources()
				.getString(R.string.title_list,
						getResources().getString(mEntityNameResId)));
		((BaseActivity) getActivity()).setSubtitle(getArguments().getString(ARG_KEY_SUBTITLE));
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		if(listSubFragment == null){
			listSubFragment = createListSubFragment();
			Bundle listSubArgs = listSubFragment.getArguments();
			listSubArgs.putString(ListSubFragment.ARG_KEY_FILTER_COLUMN_NAME, getArguments().getString(ARG_KEY_FILTER_COLUMN_NAME));
			listSubArgs.putLong(ListSubFragment.ARG_KEY_FILTER_VALUE, getArguments().getLong(ARG_KEY_FILTER_VALUE));
//			if(savedInstanceState != null)
//				listSubArgs.putParcelable(ListSubFragment.ARG_KEY_LISTVIEW_STATE, savedInstanceState.getParcelable(STATE_KEY_LISTVIEW_STATE));
			if(savedInstanceState == null)
				getChildFragmentManager().beginTransaction()
					.replace(R.id.subfragment_container, listSubFragment).commit();
		}
	}
	
	public void onSaveInstanceState(Bundle outState) {
//		if(listSubFragment != null)
//			outState.putParcelable(STATE_KEY_LISTVIEW_STATE, listSubFragment.getListViewState());
		super.onSaveInstanceState(outState);
	}

	protected abstract ListSubFragment createListSubFragment();

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.list_menu, menu);
		SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu
				.findItem(R.id.action_search));
		searchView.setOnQueryTextListener(this);
		MenuItemCompat.setOnActionExpandListener(
				menu.findItem(R.id.action_search),
				new MenuItemCompat.OnActionExpandListener() {

					@Override
					public boolean onMenuItemActionExpand(MenuItem item) {
						return true;
					}

					@Override
					public boolean onMenuItemActionCollapse(MenuItem item) {
						onQueryTextChange("");
						// MenuItemCompat.collapseActionView(item);
						return true;
					}
				});
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_new) {
			openNew();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	public boolean onQueryTextChange(String newText) {
		String searchQuery = !TextUtils.isEmpty(newText) ? "%" + newText  + "%" : "%%";
		listSubFragment.fillData(getSelectionForSearchQuery(), new String[] {searchQuery});
		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		ViewUtils.closeKeyboard(getActivity());
		getActivity().getCurrentFocus().clearFocus();
		return true;
	}

	public void openNew() {
		String fragmentClassName = getArguments().getString(
				ARG_KEY_NEW_FRAG_CLASS_NAME);

		Fragment fragment;
		try {
			String filterColumnName = getArguments().getString(ARG_KEY_FILTER_COLUMN_NAME);
			long filterValue = getArguments().getLong(ARG_KEY_FILTER_VALUE);
			if(TextUtils.isEmpty(filterColumnName) || filterValue == 0L )
				fragment = (Fragment) Class
						.forName(fragmentClassName)
						.getMethod(BaseActivity.NEW_INSTANCE_METHOD_NAME,
								(Class[]) null).invoke(null, (Object[]) null);
			else {
				//Stripping the TABLE_NAME part of the ColumnName
				filterColumnName = filterColumnName.substring(filterColumnName.lastIndexOf(".") + 1);
				fragment = (Fragment) Class
						.forName(fragmentClassName)
						.getMethod(BaseActivity.NEW_INSTANCE_METHOD_NAME,
								(new Class[] {String.class, Long.TYPE})).invoke(null, new Object[] {filterColumnName, filterValue});
			}
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


	/**
	 * Returns the SQL selection string based on the search query. If the search
	 * query is empty, returns null.
	 * 
	 * @return
	 */
	protected abstract String getSelectionForSearchQuery();

}
