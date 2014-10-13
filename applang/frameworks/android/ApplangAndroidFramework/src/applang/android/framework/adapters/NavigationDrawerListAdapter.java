package applang.android.framework.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import applang.android.framework.R;

public class NavigationDrawerListAdapter extends BaseAdapter {
	
	private final String[] labels;
//	private final String[] iconNames;
	private final String[] tags;
	private Context context;
	
	public NavigationDrawerListAdapter(Context context){
		super();
		this.context = context;
		labels = context.getResources().getStringArray(R.array.nav_drawer_labels);
		tags = context.getResources().getStringArray(R.array.nav_drawer_tags);
	}
	
	@Override
	public int getCount() {
		return labels.length;
	}

	@Override
	public Object getItem(int position) {
		return labels[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.fw_navigation_drawer_list_item, null);
        }
        
        TextView textView = (TextView) view.findViewById(R.id.tvDrawerItem);
//        ImageView imageView = (ImageView) view.findViewById(R.id.ivDrawerItem);
        textView.setText(labels[position]);
//        int imageResourceId = context.getResources().getIdentifier(
//        		iconNames[position], "drawable", context.getPackageName());
//        Drawable icon;
//        if(imageResourceId == 0)
//        	icon = context.getResources().getDrawable(R.drawable.question_icon);
//        else
//        	icon = context.getResources().getDrawable(imageResourceId);
//        if(imageResourceId == 0)
//        	imageResourceId = R.drawable.question_icon;
//        new ImageLoader().execute(view, context, imageResourceId);
        view.setTag(tags[position]);
        return view;
	}
	
/*	private class ImageLoader extends AsyncTask<Object, Void, Drawable> {

	    private View view;

	    @Override
	    protected Drawable doInBackground(Object... parameters) {

	        view = (View) parameters[0];
	        Context context = (Context) parameters[1];
	        Integer drawableId = (Integer) parameters[2];
	        return context.getResources().getDrawable(drawableId);
	    }

	    @Override
	    protected void onPostExecute(Drawable result) {
	        if (result != null && view != null) {
	            ImageView ivDrawerItem = (ImageView) view.findViewById(R.id.ivDrawerItem);
	            ivDrawerItem.setImageDrawable(result);
	        }
	    }
	}	
*/
}
