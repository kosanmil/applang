package applang.android.framework.utils;

import android.graphics.Bitmap;
import android.view.View;
import applang.android.framework.R;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


public class ImageUtils {
	
	public static final int DEFAULT_MINIMUM_SIZE = 256;
	public static final int LOADING_IMAGE_RES_ID = R.drawable.ic_action_time;
	public static final int FAILED_TO_LOAD_IMAGE_RES_ID = R.drawable.ic_action_warning;
	public static final int EMPTY_IMAGE_RES_ID = R.drawable.ic_action_picture;

	/**
	 * Options for the ImageLoader, modified for the Applang framework.
	 */
	public final static DisplayImageOptions DISPLAY_OPTIONS = new DisplayImageOptions.Builder()
	    .showImageOnLoading(LOADING_IMAGE_RES_ID) // resource or drawable
	    .showImageForEmptyUri(EMPTY_IMAGE_RES_ID) // resource or drawable
	    .showImageOnFail(FAILED_TO_LOAD_IMAGE_RES_ID) // resource or drawable
//	    .delayBeforeLoading(500)
		.cacheInMemory(true)
		.cacheOnDisk(true)
	    .build();
	
	/**
	 * Extends the <code>ImageLoadingListener</code> to set the tag of the view to the <code>imageUri</code> if the loading is completed, 
	 * or sets the tag to <code>null</code> if the loading has failed or has been cancelled.
	 * @author kosan
	 *
	 */
	public static class TagImageViewLoadingListener implements ImageLoadingListener {

		@Override
		public void onLoadingStarted(String imageUri, View view) {
			//Do nothing
		}

		@Override
		public void onLoadingFailed(String imageUri, View view,
				FailReason failReason) {
			view.setTag(null);
		}

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			view.setTag(imageUri);
		}

		@Override
		public void onLoadingCancelled(String imageUri, View view) {
			view.setTag(null);
		}
		
	}

	
	
	
	
//	
//	/**
//	 * Downsamples the image that is located in the <code>imageUri</code>, and puts the imageUri in the imageView's tag for later use.
//	 * If the imageUri doesn't point to an image, or if it's null, the drawable resource with the ID of <code>defaultDrawableId</code> 
//	 * is put in the imageView.
//	 * <br/>This operation is done in the background thread.
//	 * @param context - used to get the content provider.
//	 * @param imageViewToAttach - ImageView to attach the image to.
//	 * @param imageUri - Uri location of the image
//	 * @param defaultDrawableId - Drawable to attach to the ImageView if the image cannot be found or loaded.
//	 */
//	public static void downsampleAndAttachImage(Context context, ImageView imageViewToAttach, Uri imageUri,
//			Integer defaultDrawableId) {
//		new AttachImageTask().execute(context, imageViewToAttach, imageUri, defaultDrawableId, null, null);
//	
//	}
//	
//	/**
//	 * Downsamples the image that is located in the <code>imageUri</code>, and puts the imageUri in the imageView's tag for later use.
//	 * If the imageUri doesn't point to an image, or if it's null, the drawable resource with the ID of <code>defaultDrawableId</code> 
//	 * is put in the imageView.
//	 * <br/>This operation is done in the background thread.
//	 * @param context - used to get the content provider.
//	 * @param imageViewToAttach - ImageView to attach the image to.
//	 * @param imageUri - Uri location of the image
//	 * @param defaultDrawableId - Drawable to attach to the ImageView if the image cannot be found or loaded.
//	 * @param minimumSize - size that the width or height of the downsampled image must be near to.
//	 */
//	public static void downsampleAndAttachImage(Context context, ImageView imageViewToAttach, Uri imageUri,
//			Integer defaultDrawableId, int minimumSize) {
//		new AttachImageTask().execute(context, imageViewToAttach, imageUri, defaultDrawableId, minimumSize, minimumSize);
//	
//	}
//	
//	/**
//	 * Downsamples the image that is located in the <code>imageUri</code>, and puts the imageUri in the imageView's tag for later use.
//	 * If the imageUri doesn't point to an image, or if it's null, the drawable resource with the ID of <code>defaultDrawableId</code> 
//	 * is put in the imageView.
//	 * <br/>This operation is done in the background thread.
//	 * @param context - used to get the content provider.
//	 * @param imageViewToAttach - ImageView to attach the image to.
//	 * @param imageUri - Uri location of the image
//	 * @param defaultDrawableId - Drawable to attach to the ImageView if the image cannot be found or loaded.
//	 * @param minimumWidth - size that the width of the downsampled image must be near to.
//	 * @param minimumHeight - size that the height of the downsampled image must be near to.
//	 */
//	public static void downsampleAndAttachImage(Context context, ImageView imageViewToAttach, Uri imageUri,
//			Integer defaultDrawableId, int minimumWidth, int minimumHeight) {
//		new AttachImageTask().execute(context, imageViewToAttach, imageUri, defaultDrawableId, minimumWidth, minimumHeight);
//	
//	}
//	
//	private static class AttachImageTask extends AsyncTask<Object, Bitmap, Object[]> {
//
//	    private ImageView imageView;
//
//	    @Override
//	    protected Object[] doInBackground(Object... parameters) {
//
//	    	Context context = (Context) parameters[0];
//	    	imageView = (ImageView) parameters[1];
//	        Uri imageUri = (Uri) parameters[2];
//	        Integer defaultDrawableId = (Integer) parameters[3];
//	        Integer minimumWidth = (Integer) parameters[4];
//	        Integer minimumHeight = (Integer) parameters[5];
//	        Bitmap retBitmap;
//	        //Putting this in block so as to not keep the Bitmap progressImage reference for too long
//	        {
//	        	Bitmap progressImage = BitmapFactory.decodeResource(context.getResources(), LOADING_IMAGE_RES_ID);
//	        	publishProgress(progressImage);
//	        }
//	        try {
//	        	if(imageUri == null)
//	        		throw new FileNotFoundException("imageUri is null!");
//	        	if(minimumWidth != null && minimumHeight != null)
//	        		retBitmap = downsampleBitmap(context, imageUri, minimumWidth, minimumHeight);
//	        	else
//	        		retBitmap = downsampleBitmap(context, imageUri);
//			} catch (FileNotFoundException e) {
//				Log.w("ImageUtils", e.getMessage());
//				retBitmap = BitmapFactory.decodeResource(
//						context.getResources(), defaultDrawableId);
//				imageView.setTag(null);
//			}
//	        return new Object[] {retBitmap, imageUri};
//	    }
//	    
//	    @Override
//	    protected void onProgressUpdate(Bitmap... values) {
//	    	if(imageView != null && values.length > 0 && values[0] != null)
//	    		imageView.setImageBitmap(values[0]);
//	    }
//
//	    @Override
//	    protected void onPostExecute(Object[] result) {
//	        if (result != null) {
//	            imageView.setImageBitmap((Bitmap) result[0]);
//	            imageView.setTag(result[1]);
//	        }
//	        else
//	        	Log.w("ImageUtilsTask", "Result == null in AttachImageTask!");
//	    }
//	}
//	
//	
//	/**
//	 * Downsamples (lowers by a scale) the bitmap that's in the <code>imageUri</code> until the width or height are near the <code>ImageUtils.DEFAULT_MINIMUM_SIZE</code> 
//	 * without lowering it even further with the next scale, and returns it.
//	 * @param context - used to get the ContentResolver to get the image from the Uri
//	 * @param imageUri - location of the image to downsample
//	 * @return - downsampled (lowered by a scale) bitmap
//	 * @throws FileNotFoundException - if the <code>imageUri</code> doesn't point to the image.
//	 */
//	@SuppressLint("NewApi")
//	public static Bitmap downsampleBitmap(Context context, Uri imageUri) throws FileNotFoundException {
//
//        return downsampleBitmap(context, imageUri, DEFAULT_MINIMUM_SIZE);
//    }
//	
//	/**
//	 * Downsamples (lowers by a scale) the bitmap that's in the <code>imageUri</code> until the width or height are near the <code>minimumSize</code> 
//	 * without lowering it even further with the next scale, and returns it.
//	 * @param context - used to get the ContentResolver to get the image from the Uri
//	 * @param imageUri - location of the image to downsample
//	 * @param minimumSize - size that the width or height must be near to.
//	 * @return - downsampled (lowered by a scale) bitmap
//	 * @throws FileNotFoundException - if the <code>imageUri</code> doesn't point to the image.
//	 */
//	@SuppressLint("NewApi")
//	public static Bitmap downsampleBitmap(Context context, Uri imageUri, int minimumSize) throws FileNotFoundException {
//
//        return downsampleBitmap(context, imageUri, minimumSize, minimumSize);
//    }
//	
//	/**
//	 * Downsamples (lowers by a scale) the bitmap that's in the <code>imageUri</code> until the width is near the <code>minimumWidth</code> 
//	 * OR the height is near the <code>minimumHeight</code> 
//	 * without lowering it even further with the next scale, and returns it.
//	 * @param context - used to get the ContentResolver to get the image from the Uri
//	 * @param imageUri - location of the image to downsample
//	 * @param minimumWidth - size that the width must be near to.
//	 * @param minimumHeight - size that the height must be near to.
//	 * @return - downsampled (lowered by a scale) bitmap
//	 * @throws FileNotFoundException - if the <code>imageUri</code> doesn't point to the image.
//	 */
//	public static Bitmap downsampleBitmap(Context context, Uri imageUri, int minimumWidth, int minimumHeight) throws FileNotFoundException {
//
//        // Decode image size
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, o);
//
//        // Find the correct scale value. It should be the power of 2.
//        int width_tmp = o.outWidth, height_tmp = o.outHeight;
//        int scale = 1;
//        while (true) {
//            if (width_tmp / 2 < minimumWidth
//               || height_tmp / 2 < minimumHeight) {
//                break;
//            }
//            width_tmp /= 2;
//            height_tmp /= 2;
//            scale *= 2;
//        }
//
//        // Decode with inSampleSize
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize = scale;
//        Bitmap downsampledImage = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, o2);
//        return downsampledImage;
//
//    }
//	
//	/**
//	 * Clears the imageView's tag (which should hold the image's location Uri) by setting it to <code>null</code>
//	 * , and loads the drawable with the id <code>ImageUtils.EMPTY_IMAGE_RES_ID</code> as the new image of the imageView.
//	 * @param context - context to get the drawable from
//	 * @param imageView - imageView to clear the tag and replace the image
//	 */
//	public static void clearImageView(Context context, ImageView imageView){
//		clearImageView(context, imageView, EMPTY_IMAGE_RES_ID);
//	}
//	
//	/**
//	 * Clears the imageView's tag (which should hold the image's location Uri) by setting it to <code>null</code>
//	 * , and loads the drawable with the id <code>replacementDrawableId</code> as the new image of the imageView.
//	 * @param context - context to get the drawable from
//	 * @param imageView - imageView to clear the tag and replace the image
//	 * @param replacementDrawableId - id of the replacement drawable to load in the imageView
//	 */
//	public static void clearImageView(Context context, ImageView imageView, int replacementDrawableId){
//		Drawable drawable = context.getResources().getDrawable(replacementDrawableId);
//		imageView.setTag(null);
//		imageView.setImageDrawable(drawable);
//	}
	
}
