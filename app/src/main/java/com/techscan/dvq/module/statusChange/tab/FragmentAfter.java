package com.techscan.dvq.module.statusChange.tab;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.Goods;
import com.techscan.dvq.bean.PurGood;
import com.techscan.dvq.common.SoundHelper;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.login.MainLogin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.techscan.dvq.common.Utils.formatDecimal;
import static com.techscan.dvq.common.Utils.isNumber;
import static com.techscan.dvq.common.Utils.showToast;
import static com.techscan.dvq.module.statusChange.tab.StatusChangeScanAct.detailList;
import static com.techscan.dvq.module.statusChange.tab.StatusChangeScanAct.taskList;

/**
 * Created by cloverss on 2017/8/14.
 */

public class FragmentAfter extends Fragment {


    @InjectView(R.id.ed_encoding)
    EditText edEncoding;
    @InjectView(R.id.ed_name)
    EditText edName;
    @InjectView(R.id.ed_type)
    EditText edType;
    @InjectView(R.id.ed_spectype)
    EditText edSpectype;
    @InjectView(R.id.ed_lot)
    EditText edLot;
    @InjectView(R.id.ed_cost_object)
    EditText edCostObject;
    @InjectView(R.id.ed_num)
    EditText edNum;
    @InjectView(R.id.ed_weight)
    EditText edWeight;
    @InjectView(R.id.ed_qty)
    EditText edQty;
    @InjectView(R.id.ed_unit)
    EditText edUnit;


    String currentBarCode;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_after, null);
        ButterKnife.inject(this, view);
        edNum.setOnKeyListener(onKeyListener);
        edNum.addTextChangedListener(new CustomTextWatcher(edNum));
        edQty.setOnKeyListener(onKeyListener);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventBean bean) {
        if (bean.position == -1) {
            String bar = bean.barcode.split(",")[1];
            currentBarCode = bar;
            barAnalysis(bar);
        }

        if (bean.position == 1) {
            currentBarCode = bean.barcode;
            barAnalysis(bean.barcode);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 3:
                    //��������������������Ұ��������õ�UI��
                    setInvBaseToUI((JSONObject) msg.obj);
                    break;

            }
        }
    };

    /**
     * ��ȡ���������Ϣ
     *
     * @param invcode ���ϱ���
     */
    private void getInvBaseInfo(String invcode) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("InvCode", invcode);
        parameter.put("TableName", "baseInfo");
        Utils.doRequest(parameter, handler, 3);
    }

    /**
     * ͨ����ȡ����json �����õ�������Ϣ,�����õ�UI��
     *
     * @param json
     * @throws JSONException
     */
    String pk_invbasdoc = "";
    String pk_invmandoc = "";

    private void setInvBaseToUI(JSONObject json) {
        Log.d("TAG", "setInvBaseToUI: " + json);
        try {
            if (json == null) {
                showToast(getActivity(), "�����������");
                return;
            }

            if (json.getBoolean("Status")) {
                JSONArray               val = json.getJSONArray("baseInfo");
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
                    map.put("invtype", tempJso.getString("invtype"));   //�ͺ�
                    map.put("invspec", tempJso.getString("invspec"));   //���
                    map.put("oppdimen", tempJso.getString("oppdimen"));   //����
                }
                if (map != null) {
                    edName.setText(map.get("invname").toString());
                    edUnit.setText(map.get("measname").toString());
                    edType.setText(map.get("invtype").toString());
                    edSpectype.setText(map.get("invspec").toString());
                    edCostObject.setText(map.get("invname").toString());
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean barAnalysis(String bar) {
        bar = bar.trim();
        if (bar.contains("\n")) {
            bar = bar.replace("\n", "");
        }
        SplitBarcode barDecoder = new SplitBarcode(bar);

        if (barDecoder.BarcodeType.equals("P")) {
            //�жϸ������ڡ����� �б����Ƿ����
            for (PurGood pur : taskList) {
                if (!pur.getInvcode().equals(barDecoder.cInvCode) && !pur.getVbatchcode().equals(barDecoder.cBatch)) {
                    Utils.showToast(getActivity(), "����Ʒ���������б�");
                    SoundHelper.playWarning();
                    return false;
                }
            }
            edNum.setEnabled(true);
            String invCode = barDecoder.cInvCode;
            if (invCode.contains(",")) {
                invCode = invCode.split(",")[0];
            }
            edEncoding.setText(invCode);
            getInvBaseInfo(invCode);
            String batch = barDecoder.cBatch;
            if (batch.contains(",")) {
                batch = batch.split(",")[0];
            }
            edLot.setText(batch);
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edQty.setText("");
            edNum.setText("1");
            edNum.setSelection(edNum.length());   //������ƶ�������λ��
            edQty.setText(String.format("%.2f", barDecoder.dQuantity));
            edNum.requestFocus();  //����ɨ�����������������,��������,��ӵ��б�
            return true;
        } else if (barDecoder.BarcodeType.equals("TP")) {

            //�жϸ������ڡ����� �б����Ƿ����
            for (PurGood pur : taskList) {
                if (!pur.getInvcode().equals(barDecoder.cInvCode) && !pur.getVbatchcode().equals(barDecoder.cBatch)) {
                    Utils.showToast(getActivity(), "����Ʒ���������б�");
                    SoundHelper.playWarning();
                    return false;
                }
            }

            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(bar)) {
                    showToast(getActivity(), "��������ɨ��");
                    SoundHelper.playWarning();
                    return false;
                }
            }
            edQty.setEnabled(true);
            String encoding = barDecoder.cInvCode;
            if (encoding.contains(",")) {
                encoding = encoding.split(",")[0];
            }
            edEncoding.setText(encoding);
            getInvBaseInfo(encoding);
            String batch = barDecoder.cBatch;
            if (batch.contains(",")) {
                batch = batch.split(",")[0];
            }
            edLot.setText(batch);
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edNum.setText(String.valueOf(barDecoder.iNumber));
            double weight = barDecoder.dQuantity;
            double edNum  = Double.valueOf(barDecoder.iNumber);
            edQty.setText(formatDecimal(weight * edNum));
            edQty.requestFocus();
            return true;
        } else {
            showToast(getActivity(), "����������������");
            SoundHelper.playWarning();
            return false;
        }
    }

    /**
     * �����Ϣ�� ������
     *
     * @return
     */
    private void addDataToDetailList() {
        Goods goods = new Goods();
        goods.setBarcode(currentBarCode);
        goods.setEncoding(edEncoding.getText().toString());
        goods.setName(edName.getText().toString());
        goods.setType(edType.getText().toString());
        goods.setSpec(edSpectype.getText().toString());
        goods.setUnit(edUnit.getText().toString());
        goods.setLot(edLot.getText().toString());
        goods.setQty(Float.valueOf(edQty.getText().toString()));
        goods.setCostObject(edCostObject.getText().toString());
        goods.setPk_invbasdoc(pk_invbasdoc);
        goods.setPk_invmandoc(pk_invmandoc);
        for (PurGood pur : taskList) {
            if (pur.getInvcode().equals(goods.getEncoding()) &&
                    pur.getVbatchcode().equals(goods.getLot())) {
                float nowNum = goods.getQty() + Float.valueOf(pur.getNum_task());
                if (nowNum > Float.valueOf(pur.getNshouldinnum())) {
                    Utils.showToast(getActivity(), "��������,������ɨ��");
                    SoundHelper.playWarning();
                    return;
                }
                pur.setNum_task(String.valueOf(nowNum));
            }
        }
        detailList.add(goods);
        SoundHelper.playOK();
    }

    private void changeAllEdTextToEmpty() {
        edEncoding.setText("");
        edName.setText("");
        edType.setText("");
        edSpectype.setText("");
        edLot.setText("");
        edCostObject.setText("");
        edNum.setText("");
        edWeight.setText("");
        edQty.setText("");
        edUnit.setText("");
        edQty.setEnabled(false);
        edNum.setEnabled(false);
    }

    /**
     * �ж����е�edtext�Ƿ�Ϊ��
     *
     * @return true---->���е�ed����Ϊ��,false---->���е�ed��Ϊ��
     */
    private boolean isAllEdNotNull() {

        if (TextUtils.isEmpty(edEncoding.getText())) {
            showToast(getActivity(), "���ϱ��벻��Ϊ��");
            return false;
        }

        if (TextUtils.isEmpty(edName.getText())) {
            showToast(getActivity(), "�������Ʋ���Ϊ��");
            return false;
        }

        if (TextUtils.isEmpty(edType.getText())) {
            showToast(getActivity(), "���Ͳ���Ϊ��");
            return false;
        }

        if (TextUtils.isEmpty(edSpectype.getText())) {
            showToast(getActivity(), "��񲻿�Ϊ��");
            return false;
        }

        if (TextUtils.isEmpty(edUnit.getText())) {
            showToast(getActivity(), "��λ����Ϊ��");
            return false;
        }

        if (TextUtils.isEmpty(edLot.getText())) {
            showToast(getActivity(), "���β���Ϊ��");
            return false;
        }

        if (TextUtils.isEmpty(edQty.getText())) {
            showToast(getActivity(), "��������Ϊ��");
            return false;
        }
        return true;
    }

    /**
     * �س����ĵ���¼�
     */

    View.OnKeyListener onKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.ed_qty:
                        if (TextUtils.isEmpty(edQty.getText().toString())) {
                            showToast(getActivity(), "����������");
                            return true;
                        }
                        String qty_s = edQty.getText().toString();
                        if (!isNumber(qty_s)) {
                            showToast(getActivity(), "��������ȷ");
                            edQty.setText("");
                            return true;
                        }
                        float qty_f = Float.valueOf(qty_s);
                        if (qty_f <= 0) {
                            showToast(getActivity(), "��������ȷ");
                            edQty.setText("");
                            return true;
                        }

                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            changeAllEdTextToEmpty();
                            return true;
                        }
                        return true;
                    case R.id.ed_num:
                        if (TextUtils.isEmpty(edNum.getText().toString())) {
                            Utils.showToast(getActivity(), "����������");
                            return true;
                        }
                        if (!isNumber(edNum.getText().toString())) {
                            Utils.showToast(getActivity(), "��������ȷ");
                            return true;
                        }
                        //������Ҫ���� �ж��ٰ����������������
                        float num = Float.valueOf(edNum.getText().toString());
                        if (num < 0) {
                            Utils.showToast(getActivity(), "��������ȷ");
                            return true;
                        }

                        float weight = Float.valueOf(edWeight.getText().toString());
                        edQty.setText(String.valueOf(num * weight));
                        addDataToDetailList();
                        changeAllEdTextToEmpty();
                        return true;
                }
            }
            return false;
        }
    };

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
                case R.id.ed_num:
                    if (TextUtils.isEmpty(edNum.getText())) {
                        edQty.setText("");
                        return;
                    }
                    if (!isNumber(edNum.getText().toString())) {
                        Utils.showToast(getActivity(), "��������ȷ");
                        edNum.setText("");
                        return;
                    }
                    if (Float.valueOf(edNum.getText().toString()) < 0) {
                        Utils.showToast(getActivity(), "��������ȷ");
                        return;
                    }
                    float num = Float.valueOf(edNum.getText().toString());
                    float weight = Float.valueOf(edWeight.getText().toString());
                    edQty.setText(String.valueOf(num * weight));
                    break;
            }
        }
    }
}
