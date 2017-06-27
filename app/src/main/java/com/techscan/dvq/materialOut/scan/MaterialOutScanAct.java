package com.techscan.dvq.materialOut.scan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.techscan.dvq.MainLogin;
import com.techscan.dvq.R;
import com.techscan.dvq.bean.Goods;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.materialOut.MyBaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MaterialOutScanAct extends Activity {

    @InjectView(R.id.ed_bar_code)
    EditText mEdBarCode;
    @InjectView(R.id.ed_encoding)
    EditText mEdEncoding;
    @InjectView(R.id.ed_type)
    EditText mEdType;   // �ͺ�
    @InjectView(R.id.ed_spectype)
    EditText mEdSpectype;   //���
    @InjectView(R.id.ed_lot)
    EditText mEdLot;        //����
    @InjectView(R.id.ed_name)
    EditText mEdName;
    @InjectView(R.id.ed_unit)
    EditText mEdUnit;
    @InjectView(R.id.ed_qty)
    EditText mEdQty;
    @InjectView(R.id.btn_overview)
    Button mBtnOverview;
    @InjectView(R.id.btn_detail)
    Button mBtnDetail;
    @InjectView(R.id.btn_back)
    Button mBtnBack;
    @InjectView(R.id.ed_num)
    EditText mEdNum;


    String TAG = "MaterialOutScanAct";
    List<HashMap<String, String>> detailList;
    List<Goods> ovList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_out_scan);
        ButterKnife.inject(this);
        initView();
    }

    @OnClick({R.id.btn_overview, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_overview:
                OvAdapter ovAdapter = new OvAdapter(MaterialOutScanAct.this, ovList);
                showDialog(ovList, ovAdapter, "ɨ������");
                break;
            case R.id.btn_detail:
                MyBaseAdapter myBaseAdapter = new MyBaseAdapter(MaterialOutScanAct.this, detailList);
                showDialog(detailList, myBaseAdapter, "ɨ����ϸ");
                break;
            case R.id.btn_back:
                if (ovList.size() > 0) {
                    Intent in = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("overViewList", (ArrayList<? extends Parcelable>) ovList);
                    in.putExtras(bundle);
                    MaterialOutScanAct.this.setResult(5, in);
                } else {
                    Toast.makeText(this, "û��ɨ�赥��", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
        }
    }

    private void initView() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("��Ʒɨ��");
        mEdBarCode.setOnKeyListener(mOnKeyListener);
        mEdLot.setOnKeyListener(mOnKeyListener);
        mEdQty.setOnKeyListener(mOnKeyListener);
        mEdBarCode.addTextChangedListener(mTextWatcher);
        detailList = new ArrayList<HashMap<String, String>>();
        ovList = new ArrayList<Goods>();
    }

    /**
     * �����������߳�ͨ��
     * msg.obj �Ǵ����̴߳��ݹ���������
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    JSONObject json = (JSONObject) msg.obj;
                    if (json != null) {
                        try {
                            SetInvBaseToUI(json);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        return;
                    }

                    break;
                default:
                    break;
            }
        }
    };

    private void showDialog(List list, BaseAdapter adapter, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MaterialOutScanAct.this);
        builder.setTitle(title);
        if (list.size() > 0) {
            View view = LayoutInflater.from(MaterialOutScanAct.this).inflate(R.layout.dialog_scan_details, null);
            ListView lv = (ListView) view.findViewById(R.id.lv);
            lv.setAdapter(adapter);
            builder.setView(view);
        } else {
            builder.setMessage("û��ɨ������");
        }
        builder.setPositiveButton("ȡ��", null);
        builder.show();
    }

    /**
     * �������
     */
    private boolean BarAnalysis() {
        String Bar = mEdBarCode.getText().toString().trim();
        if (Bar.contains("\n")) {
            Bar = Bar.replace("\n", "");
        }
        mEdBarCode.setText(Bar);
        String[] barCode = Bar.split("\\|");
        if (barCode.length == 2 && barCode[0].equals("Y")) {          //Y|SKU
            //�����Һ��Ļ���Ҫ����Һ������
            String encoding = barCode[1];
            mEdEncoding.setText(encoding);
            GetInvBaseInfo(encoding);

            mEdQty.setText("");
            mEdLot.requestFocus();
            mEdType.setText("");
            mEdLot.setText("");
            mEdName.setText("");
            mEdUnit.setText("");
            return true;
        } else if (barCode.length == 6 && barCode[0].equals("C")) {   //C|SKU|LOT|TAX|QTY|SN
            String encoding = barCode[1];
            mEdEncoding.setText(encoding);
            GetInvBaseInfo(encoding);

            mEdLot.setText(barCode[2]);
            mEdQty.setText(barCode[4]);
            return true;
        } else if (barCode.length == 7 && barCode[0].equals("TC")) {    //TC|SKU|LOT|TAX|QTY|NUM|SN
            String encoding = barCode[1];
            mEdEncoding.setText(encoding);
            GetInvBaseInfo(encoding);
            mEdLot.setText(barCode[2]);
            float qty = Float.valueOf(barCode[4]);
            float num = Float.valueOf(barCode[5]);
            mEdQty.setText(String.valueOf(qty * num));
            return true;
        } else {
            Toast.makeText(MaterialOutScanAct.this, "����������������", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * �����Ϣ�� ������
     *
     * @return
     */
    private boolean addDataToList() {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("barcode", mEdBarCode.getText().toString());
        hashMap.put("encoding", mEdEncoding.getText().toString());
        hashMap.put("name", mEdName.getText().toString());
        hashMap.put("type", mEdType.getText().toString());
        hashMap.put("unit", mEdUnit.getText().toString());
        hashMap.put("lot", mEdLot.getText().toString());
        hashMap.put("qty", mEdQty.getText().toString());
        hashMap.put("pk_invbasdoc", pk_invbasdoc);
        hashMap.put("pk_invmandoc", pk_invmandoc);
        detailList.add(hashMap);
        // �ϲ����ε�ovlist
        // ����ͬ����������ϲ� �� A_01_20 A_02_30 �ϲ�ΪA_50

        if (ovList.size() == 0) {
            Goods goods = new Goods();
            String qty = String.valueOf(hashMap.get("qty"));
            goods.setBarcode(hashMap.get("barcode"));
            goods.setEncoding(hashMap.get("encoding"));
            goods.setName(hashMap.get("name"));
            goods.setType(hashMap.get("type"));
            goods.setUnit(hashMap.get("unit"));
            goods.setLot(hashMap.get("lot"));
            goods.setPk_invbasdoc(hashMap.get("pk_invbasdoc"));
            goods.setPk_invmandoc(hashMap.get("pk_invmandoc"));
            if (TextUtils.isEmpty(qty)) {
                qty = "0.0";
            }
            goods.setQty(Float.valueOf(qty));
            ovList.add(goods);
            return true;
        } else {
            for (int j = 0; j < ovList.size(); j++) {
                Goods existGoods = ovList.get(j);
                //��ͬ���ϲ�ͬ���ε�Ҫ�ϲ���ͨ�����ֱȽ�
                if (hashMap.get("name").equals(existGoods.getName())) {
                    existGoods.setQty(existGoods.getQty() + Float.valueOf(hashMap.get("qty")));
                    return true;
                } else {
                    Goods goods1 = new Goods();
                    String qty = String.valueOf(hashMap.get("qty"));
                    goods1.setBarcode(hashMap.get("barcode"));
                    goods1.setEncoding(hashMap.get("encoding"));
                    goods1.setName(hashMap.get("name"));
                    goods1.setType(hashMap.get("type"));
                    goods1.setUnit(hashMap.get("unit"));
                    goods1.setLot(hashMap.get("lot"));
                    goods1.setPk_invbasdoc(hashMap.get("pk_invbasdoc"));
                    goods1.setPk_invmandoc(hashMap.get("pk_invmandoc"));
                    if (TextUtils.isEmpty(qty)) {
                        qty = "0.0";
                    }
                    goods1.setQty(Float.valueOf(qty));
                    ovList.add(goods1);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * ������е�Edtext
     */
    private void ChangeAllEdTextToEmpty() {
        mEdBarCode.setText("");
        mEdEncoding.setText("");
        mEdName.setText("");
        mEdType.setText("");
        mEdUnit.setText("");
        mEdLot.setText("");
        mEdQty.setText("");
    }

    /**
     * �ж����е�edtext�Ƿ�Ϊ��
     *
     * @return true---->���е�ed����Ϊ��,false---->���е�ed��Ϊ��
     */
    private boolean isAllEdNotNull() {
        return (!TextUtils.isEmpty(mEdBarCode.getText())
                && !TextUtils.isEmpty(mEdEncoding.getText())
                && !TextUtils.isEmpty(mEdName.getText())
                && !TextUtils.isEmpty(mEdType.getText())
                && !TextUtils.isEmpty(mEdUnit.getText())
                && !TextUtils.isEmpty(mEdLot.getText())
                && !TextUtils.isEmpty(mEdQty.getText()));
    }

    /**
     * ��ȡ���������Ϣ
     *
     * @param sku
     */
    private void GetInvBaseInfo(String sku) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("InvCode", sku);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread td = new Thread(requestThread);
        td.start();
    }


    /**
     * ͨ����ȡ����json �����õ�������Ϣ,�����õ�UI��
     *
     * @param json
     * @throws JSONException
     */
    String pk_invbasdoc = "";
    String pk_invmandoc = "";

    private void SetInvBaseToUI(JSONObject json) throws JSONException {
        Log.d(TAG, "SetInvBaseToUI: " + json);
        if (json.getBoolean("Status")) {
            JSONArray val = json.getJSONArray("baseInfo");
            HashMap<String, Object> map = null;
            for (int i = 0; i < val.length(); i++) {
                JSONObject tempJso = val.getJSONObject(i);
                map = new HashMap<String, Object>();
                map.put("invname", tempJso.getString("invname"));   //�������
                map.put("invcode", tempJso.getString("invcode"));   //00179
                map.put("measname", tempJso.getString("measname"));   //ǧ��
                map.put("pk_invbasdoc", tempJso.getString("pk_invbasdoc"));
                pk_invbasdoc = tempJso.getString("pk_invbasdoc");
                map.put("pk_invmandoc", tempJso.getString("pk_invmandoc"));
                pk_invmandoc = tempJso.getString("pk_invmandoc");
//                map.put("invtype", tempJso.getString("invtype"));   //�ͺ�
//                map.put("invspec", tempJso.getString("invspec"));   //���
                map.put("oppdimen", tempJso.getString("oppdimen"));   //����
            }
            if (map != null) {
                mEdName.setText(map.get("invname").toString());
                mEdUnit.setText(map.get("measname").toString());
//                mEdType.setText(map.get("invtype").toString());
//                mEdSpectype.setText(map.get("invspec").toString());
            }

        }
    }

    /**
     * mEdBarCode�����룩�ļ���
     */
    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(mEdBarCode.getText().toString())) {
                mEdEncoding.setText("");
                mEdType.setText("");
                mEdLot.setText("");
                mEdName.setText("");
                mEdUnit.setText("");
                mEdQty.setText("");
            }
        }
    };


    /**
     * �س����ĵ���¼�
     */
    View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.ed_bar_code:
                        BarAnalysis();
                        if (isAllEdNotNull() && addDataToList()) {
                            ChangeAllEdTextToEmpty();
                        }
                        return true;
                    case R.id.ed_lot:
                        if (TextUtils.isEmpty(mEdLot.getText())) {
                            Toast.makeText(MaterialOutScanAct.this, "���������κ�", Toast.LENGTH_SHORT).show();
                        } else {
                            mEdQty.requestFocus();
                        }
                        return true;
                    case R.id.ed_qty:
                        if (TextUtils.isEmpty(mEdQty.getText())) {
                            Toast.makeText(MaterialOutScanAct.this, "����������", Toast.LENGTH_SHORT).show();
                        } else {
//                            if (isAllEdNotNull() && ) {
                            if (addDataToList()){
                                mEdBarCode.requestFocus();
                                ChangeAllEdTextToEmpty();
                            }
//                            }

                        }
                        return true;
                }
            }
            return false;
        }
    };
}
