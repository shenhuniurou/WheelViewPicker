package com.zj.mpocket.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zj.mpocket.R;
import com.zj.mpocket.model.OrderModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/4/27.
 */
public class OrderAdapter extends RecyclerView.Adapter {

    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
    }

    private OnRecyclerViewListener onRecyclerViewListener;

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    private List<OrderModel> orderList;

    public OrderAdapter(List<OrderModel> orderList) {
        this.orderList = orderList;
    }

    public void refreshData(List<OrderModel> orderList) {
        this.orderList = orderList;
        //notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_item, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        OrderViewHolder holder = (OrderViewHolder) viewHolder;
        holder.position = position;
        OrderModel orderModel = orderList.get(position);
        String dateStr = orderModel.getOrder_date();
        try {
            String newDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new SimpleDateFormat("yyyyMMddHHmmss").parse(dateStr));
            holder.tvOrderDate.setText(newDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String merchantName = orderModel.getName();
        holder.tvOrderName.setText(merchantName);
        String orderAmt = orderModel.getOrder_amt();
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        holder.tvOrderAmt.setText(df.format(Double.parseDouble(orderAmt)) + "元");
    }

    @Override
    public int getItemCount() {
        return orderList == null ? 0 : orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public View rootView;
        public TextView tvOrderName;
        public TextView tvOrderDate;
        public TextView tvOrderAmt;
        public int position;

        public OrderViewHolder(View itemView) {
            super(itemView);
            rootView = itemView.findViewById(R.id.rootView);
            tvOrderName = (TextView) itemView.findViewById(R.id.tvOrderName);
            tvOrderDate = (TextView) itemView.findViewById(R.id.tvOrderDate);
            tvOrderAmt = (TextView) itemView.findViewById(R.id.tvOrderAmt);
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
