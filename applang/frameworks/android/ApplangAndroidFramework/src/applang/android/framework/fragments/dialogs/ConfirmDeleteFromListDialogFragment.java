package applang.android.framework.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import applang.android.framework.R;
import applang.android.framework.fragments.ListSubFragment;

public class ConfirmDeleteFromListDialogFragment extends DialogFragment {

	public static final String ARG_KEY_ENTITY_NAME = "entity_name";
	public static final String ARG_KEY_ID = "id";

	public static ConfirmDeleteFromListDialogFragment newInstance(
			String entityName, Long id) {
		ConfirmDeleteFromListDialogFragment fragment = new ConfirmDeleteFromListDialogFragment();
		Bundle args = new Bundle();
		args.putString(ARG_KEY_ENTITY_NAME, entityName);
		args.putLong(ARG_KEY_ID, id);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		String message = getResources().getString(R.string.question_delete,
				getArguments().getString(ARG_KEY_ENTITY_NAME));
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setMessage(message)
				.setPositiveButton(
						getResources().getString(R.string.yes),
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ListSubFragment fragment = (ListSubFragment) getTargetFragment();
								fragment.delete(getArguments().getLong(
										ARG_KEY_ID));
							}
						})
				.setNegativeButton(
						getResources().getString(R.string.no),
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ConfirmDeleteFromListDialogFragment.this
										.dismiss();
							}
						}).create();
		dialog.setCanceledOnTouchOutside(true);

		return dialog;
	}

}
