package applang.android.framework;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ApplangApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		initImageLoader(getApplicationContext());
	}
	
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		Log.i(ApplangApplication.class.getCanonicalName(), "Initializing the ImageLoader with the configuration");
		ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(context);
//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//		.threadPriority(Thread.NORM_PRIORITY - 2)
//		.denyCacheImageMultipleSizesInMemory()
//		.diskCacheFileNameGenerator(new Md5FileNameGenerator())
//		.diskCacheSize(50 * 1024 * 1024) // 50 Mb
//		.tasksProcessingOrder(QueueProcessingType.LIFO)
//		.writeDebugLogs() // Remove for release app
//		.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
		Log.i(ApplangApplication.class.getCanonicalName(), "Finished initializing the ImageLoader with the configuration");
		}
}
