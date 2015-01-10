package org.zywx.wbpalmstar.plugin.uextimemachine;

import java.util.HashSet;

import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

public class EUExTimeMachine extends EUExBase {

	public static final String CALLBACK_LOAD_DATA = "uexTimeMachine.loadData";
	public static final String cbOpenFunName = "uexTimeMachine.cbOpen";
	
	public static final String ON_ITEM_SELECTED = "uexTimeMachine.onItemSelected";
	public static final String TAG = "EUExTimeMachine";
	private static String currentTag = null;
	private HashSet<String> hashSet = new HashSet<String>();
	public EUExTimeMachine(Context context, EBrowserView inParent) {
		super(context, inParent);
	}

	/**
	 * 打开TimeMachine<br>
	 * 实际形式:open(String id,String x,String y,String w,String h);
	 * 
	 * @param params
	 */
	public void open(String[] params) {
		if (params.length != 5) {
			return;
		}
		try {
			final String tmId = params[0];
			final int x = Integer.parseInt(params[1]);
			final int y = Integer.parseInt(params[2]);
			final int w = Integer.parseInt(params[3]);
			final int h = Integer.parseInt(params[4]);
			final ActivityGroup activityGroup = (ActivityGroup) mContext;
			activityGroup.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(mContext, TimeMachineActivity.class);
					LocalActivityManager mgr = activityGroup.getLocalActivityManager();
					currentTag = TAG + tmId;
					hashSet.add(tmId);
					Window window = mgr.startActivity(currentTag, intent);
					View decorView = window.getDecorView();
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(w, h);
					lp.leftMargin = x;
					lp.topMargin = y;
					addViewToCurrentWindow(decorView, lp);
					String js = SCRIPT_HEADER + "if(" + CALLBACK_LOAD_DATA + "){" + CALLBACK_LOAD_DATA + "('" + tmId
							+ "');}";
					onCallback(js);
					
					js = SCRIPT_HEADER + "if(" + cbOpenFunName + "){" + cbOpenFunName + "('" + tmId
							+ "');}";
					onCallback(js);
					
					
				}
			});
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 为TimeMachine设置参数<br>
	 * 实际形式:setJsonData(String json);
	 * 
	 * @param params
	 */
	public void setJsonData(String[] params) {
		if (params.length != 1) {
			return;
		}
		final TimeMachine timeMachine = TimeMachine.parseTimeMachineJson(params[0]);
		if (timeMachine == null) {
			return;
		}
		final ActivityGroup activityGroup = (ActivityGroup) mContext;
		activityGroup.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				LocalActivityManager mgr = activityGroup.getLocalActivityManager();
				Activity activity = mgr.getActivity(TAG + timeMachine.getTmId());
				if (activity != null && activity instanceof TimeMachineActivity) {
					TimeMachineActivity timeMachineActivity = ((TimeMachineActivity) activity);
					timeMachineActivity.setData(timeMachine, new CarouselAdapter.OnItemClickListener() {

						@Override
						public void onItemClick(CarouselAdapter<?> parent, View view, int position, long id) {
							String js = SCRIPT_HEADER + "if(" + ON_ITEM_SELECTED + "){" + ON_ITEM_SELECTED + "('"
									+ timeMachine.getTmId() + "'," + position + ");}";
							onCallback(js);
						}
					});

				}
			}
		});
	}

	/**
	 * 关闭TimeMachine<br>
	 * 实际形式close(String tmId);
	 * 
	 * @param params
	 */
	public void close(String[] params) {
		if (params.length != 1) {
			return;
		}
		final String tmId = params[0];
		if (tmId == null || tmId.length() == 0) {
			return;
		}
		final ActivityGroup activityGroup = (ActivityGroup) mContext;
		activityGroup.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (currentTag == null) {
					return;
				}
				String tmIdStr = tmId.substring(0, tmId.length());
				String[] tmIds = tmIdStr.split(",");
				if(tmIds != null){
					for(int i =0,length = tmIds.length; i<length;i++){
						closeTimeMachine(tmIds[i]);
					}
					currentTag = null;
				}
				
			}
		});
	}

	@Override
	protected boolean clean() {
		
		final ActivityGroup activityGroup = (ActivityGroup) mContext;
		activityGroup.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (currentTag == null) {
					return;
				}
				for(String tmId:hashSet){
					closeTimeMachine(tmId);
				}
				currentTag = null;
			}
		});
		return true;
	}
	
	
	 private void closeTimeMachine(final String tmId){
		 final ActivityGroup activityGroup = (ActivityGroup) mContext;
		 LocalActivityManager mgr = activityGroup.getLocalActivityManager();
			Activity activity = mgr.getActivity(TAG +  tmId);
			if (activity != null && activity instanceof TimeMachineActivity) {
				TimeMachineActivity timeMachineActivity = ((TimeMachineActivity) activity);
				View decorView = timeMachineActivity.getWindow().getDecorView();
				removeViewFromCurrentWindow(decorView);
			}
	    }
}
