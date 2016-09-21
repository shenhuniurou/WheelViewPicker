package com.zj.mpocket.view.wheel;

import android.view.View;

import com.zj.mpocket.R;
import com.zj.mpocket.model.ChildrenClass;
import com.zj.mpocket.model.StoreClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WheelMain {

	private View view;
	private WheelView wv_first;
	private WheelView wv_second;
	public int screenheight;
	private boolean hasSelectValue;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public WheelMain(View view) {
		super();
		this.view = view;
		hasSelectValue = false;
		setView(view);
	}
	
	public WheelMain(View view, boolean hasSelectValue) {
		super();
		this.view = view;
		this.hasSelectValue = hasSelectValue;
		setView(view);
	}
	
	/**
	 * @Description: 初始化级联选择器
	 */
	public void initPickerView(final List<StoreClass> proClassList) {

		final List<String> firstList = new ArrayList<>();
		final Map<String, List<ChildrenClass>> secondMap =  new HashMap<>();

		for (StoreClass storeClass: proClassList) {
			firstList.add(storeClass.getText());
			secondMap.put(storeClass.getText(), storeClass.getChildren());
		}

		// 一级分类
		wv_first = (WheelView) view.findViewById(R.id.first);
		wv_first.setAdapter(new ClassAdapter(firstList));
		wv_first.setCyclic(false);// 设置不可循环滚动
		wv_first.setCurrentItem(0);

		// 二级分类
		wv_second = (WheelView) view.findViewById(R.id.second);
		String firstValue = proClassList.get(wv_first.getCurrentItem()).getText();
		List<ChildrenClass> childrenClassList = secondMap.get(firstValue);
		wv_second.setAdapter(new ClassAdapter(1, childrenClassList));
		wv_second.setCyclic(false);
		wv_second.setCurrentItem(0);
		
		// 监听一级分类的滚动
		OnWheelChangedListener wheelListener_first = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				if (oldValue != newValue) {
					String textValue = proClassList.get(newValue).getText();
					List<ChildrenClass> childrenClassList = secondMap.get(textValue);
					wv_second.setAdapter(new ClassAdapter(1, childrenClassList));
					wv_second.setCurrentItem(0);
					listener.getIndustry(childrenClassList.get(0).getText(), childrenClassList.get(0).getValue());
				}
			}
		};
		
		// 监听二级分类的滚动
		OnWheelChangedListener wheelListener_second = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				String textValue = proClassList.get(wv_first.getCurrentItem()).getText();
				List<ChildrenClass> childrenClassList = secondMap.get(textValue);
				ChildrenClass childrenClass = childrenClassList.get(newValue);
				listener.getIndustry(childrenClass.getText(), childrenClass.getValue());
			}
		};
		wv_first.addChangingListener(wheelListener_first);
		wv_second.addChangingListener(wheelListener_second);

		// 根据屏幕密度来指定选择器字体的大小(不同屏幕可能不同)
		int textSize = 0;
		if(hasSelectValue)
			textSize = (screenheight / 100) * 4;
		else
			textSize = (screenheight / 100) * 5;
		wv_first.TEXT_SIZE = textSize;
		wv_second.TEXT_SIZE = textSize;
	}

	public String getValue() {
		StringBuffer sb = new StringBuffer();
		if(hasSelectValue)
			sb.append(wv_first.getCurrentItem()).append(wv_second.getCurrentItem());
		return sb.toString();
	}

	WheelMainListener listener;

	public void setListener(WheelMainListener listener) {
		this.listener = listener;
	}

	public interface WheelMainListener {
		void getIndustry(String text, String value);
	}
	
}
