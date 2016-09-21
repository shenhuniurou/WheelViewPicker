package com.zj.mpocket.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zj.mpocket.R;
import com.zj.mpocket.model.SettleModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/4/27.
 */
public class SettleAdapter extends RecyclerView.Adapter {

    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<SettleModel> settleList;

    public SettleAdapter(List<SettleModel> settleList) {
        this.settleList = settleList;
    }

    public void refreshData(List<SettleModel> settleList) {
        this.settleList = settleList;
        //notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_settle_item, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new SettleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        SettleViewHolder holder = (SettleViewHolder) viewHolder;
        holder.position = position;
        SettleModel settleModel = settleList.get(position);
        String dateStr = settleModel.getSettle_date();
        try {
            String newDateStr = new SimpleDateFormat("yyyy-MM-dd").format(new SimpleDateFormat("yyyyMMdd").parse(dateStr));
            holder.tvSettleDate.setText(newDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String settleAmt = settleModel.getSettle_amt();
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        holder.tvSettleAmt.setText(df.format(Double.parseDouble(settleAmt)) + "元");
    }

    @Override
    public int getItemCount() {
        return settleList == null ? 0 : settleList.size();
    }

    class SettleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View rootView;
        public TextView tvSettleDate;
        public TextView tvSettleAmt;
        public int position;

        public SettleViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            tvSettleDate = (TextView) itemView.findViewById(R.id.tvSettleDate);
            tvSettleAmt = (TextView) itemView.findViewById(R.id.tvSettleAmt);
            rootView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != onRecyclerViewListener) {
                onRecyclerViewListener.onItemClick(position);
            }
        }

    }

}
