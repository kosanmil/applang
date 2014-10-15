package applang.android.framework.fragments.dialogs;

import java.util.ArrayList;
import java.util.List;

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
		ListSubFragment targetFragment = (ListSubFragment) getTargetFragment();
		final List<String> dialogItemTexts = new ArrayList<String>();
		List<Integer> images = new ArrayList<Integer>();
		dialogItemTexts.add(getResources().getString(R.string.action_details));
		images.add(R.drawable.ic_action_next_item);
		if(targetFragment.getArguments().getBoolean(ListSubFragment.ARG_KEY_ACTION_EDIT_ENABLED, true)){
			dialogItemTexts.add(getResources().getString(R.string.action_edit));
			images.add(R.drawable.ic_action_edit_dark);
		}
		if(targetFragment.getArguments().getBoolean(ListSubFragment.ARG_KEY_ACTION_DELETE_ENABLED, true)){
			dialogItemTexts.add(getResources().getString(R.string.action_delete));
			images.add(R.drawable.ic_action_discard_dark);
		}
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle(getArguments().getString(ARG_KEY_TITLE))
				.setAdapter(new ArrayAdapterWithIcon(getActivity(), dialogItemTexts, images), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ListSubFragment targetFragment = (ListSubFragment) getTargetFragment();
						Long id = getArguments().getLong(ARG_KEY_ID);
						if (which >= dialogItemTexts.size()){
							Log.w(ListLongClickDialogFragment.class
									.getCanonicalName(),
									"Accessed a 'which' index that should not exist! : "
											+ which);
							return;
						}
						String selectedDialogItem = dialogItemTexts.get(which);
						if (selectedDialogItem.equals(getResources().getString(R.string.action_details))){
							targetFragment.openDetails(id);
							return;
						}
						else if (selectedDialogItem.equals(getResources().getString(R.string.action_edit))){
							targetFragment.openEdit(id);
							return;
						}
						else if (selectedDialogItem.equals(getResources().getString(R.string.action_delete))){
							targetFragment.confirmDelete(id);
							return;
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
