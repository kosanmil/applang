package applang.android.framework.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import applang.android.framework.fragments.AbstractDetailsFragment;
import ftn.masterproba.R;

public class ConfirmDeleteFromDetailsDialogFragment extends DialogFragment {

	public static final String ARG_KEY_ENTITY_NAME = "entity_name";

	public static ConfirmDeleteFromDetailsDialogFragment newInstance(
			String entityName) {
		ConfirmDeleteFromDetailsDialogFragment fragment = new ConfirmDeleteFromDetailsDialogFragment();
		Bundle args = new Bundle();
		args.putString(ARG_KEY_ENTITY_NAME, entityName);
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
								AbstractDetailsFragment fragment = (AbstractDetailsFragment) getTargetFragment();
								fragment.delete();
							}
						})
				.setNegativeButton(
						getResources().getString(R.string.no),
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								ConfirmDeleteFromDetailsDialogFragment.this
										.dismiss();
							}
						}).create();
		dialog.setCanceledOnTouchOutside(true);

		return dialog;
	}

}
