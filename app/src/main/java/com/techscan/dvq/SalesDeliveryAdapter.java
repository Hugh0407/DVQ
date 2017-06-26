package com.techscan.dvq;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by cloverss on 2017/6/23.
 */

public class SalesDeliveryAdapter extends BaseAdapter {

    Context mContext;
    List<Map<String, Object>> mList;

    public SalesDeliveryAdapter(Context context, List<Map<String, Object>> list) {
        mContext = context;
        mList = list;
    }

    public void setList(List<Map<String, Object>> list) {
        mList = list;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//
//
//                new int[]{R.id.txtTransScanInvCode, R.id.txtTransScanInvName,
//                        R.id.txtTransScanBatch, R.id.txtTransScanAccId,
//                        R.id.txtTransScanTotalNum, R.id.txtTransScanBarCode,
//                        R.id.txtTransScanSeriNo, R.id.txtTransScanBillCode,
//                        R.id.txtTransScanScanCount, R.id.txtTransBox}


        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_san, null);
            viewHolder.InvCode = (TextView) convertView.findViewById(R.id.txtInvCodes);
            viewHolder.InvName = (TextView) convertView.findViewById(R.id.txtInvCodeName);
            viewHolder.Batch = (TextView) convertView.findViewById(R.id.txtBatch);
            viewHolder.QTY = (TextView) convertView.findViewById(R.id.txtQTY);
            viewHolder.UNIT = (TextView) convertView.findViewById(R.id.txtUnit);
//            viewHolder.BarCode = (TextView) convertView.findViewById(R.id.txtTransScanBarCode);
//            viewHolder.SeriNo = (TextView) convertView.findViewById(R.id.txtTransScanSeriNo);
//            viewHolder.BillCode = (TextView) convertView.findViewById(R.id.txtTransScanBillCode);
//            viewHolder.ScanedNum = (TextView) convertView.findViewById(R.id.txtTransScanScanCount);
//            viewHolder.box = (TextView) convertView.findViewById(R.id.txtTransBox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.InvCode.setText(mList.get(position).get("InvCode").toString());
        viewHolder.InvName.setText(mList.get(position).get("InvName").toString());
        viewHolder.Batch.setText(mList.get(position).get("Batch").toString());
        viewHolder.QTY.setText(mList.get(position).get("QTY").toString());
        viewHolder.UNIT.setText(mList.get(position).get("Measname").toString());
//        viewHolder.BarCode.setText(mList.get(position).get("BarCode").toString());
//        viewHolder.SeriNo.setText(mList.get(position).get("SeriNo").toString());
//        viewHolder.BillCode.setText(mList.get(position).get("BillCode").toString());
//        viewHolder.ScanedNum.setText(mList.get(position).get("ScanedNum").toString());
//        viewHolder.box.setText(mList.get(position).get("box").toString());
        return convertView;
    }

    static class ViewHolder {
//                new String[]{"InvCode", "InvName", "Batch", "AccID", "TotalNum",
//                "BarCode", "SeriNo", "BillCode", "ScanedNum", "box"},
        TextView InvCode;
        TextView InvName;
        TextView Batch;
        TextView QTY;
        TextView UNIT;
//        TextView BarCode;
//        TextView SeriNo;
//        TextView BillCode;
//        TextView ScanedNum;
//        TextView box;
    }
}
