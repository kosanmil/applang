package applang.android.framework.activities;

import java.lang.reflect.InvocationTargetException;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import applang.android.framework.adapters.NavigationDrawerListAdapter;
import applang.android.framework.utils.ViewUtils;
import applang.android.framework.R;

public class BaseActivity extends ActionBarActivity {
	
	public static final String NEW_INSTANCE_METHOD_NAME = "newInstance";
	public static final String FRAG_TAG_DIALOG = "dialog";
	
	//ID of the cursor in the spinner that symbolizes a null row
	public static final Long SPINNER_EMPTY_ID = -1l;
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mTitle = "Naslov projekta";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fw_navigation_drawer);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		mDrawerList.setAdapter(new NavigationDrawerListAdapter(
				getApplicationContext()));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description */
		R.string.drawer_close /* "close drawer" description */
		) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);

			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				ViewUtils.closeKeyboard(BaseActivity.this);

			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			int position = getResources().getInteger(R.integer.nav_drawer_default_position);
			selectItem(position, getResources().getStringArray(R.array.nav_drawer_tags)[position]);
		}
		getSupportActionBar().setTitle(mTitle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.base, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...
		if (mDrawerLayout.isDrawerOpen(mDrawerList))
			mDrawerLayout.closeDrawer(mDrawerList);
		return super.onOptionsItemSelected(item);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@SuppressWarnings("rawtypes")
		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			selectItem(position, view.getTag());
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}
	
	public void setSubtitle(CharSequence subTitle) {
		getSupportActionBar().setSubtitle(subTitle);
	}

	private void selectItem(int position, Object viewTag) {
		// update the main content by replacing fragments
		String fragmentClassName = (String) viewTag;
		Fragment fragment;
		try {
			fragment = (Fragment) Class
					.forName(fragmentClassName)
					.getMethod(NEW_INSTANCE_METHOD_NAME, (Class[]) null)
					.invoke(null, (Object[]) null);
		} catch (IllegalAccessException | ClassNotFoundException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException e) {
			Log.e(this.getLocalClassName(),
					"An error has occured while creating the fragment from the view tag.", e);
			return;
		}
		FragmentManager manager = getSupportFragmentManager();
		//Reset the backstack
		if(manager.getBackStackEntryCount() > 0){
			manager.popBackStack(manager.getBackStackEntryAt(0).getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
		}
		FragmentTransaction transaction = manager.beginTransaction()
				.replace(R.id.content_frame, fragment);
		transaction.commit();
		mDrawerList.setItemChecked(position, true);

		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls

		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			super.onBackPressed();
		}
	}

}
