package applang.android.framework.fragments.dialogs;

import ftn.masterproba.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import applang.android.framework.fragments.AbstractDetailsFragment;

public class SimpleMessageDialogFragment extends DialogFragment {

	public static final String ARG_KEY_MESSAGE = "message";
	public static final String ARG_KEY_TITLE = "title";
	public static final String ARG_KEY_ICON_ID = "icon_id";
	
	public static final int NO_INT_ARGUMENT = 0;

	public static SimpleMessageDialogFragment newInstance(
			String message) {
		SimpleMessageDialogFragment fragment = new SimpleMessageDialogFragment();
		Bundle args = new Bundle();
		args.putString(ARG_KEY_MESSAGE, message);
		fragment.setArguments(args);
		return fragment;
	}
	public static SimpleMessageDialogFragment newInstance(
			String message, String title) {
		SimpleMessageDialogFragment fragment = newInstance(message);
		Bundle args = fragment.getArguments();
		args.putString(ARG_KEY_TITLE, title);
		fragment.setArguments(args);
		return fragment;
	}
	
	public static SimpleMessageDialogFragment newInstance(
			String message, String title, int iconResId) {
		SimpleMessageDialogFragment fragment = newInstance(message, title);
		Bundle args = fragment.getArguments();
		args.putInt(ARG_KEY_ICON_ID, iconResId);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		String title = getArguments().getString(ARG_KEY_TITLE);
		int iconResId = getArguments().getInt(ARG_KEY_ICON_ID, NO_INT_ARGUMENT);
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setMessage(getArguments().getString(ARG_KEY_MESSAGE))
				.setTitle("Info")
				.setPositiveButton(
						getResources().getString(R.string.ok),
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								SimpleMessageDialogFragment.this.dismiss();
							}
						}).create();
		dialog.setCanceledOnTouchOutside(true);
		if(!TextUtils.isEmpty(title))
			dialog.setTitle(title);
		if(iconResId != NO_INT_ARGUMENT)
			dialog.setIcon(iconResId);

		return dialog;
	}
	
}
