package applang.android.framework.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import ftn.masterproba.R;


public class ButtonMethodsUtils {

	public static void openMapForAddress(Context context, String address){
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?q=" + address));
			context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Log.w(ButtonMethodsUtils.class.getCanonicalName(), "Device cannot open map");
				Toast.makeText(context, context.getResources().getString(R.string.toast_device_cannot_open_map), Toast.LENGTH_LONG).show();
			}
	}
	
	/**
	 * Dials the telephone number by passing the intent for an activity responsible for it.
	 * Displays an toast message if no activity that can dialed could be found.
	 * @param context - used to start activity, and if needed, display the toast message
	 * @param telephoneNumber - number to dial
	 */
	public static void dialTelephoneNumber(Context context, String telephoneNumber){
		try {
			Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telephoneNumber));
			context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Log.w(ButtonMethodsUtils.class.getCanonicalName(), "Device cannot call");
				Toast.makeText(context, context.getResources().getString(R.string.toast_device_cannot_call), Toast.LENGTH_LONG).show();
			}
	}
	
	/**
	 * Messages the telephone number by passing the intent for an activity responsible for it.
	 * Displays an toast message if no activity that can message could be found.
	 * @param context - used to start activity, and if needed, display the toast message
	 * @param telephoneNumber - number to message
	 */
	public static void messageTelephoneNumber(Context context, String telephoneNumber){
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + telephoneNumber));
			context.startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Log.w(ButtonMethodsUtils.class.getCanonicalName(), "Device cannot message");
				Toast.makeText(context, context.getResources().getString(R.string.toast_device_cannot_message), Toast.LENGTH_LONG).show();
			}
	}
	
}
