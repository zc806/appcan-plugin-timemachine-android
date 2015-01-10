package org.zywx.wbpalmstar.plugin.uextimemachine;

import java.util.List;
import org.zywx.wbpalmstar.plugin.uextimemachine.TimeMachine.ItemInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CarouselItemAdapter extends BaseAdapter {

	private List<ItemInfo> list;
	private CarouselImageView[] views;
	private LayoutInflater inflater;
	private static final int ITEM_WIDTH = 260;

	public CarouselItemAdapter(Context context, List<ItemInfo> infos) {
		list = infos;
		inflater = LayoutInflater.from(context);
		views = new CarouselImageView[list.size()];
		int itemWidth = (int) (ITEM_WIDTH * context.getResources().getDisplayMetrics().density);
		for (int i = 0, size = views.length; i < size; i++) {
			CarouselImageView imageView = new CarouselImageView(context);
			imageView.setIndex(i);
			ItemInfo itemInfo = list.get(i);
			Bitmap bitmap = loadBitmapByUrl(itemInfo.getImgUrl(), itemWidth);
			imageView.setImageBitmap(bitmap);
			views[i] = imageView;
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public ItemInfo getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return views[position];
	}

	private Bitmap loadBitmapByUrl(String url, int maxWidth) {
		Bitmap bitmap = TimeMachineUtility.getImage(inflater.getContext(), url);
		if (bitmap == null) {
			return null;
		}
		float scaleRate = ((float) maxWidth) / bitmap.getWidth();
		Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, maxWidth, (int) (scaleRate * bitmap.getHeight()), false);
		Bitmap refelctionBitmap = createRefelctionBitmap(scaleBitmap);
		return refelctionBitmap;
	}

	private Bitmap createRefelctionBitmap(Bitmap originalImage) {
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();
		int reflectionGap = 0;
		// This will not scale but will flip on the Y axis
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		// Create a Bitmap with the flip matrix applied to it.
		// We only want the bottom half of the image
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false);

		// Create a new bitmap with same width but taller to fit
		// reflection
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

		// Create a new Canvas with the bitmap that's big enough for
		// the image plus gap plus reflection
		Canvas canvas = new Canvas(bitmapWithReflection);
		// Draw in the original image
		canvas.drawBitmap(originalImage, 0, 0, null);
		// Draw in the gap
		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafaultPaint);
		// Draw in the reflection
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		// Create a shader that is a linear gradient that covers the
		// reflection
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
				+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		// Set the paint to use this shader (linear gradient)
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination
		// in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
		return bitmapWithReflection;
	}

}
