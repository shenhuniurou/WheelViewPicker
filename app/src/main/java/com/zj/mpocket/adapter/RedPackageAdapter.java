package com.zj.mpocket.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zj.mpocket.R;
import com.zj.mpocket.model.RedPackageModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Administrator on 2016/4/27.
 */
public class RedPackageAdapter extends RecyclerView.Adapter {

    private List<RedPackageModel> redPackageList;

    public RedPackageAdapter(List<RedPackageModel> redPackageList) {
        this.redPackageList = redPackageList;
    }

    public void refreshData(List<RedPackageModel> redPackageList) {
        this.redPackageList = redPackageList;
        //notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_red_package_item, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        OrderViewHolder holder = (OrderViewHolder) viewHolder;
        holder.position = position;
        RedPackageModel redPackageModel = redPackageList.get(position);
        String dateStr = redPackageModel.getDate();
        try {
            String newDateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new SimpleDateFormat("yyyyMMddHHmmss").parse(dateStr));
            holder.tvDate.setText(newDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String tradeType = redPackageModel.getTrade_type();
        switch (Integer.parseInt(tradeType)) {
            case 1: tradeType = "消费";break;
            case 2: tradeType = "冻结";break;
            case 3: tradeType = "撤销";break;
            case 4: tradeType = "赠送";break;
            case 5: tradeType = "赠送";break;
            case 6: tradeType = "提现";break;
            case 7: tradeType = "用户消费";break;
            case 8: tradeType = "签到赠送";break;
        }
        String tradeAccount = redPackageModel.getTrade_account();
        java.text.DecimalFormat df = new java.text.DecimalFormat("0.00");
        holder.tvType.setText(tradeType + "：" + df.format(Double.parseDouble(tradeAccount)) + "元");
    }

    @Override
    public int getItemCount() {
        return redPackageList == null ? 0 : redPackageList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        public TextView tvType;
        public TextView tvDate;
        public int position;

        public OrderViewHolder(View itemView) {
            super(itemView);
            tvType = (TextView) itemView.findViewById(R.id.tvType);
            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
        }

    }

}
