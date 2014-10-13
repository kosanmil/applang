package applang.android.framework.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import applang.android.framework.R;
import applang.android.framework.utils.DialogUtils;

public class AbstractFragment extends Fragment {
	
	public static final String ARG_KEY_LAYOUT_ID = "arg_layoutId";
	public static final String ARG_KEY_ENTITY_NAME_RES_ID = "arg_entityNameResId";
	public static final String ARG_KEY_CONTENT_URI_STRING = "arg_content_uri_string";

	protected int mEntityNameResId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		mEntityNameResId = getArguments().getInt(ARG_KEY_ENTITY_NAME_RES_ID);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(getArguments().getInt(ARG_KEY_LAYOUT_ID),
				container, false);
		return view;
	}
	
	/**
	 * Returns the Content Provider of the URI associated with the entity that is tied to this fragment.
	 * @return
	 */
	protected Uri getContentUri() {
		return Uri.parse(getArguments().getString(ARG_KEY_CONTENT_URI_STRING));
	}
	
	/**
	 * Returns the Content Provider of the URI associated with the entity that is tied to this fragment.
	 * <br/> Also appends '/id' to the end, for operations that require IDs in their URIs.
	 * @param id - to be appended to the URI using a '/' as a separator.
	 * @return
	 */
	protected Uri getContentUri(Long id) {
		return Uri.parse(getArguments().getString(ARG_KEY_CONTENT_URI_STRING) + "/" + id);
	}
	
	protected void showDatabaseErrorDialog(Exception e){
		Log.e(this.getClass().getCanonicalName(),
				"Database error: ", e);
		DialogUtils
		.showSimpleMessageDialog(
				getFragmentManager(),
				getResources()
						.getString(
								R.string.dialog_message_db_error),
				getResources().getString(
						R.string.dialog_title_error),
				R.drawable.ic_action_warning);
	}
	
}
