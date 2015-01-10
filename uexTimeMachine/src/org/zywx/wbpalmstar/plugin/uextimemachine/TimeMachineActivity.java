package org.zywx.wbpalmstar.plugin.uextimemachine;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class TimeMachineActivity extends Activity {

	private PageIndicator pageIndicator;
	private Carousel carousel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(EUExUtil
				.getResLayoutID("plugin_timemachine_main_layout"));
		carousel = (Carousel) findViewById(EUExUtil
				.getResIdID("plugin_timemachine_carousel"));
		if (Build.VERSION.SDK_INT >= 14) {
			// 4.0以上默认关闭硬件加速
			carousel.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
		pageIndicator = (PageIndicator) findViewById(EUExUtil
				.getResIdID("plugin_timemachine_page_indictor"));
	}

	public void setData(TimeMachine timeMachine,
			CarouselAdapter.OnItemClickListener listener) {
		CarouselItemAdapter adapter = new CarouselItemAdapter(this,
				timeMachine.getList());
		carousel.setAdapter(adapter);
		pageIndicator.setTotalPageSize(adapter.getCount());
		pageIndicator.setCurrentPage(carousel.getSelectedItemPosition());
		carousel.setOnItemClickListener(listener);
		carousel.setOnItemSelectedListener(new CarouselAdapter.OnItemSelectedListener() {

			@Override
			public void onItemSelected(CarouselAdapter<?> parent, View view,
					int position, long id) {
				pageIndicator.setCurrentPage(position);
			}

			@Override
			public void onNothingSelected(CarouselAdapter<?> parent) {

			}
		});
	}

}
