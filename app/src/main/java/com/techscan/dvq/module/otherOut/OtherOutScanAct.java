package com.techscan.dvq.module.otherOut;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.Goods;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SoundHelper;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.login.MainLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.techscan.dvq.R.id.ed_num;
import static com.techscan.dvq.common.Utils.formatDecimal;
import static com.techscan.dvq.common.Utils.isNumber;
import static com.techscan.dvq.common.Utils.showToast;

public class OtherOutScanAct extends Activity {

    @InjectView(R.id.ed_bar_code)
    EditText edBarCode;
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
    @InjectView(R.id.ed_cost_name)
    EditText edCostName;
    @InjectView(R.id.ed_manual)
    EditText edManual;
    @InjectView(ed_num)
    EditText edNum;
    @InjectView(R.id.ed_weight)
    EditText edWeight;
    @InjectView(R.id.ed_qty)
    EditText edQty;
    @InjectView(R.id.ed_unit)
    EditText edUnit;
    @InjectView(R.id.btn_overview)
    Button   btnOverview;
    @InjectView(R.id.btn_detail)
    Button   btnDetail;
    @InjectView(R.id.btn_back)
    Button   btnBack;

    String TAG = this.getClass().getSimpleName();
    public static List<Goods> detailList = new ArrayList<Goods>();
    public static List<Goods> ovList     = new ArrayList<Goods>();

    Activity activity;
    String CWAREHOUSEID = "";
    String PK_CALBODY   = "";
    String BATCH        = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_out_scan);
        ButterKnife.inject(this);
        activity = this;
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    @Override
    public void onBackPressed() {
    }

    private void init() {
        CWAREHOUSEID = getIntent().getStringExtra("CWAREHOUSEID");
        PK_CALBODY = getIntent().getStringExtra("PK_CALBODY");
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("��������ɨ��");
        edBarCode.setOnKeyListener(mOnKeyListener);
        edLot.setOnKeyListener(mOnKeyListener);
        edQty.setOnKeyListener(mOnKeyListener);
        edNum.setOnKeyListener(mOnKeyListener);
        edManual.setOnKeyListener(mOnKeyListener);
        edCostObject.setOnKeyListener(mOnKeyListener);
        edNum.addTextChangedListener(new CustomTextWatcher(edName));
        edBarCode.addTextChangedListener(new CustomTextWatcher(edBarCode));
    }

    @OnClick({R.id.btn_overview, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_overview:
                break;
            case R.id.btn_detail:
                break;
            case R.id.btn_back:
                break;
        }
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
                    try {
                        if (json != null && json.getBoolean("Status")) {
                            Log.d(TAG, "InvBaseInfo: " + json.toString());
                            setInvBaseToUI(json);
                            getInvBaseVFree4(BATCH);// ��ȡ�����ֲ��
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    //���ú����ֲ��
                    JSONObject jsonObject = (JSONObject) msg.obj;
                    try {
                        if (jsonObject != null && jsonObject.getBoolean("Status")) {
                            Log.d("TAG", "vfree4: " + jsonObject);
                            JSONArray jsonArray = jsonObject.getJSONArray("vfree4");
                            if (jsonArray.length() > 0) {
                                JSONObject j      = jsonArray.getJSONObject(0);
                                String     vfree4 = j.getString("vfree4");
                                if (vfree4.equals("null")) {
                                    edManual.setText("");
                                } else {
                                    edManual.setText(vfree4);
                                }
                            }
                        } else {
                            Log.d("TAG", "vfree4: null");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    JSONObject costObj = (JSONObject) msg.obj;
                    try {
                        if (costObj != null && costObj.getBoolean("Status")) {
                            Log.d(TAG, "InvBaseInfo: " + costObj.toString());
                            setInvBaseCostObjToUI(costObj);
                            edManual.requestFocus();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };


    private void showDialog(final List list, final BaseAdapter adapter, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        if (list.size() > 0) {
            View     view = LayoutInflater.from(activity).inflate(R.layout.dialog_scan_details, null);
            ListView lv   = (ListView) view.findViewById(R.id.lv);
            if (title.equals("ɨ����ϸ")) { //ֻ����ϸ��ҳ���ǿɵ���ģ�����ҳ���ǲ��ɵ����
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        AlertDialog.Builder delDialog = new AlertDialog.Builder(activity);
                        delDialog.setTitle("�Ƿ�ɾ����������");
                        delDialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                adapter.notifyDataSetChanged();
                                addDataToOvList();
                            }
                        });
                        delDialog.setNegativeButton("ȡ��", null);
                        delDialog.show();
                    }
                });
            }
            lv.setAdapter(adapter);
            builder.setView(view);
        } else {
            builder.setMessage("û��ɨ������");
        }
        builder.setPositiveButton("ȷ��", null);
        builder.show();
    }


    /**
     * �������
     */
    private boolean barAnalysis() {
        String bar = edBarCode.getText().toString().trim();
        if (bar.contains("\n")) {
            bar = bar.replace("\n", "");
        }
        edBarCode.setText(bar);
        edBarCode.setSelection(edBarCode.length());   //������ƶ�������λ��
        edBarCode.selectAll();
        SplitBarcode barDecoder = new SplitBarcode(bar);

        if (!barDecoder.creatorOk) {
            showToast(activity, "��������");
            return false;
        }
        if (barDecoder.BarcodeType.equals("Y")) {
            //�����Һ��Ļ���Ҫ����Һ�����������������ò��ɱ༭
            edNum.setEnabled(false);
            edLot.setEnabled(true);
            edQty.setEnabled(true);
            edEncoding.setText(barDecoder.cInvCode);
            edLot.setText("");
            edQty.setText("");
            getInvBaseInfo(barDecoder.cInvCode);
            edLot.requestFocus();  //�����Һ����Ҫ�ֶ����롰���Ρ��͡�������,���ｫ������������Σ�lot����
            return true;
        } else if (barDecoder.BarcodeType.equals("C")) {
            //����ǰ��룬���κ����ض��ı�Ϊ���ɱ༭��������Ա�����룬����������������
            BATCH = barDecoder.cBatch;
            edLot.setEnabled(false);
            edQty.setEnabled(false);
            edNum.setEnabled(true);
            edEncoding.setText(barDecoder.cInvCode);
            edLot.setText(barDecoder.cBatch);
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edQty.setText("");
            edNum.setText("1");
            edNum.selectAll();
            edNum.setSelection(edNum.length());   //������ƶ�������λ��
            getInvBaseInfo(barDecoder.cInvCode);
            edNum.requestFocus();  //����ɨ�����������������,��������,��ӵ��б�
            return true;
        } else if (barDecoder.BarcodeType.equals("TC")) {
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(bar)) {
                    showToast(activity, "��������ɨ��");
                    SoundHelper.playWarning();
                    return false;
                }
            }
            //��������룬ȫ������Ϊ���ɱ༭
            BATCH = barDecoder.cBatch;
            edLot.setEnabled(false);
            edQty.setEnabled(false);
            edNum.setEnabled(false);
            edManual.setEnabled(true);
            edEncoding.setText(barDecoder.cInvCode);
            edLot.setText(barDecoder.cBatch);
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edNum.setText(String.valueOf(barDecoder.iNumber));
            double qty = barDecoder.dQuantity;
            double num = barDecoder.iNumber;
            edQty.setText(formatDecimal(qty * num));
            getInvBaseInfo(barDecoder.cInvCode);
            edCostObject.requestFocus();
            return true;
        } else {
            showToast(activity, "��������");
            SoundHelper.playWarning();
            return false;
        }
    }


    /**
     * ������ݵ������б��У���д��Goods��equals��hashcode
     */
    private void addDataToOvList() {
        ovList.clear();
        int detailSize = detailList.size();
        for (int i = 0; i < detailSize; i++) {
            Goods dtGood = detailList.get(i);
            if (ovList.contains(dtGood)) {
                int   j      = ovList.indexOf(dtGood);
                Goods ovGood = ovList.get(j);
                ovGood.setQty(ovGood.getQty() + dtGood.getQty());
            } else {
                Goods good = new Goods();
                good.setBarcode(dtGood.getBarcode());
                good.setEncoding(dtGood.getEncoding());
                good.setName(dtGood.getName());
                good.setType(dtGood.getType());
                good.setUnit(dtGood.getUnit());
                good.setLot(dtGood.getLot());
                good.setSpec(dtGood.getSpec());
                good.setQty(dtGood.getQty());
                good.setNum(dtGood.getNum());
                good.setPk_invbasdoc(dtGood.getPk_invbasdoc());
                good.setPk_invmandoc(dtGood.getPk_invmandoc());
                good.setCostObject(dtGood.getCostObject());
                good.setManual(dtGood.getManual());
                good.setPk_invmandoc_cost(dtGood.getPk_invmandoc_cost());
                ovList.add(good);
            }
        }
    }

    /**
     * �����Ϣ�� ������
     */
    private void addDataToDetailList() {
        Goods goods = new Goods();
        goods.setBarcode(edBarCode.getText().toString());
        goods.setEncoding(edEncoding.getText().toString());
        goods.setName(edName.getText().toString());
        goods.setType(edType.getText().toString());
        goods.setSpec(edSpectype.getText().toString());
        goods.setUnit(edUnit.getText().toString());
        goods.setLot(edLot.getText().toString());
        goods.setQty(Float.valueOf(edQty.getText().toString()));
        goods.setCostObject(edCostObject.getText().toString());
        goods.setManual(edManual.getText().toString());
        goods.setPk_invbasdoc(pk_invbasdoc);
        goods.setPk_invmandoc(pk_invmandoc);
        goods.setManual(edManual.getText().toString());
        goods.setPk_invmandoc_cost(pk_invmandoc_cost);
        detailList.add(goods);
        addDataToOvList();
        SoundHelper.playOK();
    }

    /**
     * ������е�Edtext
     */
    private void changeAllEdTextToEmpty() {
        edNum.setText("");
        edBarCode.setText("");
        edEncoding.setText("");
        edName.setText("");
        edType.setText("");
        edUnit.setText("");
        edLot.setText("");
        edQty.setText("");
        edWeight.setText("");
        edSpectype.setText("");
        edCostObject.setText("");
        edManual.setText("");
        edLot.setEnabled(false);
        edNum.setEnabled(false);
        edQty.setEnabled(false);
        edManual.setEnabled(false);
    }

    /**
     * �ж����е�edtext�Ƿ�Ϊ��
     *
     * @return true---->���е�ed����Ϊ��,false---->���е�ed��Ϊ��
     */
    private boolean isAllEdNotNull() {
        if (edManual.isEnabled()) {
            if (TextUtils.isEmpty(edManual.getText())) {
                showToast(activity, "�����ֲ�Ų���Ϊ��");
                return false;
            }
        }
        if (!TextUtils.isEmpty(edBarCode.getText())
                && !TextUtils.isEmpty(edEncoding.getText())
                && !TextUtils.isEmpty(edName.getText())
                && !TextUtils.isEmpty(edType.getText())
                && !TextUtils.isEmpty(edSpectype.getText())
                && !TextUtils.isEmpty(edUnit.getText())
                && !TextUtils.isEmpty(edLot.getText())
                && !TextUtils.isEmpty(edCostObject.getText())
                && !TextUtils.isEmpty(edQty.getText())) {
            return true;
        } else {
            showToast(activity, "��Ϣ����������˶�");
            return false;
        }
    }

    /**
     * ��ȡ���������Ϣ
     *
     * @param invCode ���ϱ���
     */
    private void getInvBaseInfo(String invCode) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("InvCode", invCode);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread        td            = new Thread(requestThread);
        td.start();
    }


    /**
     * ��ȡ���������Ϣ �����ֲ��
     */
    private void getInvBaseVFree4(String batch) {
        if (TextUtils.isEmpty(batch)) {
            return;
        }
        HashMap<String, String> para = new HashMap<String, String>();
        para.put("FunctionName", "GetInvFreeByInvCodeAndLot");
        para.put("CORP", MainLogin.objLog.STOrgCode);
        para.put("BATCH", batch);
        para.put("WAREHOUSEID", CWAREHOUSEID);
        para.put("CALBODYID", PK_CALBODY);
        para.put("CINVBASID", pk_invbasdoc);
        para.put("INVENTORYID", pk_invmandoc);
        para.put("TableName", "vfree4");
        RequestThread requestThread = new RequestThread(para, mHandler, 2);
        Thread        td            = new Thread(requestThread);
        td.start();
    }

    /**
     * ��ȡ��Ʒ����
     *
     * @param invCode ���ϱ���
     */
    private void getInvCostObj(String invCode) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("InvCode", invCode);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 3);
        Thread        td            = new Thread(requestThread);
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

    private void setInvBaseToUI(JSONObject json) {
        Log.d(TAG, "setInvBaseToUI: " + json);
        try {
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
                    map.put("isfree4", tempJso.getString("isfree4"));   //����
                }
                if (map != null) {
                    edName.setText(map.get("invname").toString());
                    edUnit.setText(map.get("measname").toString());
                    edType.setText(map.get("invtype").toString());
                    edSpectype.setText(map.get("invspec").toString());
                    String vFree4 = map.get("isfree4").toString();
                    if (vFree4.equals("Y")) {   //ֻ��Y,����ֵ
                        edManual.setEnabled(true);
                    } else {
                        edManual.setEnabled(false);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * ��ȡ�ɱ�����
     *
     * @param json
     * @throws JSONException
     */
    String pk_invmandoc_cost = "";

    private void setInvBaseCostObjToUI(JSONObject json) {
        Log.d(TAG, "setInvBaseCostObjToUI: " + json);
        try {
            if (json.getBoolean("Status")) {
                JSONArray               val = json.getJSONArray("baseInfo");
                HashMap<String, Object> map = null;
                for (int i = 0; i < val.length(); i++) {
                    JSONObject tempJso = val.getJSONObject(i);
                    map = new HashMap<String, Object>();
                    map.put("invname", tempJso.getString("invname"));   //�������
                    pk_invmandoc_cost = tempJso.getString("pk_invmandoc");
                }
                if (map != null) {
                    edCostName.setText(map.get("invname").toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * edBarCode�����룩�ļ���
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
                    if (TextUtils.isEmpty(edBarCode.getText().toString())) {
                        edNum.setText("");
                        edEncoding.setText("");
                        edName.setText("");
                        edType.setText("");
                        edUnit.setText("");
                        edLot.setText("");
                        edQty.setText("");
                        edWeight.setText("");
                        edSpectype.setText("");
                        edCostObject.setText("");
                    }
                    break;
                case ed_num:
                    if (TextUtils.isEmpty(edNum.getText())) {
                        edQty.setText("");
                        return;
                    }
                    if (!isNumber(edNum.getText().toString())) {
                        showToast(activity, "��������ȷ");
                        edNum.setText("");
                        return;
                    }
                    if (Float.valueOf(edNum.getText().toString()) <= 0) {
                        edNum.setText("");
                        showToast(activity, "��������ȷ");
                        return;
                    }
                    float num = Float.valueOf(edNum.getText().toString());
                    float weight = Float.valueOf(edWeight.getText().toString());
                    edQty.setText(formatDecimal(num * weight));
                    break;
            }
        }
    }

    /**
     * �س����ĵ���¼�
     */
    View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.ed_bar_code:
                        if (TextUtils.isEmpty(edBarCode.getText().toString())) {
                            showToast(activity, "����������");
                            return true;
                        }
                        barAnalysis();
//                        if (isAllEdNotNull()) {
////                            addDataToDetailList();
//                            edBarCode.requestFocus();  //�����ӳɹ����ܱ����������롱��
//                            changeAllEdTextToEmpty();
//                        } else {
//                        }

                        return true;
                    case R.id.ed_lot:
                        if (TextUtils.isEmpty(edLot.getText().toString())) {
                            showToast(activity, "���������κ�");
                            return true;
                        } else {
                            BATCH = edLot.getText().toString();
                            getInvBaseVFree4(BATCH);// ��ȡ�����ֲ��
                            edQty.requestFocus();  //���������κ󽲽���������������edQty����
                            return true;
                        }
                    case R.id.ed_qty:
                        if (TextUtils.isEmpty(edQty.getText().toString())) {
                            showToast(activity, "����������");
                            return true;
                        }
                        String qty_s = edQty.getText().toString();
                        if (!isNumber(qty_s)) {
                            showToast(activity, "��������ȷ");
                            edQty.setText("");
                            return true;
                        }
                        float qty_f = Float.valueOf(qty_s);
                        if (qty_f <= 0) {
                            showToast(activity, "��������ȷ");
                            edQty.setText("");
                            return true;
                        }

                        addDataToDetailList();
                        edBarCode.requestFocus();  //�����ӳɹ����ܱ����������롱��
                        changeAllEdTextToEmpty();
                        return true;
                    case R.id.ed_num:
                        if (TextUtils.isEmpty(edNum.getText().toString())) {
                            showToast(activity, "����������");
                            return true;
                        }
                        if (!isNumber(edNum.getText().toString())) {
                            showToast(activity, "��������ȷ");
                            return true;
                        }
                        //������Ҫ���� �ж��ٰ����������������
                        float num = Float.valueOf(edNum.getText().toString());
                        if (num <= 0) {
                            edNum.setText("");
                            showToast(activity, "��������ȷ");
                            return true;
                        }

                        float weight = Float.valueOf(edWeight.getText().toString());
                        edQty.setText(String.valueOf(num * weight));
                        addDataToDetailList();
                        edBarCode.requestFocus();  //�����ӳɹ����ܱ����������롱��
                        changeAllEdTextToEmpty();
                        return true;
                    case R.id.ed_manual:
                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            edBarCode.requestFocus();  //�����ӳɹ����ܱ����������롱��
                            changeAllEdTextToEmpty();
                        }
                        return true;
                    case R.id.ed_cost_object:
                        String invCode = edCostObject.getText().toString();
                        getInvCostObj(invCode);
                        return true;
                }
            }
            return false;
        }
    };
}
