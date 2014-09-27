package applang.android.framework.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import applang.android.framework.activities.BaseActivity;
import applang.android.framework.adapters.ArrayAdapterWithIcon;
import ftn.masterproba.R;

public class ChooseImageLoadDialogFragment extends DialogFragment {

	public static final String ARG_KEY_TAKE_PHOTO_REQ_CODE = "takePhotoRequestCode";
	public static final String ARG_KEY_CHOOSE_IMAGE_REQ_CODE = "chooseImageRequestCode";
	
	private static final int WHICH_INDEX_TAKE_PHOTO = 0;
	private static final int WHICH_INDEX_CHOOSE_IMAGE = 1;

	public static ChooseImageLoadDialogFragment newInstance(int takePhotoRequestCode, int chooseImageRequestCode) {
		ChooseImageLoadDialogFragment fragment = new ChooseImageLoadDialogFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_KEY_TAKE_PHOTO_REQ_CODE, takePhotoRequestCode);
		args.putInt(ARG_KEY_CHOOSE_IMAGE_REQ_CODE, chooseImageRequestCode);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		String title = getResources().getString(R.string.dialog_title_load_image);
		String[] mDialogItemTexts = new String[] {
				getResources().getString(R.string.dialog_option_take_photo),
				getResources().getString(R.string.dialog_option_choose_image)};
		Integer[] images = new Integer[] {
				R.drawable.ic_action_camera,
				R.drawable.ic_action_picture };
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setTitle(title)
				.setAdapter(new ArrayAdapterWithIcon(getActivity(), mDialogItemTexts, images), new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Fragment targetFragment = getTargetFragment();
						Intent intent = new Intent();
						intent.putExtra("bla", "OVO JE EXTRA");
						switch (which) {
						case WHICH_INDEX_TAKE_PHOTO:{
							intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
							targetFragment.startActivityForResult(intent, getArguments().getInt(ARG_KEY_TAKE_PHOTO_REQ_CODE));
							break;
						}
						case WHICH_INDEX_CHOOSE_IMAGE:{
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							targetFragment.startActivityForResult(intent, getArguments().getInt(ARG_KEY_CHOOSE_IMAGE_REQ_CODE));
							break;
						}
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
						ChooseImageLoadDialogFragment.this.dismiss();
					}
				}).create();
		dialog.setCanceledOnTouchOutside(true);

		return dialog;
	}
	
}
