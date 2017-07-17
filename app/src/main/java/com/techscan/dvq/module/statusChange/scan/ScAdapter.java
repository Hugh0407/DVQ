package com.techscan.dvq.module.statusChange.scan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.PurGood;

import java.util.List;

/**
 * Created by cloverss on 2017/6/30.
 */

public class ScAdapter extends BaseAdapter {


    private List<PurGood> mList;

    public ScAdapter(List<PurGood> list) {
        mList = list;
    }

    public void setList(List<PurGood> list) {
        mList = list;
    }

    public List<PurGood> getList() {
        return mList;
    }

    /**
     * @return ��������Ŀ�ĸ�����Ҳ�����ж�����
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
     * @param position--> int ���� ��ָ��ǰ��λ�ã��ӡ�0�� ��ʼ
     * @param convertView
     * @param parent
     * @return ��������Ĳ���
     * ÿ������һ������÷���ִ��һ��
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sc_scan, null);
            viewHolder.bill = (TextView) convertView.findViewById(R.id.bill);
            viewHolder.num = (TextView) convertView.findViewById(R.id.num);
            viewHolder.num_task = (TextView) convertView.findViewById(R.id.num_task);
            viewHolder.sku = (TextView) convertView.findViewById(R.id.sku);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.lot = (TextView) convertView.findViewById(R.id.lot);
            viewHolder.change = (TextView) convertView.findViewById(R.id.change);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.bill.setText(mList.get(position).getSourceBill());
        viewHolder.num.setText(mList.get(position).getNshouldinnum());
        viewHolder.sku.setText(mList.get(position).getInvcode());
        viewHolder.name.setText(mList.get(position).getInvname());
        viewHolder.lot.setText(mList.get(position).getVbatchcode());
        viewHolder.num_task.setText(mList.get(position).getNum_task());
        String fbillrowflag = mList.get(position).getFbillrowflag();
        if (fbillrowflag.equals("2")) {
            viewHolder.change.setText("ת��ǰ");
        }
        if (fbillrowflag.equals("3")) {
            viewHolder.change.setText("ת����");
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView bill;
        TextView num;
        TextView num_task;
        TextView sku;
        TextView name;
        TextView lot;
        TextView change;
    }
}
