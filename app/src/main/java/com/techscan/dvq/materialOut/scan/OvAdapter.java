package com.techscan.dvq.materialOut.scan;

import android.content.Context;
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
 * �������� ��ť�е�dialog��ͼ�� Adapter
 */

public class OvAdapter extends BaseAdapter {

    Context mContext;
    List<Goods> mList;

    public OvAdapter(Context context, List<Goods> list) {
        mContext = context;
        mList = list;
    }

    public void setList(List<Goods> list) {
        mList = list;
    }

    public List<Goods> getList() {
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_scan_ov, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.encoding = (TextView) convertView.findViewById(R.id.encoding);
            viewHolder.qty = (TextView) convertView.findViewById(R.id.qty);
            viewHolder.lot = (TextView) convertView.findViewById(R.id.lot);
            viewHolder.spec = (TextView) convertView.findViewById(R.id.spec);
            viewHolder.type = (TextView) convertView.findViewById(R.id.type);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(mList.get(position).getName());
        viewHolder.encoding.setText(mList.get(position).getEncoding());
        viewHolder.lot.setText(mList.get(position).getLot());
        viewHolder.spec.setText(mList.get(position).getSpec());
        viewHolder.type.setText(mList.get(position).getType());
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
        TextView qty;
        TextView lot;
        TextView spec;
        TextView type;
    }
}
