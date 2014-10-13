package applang.android.framework.utils;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import applang.android.framework.activities.BaseActivity;
import applang.android.framework.fragments.dialogs.ChooseImageLoadDialogFragment;
import applang.android.framework.fragments.dialogs.ConfirmDeleteFromDetailsDialogFragment;
import applang.android.framework.fragments.dialogs.ConfirmDeleteFromListDialogFragment;
import applang.android.framework.fragments.dialogs.ListLongClickDialogFragment;
import applang.android.framework.fragments.dialogs.SimpleMessageDialogFragment;

public class DialogUtils {

	public static void showChooseImageLoadDialog(FragmentManager fragmentManager, Fragment targetFragment,
			int takePhotoRequestCode, int chooseImageRequestCode){
		FragmentTransaction ft = getTransactionCloseExistingDialogs(fragmentManager);

		// Create and show the dialog.
		DialogFragment dialogFragment = ChooseImageLoadDialogFragment
				.newInstance(takePhotoRequestCode, chooseImageRequestCode);
		dialogFragment.setTargetFragment(targetFragment, 0);
		dialogFragment.show(ft, BaseActivity.FRAG_TAG_DIALOG);
	}
	
	public static void showListLongClickDialog(FragmentManager fragmentManager,
			Fragment targetFragment, String title, Long id) {
		FragmentTransaction ft = getTransactionCloseExistingDialogs(fragmentManager);

		// Create and show the dialog.
		DialogFragment dialogFragment = ListLongClickDialogFragment
				.newInstance(title, id);
		dialogFragment.setTargetFragment(targetFragment, 0);
		dialogFragment.show(ft, BaseActivity.FRAG_TAG_DIALOG);
	}

	public static void showConfirmDeleteFromListDialog(
			FragmentManager fragmentManager, Fragment targetFragment,
			String entityName, Long id) {
		FragmentTransaction ft = getTransactionCloseExistingDialogs(fragmentManager);

		// Create and show the dialog.
		ConfirmDeleteFromListDialogFragment dialogFragment = ConfirmDeleteFromListDialogFragment
				.newInstance(entityName, id);
		dialogFragment.setTargetFragment(targetFragment, 0);
		dialogFragment.show(ft, BaseActivity.FRAG_TAG_DIALOG);
	}
	
	public static void showConfirmDeleteFromDetailsDialog(
			FragmentManager fragmentManager, Fragment targetFragment,
			String entityName) {
		FragmentTransaction ft = getTransactionCloseExistingDialogs(fragmentManager);

		// Create and show the dialog.
		ConfirmDeleteFromDetailsDialogFragment dialogFragment = ConfirmDeleteFromDetailsDialogFragment
				.newInstance(entityName);
		dialogFragment.setTargetFragment(targetFragment, 0);
		dialogFragment.show(ft, BaseActivity.FRAG_TAG_DIALOG);
	}
	
	public static void showSimpleMessageDialog(FragmentManager fragmentManager,
			String message) {
		FragmentTransaction ft = getTransactionCloseExistingDialogs(fragmentManager);

		// Create and show the dialog.
		SimpleMessageDialogFragment dialogFragment = SimpleMessageDialogFragment
				.newInstance(message);
		dialogFragment.show(ft, BaseActivity.FRAG_TAG_DIALOG);
	}
	
	public static void showSimpleMessageDialog(FragmentManager fragmentManager,
			String message, String title) {
		FragmentTransaction ft = getTransactionCloseExistingDialogs(fragmentManager);

		// Create and show the dialog.
		SimpleMessageDialogFragment dialogFragment = SimpleMessageDialogFragment
				.newInstance(message, title);
		dialogFragment.show(ft, BaseActivity.FRAG_TAG_DIALOG);
	}
	
	public static void showSimpleMessageDialog(FragmentManager fragmentManager,
			String message, String title, int iconResId) {
		FragmentTransaction ft = getTransactionCloseExistingDialogs(fragmentManager);

		// Create and show the dialog.
		SimpleMessageDialogFragment dialogFragment = SimpleMessageDialogFragment
				.newInstance(message, title, iconResId);
		dialogFragment.show(ft, BaseActivity.FRAG_TAG_DIALOG);
	}

	/**
	 * Returns the uncommited fragment transaction that removes any existing
	 * dialog fragments (searched by the dialog fragment tag located in
	 * <code>BaseActivity</code>.<br/>
	 * Note. The transaction is UNCOMMITED
	 * 
	 * @param fragmentManager
	 *            - manager used to create the transaction and find the dialog
	 *            fragments
	 * @return the uncommited fragment transaction
	 */
	protected static FragmentTransaction getTransactionCloseExistingDialogs(
			FragmentManager fragmentManager) {
		FragmentTransaction ft = fragmentManager.beginTransaction();
		Fragment prev = fragmentManager
				.findFragmentByTag(BaseActivity.FRAG_TAG_DIALOG);
		if (prev != null) {
			ft.remove(prev);
		}
		return ft;
	}

}
