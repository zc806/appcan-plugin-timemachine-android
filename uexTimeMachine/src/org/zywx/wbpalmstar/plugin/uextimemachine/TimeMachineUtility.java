package org.zywx.wbpalmstar.plugin.uextimemachine;

import java.io.IOException;
import java.io.InputStream;
import org.zywx.wbpalmstar.base.BUtility;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class TimeMachineUtility {

	public static Bitmap getImage(Context ctx, String imgUrl) {
		if (imgUrl == null || imgUrl.length() == 0) {
			return null;
		}
		Bitmap bitmap = null;
		InputStream is = null;
		try {
			if (imgUrl.startsWith(BUtility.F_Widget_RES_SCHEMA)) {
				is = BUtility.getInputStreamByResPath(ctx, imgUrl);
				bitmap = BitmapFactory.decodeStream(is);
			} else if (imgUrl.startsWith(BUtility.F_FILE_SCHEMA)) {
				imgUrl = imgUrl.replace(BUtility.F_FILE_SCHEMA, "");
				bitmap = BitmapFactory.decodeFile(imgUrl);
			} else if (imgUrl.startsWith(BUtility.F_Widget_RES_path)) {
				try {
					is = ctx.getAssets().open(imgUrl);
					if (is != null) {
						bitmap = BitmapFactory.decodeStream(is);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else if (imgUrl.startsWith("/")) {
				bitmap = BitmapFactory.decodeFile(imgUrl);
			}
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return bitmap;
	}
}
