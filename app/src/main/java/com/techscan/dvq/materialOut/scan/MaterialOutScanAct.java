package com.techscan.dvq.materialOut.scan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;

import com.techscan.dvq.MainLogin;
import com.techscan.dvq.R;
import com.techscan.dvq.bean.Goods;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.materialOut.MyBaseAdapter;
import com.techscan.dvq.productOut.scan.ProductOutScanAct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.techscan.dvq.R.id.ed_num;


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
    Button mBtnOverview;
    @InjectView(R.id.btn_detail)
    Button mBtnDetail;
    @InjectView(R.id.btn_back)
    Button mBtnBack;
    @InjectView(R.id.ed_num)
    EditText mEdNum;
    @InjectView(R.id.ed_weight)
    EditText mEdWeight;
    @InjectView(R.id.ed_cost_object)
    EditText mEdCostObject;


    String TAG = "MaterialOutScanAct";
    List<Goods> detailList;
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
                addDataToOvList();
                MyBaseAdapter ovAdapter = new MyBaseAdapter(MaterialOutScanAct.this, ovList);
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
        mEdNum.setOnKeyListener(mOnKeyListener);
        mEdNum.addTextChangedListener(new CustomTextWatcher(mEdNum));
        mEdBarCode.addTextChangedListener(new CustomTextWatcher(mEdBarCode));
        detailList = new ArrayList<Goods>();
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
            }
        }
    };

    private void showDialog(final List list, final BaseAdapter adapter, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MaterialOutScanAct.this);
        builder.setTitle(title);
        if (list.size() > 0) {
            View view = LayoutInflater.from(MaterialOutScanAct.this).inflate(R.layout.dialog_scan_details, null);
            ListView lv = (ListView) view.findViewById(R.id.lv);
            if (title.equals("ɨ����ϸ")) { //ֻ����ϸ��ҳ���ǿɵ���ģ�����ҳ���ǲ��ɵ����
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        AlertDialog.Builder delDialog = new AlertDialog.Builder(MaterialOutScanAct.this);
                        delDialog.setTitle("�Ƿ�ɾ����������");
                        delDialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                adapter.notifyDataSetChanged();
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
    private boolean BarAnalysis() {
        String Bar = mEdBarCode.getText().toString().trim();
        if (Bar.contains("\n")) {
            Bar = Bar.replace("\n", "");
        }
        mEdBarCode.setText(Bar);
        mEdBarCode.setSelection(mEdBarCode.length());   //������ƶ�������λ��
        mEdBarCode.selectAll();
        String[] barCode = Bar.split("\\|");
        for (int i = 0; i < barCode.length; i++) {
            if (TextUtils.isEmpty(barCode[i])) {
                return false;
            }
            if (i == 0) {
                continue;
            }
            if (!isNumber(barCode[i])) {
                return false;
            }
        }
        if (barCode.length == 2 && barCode[0].equals("Y")) {          //Y|SKU
            //�����Һ��Ļ���Ҫ����Һ�����������������ò��ɱ༭
            mEdNum.setEnabled(false);
            mEdLot.setEnabled(true);
            mEdQty.setEnabled(true);
            mEdLot.requestFocus();  //�����Һ����Ҫ�ֶ����롰���Ρ��͡�������,���ｫ������������Σ�lot����
            String encoding = barCode[1];
            mEdEncoding.setText(encoding);
            GetInvBaseInfo(encoding);
            mEdLot.setText("");
            mEdQty.setText("");
            return true;
        } else if (barCode.length == 6 && barCode[0].equals("C")) {   //C|SKU|LOT|TAX|QTY|SN
            //����ǰ��룬���κ����ض��ı�Ϊ���ɱ༭��������Ա�����룬����������������
            mEdLot.setEnabled(false);
            mEdQty.setEnabled(false);
            mEdLot.setTextColor(Color.WHITE);
            mEdQty.setTextColor(Color.WHITE);
            mEdNum.setEnabled(true);
            mEdNum.requestFocus();  //����ɨ�����������������,��������,��ӵ��б�
            String encoding = barCode[1];
            mEdEncoding.setText(encoding);
            GetInvBaseInfo(encoding);
            mEdLot.setText(barCode[2]);
            mEdWeight.setText(barCode[4]);
            mEdQty.setText("");
            mEdNum.setText("1");
            mEdNum.selectAll();
            mEdNum.setSelection(mEdNum.length());   //������ƶ�������λ��
            return true;
        } else if (barCode.length == 7 && barCode[0].equals("TC")) {    //TC|SKU|LOT|TAX|QTY|NUM|SN
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(Bar)) {
                    Utils.showToast(MaterialOutScanAct.this, "��������ɨ��");
                    return false;
                }
            }
            //��������룬ȫ������Ϊ���ɱ༭
            mEdLot.setEnabled(false);
            mEdQty.setEnabled(false);
            mEdNum.setEnabled(false);
            mEdLot.setTextColor(Color.WHITE);
            mEdQty.setTextColor(Color.WHITE);
            mEdNum.setTextColor(Color.WHITE);
            String encoding = barCode[1];
            mEdEncoding.setText(encoding);
            GetInvBaseInfo(encoding);
            mEdLot.setText(barCode[2]);
            mEdWeight.setText(barCode[4]);
            mEdNum.setText(barCode[5]);
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
     * �ж��Ƿ������֣�ʹ��������ʽ
     *
     * @param str
     * @return
     */
    public boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
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
                int j = ovList.indexOf(dtGood);
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
                ovList.add(good);
            }
        }
    }

    /**
     * �����Ϣ�� ������
     *
     * @return
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
        goods.setPk_invbasdoc(pk_invbasdoc);
        goods.setPk_invmandoc(pk_invmandoc);
        detailList.add(goods);
    }

    /**
     * ������е�Edtext
     */
    private void ChangeAllEdTextToEmpty() {
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
                && !TextUtils.isEmpty(mEdSpectype.getText())
                && !TextUtils.isEmpty(mEdUnit.getText())
                && !TextUtils.isEmpty(mEdLot.getText())
                && !TextUtils.isEmpty(mEdQty.getText()));
    }

    /**
     * ��ȡ���������Ϣ
     *
     * @param sku ���ϱ���
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
                map.put("invtype", tempJso.getString("invtype"));   //�ͺ�
                map.put("invspec", tempJso.getString("invspec"));   //���
                map.put("oppdimen", tempJso.getString("oppdimen"));   //����
            }
            if (map != null) {
                mEdName.setText(map.get("invname").toString());
                mEdUnit.setText(map.get("measname").toString());
                mEdType.setText(map.get("invtype").toString());
                mEdSpectype.setText(map.get("invspec").toString());
                mEdCostObject.setText(map.get("invname").toString());
            }

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
//                    if (!TextUtils.isEmpty(mEdNum.getText().toString())) {
//                        if (Float.valueOf(mEdNum.getText().toString()) < 0) {
//                            Toast.makeText(MaterialOutScanAct.this, "��������Ϊ0", Toast.LENGTH_SHORT).show();
//                        } else {
//                            float num = Float.valueOf(mEdNum.getText().toString());
//                            float weight = Float.valueOf(mEdWeight.getText().toString());
//                            mEdQty.setText(String.valueOf(num * weight));
//                        }
//                    } else {
//                        mEdQty.setText("0.00");
//                    }
                    if (TextUtils.isEmpty(mEdNum.getText())) {
                        mEdQty.setText("0.00");
                        return;
                    }
                    if (Float.valueOf(mEdNum.getText().toString()) < 0) {
                        Utils.showToast(MaterialOutScanAct.this, "��������Ϊ0");
                        return;
                    }
                    float num = Float.valueOf(mEdNum.getText().toString());
                    float weight = Float.valueOf(mEdWeight.getText().toString());
                    mEdQty.setText(String.valueOf(num * weight));
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
                            Toast.makeText(MaterialOutScanAct.this, "����������", Toast.LENGTH_SHORT).show();
                            return true;
                        } else {
                            if (!BarAnalysis()) {
                                Utils.showToast(MaterialOutScanAct.this, "��������");
                                return true;
                            }
                        }

                        if (isAllEdNotNull()) {
                            addDataToDetailList();
                            mEdBarCode.requestFocus();  //�����ӳɹ����ܱ����������롱��
                            ChangeAllEdTextToEmpty();
                        }
//                        if (!TextUtils.isEmpty(mEdBarCode.getText().toString())) {
//                            if (isAllEdNotNull()) {
//                                addDataToDetailList();
//                                mEdBarCode.requestFocus();  //�����ӳɹ����ܱ����������롱��
//                                ChangeAllEdTextToEmpty();
//                            } else {
//                                if (!BarAnalysis()) {
//                                    Utils.showToast(MaterialOutScanAct.this, "��������");
//                                }
//                            }
//                        } else {
//                            Toast.makeText(MaterialOutScanAct.this, "����������", Toast.LENGTH_SHORT).show();
//                        }

                        return true;
                    case R.id.ed_lot:
                        if (TextUtils.isEmpty(mEdLot.getText().toString())) {
                            Toast.makeText(MaterialOutScanAct.this, "���������κ�", Toast.LENGTH_SHORT).show();
                        } else {
                            mEdQty.requestFocus();  //���������κ󽲽���������������mEdQty����
                        }
                        return true;
                    case R.id.ed_qty:
                        if (TextUtils.isEmpty(mEdQty.getText().toString())) {
                            Toast.makeText(MaterialOutScanAct.this, "����������", Toast.LENGTH_SHORT).show();
                        } else {
                            //ֻ����Һ���ʱ����Ҫ����������������ɽ�������ӵ�list
//                            if (isAllEdNotNull() && ) {
                            addDataToDetailList();
                            mEdBarCode.requestFocus();  //�����ӳɹ����ܱ����������롱��
                            ChangeAllEdTextToEmpty();
//                            }

                        }
                        return true;
                    case ed_num:
//                        if (TextUtils.isEmpty(mEdNum.getText().toString())) {
//                            Toast.makeText(MaterialOutScanAct.this, "����������", Toast.LENGTH_SHORT).show();
//                        } else {
//                            //������Ҫ���� �ж��ٰ����������������
//                            float num = Float.valueOf(mEdNum.getText().toString());
//                            if (num > 0) {
//                                float weight = Float.valueOf(mEdWeight.getText().toString());
//                                mEdQty.setText(String.valueOf(num * weight));
////                            if (isAllEdNotNull() && ) {
//                                addDataToDetailList();
//                                mEdBarCode.requestFocus();  //�����ӳɹ����ܱ����������롱��
//                                ChangeAllEdTextToEmpty();
////                            }
//                            } else {
//                                Toast.makeText(MaterialOutScanAct.this, "��������ȷ", Toast.LENGTH_SHORT).show();
//                            }
//                        }
                        if (TextUtils.isEmpty(mEdNum.getText().toString())) {
                            Utils.showToast(MaterialOutScanAct.this, "����������");
                            return true;
                        }

                        //������Ҫ���� �ж��ٰ����������������
                        float num = Float.valueOf(mEdNum.getText().toString());
                        if (num < 0) {
                            Utils.showToast(MaterialOutScanAct.this, "��������ȷ");
                            return true;
                        }

                        float weight = Float.valueOf(mEdWeight.getText().toString());
                        mEdQty.setText(String.valueOf(num * weight));
//                            if (isAllEdNotNull() && ) {
                        addDataToDetailList();
                        mEdBarCode.requestFocus();  //�����ӳɹ����ܱ����������롱��
                        ChangeAllEdTextToEmpty();
                        return true;
                }
            }
            return false;
        }
    };
}
