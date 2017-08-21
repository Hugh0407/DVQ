package com.techscan.dvq.module.otherOut;

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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.Goods;
import com.techscan.dvq.common.SoundHelper;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.common.Utils;
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
    @InjectView(R.id.ed_num)
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
    @InjectView(R.id.packed)
    TextView packed;
    @InjectView(R.id.switch_m)
    Switch   switchM;
    @InjectView(R.id.ed_pur_lot)
    EditText edPurLot;

    String TAG = this.getClass().getSimpleName();
    public static List<Goods> detailList = new ArrayList<Goods>();
    public static List<Goods> ovList     = new ArrayList<Goods>();
    ArrayList<EditText> edList;


    Activity activity;
    String CWAREHOUSEID = "";
    String PK_CALBODY   = "";
    String vFree4       = "";
    String vFree5       = "";
    SplitBarcode barDecoder;
    String       barQty;
    boolean      isPacked;


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
        edPurLot.setOnKeyListener(mOnKeyListener);
        edCostObject.setOnKeyListener(mOnKeyListener);
        edNum.addTextChangedListener(new CustomTextWatcher(edNum));
        edBarCode.addTextChangedListener(new CustomTextWatcher(edBarCode));
        addEdToList();
        switchM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    packed.setText("����");
                    isPacked = true;
                } else {
                    packed.setText("������");
                    isPacked = false;
                }
                // ÿ�������п��ء� �����ı��ʱ����Ϊ��ȡ�Ľӿڲ�ͬ����Ҫ���µ��ã����Խ������
                changeAllEdTextToEmpty();
            }
        });
    }

    /**
     * ����˳�����е��������뵽������
     */
    private void addEdToList() {
        edList = new ArrayList<EditText>();
        edList.add(edBarCode);
        edList.add(edEncoding);
        edList.add(edName);
        edList.add(edType);
        edList.add(edSpectype);
        edList.add(edLot);
//        edList.add(edCostObject);
        edList.add(edCostName);
        edList.add(edManual);
        edList.add(edNum);
        edList.add(edWeight);
        edList.add(edQty);
        edList.add(edUnit);
    }

    /**
     * �������ϣ����edtext�� enabled ���� Ϊ�գ����edtext��ȡ����
     */
    private void nextUIGetFocus() {
        for (EditText ed : edList) {
            if (ed.isEnabled() && TextUtils.isEmpty(ed.getText().toString())) {
                ed.requestFocus();
                break;
            }
        }
    }

    @OnClick({R.id.btn_overview, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_overview:
                showLv(ovList, "ɨ������");
                break;
            case R.id.btn_detail:
                showLv(detailList, "ɨ����ϸ");
                break;
            case R.id.btn_back:
                if (ovList.size() > 0) {
                    Intent in     = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("overViewList", (ArrayList<? extends Parcelable>) ovList);
                    in.putExtras(bundle);
                    activity.setResult(5, in);
                } else {
                    showToast(activity, "û��ɨ�赥��");
                }
                finish();
                break;
        }
    }

    private void showLv(List<Goods> ovList, String title) {
        MyBaseAdapter ovAdapter = new MyBaseAdapter(ovList);
        showDialog(ovList, ovAdapter, title);
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
                    //��ȡ���������Ϣ
                    setInvBaseToUI((JSONObject) msg.obj);
                    // ��ȡ�����ֲ��
                    getInvBaseVFree4(edLot.getText().toString());
                    break;
                case 2:
                    //���ú����ֲ��
                    setManualToUI((JSONObject) msg.obj);
                    break;
                case 3:
                    //���óɱ�����
                    setInvBaseCostObjToUI((JSONObject) msg.obj);
                    break;
            }
        }
    };

    private void setManualToUI(JSONObject jsonObject) {
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
    }


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

        barDecoder = new SplitBarcode(bar);
        barQty = String.valueOf(barDecoder.dQuantity);

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

            edLot.setEnabled(false);
            edEncoding.setText(barDecoder.cInvCode);
            edLot.setText(barDecoder.cBatch);
            edWeight.setText(barQty);
            edNum.setText("1");
            edQty.setText(barQty);
            edPurLot.setText(barDecoder.purductBatch);
            if (isPacked) {
                edNum.setEnabled(false);
                edQty.setEnabled(true);
                edQty.selectAll();
                edQty.setSelection(edQty.length());   //������ƶ�������λ��
                edQty.requestFocus();
            } else {
                edQty.setEnabled(false);
                edNum.setEnabled(true);
                edNum.selectAll();
                edNum.setSelection(edNum.length());   //������ƶ�������λ��
                edNum.requestFocus();                  //����ɨ�����������������,��������,��ӵ��б�
            }
            getInvBaseInfo(barDecoder.cInvCode, bar);
            return true;
        } else if (barDecoder.BarcodeType.equals("TC")) {
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(bar)) {
                    showToast(activity, "��������ɨ��");
                    SoundHelper.playWarning();
                    return false;
                }
            }
            edLot.setEnabled(false);
            edNum.setEnabled(false);
            edPurLot.setText(barDecoder.purductBatch);
            edEncoding.setText(barDecoder.cInvCode);
            edLot.setText(barDecoder.cBatch);
            //��������,�������У�ֻ��ʾ������δ���е�������ʾ
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edNum.setText(String.valueOf(barDecoder.iNumber));
            double qty = barDecoder.dQuantity;
            double num = barDecoder.iNumber;
            barQty = formatDecimal(qty * num);
            if (isPacked) {
                edQty.setEnabled(true);
            } else {
                edQty.setText(formatDecimal(qty * num));
                edQty.setEnabled(false);
            }
            edCostObject.requestFocus();
            getInvBaseInfo(barDecoder.cInvCode, bar);
            return true;
        } else if (barDecoder.BarcodeType.equals("P")) {
            String invCode = barDecoder.cInvCode;
            if (invCode.contains(",")) {
                invCode = invCode.split(",")[1];
            }
            edEncoding.setText(invCode);
            String batch = barDecoder.cBatch;
            if (batch.contains(",")) {
                batch = batch.split(",")[1];
            }
            edLot.setText(batch);
            edWeight.setText(barQty);
            edQty.setText("");
            edNum.setText("1");
            edPurLot.setText(barDecoder.purductBatch);
            if (isPacked) {
                edNum.setEnabled(false);
                edQty.setEnabled(true);
                edQty.setSelection(edQty.length());   //������ƶ�������λ��
                edQty.requestFocus();  //����ɨ�����������������,��������,��ӵ��б�
            } else {
                edQty.setEnabled(false);
                edNum.setEnabled(true);
                edNum.setSelection(edNum.length());   //������ƶ�������λ��
                edNum.requestFocus();  //����ɨ�����������������,��������,��ӵ��б�
            }
            getInvBaseInfo(invCode, bar);
            return true;
        } else if (barDecoder.BarcodeType.equals("TP")) {
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(bar)) {
                    showToast(activity, "��������ɨ��");
                    SoundHelper.playWarning();
                    return false;
                }
            }
            String encoding = barDecoder.cInvCode;
            if (encoding.contains(",")) {
                encoding = encoding.split(",")[1];
            }
            edEncoding.setText(encoding);
            String batch = barDecoder.cBatch;
            if (batch.contains(",")) {
                batch = batch.split(",")[1];
            }
            edLot.setText(batch);
            edPurLot.setText(barDecoder.purductBatch);
            edWeight.setText(String.valueOf(barDecoder.dQuantity));
            edNum.setText(String.valueOf(barDecoder.iNumber));
            double weight = barDecoder.dQuantity;
            double edNum  = Double.valueOf(barDecoder.iNumber);
            barQty = formatDecimal(weight * edNum);
            if (isPacked) {
                edQty.setEnabled(true);
                edQty.setText(formatDecimal(weight * edNum));
                // TODO: 2017/8/17 ���������⣬ɨ�������Ҫ���õ����
                edQty.requestFocus();
                edQty.selectAll();
                edQty.setSelection(edQty.length());
            } else {
                edQty.setEnabled(false);
                edQty.setText(formatDecimal(weight * edNum));
            }
            getInvBaseInfo(encoding, bar);
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
                good.setProductLot(dtGood.getProductLot());
                ovList.add(good);
            }
        }
    }

    /**
     * �����Ϣ�� ������
     */
    private void addDataToDetailList() {
        Goods goods = new Goods();
        goods.setName(edName.getText().toString());
        goods.setType(edType.getText().toString());
        goods.setSpec(edSpectype.getText().toString());
        goods.setUnit(edUnit.getText().toString());
        goods.setLot(edLot.getText().toString());
        goods.setCostObject(edCostObject.getText().toString());
        goods.setManual(edManual.getText().toString());
        goods.setPk_invbasdoc(pk_invbasdoc);
        goods.setPk_invmandoc(pk_invmandoc);
        goods.setPk_invmandoc_cost(pk_invmandoc_cost);
        goods.setProductLot(edPurLot.getText().toString());
        /*********************************************************************/
        //�����Ҫ������
        goods.setBarcode(edBarCode.getText().toString());
        goods.setEncoding(edEncoding.getText().toString());
        goods.setQty(Float.valueOf(edQty.getText().toString()));
        goods.setCodeType(barDecoder.BarcodeType);
        goods.setBarQty(barQty);
        goods.setDoPacked(isPacked);
        /*********************************************************************/
        detailList.add(goods);
        addDataToOvList();
        SoundHelper.playOK();
    }

    /**
     * ������е�Edtext
     */
    private void changeAllEdTextToEmpty() {
        edBarCode.setText("");  // edBarCode ��һ�������������ｫȫ��ed���
        edBarCode.requestFocus();
        edLot.setEnabled(false);
        edNum.setEnabled(false);
        edQty.setEnabled(false);
    }

    /**
     * �ж����е�edtext�Ƿ�Ϊ��
     *
     * @return true---->���е�ed����Ϊ��,false---->���е�ed��Ϊ��
     */
    private boolean isAllEdNotNull() {

        if (vFree4.equals("Y") && TextUtils.isEmpty(edManual.getText().toString())) {
            showToast(activity, "�����ֲ�Ų���Ϊ��");
            edManual.requestFocus();
            return false;
        }

        if (vFree4.equals("N") && !TextUtils.isEmpty(edManual.getText().toString())) {
            showToast(activity, "������û�к����ֲ�");
            edManual.requestFocus();
            return false;
        }

        if (vFree5.equals("Y") && TextUtils.isEmpty(edPurLot.getText().toString())) {
            showToast(activity, "�������β���Ϊ��");
            edManual.requestFocus();
            return false;
        }

        if (vFree5.equals("N") && !TextUtils.isEmpty(edPurLot.getText().toString())) {
            showToast(activity, "������û����������");
            edManual.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(edBarCode.getText().toString())) {
            showToast(activity, "���벻��Ϊ��");
            return false;
        }

        if (TextUtils.isEmpty(edEncoding.getText().toString())) {
            showToast(activity, "���ϱ��벻��Ϊ��");
            return false;
        }

        if (TextUtils.isEmpty(edName.getText().toString())) {
            showToast(activity, "�������Ʋ���Ϊ��");
            return false;
        }

        if (TextUtils.isEmpty(edType.getText().toString())) {
            showToast(activity, "�����ͺŲ���Ϊ��");
            return false;
        }

        if (TextUtils.isEmpty(edSpectype.getText().toString())) {
            showToast(activity, "���Ϲ�񲻿�Ϊ��");
            return false;
        }

        if (TextUtils.isEmpty(edUnit.getText().toString())) {
            showToast(activity, "��λ����Ϊ��");
            return false;
        }

        if (TextUtils.isEmpty(edLot.getText().toString())) {
            showToast(activity, "���β���Ϊ��");
            return false;
        }

//        if (TextUtils.isEmpty(edCostObject.getText().toString())) {
//            showToast(activity, "�ɱ����󲻿�Ϊ��");
//            return false;
//        }

        if (TextUtils.isEmpty(edQty.getText().toString())) {
            showToast(activity, "��������Ϊ��");
            return false;
        }
        return true;
    }

    private void getInvBaseInfo(String invCode) {
        this.getInvBaseInfo(invCode, "");
    }

    /**
     * ��ȡ���������Ϣ
     *
     * @param invCode ���ϱ���
     */
    private void getInvBaseInfo(String invCode, String barcode) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("InvCode", invCode);
        parameter.put("BarCode", barcode);
        parameter.put("TableName", "baseInfo");
        Utils.doRequest(parameter, mHandler, 1);
    }


    /**
     * ��ȡ���������Ϣ �����ֲ��
     */
    private void getInvBaseVFree4(String batch) {
        if (TextUtils.isEmpty(batch)) {
            return;
        }
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvFreeByInvCodeAndLot");
        parameter.put("CORP", MainLogin.objLog.STOrgCode);
        parameter.put("BATCH", batch);
        parameter.put("WAREHOUSEID", CWAREHOUSEID);
        parameter.put("CALBODYID", PK_CALBODY);
        parameter.put("CINVBASID", pk_invbasdoc);
        parameter.put("INVENTORYID", pk_invmandoc);
        parameter.put("TableName", "vfree4");
        Utils.doRequest(parameter, mHandler, 2);
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
        Utils.doRequest(parameter, mHandler, 3);
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
        try {
            if (json != null && json.getBoolean("Status")) {
                Log.d(TAG, "setInvBaseToUI: " + json);
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
                    map.put("isfree5", tempJso.getString("isfree5"));
                    map.put("currentweight", tempJso.getString("currentweight"));
                }
                if (map != null) {
                    edName.setText(map.get("invname").toString());
                    edUnit.setText(map.get("measname").toString());
                    edType.setText(map.get("invtype").toString());
                    edSpectype.setText(map.get("invspec").toString());

                    //�����ֲ�� �л��޵ı�־λ ����Ϊ Y �� N ����
                    vFree4 = map.get("isfree4").toString();
                    vFree5 = map.get("isfree5").toString();
                    String cw = map.get("currentweight").toString();
                    if (!cw.equals("null")) {
                        edQty.setText(cw);
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
        try {
            if (json != null && json.getBoolean("Status")) {
                Log.d(TAG, "setInvBaseCostObjToUI: " + json);
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
                        edCostName.setText("");
                        edManual.setText("");
                        edPurLot.setText("");
                    }
                    break;
                case R.id.ed_num:
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

                        return true;
                    case R.id.ed_lot:
                        if (TextUtils.isEmpty(edLot.getText().toString())) {
                            showToast(activity, "���������κ�");
                            return true;
                        } else {
                            getInvBaseVFree4(edLot.getText().toString());// ��ȡ�����ֲ��
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

                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            changeAllEdTextToEmpty();
                            return true;
                        }
                        return true;
                    case R.id.ed_num:
                        if (TextUtils.isEmpty(edNum.getText().toString())) {
                            showToast(activity, "����������");
                            return true;
                        }
                        String num_s = edNum.getText().toString();
                        if (!isNumber(num_s)) {
                            showToast(activity, "��������ȷ");
                            return true;
                        }
                        //������Ҫ���� �ж��ٰ����������������
                        float num_f = Float.valueOf(num_s);
                        if (num_f <= 0) {
                            edNum.setText("");
                            showToast(activity, "��������ȷ");
                            return true;
                        }

                        float weight = Float.valueOf(edWeight.getText().toString());
                        edQty.setText(String.valueOf(num_f * weight));

                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            changeAllEdTextToEmpty();
                            return true;
                        }
                        return true;
                    case R.id.ed_manual:

                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            changeAllEdTextToEmpty();
                            nextUIGetFocus();
                        }
                        return true;
                    case R.id.ed_cost_object:
//                        edManual.requestFocus();
                        // �ɱ�����Ϊ�գ�˵������û�У�ֱ����
                        if (TextUtils.isEmpty(edCostObject.getText())) {
                            if (isAllEdNotNull()) {
                                addDataToDetailList();
                                changeAllEdTextToEmpty();
                                return true;
                            }
                        }
                        // �ɱ������У�����Ҳ�� ��˵�����Դ�����ֵ��ֱ����
                        if (!TextUtils.isEmpty(edCostName.getText()) && !TextUtils.isEmpty(edCostObject.getText())) {
                            if (isAllEdNotNull()) {
                                addDataToDetailList();
                                changeAllEdTextToEmpty();
                                return true;
                            }
                        }
                        String invCode = edCostObject.getText().toString();
                        getInvCostObj(invCode);
                        return true;
                    case R.id.ed_pur_lot:
                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            changeAllEdTextToEmpty();
                            return true;
                        }
                        return true;
                }
            }
            return false;
        }
    };
}
