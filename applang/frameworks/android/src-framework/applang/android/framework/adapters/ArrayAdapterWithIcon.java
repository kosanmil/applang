package applang.android.framework.adapters;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Array Adapter with an Icon added to the textView <code>android.R.id.text1</code>
 * @author kosan
 *
 */
public class ArrayAdapterWithIcon extends ArrayAdapter<String> {

	private int mPadding = 12; 
	private List<Integer> mImages;
	
	public ArrayAdapterWithIcon(Context context, String[] texts, Integer[] images) {
	    super(context, android.R.layout.select_dialog_item, texts);
	    this.mImages = Arrays.asList(images);
	}

	public ArrayAdapterWithIcon(Context context, List<String> texts, List<Integer> images) {
	    super(context, android.R.layout.select_dialog_item, texts);
	    this.mImages = images;
	}
	
	public ArrayAdapterWithIcon(Context context, String[] texts, Integer[] images, int padding) {
	    this(context, texts, images);
	    this.mPadding = padding;
	}

	public ArrayAdapterWithIcon(Context context, List<String> texts, List<Integer> images, int padding) {
	    this(context, texts, images);
	    this.mPadding = padding;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View view = super.getView(position, convertView, parent);
	    TextView textView = (TextView) view.findViewById(android.R.id.text1);
	    textView.setCompoundDrawablesWithIntrinsicBounds(mImages.get(position), 0, 0, 0);
	    textView.setCompoundDrawablePadding(
	            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mPadding, getContext().getResources().getDisplayMetrics()));
	    return view;
	}
	
}
