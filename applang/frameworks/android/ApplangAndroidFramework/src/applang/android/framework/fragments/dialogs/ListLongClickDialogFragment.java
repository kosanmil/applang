package applang.android.framework.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import applang.android.framework.R;
import applang.android.framework.adapters.ArrayAdapterWithIcon;
import applang.android.framework.fragments.ListSubFragment;

public class ListLongClickDialogFragment extends DialogFragment {

	private static final String ARG_KEY_TITLE = "title";
	private static final String ARG_KEY_ID = "id";

	private static final int WHICH_INDEX_DETAILS = 0;
	private static final int WHICH_INDEX_EDIT = 1;
	private static final int WHICH_INDEX_DELETE = 2;

	public static ListLongClickDialogFragment newInstance(String title, Long id) {
		ListLongClickDialogFragment fragment = new ListLongClickDialogFragment();
		Bundle args = new Bundle();
		args.putString(ARG_KEY_TITLE, title);
		args.putLong(ARG_KEY_ID, id);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		String[] mDialogItemTexts = new String[] {
				getResources().getString(R.string.action_details),
				getResources().getString(R.string.action_edit),
				getResources().getString(R.string.action_delete) };
		Integer[] images = new Integer[] {
				R.drawable.ic_action_next_item,
				R.drawable.ic_action_edit_dark,
				R.drawable.ic_action_discard_dark };
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle(getArguments().getString(ARG_KEY_TITLE))
				.setAdapter(new ArrayAdapterWithIcon(getActivity(), mDialogItemTexts, images), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ListSubFragment targetFragment = (ListSubFragment) getTargetFragment();
						Long id = getArguments().getLong(ARG_KEY_ID);
						switch (which) {
						case WHICH_INDEX_DETAILS:
							targetFragment.openDetails(id);
							break;
						case WHICH_INDEX_EDIT:
							targetFragment.openEdit(id);
							break;
						case WHICH_INDEX_DELETE:
							targetFragment.confirmDelete(id);
							break;
						default:
							Log.w(ListLongClickDialogFragment.class
									.getCanonicalName(),
									"Accessed a 'which' index that should not exist! : "
											+ which);
						}
					}
				})
				.setNegativeButton(R.string.action_cancel, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ListLongClickDialogFragment.this.dismiss();
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);

		return dialog;
	}
}
