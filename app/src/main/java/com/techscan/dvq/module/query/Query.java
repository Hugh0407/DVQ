package com.techscan.dvq.module.query;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.techscan.dvq.R;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SoundHelper;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.login.MainLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.techscan.dvq.common.Utils.formatDecimal;
import static com.techscan.dvq.common.Utils.showToast;

public class Query extends Activity {

    @Nullable
    @InjectView(R.id.ed_bar_code)
    EditText mEdBarCode;
    @Nullable
    @InjectView(R.id.ed_name)
    EditText mEdName;
    @Nullable
    @InjectView(R.id.ed_spec)
    EditText mEdSpec;
    @Nullable
    @InjectView(R.id.ed_lot)
    EditText mEdLot;
    @Nullable
    @InjectView(R.id.ed_supplier)
    EditText mEdSupplier;
    @Nullable
    @InjectView(R.id.ed_shelf_life)
    EditText mEdShelfLife;
    @Nullable
    @InjectView(R.id.ed_manual)
    EditText mEdManual;
    @Nullable
    @InjectView(R.id.ed_in_time)
    EditText mEdInTime;
    @Nullable
    @InjectView(R.id.ed_total_num)
    EditText mEdTotalNum;
    @Nullable
    @InjectView(R.id.btn_back)
    Button   mBtnBack;

    @Nullable
    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        ButterKnife.inject(this);
        mActivity = this;
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    @Override
    public void onBackPressed() {
    }

    @OnClick(R.id.btn_back)
    public void onViewClicked() {
        finish();
    }

    private void init() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("��ѯɨ��");
        mEdBarCode.setOnKeyListener(new MyOnKeyListener(mEdBarCode));
        mEdBarCode.addTextChangedListener(new CustomTextWatcher(mEdBarCode));
    }

    @Nullable
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    JSONObject json = (JSONObject) msg.obj;
                    if (null == json) {
                        showToast(mActivity, "���浥��ʧ��");
                        return;
                    }
                    SoundHelper.playOK();
                    Log.d("TAG", "'json: " + json.toString());
                    setInvBaseToUI(json);
                    break;
                default:
                    break;
            }
        }
    };

    private boolean barAnalysis() {
        String Bar = mEdBarCode.getText().toString().trim();
        if (Bar.contains("\n")) {
            Bar = Bar.replace("\n", "");
        }
        mEdBarCode.setText(Bar);
        mEdBarCode.setSelection(mEdBarCode.length());   //������ƶ�������λ��
        mEdBarCode.selectAll();

        SplitBarcode barDecoder = new SplitBarcode(Bar);
        if (barDecoder.BarcodeType.equals("C")) {   //C|SKU|LOT|TAX|QTY|SN
            mEdLot.setText(barDecoder.cBatch);
            mEdTotalNum.setText(String.valueOf(barDecoder.dQuantity));
            getInvBaseInfoByBarcode(barDecoder.cInvCode, barDecoder.cBatch, MainLogin.objLog.STOrgCode);
            return true;
        } else if (barDecoder.BarcodeType.equals("TC")) {    //TC|SKU|LOT|TAX|QTY|NUM|SN
            mEdLot.setText(barDecoder.cBatch);
            double qty = barDecoder.dQuantity;
            double num = barDecoder.iNumber;
            mEdTotalNum.setText(formatDecimal(qty * num));
            getInvBaseInfoByBarcode(barDecoder.cInvCode, barDecoder.cBatch, MainLogin.objLog.STOrgCode);
            return true;
        } else if (barDecoder.BarcodeType.equals("P")) {// ���� P|SKU|LOT|WW|TAX|QTY|CW|ONLY|SN    9λ
            mEdLot.setText(barDecoder.cBatch);
            mEdTotalNum.setText(barDecoder.iNumber);
            getInvBaseInfoByBarcode(barDecoder.cInvCode, barDecoder.cBatch, MainLogin.objLog.STOrgCode);
            return true;
        } else if (barDecoder.BarcodeType.equals("TP")) {//����TP|SKU|LOT|WW|TAX|QTY|NUM|CW|ONLY|SN
            mEdLot.setText(barDecoder.cBatch);
            double weight = barDecoder.dQuantity;
            double mEdNum = Double.valueOf(barDecoder.iNumber);
            mEdTotalNum.setText(formatDecimal(weight * mEdNum));
            getInvBaseInfoByBarcode(barDecoder.cInvCode, barDecoder.cBatch, MainLogin.objLog.STOrgCode);
            return true;
        } else {
            showToast(mActivity, "��������,��������");
            return false;
        }
    }

    /**
     * ������е�Edtext
     */
    private void changeAllEdTextToEmpty() {
        mEdName.setText("");
        mEdSpec.setText("");
        mEdLot.setText("");
        mEdTotalNum.setText("");
        mEdSupplier.setText("");
        mEdInTime.setText("");
        mEdShelfLife.setText("");
        mEdManual.setText("");

    }

    /**
     * ��ȡ���������Ϣ
     *
     * @param sku ���ϱ���
     */
    private void getInvBaseInfoByBarcode(String sku, String batchcode, String crop) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvInfoByBarcode");
        parameter.put("CompanyCode", MainLogin.objLog.STOrgCode);
        parameter.put("Invcode", sku);
        parameter.put("BatchCode", batchcode);
        parameter.put("Crop", crop);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread        td            = new Thread(requestThread);
        td.start();
    }


    /**
     * ͨ����ȡ����json �����õ�������Ϣ,�����õ�UI��
     *
     * @param json
     * @throws JSONException
     */

    private void setInvBaseToUI(JSONObject json) {
        Log.d("TAG", "setInvBaseToUI: " + json);
        try {
            if (json.getBoolean("Status")) {
                JSONArray               val = json.getJSONArray("baseInfo");
                HashMap<String, Object> map = null;
                for (int i = 0; i < val.length(); i++) {
                    JSONObject tempJso = val.getJSONObject(i);
                    map = new HashMap<String, Object>();
                    map.put("invcode", tempJso.getString("invcode"));
                    map.put("invname", tempJso.getString("invname"));
                    map.put("pk_cubasdoc", tempJso.getString("pk_cubasdoc"));
                    map.put("vfree4", tempJso.getString("vfree4"));   //�����ֲ��
                    map.put("invtype", tempJso.getString("invtype"));   //�ͺ�
                    map.put("invspec", tempJso.getString("invspec"));   //���
                    map.put("dbilldate", tempJso.getString("dbilldate"));
                    map.put("vbatchcode", tempJso.getString("vbatchcode")); //����
                    map.put("custname", tempJso.getString("custname"));
                }
                if (map != null) {
                    mEdName.setText(map.get("invname").toString());
                    mEdSpec.setText(map.get("invspec").toString());
                    mEdLot.setText(map.get("vbatchcode").toString());
                    mEdSupplier.setText(map.get("custname").toString());
//                mEdShelfLife.setText(map.get("createtime").toString());
                    String s = map.get("vfree4").toString();
                    if (s.equals("null")) {
                        mEdManual.setText("");
                    } else {
                        mEdManual.setText(s);
                    }
                    mEdInTime.setText(map.get("dbilldate").toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class MyOnKeyListener implements View.OnKeyListener {
        EditText ed;

        public MyOnKeyListener(EditText ed) {
            this.ed = ed;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.ed_bar_code:
                        barAnalysis();
                        return true;
                }
            }
            return false;
        }
    }

    /**
     * mEdBarCode�����룩�ļ���
     */
    private class CustomTextWatcher implements TextWatcher {
        EditText ed;

        public CustomTextWatcher(EditText ed) {
            this.ed = ed;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (ed.getId()) {
                case R.id.ed_bar_code:
                    if (TextUtils.isEmpty(mEdBarCode.getText().toString())) {
                        changeAllEdTextToEmpty();
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
