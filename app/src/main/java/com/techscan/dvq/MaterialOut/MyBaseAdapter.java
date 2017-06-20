package com.techscan.dvq.MaterialOut;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.techscan.dvq.R;

import java.util.List;

/**
 * Created by cloverss on 2017/6/20.
 */

public class MyBaseAdapter extends BaseAdapter {

    Context mContext;
    List<String> mList;

    public MyBaseAdapter(Context context, List<String> list) {
        mContext = context;
        mList = list;
    }

    public void setList(List<String> list) {
        mList = list;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.listview_item, null);
            viewHolder.nameView = (TextView) convertView.findViewById(R.id.num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.nameView.setText(mList.get(position));
        return convertView;
    }

    static class ViewHolder {
        TextView nameView;
        TextView signView;
    }
}
