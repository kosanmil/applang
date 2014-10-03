package applang.android.framework.utils;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.DatePicker;

public class DatePickerUtils {

	/**
	 * Returns the Calendar object with the date shown on the <code>datePicker</code>, or returns null if the <code>datePicker</code> is null.
	 * @param datePicker - The Datepicker where the date is read from.
	 * @return the Calendar object with the date shown on the <code>datePicker</code>, or null if the <code>datePicker</code> is null.
	 */
	public static Calendar getCalendarFromDatePicker(DatePicker datePicker){
		if(datePicker == null)
			return null;
		Calendar calendar = Calendar.getInstance();
		calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
		return calendar;
	}
	
	/**
	 * Updates the datePicker with the date represented in the Unix time milliseconds.
	 * @param datePicker - datePicker to be updated
	 * @param dateInMilliseconds - date represented in Unix time milliseconds
	 */
	public static void updateDatePicker(DatePicker datePicker, long dateInMilliseconds){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(dateInMilliseconds);
		updateDatePicker(datePicker, calendar);
	}
	
	/**
	 * Updates the datePicker with the date.
	 * @param datePicker - datePicker to be updated
	 * @param dateInMilliseconds - date used to update the datePicker
	 */
	public static void updateDatePicker(DatePicker datePicker, Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		updateDatePicker(datePicker, calendar);
	}
	
	/**
	 * Updates the datePicker with the date represented by the Calendar object.
	 * @param datePicker - datePicker to be updated
	 * @param dateInMilliseconds - date represented by the Calendar object
	 */
	public static void updateDatePicker(DatePicker datePicker, Calendar calendar){
		datePicker.updateDate(
				calendar.get(Calendar.YEAR), 
				calendar.get(Calendar.MONTH), 
				calendar.get(Calendar.DAY_OF_MONTH));
	}
	
	/**
	 * Formats the date represented by the Unix time milliseconds to a String depending on the locale of the device.
	 * The format used is the <code>DateFormat.getMediumDateFormat()</code>
	 * @param context - used to get the locale of the device
	 * @param dateInMilliseconds - date to format represented by the Unix time milliseconds
	 * @return - formatted date String depending on the locale of the device
	 */
	public static String formatDateByLocale(Context context, Long dateInMilliseconds) {
		return formatDateByLocale(context, new Date(dateInMilliseconds));
	}
	
	/**
	 * Formats the date represented by the Calendar to a String depending on the locale of the device.
	 * The format used is the <code>DateFormat.getMediumDateFormat()</code>
	 * @param context - used to get the locale of the device
	 * @param dateInMilliseconds - date to format represented by the Calendar
	 * @return - formatted date String depending on the locale of the device
	 */
	public static String formatDateByLocale(Context context, Calendar calendar){
		return formatDateByLocale(context, calendar.getTime());
	}
	
	/**
	 * Formats the date to a String depending on the locale of the device.
	 * The format used is the <code>DateFormat.getMediumDateFormat()</code>
	 * @param context - used to get the locale of the device
	 * @param dateInMilliseconds - date to format
	 * @return - formatted date String depending on the locale of the device
	 */
	public static String formatDateByLocale(Context context, Date date){
		return DateFormat.getMediumDateFormat(context).format(date);
	}
	
}
