package com.techscan.dvq.module.materialOut;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.Goods;

import java.util.List;

/**
 * Created by liuya on 2017/6/20.
 */

public class MyBaseAdapter extends BaseAdapter {

    private List<Goods> mList;

    public MyBaseAdapter(List<Goods> list) {
        mList = list;
    }

    public void setList(List<Goods> list) {
        mList = list;
    }

    public List<Goods> getList() {
        return mList;
    }

    /**
     * @return 返回子项目的个数，也就是有多少条
     */
    @Override
    public int getCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @param position--> int 类型 ，指当前的位置，从“0” 开始
     * @param convertView
     * @param parent
     * @return 返回子项的布局
     * 每次生成一次子项，该方法执行一次
     */
    @Nullable
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_details, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.encoding = (TextView) convertView.findViewById(R.id.encoding);
            viewHolder.type = (TextView) convertView.findViewById(R.id.type);
            viewHolder.lot = (TextView) convertView.findViewById(R.id.lot);
            viewHolder.qty = (TextView) convertView.findViewById(R.id.qty);
            viewHolder.spec = (TextView) convertView.findViewById(R.id.spec);
            viewHolder.costObj = (TextView) convertView.findViewById(R.id.costObj);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(mList.get(position).getName());
        viewHolder.encoding.setText(mList.get(position).getEncoding());
        viewHolder.type.setText(mList.get(position).getType());
        viewHolder.lot.setText(mList.get(position).getLot());
        viewHolder.spec.setText(mList.get(position).getSpec());
        viewHolder.costObj.setText(mList.get(position).getCostObjName());
        if (TextUtils.isEmpty(String.valueOf(mList.get(position).getQty()))) {
            viewHolder.qty.setText("0.00");
        } else {
            viewHolder.qty.setText(String.valueOf(mList.get(position).getQty()));
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView encoding;
        TextView type;
        TextView lot;
        TextView qty;
        TextView spec;
        TextView costObj;
    }
}
