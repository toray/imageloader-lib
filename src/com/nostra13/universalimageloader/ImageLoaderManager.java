package com.nostra13.universalimageloader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class ImageLoaderManager {
	static ImageLoaderManager mImageManager;
	DisplayImageOptions options;
	AnimateFirstDisplayListener mAnimateFirstDisplayListener;

	private ImageLoaderManager() {
		options = new DisplayImageOptions.Builder()
		 	.cacheInMemory(true).cacheOnDisk(true)
		 		.considerExifParams(true).build();
		mAnimateFirstDisplayListener = new AnimateFirstDisplayListener();
	}

	public static ImageLoaderManager get() {
		if (mImageManager == null) {
			mImageManager = new ImageLoaderManager();
		}
		return mImageManager;
	}
	
	public void init(Context context){
		initImageLoader(context);
	}
	
	void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
				context);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}
	
	public DisplayImageOptions getDisplayImageOptions(){
		return options;
	}
	
	public AnimateFirstDisplayListener getAnimateFirstDisplayListener(){
		return mAnimateFirstDisplayListener;
	}
	
	
	static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}
	
	public void displayImage(String url, ImageView iv){
		displayImage(url, iv, options, getAnimateFirstDisplayListener());
	}
	
	public void displayImage(String url, ImageView iv, DisplayImageOptions options, SimpleImageLoadingListener listener){
		ImageLoader.getInstance().displayImage(url, iv, options, listener);
	}
	
	public void loadImage(String url, SimpleImageLoadingListener listener){
		ImageLoader.getInstance().loadImage(url, options, listener);
	}
}
