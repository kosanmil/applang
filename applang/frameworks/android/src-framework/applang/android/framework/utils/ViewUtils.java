package applang.android.framework.utils;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.Spinner;
import applang.android.framework.activities.BaseActivity;
import ftn.masterproba.R;

public class ViewUtils {
	
	/**
	 * Selects the item in the spinner whose Id matches the parameter <code>itemId</code>.
	 * Returns true if the item was selected or false if the item with the Id was not found.
	 * @param spinner - spinner in which to select the item.
	 * @param itemId - Item Id that should be selected.
	 * @return true if the item was selected or false if the item with the Id was not found.
	 */
	public static boolean selectSpinnerItemById(Spinner spinner, long itemId) {
		if(itemId == BaseActivity.SPINNER_EMPTY_ID)
			return false;
		for (int i = 0; i < spinner.getCount(); i++) {
			long currentItemId = spinner.getItemIdAtPosition(i);
			if (currentItemId == itemId) {
				spinner.setSelection(i, false);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the string value of the columnName in the cursor. If the value is null, returns the <code>R.string.none</code> resource.
	 * @param context - used to get the String resource
	 * @param cursor - cursor from where to get the String value
	 * @param columnName - columnName where the value is stored.
	 * @return the string value of the columnName in the cursor. If the value is null, returns the <code>R.string.none</code> resource.
	 */
	public static String getStringOrNoneFromCursor(Context context, Cursor cursor, String columnName ){
		String retVal = cursor.getString(cursor.getColumnIndex(columnName));
		if(retVal == null)
			retVal = context.getResources().getString(R.string.none);
		return retVal;
	}
	
	/**
	 * Searches for the view in the activity that has focus, and tries to open
	 * the keyboard if one is associated with that view.
	 * 
	 * @param activity
	 */
	public static void openKeyboard(Activity activity) {
		View viewWithFocus = activity.getCurrentFocus();
		if (viewWithFocus == null)
			return;
		InputMethodManager mgr = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.showSoftInput(viewWithFocus, InputMethodManager.SHOW_IMPLICIT);
	}

	/**
	 * Searches for the view in the activity that has focus, and tries to close
	 * the keyboard if one is associated with that view.
	 * 
	 * @param activity
	 */
	public static void closeKeyboard(Activity activity) {
		View viewWithFocus = activity.getCurrentFocus();
		if (viewWithFocus == null)
			return;
		InputMethodManager mgr = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(viewWithFocus.getWindowToken(), 0);
	}
	
	/**
	 * Returns the string representation of "yes" or "no", depending on the <code>booleanValue</code> parameter.
	 * @param resources - needed to get the string resource value
	 * @param booleanValue - the value that the output is dependent on.
	 * @return localized "yes" if <code>booleanValue</code> is true, else localized "no" is returned
	 */
	public static String getYesNoStringByBoolean(Resources resources, boolean booleanValue){
		int resourceId = booleanValue ? R.string.yes : R.string.no;
		return resources.getString(resourceId);
	}
	
	
	

}
