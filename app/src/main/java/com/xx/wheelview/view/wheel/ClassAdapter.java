package com.zj.wheelview.view.wheel;

import com.zj.wheelview.model.ChildrenClass;

import java.util.List;

/**
 * Created by Administrator on 2016/4/22.
 */
public class ClassAdapter implements WheelAdapter {

    List<String> firstList;
    List<ChildrenClass> childrenClassList;

    public ClassAdapter(List<String> firstList) {
        this.firstList = firstList;
    }

    public ClassAdapter(int i, List<ChildrenClass> childrenClassList) {
        this.childrenClassList = childrenClassList;
    }

    @Override
    public int getItemsCount() {
        if(firstList == null && childrenClassList != null) {
            return childrenClassList.size();
        }else if (firstList != null && childrenClassList == null) {
            return firstList.size();
        }else {
            return 0;
        }
    }

    @Override
    public String getItem(int index) {
        if (firstList != null) {
            return firstList.get(index);
        }else if (childrenClassList != null) {
            ChildrenClass childrenClass = childrenClassList.get(index);
            return childrenClass.getText();
        }
        return null;
    }

    @Override
    public int getMaximumLength() {
        return 300;
    }

}
