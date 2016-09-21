package com.zj.mpocket.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zj.mpocket.R;
import com.zj.mpocket.model.TaskModel;
import com.zj.mpocket.utils.CommonUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/5/20.
 */
public class TaskAdpater extends BaseAdapter {

    Context mContext;
    List<TaskModel> taskModelList;
    LayoutInflater inflater;

    public TaskAdpater(Context mContext, List<TaskModel> taskModelList) {
        this.mContext = mContext;
        this.taskModelList = taskModelList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return taskModelList == null ? 0 : taskModelList.size();
    }

    @Override
    public TaskModel getItem(int position) {
        return taskModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_task_item, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            holder.textView = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        TaskModel taskModel = taskModelList.get(position);
        if (!CommonUtil.isEmpty(taskModel.getText())) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.imageView.setImageResource(taskModel.getImgId());
            holder.textView.setText(taskModel.getText());
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView textView;
    }

}
