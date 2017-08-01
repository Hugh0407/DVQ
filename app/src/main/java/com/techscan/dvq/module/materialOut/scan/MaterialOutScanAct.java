package com.techscan.dvq.module.materialOut.scan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.techscan.dvq.module.materialOut.MyBaseAdapter;

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


public class MaterialOutScanAct extends Activity {

    @InjectView(R.id.ed_bar_code)
    EditText mEdBarCode;    //����
    @InjectView(R.id.ed_encoding)
    EditText mEdEncoding;   //���루Sku��
    @InjectView(R.id.ed_type)
    EditText mEdType;   // �ͺ�
    @InjectView(R.id.ed_spectype)
    EditText mEdSpectype;   //���
    @InjectView(R.id.ed_lot)
    EditText mEdLot;        //����
    @InjectView(R.id.ed_name)
    EditText mEdName;       //������
    @InjectView(R.id.ed_unit)
    EditText mEdUnit;
    @InjectView(R.id.ed_qty)
    EditText mEdQty;
    @InjectView(R.id.btn_overview)
    Button   mBtnOverview;
    @InjectView(R.id.btn_detail)
    Button   mBtnDetail;
    @InjectView(R.id.btn_back)
    Button   mBtnBack;
    @InjectView(R.id.ed_num)
    EditText mEdNum;
    @InjectView(R.id.ed_weight)
    EditText mEdWeight;
    @InjectView(R.id.ed_cost_object)
    EditText mEdCostObject;
    @InjectView(R.id.ed_manual)
    EditText mEdManual;
    @InjectView(R.id.ed_cost_name)
    EditText edCostName;

    String TAG = this.getClass().getSimpleName();
    public static List<Goods> detailList = new ArrayList<Goods>();
    public static List<Goods> ovList     = new ArrayList<Goods>();
    Activity mActivity;
    String CWAREHOUSEID = "";
    String PK_CALBODY   = "";
    String BATCH        = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_out_scan);
        ButterKnife.inject(this);
        mActivity = this;
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    @OnClick({R.id.btn_overview, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_overview:
                MyBaseAdapter ovAdapter = new MyBaseAdapter(ovList);
                showDialog(ovList, ovAdapter, "ɨ������");
                break;
            case R.id.btn_detail:
                MyBaseAdapter myBaseAdapter = new MyBaseAdapter(detailList);
                showDialog(detailList, myBaseAdapter, "ɨ����ϸ");
                break;
            case R.id.btn_back:
                if (ovList.size() > 0) {
                    Intent in     = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("overViewList", (ArrayList<? extends Parcelable>) ovList);
                    in.putExtras(bundle);
                    mActivity.setResult(5, in);
                } else {
                    showToast(mActivity, "û��ɨ�赥��");
                }
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void init() {
        CWAREHOUSEID = getIntent().getStringExtra("CWAREHOUSEID");
        PK_CALBODY = getIntent().getStringExtra("PK_CALBODY");
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("��Ʒɨ��");
        mEdBarCode.setOnKeyListener(mOnKeyListener);
        mEdLot.setOnKeyListener(mOnKeyListener);
        mEdQty.setOnKeyListener(mOnKeyListener);
        mEdNum.setOnKeyListener(mOnKeyListener);
        mEdManual.setOnKeyListener(mOnKeyListener);
        mEdCostObject.setOnKeyListener(mOnKeyListener);
        mEdNum.addTextChangedListener(new CustomTextWatcher(mEdNum));
        mEdBarCode.addTextChangedListener(new CustomTextWatcher(mEdBarCode));
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
                            Log.d(TAG, "vfree4: " + jsonObject);
                            JSONArray jsonArray = jsonObject.getJSONArray("vfree4");
                            if (jsonArray.length() > 0) {
                                JSONObject j      = jsonArray.getJSONObject(0);
                                String     vfree4 = j.getString("vfree4");
                                if (vfree4.equals("null")) {
                                    mEdManual.setText("");
                                } else {
                                    mEdManual.setText(vfree4);
                                }
                            }
                        } else {
                            Log.d(TAG, "vfree4: null");
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
                            mEdManual.requestFocus();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void showDialog(final List list, final BaseAdapter adapter, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(title);
        if (list.size() > 0) {
            View     view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_scan_details, null);
            ListView lv   = (ListView) view.findViewById(R.id.lv);
            if (title.equals("ɨ����ϸ")) { //ֻ����ϸ��ҳ���ǿɵ���ģ�����ҳ���ǲ��ɵ����
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        AlertDialog.Builder delDialog = new AlertDialog.Builder(mActivity);
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
        String bar = mEdBarCode.getText().toString().trim();
        if (bar.contains("\n")) {
            bar = bar.replace("\n", "");
        }
        mEdBarCode.setText(bar);
        mEdBarCode.setSelection(mEdBarCode.length());   //������ƶ�������λ��
        mEdBarCode.selectAll();
        SplitBarcode barDecoder = new SplitBarcode(bar);

        if (!barDecoder.creatorOk) {
            showToast(mActivity, "��������");
            return false;
        }
        if (barDecoder.BarcodeType.equals("Y")) {
            //�����Һ��Ļ���Ҫ����Һ�����������������ò��ɱ༭
            mEdNum.setEnabled(false);
            mEdLot.setEnabled(true);
            mEdQty.setEnabled(true);
            mEdEncoding.setText(barDecoder.cInvCode);
            mEdLot.setText("");
            mEdQty.setText("");
            getInvBaseInfo(barDecoder.cInvCode);
            mEdLot.requestFocus();  //�����Һ����Ҫ�ֶ����롰���Ρ��͡�������,���ｫ������������Σ�lot����
            return true;
        } else if (barDecoder.BarcodeType.equals("C")) {
            //����ǰ��룬���κ����ض��ı�Ϊ���ɱ༭��������Ա�����룬����������������
            BATCH = barDecoder.cBatch;
            mEdLot.setEnabled(false);
            mEdQty.setEnabled(false);
            mEdNum.setEnabled(true);
            mEdEncoding.setText(barDecoder.cInvCode);
            mEdLot.setText(barDecoder.cBatch);
            mEdWeight.setText(String.valueOf(barDecoder.dQuantity));
            mEdQty.setText("");
            mEdNum.setText("1");
            mEdNum.selectAll();
            mEdNum.setSelection(mEdNum.length());   //������ƶ�������λ��
            getInvBaseInfo(barDecoder.cInvCode);
            mEdNum.requestFocus();  //����ɨ�����������������,��������,��ӵ��б�
            return true;
        } else if (barDecoder.BarcodeType.equals("TC")) {
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(bar)) {
                    showToast(mActivity, "��������ɨ��");
                    SoundHelper.playWarning();
                    return false;
                }
            }
            //��������룬ȫ������Ϊ���ɱ༭
            BATCH = barDecoder.cBatch;
            mEdLot.setEnabled(false);
            mEdQty.setEnabled(false);
            mEdNum.setEnabled(false);
            mEdEncoding.setText(barDecoder.cInvCode);
            mEdLot.setText(barDecoder.cBatch);
            mEdWeight.setText(String.valueOf(barDecoder.dQuantity));
            mEdNum.setText(String.valueOf(barDecoder.iNumber));
            double qty = barDecoder.dQuantity;
            double num = barDecoder.iNumber;
            mEdQty.setText(formatDecimal(qty * num));
            getInvBaseInfo(barDecoder.cInvCode);
            mEdCostObject.requestFocus();
            return true;
        } else {
            showToast(mActivity, "��������");
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
        goods.setBarcode(mEdBarCode.getText().toString());
        goods.setEncoding(mEdEncoding.getText().toString());
        goods.setName(mEdName.getText().toString());
        goods.setType(mEdType.getText().toString());
        goods.setSpec(mEdSpectype.getText().toString());
        goods.setUnit(mEdUnit.getText().toString());
        goods.setLot(mEdLot.getText().toString());
        goods.setQty(Float.valueOf(mEdQty.getText().toString()));
        goods.setCostObject(mEdCostObject.getText().toString());
        goods.setManual(mEdManual.getText().toString());
        goods.setPk_invbasdoc(pk_invbasdoc);
        goods.setPk_invmandoc(pk_invmandoc);
        goods.setManual(mEdManual.getText().toString());
        goods.setPk_invmandoc_cost(pk_invmandoc_cost);
        detailList.add(goods);
        addDataToOvList();
        SoundHelper.playOK();
    }

    /**
     * ������е�Edtext
     */
    private void changeAllEdTextToEmpty() {
        mEdNum.setText("");
        mEdBarCode.setText("");
        mEdEncoding.setText("");
        mEdName.setText("");
        mEdType.setText("");
        mEdUnit.setText("");
        mEdLot.setText("");
        mEdQty.setText("");
        mEdWeight.setText("");
        mEdSpectype.setText("");
        mEdCostObject.setText("");
        mEdManual.setText("");
        mEdLot.setEnabled(false);
        mEdNum.setEnabled(false);
        mEdQty.setEnabled(false);
        mEdManual.setEnabled(false);
    }

    /**
     * �ж����е�edtext�Ƿ�Ϊ��
     *
     * @return true---->���е�ed����Ϊ��,false---->���е�ed��Ϊ��
     */
    private boolean isAllEdNotNull() {
        if (mEdManual.isEnabled()) {
            if (TextUtils.isEmpty(mEdManual.getText())) {
                showToast(mActivity, "�����ֲ�Ų���Ϊ��");
                return false;
            }
        }
        if (!TextUtils.isEmpty(mEdBarCode.getText())
                && !TextUtils.isEmpty(mEdEncoding.getText())
                && !TextUtils.isEmpty(mEdName.getText())
                && !TextUtils.isEmpty(mEdType.getText())
                && !TextUtils.isEmpty(mEdSpectype.getText())
                && !TextUtils.isEmpty(mEdUnit.getText())
                && !TextUtils.isEmpty(mEdLot.getText())
                && !TextUtils.isEmpty(mEdCostObject.getText())
                && !TextUtils.isEmpty(mEdQty.getText())) {
            return true;
        } else {
            showToast(mActivity, "��Ϣ����������˶�");
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
                    mEdName.setText(map.get("invname").toString());
                    mEdUnit.setText(map.get("measname").toString());
                    mEdType.setText(map.get("invtype").toString());
                    mEdSpectype.setText(map.get("invspec").toString());
                    String vFree4 = map.get("isfree4").toString();
                    if (vFree4.equals("Y")) {   //ֻ��Y,����ֵ
                        mEdManual.setEnabled(true);
                    } else {
                        mEdManual.setEnabled(false);
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
                        mEdNum.setText("");
                        mEdEncoding.setText("");
                        mEdName.setText("");
                        mEdType.setText("");
                        mEdUnit.setText("");
                        mEdLot.setText("");
                        mEdQty.setText("");
                        mEdWeight.setText("");
                        mEdSpectype.setText("");
                        mEdCostObject.setText("");
                    }
                    break;
                case R.id.ed_num:
                    if (TextUtils.isEmpty(mEdNum.getText())) {
                        mEdQty.setText("");
                        return;
                    }
                    if (!isNumber(mEdNum.getText().toString())) {
                        showToast(mActivity, "��������ȷ");
                        mEdNum.setText("");
                        return;
                    }
                    if (Float.valueOf(mEdNum.getText().toString()) <= 0) {
                        mEdNum.setText("");
                        showToast(mActivity, "��������ȷ");
                        return;
                    }
                    float num = Float.valueOf(mEdNum.getText().toString());
                    float weight = Float.valueOf(mEdWeight.getText().toString());
                    mEdQty.setText(formatDecimal(num * weight));
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
                        if (TextUtils.isEmpty(mEdBarCode.getText().toString())) {
                            showToast(mActivity, "����������");
                            return true;
                        }
                        barAnalysis();
//                        if (isAllEdNotNull()) {
////                            addDataToDetailList();
//                            mEdBarCode.requestFocus();  //�����ӳɹ����ܱ����������롱��
//                            changeAllEdTextToEmpty();
//                        } else {
//                        }

                        return true;
                    case R.id.ed_lot:
                        if (TextUtils.isEmpty(mEdLot.getText().toString())) {
                            showToast(mActivity, "���������κ�");
                            return true;
                        } else {
                            BATCH = mEdLot.getText().toString();
                            getInvBaseVFree4(BATCH);// ��ȡ�����ֲ��
                            mEdQty.requestFocus();  //���������κ󽲽���������������mEdQty����
                            return true;
                        }
                    case R.id.ed_qty:
                        if (TextUtils.isEmpty(mEdQty.getText().toString())) {
                            showToast(mActivity, "����������");
                            return true;
                        }
                        String qty_s = mEdQty.getText().toString();
                        if (!isNumber(qty_s)) {
                            showToast(mActivity, "��������ȷ");
                            mEdQty.setText("");
                            return true;
                        }
                        float qty_f = Float.valueOf(qty_s);
                        if (qty_f <= 0) {
                            showToast(mActivity, "��������ȷ");
                            mEdQty.setText("");
                            return true;
                        }

                        addDataToDetailList();
                        mEdBarCode.requestFocus();  //�����ӳɹ����ܱ����������롱��
                        changeAllEdTextToEmpty();
                        return true;
                    case ed_num:
                        if (TextUtils.isEmpty(mEdNum.getText().toString())) {
                            showToast(mActivity, "����������");
                            return true;
                        }
                        if (!isNumber(mEdNum.getText().toString())) {
                            showToast(mActivity, "��������ȷ");
                            return true;
                        }
                        //������Ҫ���� �ж��ٰ����������������
                        float num = Float.valueOf(mEdNum.getText().toString());
                        if (num <= 0) {
                            mEdNum.setText("");
                            showToast(mActivity, "��������ȷ");
                            return true;
                        }

                        float weight = Float.valueOf(mEdWeight.getText().toString());
                        mEdQty.setText(String.valueOf(num * weight));
                        addDataToDetailList();
                        mEdBarCode.requestFocus();  //�����ӳɹ����ܱ����������롱��
                        changeAllEdTextToEmpty();
                        return true;
                    case R.id.ed_manual:
                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            mEdBarCode.requestFocus();  //�����ӳɹ����ܱ����������롱��
                            changeAllEdTextToEmpty();
                        }
                        return true;
                    case R.id.ed_cost_object:
                        String invCode = mEdCostObject.getText().toString();
                        getInvCostObj(invCode);
                        return true;
                }
            }
            return false;
        }
    };
}
