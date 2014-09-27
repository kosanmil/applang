package ftn.masterproba.fragments;

import android.os.Bundle;
import applang.android.framework.fragments.AbstractListFragment;
import applang.android.framework.fragments.ListSubFragment;
import ftn.masterproba.R;
import ftn.masterproba.adapters.FirmaAdapter;
import ftn.masterproba.contentProviders.FirmaContentProvider;
import ftn.masterproba.databases.FirmaTable;
import ftn.masterproba.databases.NaseljenoMestoTable;
import ftn.masterproba.impl.fragments.FirmaDetailsFragmentImpl;
import ftn.masterproba.impl.fragments.FirmaEditFragmentImpl;
import ftn.masterproba.impl.fragments.FirmaNewFragmentImpl;

public class FirmaListFragment extends AbstractListFragment {

	public static FirmaListFragment newInstance() {
		FirmaListFragment fragment = new FirmaListFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_KEY_ENTITY_NAME_RES_ID, R.string.ent_firma);
//		args.putString(ARG_KEY_CONTENT_URI_STRING,
//				FirmaContentProvider.CONTENT_URI.toString());
		args.putString(ARG_KEY_NEW_FRAG_CLASS_NAME,
				FirmaNewFragmentImpl.class.getName());
		fragment.setArguments(args);
		return fragment;
	}
	
	public static FirmaListFragment newInstance(String filterColumnName, Long filterValue, String subtitle) {
		FirmaListFragment fragment = newInstance();
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
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_ID,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_NAZIV,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_PIB,
				FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_DOMACA,
				"naseljeno_mesto" + "." + NaseljenoMestoTable.COLUMN_NAZIV + " as " + "naseljeno_mesto_" + NaseljenoMestoTable.COLUMN_NAZIV,
				"nadfirma" + "." + FirmaTable.COLUMN_NAZIV + " as " + "nadfirma_" + FirmaTable.COLUMN_NAZIV};
		return ListSubFragment.newInstance(R.string.ent_firma, projection,
				FirmaContentProvider.CONTENT_URI.toString(),
				FirmaAdapter.class.getName(),
				FirmaEditFragmentImpl.class.getName(),
				FirmaDetailsFragmentImpl.class.getName());
	}

	@Override
	protected String getSelectionForSearchQuery() {
		String selection = FirmaTable.TABLE_NAME + "." + FirmaTable.COLUMN_NAZIV + " LIKE ?1" 
							+ " OR " 
							+ "naseljeno_mesto_" + NaseljenoMestoTable.COLUMN_NAZIV + " LIKE ?1";
		return selection;
	}
}
