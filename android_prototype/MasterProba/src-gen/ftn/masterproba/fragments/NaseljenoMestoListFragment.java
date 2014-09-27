package ftn.masterproba.fragments;

import android.os.Bundle;
import applang.android.framework.fragments.AbstractListFragment;
import applang.android.framework.fragments.ListSubFragment;
import ftn.masterproba.R;
import ftn.masterproba.adapters.NaseljenoMestoAdapter;
import ftn.masterproba.contentProviders.NaseljenoMestoContentProvider;
import ftn.masterproba.databases.DrzavaTable;
import ftn.masterproba.databases.NaseljenoMestoTable;
import ftn.masterproba.impl.fragments.NaseljenoMestoDetailsFragmentImpl;
import ftn.masterproba.impl.fragments.NaseljenoMestoEditFragmentImpl;
import ftn.masterproba.impl.fragments.NaseljenoMestoNewFragmentImpl;

public class NaseljenoMestoListFragment extends AbstractListFragment {

	public static NaseljenoMestoListFragment newInstance() {
		NaseljenoMestoListFragment fragment = new NaseljenoMestoListFragment();
		Bundle args = new Bundle();
//		args.putString(ARG_KEY_SUBTITLE, "Drzava = Srbijaaa");
//		args.putString(ARG_KEY_FILTER_COLUMN_NAME, NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_ID_DRZAVA);
//		args.putLong(ARG_KEY_FILTER_VALUE, 1);
//		args.putInt(ARG_KEY_LAYOUT_ID, R.layout.fragment_drzava_list);
		args.putInt(ARG_KEY_ENTITY_NAME_RES_ID, R.string.ent_naseljeno_mesto);
//		args.putString(ARG_KEY_CONTENT_URI_STRING,
//				NaseljenoMestoContentProvider.CONTENT_URI.toString());
		args.putString(ARG_KEY_NEW_FRAG_CLASS_NAME,
				NaseljenoMestoNewFragmentImpl.class.getName());
		fragment.setArguments(args);
		return fragment;
	}
	
	public static NaseljenoMestoListFragment newInstance(String filterColumnName, Long filterValue, String subtitle) {
		NaseljenoMestoListFragment fragment = newInstance();
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
				NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_ID,
				NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_NAZIV, 
				NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_POST_KOD,
				"drzava" + "." + DrzavaTable.COLUMN_NAZIV + " as " + "drzava_" + DrzavaTable.COLUMN_NAZIV,
				"druga_drzava" + "." + DrzavaTable.COLUMN_NAZIV + " as " + "druga_drzava_" + DrzavaTable.COLUMN_NAZIV};
		return ListSubFragment.newInstance(R.string.ent_naseljeno_mesto, projection,
				NaseljenoMestoContentProvider.CONTENT_URI.toString(),
				NaseljenoMestoAdapter.class.getName(),
				NaseljenoMestoEditFragmentImpl.class.getName(),
				NaseljenoMestoDetailsFragmentImpl.class.getName());
	}

	@Override
	protected String getSelectionForSearchQuery() {
		String selection = NaseljenoMestoTable.TABLE_NAME + "." + NaseljenoMestoTable.COLUMN_NAZIV + " LIKE ?1" 
							+ " OR " 
							+ "drzava_" + DrzavaTable.COLUMN_NAZIV + " LIKE ?1";
		return selection;
	}

}
