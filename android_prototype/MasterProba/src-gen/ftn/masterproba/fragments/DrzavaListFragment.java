package ftn.masterproba.fragments;

import android.os.Bundle;
import applang.android.framework.fragments.AbstractListFragment;
import applang.android.framework.fragments.ListSubFragment;
import ftn.masterproba.R;
import ftn.masterproba.adapters.DrzavaAdapter;
import ftn.masterproba.contentProviders.DrzavaContentProvider;
import ftn.masterproba.databases.DrzavaTable;
import ftn.masterproba.impl.fragments.DrzavaDetailsFragmentImpl;
import ftn.masterproba.impl.fragments.DrzavaEditFragmentImpl;
import ftn.masterproba.impl.fragments.DrzavaNewFragmentImpl;

public class DrzavaListFragment extends AbstractListFragment {

	public static DrzavaListFragment newInstance() {
		DrzavaListFragment fragment = new DrzavaListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_KEY_ENTITY_NAME_RES_ID, R.string.ent_drzava);
		args.putString(ARG_KEY_CONTENT_URI_STRING,
				DrzavaContentProvider.CONTENT_URI.toString());
		args.putString(ARG_KEY_NEW_FRAG_CLASS_NAME,
				DrzavaNewFragmentImpl.class.getName());
		fragment.setArguments(args);
		return fragment;
	}
	
	public static DrzavaListFragment newInstance(String filterColumnName, Long filterValue, String subtitle) {
		DrzavaListFragment fragment = newInstance();
		Bundle args = fragment.getArguments();
		args.putString(ARG_KEY_FILTER_COLUMN_NAME, filterColumnName);
		args.putLong(ARG_KEY_FILTER_VALUE, filterValue);
		args.putString(ARG_KEY_SUBTITLE, subtitle);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	protected ListSubFragment createListSubFragment() {
		String[] projection = {
				DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_ID,
				DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_SIFRA,
				DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_NAZIV,
				DrzavaTable.TABLE_NAME + "." + DrzavaTable.COLUMN_ZASTAVA};
		return ListSubFragment.newInstance(R.string.ent_drzava, projection,
				DrzavaContentProvider.CONTENT_URI.toString(),
				DrzavaAdapter.class.getName(),
				DrzavaEditFragmentImpl.class.getName(),
				DrzavaDetailsFragmentImpl.class.getName());
	}

	@Override
	protected String getSelectionForSearchQuery() {
		String selection = DrzavaTable.TABLE_NAME + "."
				+ DrzavaTable.COLUMN_SIFRA + " LIKE ?1"
				+ " OR " + DrzavaTable.TABLE_NAME + "."
				+ DrzavaTable.COLUMN_NAZIV + " LIKE ?1";
		return selection;
	}

}
