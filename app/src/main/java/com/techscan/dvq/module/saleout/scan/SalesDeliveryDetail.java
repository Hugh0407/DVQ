package com.techscan.dvq.module.saleout.scan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.login.MainLogin;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;

public class SalesDeliveryDetail extends Activity {

    String CALBODYID   = "";
    String CINVBASID   = "";
    String INVENTORYID = "";
    @Nullable
    String CORP = MainLogin.objLog.STOrgCode;
    String WAREHOUSEID = "";
    String ScanType    = "";
    String BillCode    = "";
    String CSALEID     = "";
    String PK_CORP     = "";
    String cw = "";
    Double ldTotal = 0.0;
    @Nullable
    JSONObject jsBody;
    JSONObject jsBoxTotal;
    JSONObject jsSerino;
    JSONObject jsTotal;
    @NonNull
    String weight = "";
    @NonNull
    String num    = "";
    Double number;
    Double ntotaloutinvnum;
    @Nullable
    @InjectView(R.id.TextView31)
    TextView TextView31;
    @Nullable
    @InjectView(R.id.txtBarcode)
    EditText txtBarcode;
    @Nullable
    @InjectView(R.id.TextView33)
    TextView TextView33;
    @Nullable
    @InjectView(R.id.txtSaleInvCode)
    EditText txtSaleInvCode;
    @Nullable
    @InjectView(R.id.txtSaleInvName)
    EditText txtSaleInvName;
    @Nullable
    @InjectView(R.id.txtSaleType)
    EditText txtSaleType;
    @Nullable
    @InjectView(R.id.txtSaleSpec)
    EditText txtSaleSpec;
    @Nullable
    @InjectView(R.id.txtSaleBatch)
    EditText txtSaleBatch;
    @Nullable
    @InjectView(R.id.txtSaleNumber)
    EditText txtSaleNumber;
    @Nullable
    @InjectView(R.id.txtSaleWeight)
    EditText txtSaleWeight;
    @Nullable
    @InjectView(R.id.txtSaleTotal)
    EditText txtSaleTotal;
    @Nullable
    @InjectView(R.id.txtSaleUnit)
    EditText txtSaleUnit;
    @Nullable
    @InjectView(R.id.tvSalecount)
    TextView tvSalecount;
    @Nullable
    @InjectView(R.id.btnTask)
    Button   btnTask;
    @Nullable
    @InjectView(R.id.btnDetail)
    Button   btnDetail;
    @Nullable
    @InjectView(R.id.btnReturn)
    Button   btnReturn;
    @Nullable
    @InjectView(R.id.txtSaleCustoms)
    EditText txtSaleCustoms;
    @InjectView(R.id.packed)
    TextView packed;
    @InjectView(R.id.switch_m)
    Switch   switch_m;
    boolean isPacked = false;
    @Nullable
    private GetSaleBaseInfo         objSaleBaseInfo   = null;
    @Nullable
    private HashMap<String, Object> m_mapSaleBaseInfo = null;
    @Nullable
    private SplitBarcode            m_cSplitBarcode   = null;
    private ArrayList<String>       ScanedBarcode     = new ArrayList<String>();
    @Nullable
    List<Map<String, Object>> listTaskBody = null;
    @Nullable
    List<Map<String, Object>> lstTaskBody = null;
    List<Map<String, Object>> lstDetailBody =null;
    @Nullable
    private AlertDialog   DeleteButton     = null;
    @Nullable
    private AlertDialog   SelectButton     = null;
    @NonNull
    private ButtonOnClick buttonDelOnClick = new ButtonOnClick(0);
    @Nullable
    SimpleAdapter listItemAdapter = null;
    @Nullable
    SimpleAdapter listTaskAdapter = null;
    @Nullable
    String []invcode =null;
    Double TOTAL = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_out_scan_detail);
        ButterKnife.inject(this);
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("���۳���ɨ����ϸ");
        initView();
        Intent intent = this.getIntent();
//        BillCode = intent.getStringExtra("BillCode");
//        PK_CORP = intent.getStringExtra("PK_CORP");
//        CSALEID = intent.getStringExtra("CSALEID");
//        ScanType = intent.getStringExtra("ScanType");
        try {
            ScanedBarcode = intent.getStringArrayListExtra("ScanedBarcode");
            BillCode = intent.getStringExtra("BillCode");
            PK_CORP = intent.getStringExtra("PK_CORP");
            CSALEID = intent.getStringExtra("CSALEID");
            ScanType = intent.getStringExtra("ScanType");
            WAREHOUSEID = intent.getStringExtra("CWAREHOUSEID");

            String temp = "";
            temp = intent.getStringExtra("jsbody");
            jsBody = new JSONObject(temp);
            Log.d(TAG, "onCreate: " + jsBody.toString());
            temp = intent.getStringExtra("jsserino");
            jsSerino = new JSONObject(temp);
            Log.d(TAG, "onCreate: " + jsSerino.toString());

        } catch (Exception e) {

        }
        try {
            if (jsBody == null) {
                LoadSaleOutBody();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(SalesDeliveryDetail.this, e.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            e.printStackTrace();
        }
        JSONArray arrays;
        try {
            number = 0.0;
            ntotaloutinvnum = 0.0;
            if (jsBody == null || !jsBody.has("dbBody")) {
                Common.ReScanErr = true;
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                scanErr();
                return;
            }
            arrays = jsBody.getJSONArray("dbBody");
            for (int i = 0; i < arrays.length(); i++) {
                String totalNumber = ((JSONObject) (arrays.get(i)))
                        .getString("doneqty");
                String ntotalnum = ((JSONObject) (arrays.get(i)))
                        .getString("ntotaloutinvnum");
                number = number + Double.valueOf(totalNumber);
                if (!ntotalnum.toLowerCase().equals("null") && !ntotalnum.isEmpty())
                    ntotaloutinvnum = ntotaloutinvnum + Double.valueOf(ntotalnum);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        }

        tvSalecount.setText("����" + number + " | " + "��ɨ" + ntotaloutinvnum
                + " | " + "δɨ" + (number - ntotaloutinvnum));

    }

    @NonNull
    private View.OnKeyListener myTxtListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(@NonNull View v, int keyCode, @NonNull KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.txtBarcode:
                        analyzeBarCode(txtBarcode.getText().toString());
                        txtBarcode.requestFocus();
                        txtBarcode.setText("");
                        return true;
                    case R.id.txtSaleNumber:
                        if (TextUtils.isEmpty(txtSaleNumber.getText())) {
                            Utils.showToast(SalesDeliveryDetail.this, "��������Ϊ��");
//                            txtSaleNumber.requestFocus();
                            return true;
                        }
                        if (!isNumber(txtSaleNumber.getText().toString())) {
                            Utils.showToast(SalesDeliveryDetail.this, "��������ȷ");
                            txtSaleNumber.setText("");
//                            txtSaleNumber.requestFocus();
                            return true;
                        }
                        if (Float.valueOf(txtSaleNumber.getText().toString()) <= 0) {
                            Utils.showToast(SalesDeliveryDetail.this, "��������ȷ");
//                            txtSaleNumber.requestFocus();
                            return true;
                        }
                        m_mapSaleBaseInfo.put("number", Integer.valueOf(txtSaleNumber.getText().toString()));

                        addDataToDetail();
                        IniDetail();
                        txtBarcode.setText("");
                        txtBarcode.requestFocus();
                        return true;
                    case R.id.txtSaleTotal:
                        Double S = 0.0;
                        if (isPacked&&!cw.equals("null")){
                            S = Double.valueOf(cw);
                            if (!isNumber(txtSaleTotal.getText().toString())) {
                                Utils.showToast(SalesDeliveryDetail.this, "��������ȷ");
                                txtSaleTotal.setText("");
//                            txtSaleNumber.requestFocus();
                                return true;
                            }
                            if (TextUtils.isEmpty(txtSaleTotal.getText())) {
                                Utils.showToast(SalesDeliveryDetail.this, "��������Ϊ��");
//                            txtSaleNumber.requestFocus();
                                return true;
                            }
                            if (Double.valueOf(txtSaleTotal.getText().toString())>S){
                                Utils.showToast(SalesDeliveryDetail.this, "�����ѳ���");
                                txtSaleTotal.requestFocus();
                                return true;
                            }
                        }else{
                            if (!isNumber(txtSaleTotal.getText().toString())) {
                                Utils.showToast(SalesDeliveryDetail.this, "��������ȷ");
                                txtSaleTotal.setText("");
//                            txtSaleNumber.requestFocus();
                                return true;
                            }

                            if (TextUtils.isEmpty(txtSaleTotal.getText())) {
                                Utils.showToast(SalesDeliveryDetail.this, "��������Ϊ��");
//                            txtSaleNumber.requestFocus();
                                return true;
                            }
                            if (Double.valueOf(txtSaleTotal.getText().toString())>ldTotal){
                                Utils.showToast(SalesDeliveryDetail.this, "�����ѳ���");
                                txtSaleTotal.requestFocus();
                                return true;
                            }
                        }
                        if (TextUtils.isEmpty(txtSaleTotal.getText())) {
                            Utils.showToast(SalesDeliveryDetail.this, "��������Ϊ��");
//                            txtSaleNumber.requestFocus();
                            return true;
                        }
                        if (!isNumber(txtSaleTotal.getText().toString())) {
                            Utils.showToast(SalesDeliveryDetail.this, "��������ȷ");
                            txtSaleTotal.setText("");
//                            txtSaleNumber.requestFocus();
                            return true;
                        }
                        if (Float.valueOf(txtSaleTotal.getText().toString()) <= 0) {
                            Utils.showToast(SalesDeliveryDetail.this, "��������ȷ");
//                            txtSaleNumber.requestFocus();
                            return true;
                        }
                        m_mapSaleBaseInfo.put("total", Double.valueOf(txtSaleTotal.getText().toString()));

                        addDataToDetail();
                        IniDetail();
                        txtBarcode.setText("");
                        txtBarcode.requestFocus();
                        return true;
                }
            }

            return false;
        }

    };

    /**
     * TextWatcher
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
//                case R.id.txtBarcode:
//                    if (TextUtils.isEmpty(txtBarcode.getText().toString())) {
////                        txtSaleNumber.setText("");
////                        txtSaleWeight.setText("");
////                        txtSaleInvCode.setText("");
////                        txtSaleInvName.setText("");
////                        txtSaleTotal.setText("");
////                        txtSaleType.setText("");
////                        txtSaleUnit.setText("");
////                        txtSaleSpec.setText("");
////                        txtSaleBatch.setText("");
//
//                    }
//                    break;
                case R.id.txtSaleNumber:
                    if (TextUtils.isEmpty(txtSaleNumber.getText())) {
                        txtSaleTotal.setText("");
                        return;
                    }
                    if (!isNumber(txtSaleNumber.getText().toString())) {
                        Utils.showToast(SalesDeliveryDetail.this, "��������ȷ");
                        txtSaleNumber.requestFocus();
                        return;
                    }
                    if (Float.valueOf(txtSaleNumber.getText().toString()) < 0) {
                        Utils.showToast(SalesDeliveryDetail.this, "��������ȷ");
                        txtSaleNumber.requestFocus();
                        return;
                    }

                    num = txtSaleNumber.getText().toString();

                    if (TextUtils.isEmpty(num)) {
                        num = "0";
                    }
                    weight = txtSaleWeight.getText().toString();
                    float a = Float.valueOf(num);
                    float b = Float.valueOf(weight);
                    Log.d(TAG, "afterTextChanged: " + "");
                    txtSaleTotal.setText(String.valueOf(a * b));
                    m_mapSaleBaseInfo.put("number", Integer.valueOf(txtSaleNumber.getText().toString()));
                    break;
            }
        }
    }

    /**
     * �ж��Ƿ������֣�ʹ��������ʽ
     *
     * @param str
     * @return
     */
    public boolean isNumber(@NonNull String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum   = pattern.matcher(str);
        return isNum.matches();
    }


    private boolean analyzeBarCode(@Nullable String Scanbarcode) {
        if (Scanbarcode == null || Scanbarcode.equals(""))
            return false;


        SplitBarcode bar = new SplitBarcode(Scanbarcode);

        if (bar.creatorOk == false) {
            Toast.makeText(this, "ɨ��Ĳ�����ȷ��Ʒ����", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return false;
        }
        m_cSplitBarcode = bar;

        //�ж����������Ƿ���ȷ
        if (bar.BarcodeType.equals("P") || bar.BarcodeType.equals("TP")) {
//            txtSaleCustoms.setEnabled(true);
//            txtSaleCustoms.requestFocus();
            String FinishBarCode = bar.FinishBarCode;
            if (bar.BarcodeType.equals("TP")) {
                if (ScanedBarcode != null || ScanedBarcode.size() > 0) {
                    for (int si = 0; si < ScanedBarcode.size(); si++) {
                        String BarCode = ScanedBarcode.get(si).toString();
                        if (BarCode.equals(FinishBarCode)) {
                            Toast.makeText(this, "�������Ѿ���ɨ�����,�����ٴ�ɨ��", Toast.LENGTH_SHORT)
                                    .show();
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            return false;
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, "ɨ����������Ͳ�ƥ��", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return false;
        }


        IniDetail();
        try {
            if (isPacked==false) {
                objSaleBaseInfo = new GetSaleBaseInfo(m_cSplitBarcode, mHandler, PK_CORP);
            }else{
                objSaleBaseInfo = new GetSaleBaseInfo(m_cSplitBarcode, mHandler, PK_CORP,bar.FinishBarCode);
            }
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        return true;
    }

    /**
     * �����������߳�ͨ��
     * msg.obj �Ǵ����̴߳��ݹ���������
     */
    @NonNull
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    JSONObject json = (JSONObject) msg.obj;
                    if (json != null) {
                        try {
                            Log.d(TAG, "handleMessage1: " + json.toString());
                            objSaleBaseInfo.SetSaleBaseToParam(json);
                            m_mapSaleBaseInfo = objSaleBaseInfo.mapSaleBaseInfo;
                            getInvBaseVFree4();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("TAG", "handleMessage2: " + "NULL");
                        return;
                    }
                    break;
                case 3:
                    JSONObject jSon = (JSONObject) msg.obj;
                    if (jSon != null) {
                        try {
                            Log.d(TAG, "handleMessage1: " + jSon.toString());
                            objSaleBaseInfo.SetSaleBaseToParam(jSon);
                            m_mapSaleBaseInfo = objSaleBaseInfo.mapSaleBaseInfo;
                            getInvBaseVFree4();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("TAG", "handleMessage2: " + "NULL");
                        return;
                    }
                    break;
                case 2:
                    JSONObject jsons = (JSONObject) msg.obj;
                    Log.d(TAG, "vfree4: " + jsons.toString());
                    try {
                        if (jsons.getBoolean("Status")) {
                            JSONArray jsonArray = jsons.getJSONArray("customs");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject tempJso = jsonArray.getJSONObject(i);
                                Log.d(TAG, "vfree4: " + tempJso.getString("vfree4"));
                                if (tempJso.getString("vfree4").equals("null")) {
                                    txtSaleCustoms.setText("");
                                } else {
                                    txtSaleCustoms.setText(tempJso.getString("vfree4"));
                                }
                                m_mapSaleBaseInfo.put("vfree4", txtSaleCustoms.getText().toString());
                                SetInvBaseToUI();
                            }
                        } else {
                            m_mapSaleBaseInfo.put("vfree4", txtSaleCustoms.getText().toString());
                            SetInvBaseToUI();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    //��ȡ�õ����ݼ��ص�ҳ��ؼ���
    private void SetInvBaseToUI() {
//        Double ldTotal = 0.0;
        txtSaleInvCode.setText(m_mapSaleBaseInfo.get("invcode").toString());
        txtSaleInvName.setText(m_mapSaleBaseInfo.get("invname").toString());
        txtSaleType.setText(m_mapSaleBaseInfo.get("invtype").toString());
        txtSaleSpec.setText(m_mapSaleBaseInfo.get("invspec").toString());
        txtSaleBatch.setText(m_mapSaleBaseInfo.get("batch").toString());
        txtSaleUnit.setText(m_mapSaleBaseInfo.get("measname").toString());
        txtBarcode.setText(m_mapSaleBaseInfo.get("barcode").toString());
        txtSaleWeight.setText(m_mapSaleBaseInfo.get("quantity").toString());
        txtSaleNumber.setText(m_mapSaleBaseInfo.get("number").toString());
         cw = m_mapSaleBaseInfo.get("currentweight").toString();
        if (isPacked && !cw.equals("null")) {
            txtSaleTotal.setText(cw);
        }else{
            ldTotal = (Double) m_mapSaleBaseInfo.get("quantity") * (Integer) m_mapSaleBaseInfo.get("number");
            txtSaleTotal.setText(ldTotal.toString());
        }
        if (m_mapSaleBaseInfo.get("barcodetype").toString().equals("TP")) {
            if (isPacked==false) {
                m_mapSaleBaseInfo.put("total", ldTotal);
                txtSaleBatch.setFocusableInTouchMode(false);
                txtSaleBatch.setFocusable(false);
                txtSaleNumber.setFocusableInTouchMode(false);
                txtSaleNumber.setFocusable(false);
                txtSaleTotal.setFocusableInTouchMode(false);
                txtSaleTotal.setFocusable(false);
                addDataToDetail();
            }else{
                txtSaleTotal.setEnabled(true);
                txtSaleTotal.requestFocus();
                txtSaleTotal.setFocusableInTouchMode(true);
                txtSaleTotal.setFocusable(true);
            }
        } else if (m_mapSaleBaseInfo.get("barcodetype").toString().equals("P")) {
            if (isPacked==false) {
                m_mapSaleBaseInfo.put("total", ldTotal);
                txtSaleBatch.setFocusableInTouchMode(false);
                txtSaleBatch.setFocusable(false);
                txtSaleNumber.setFocusableInTouchMode(true);
                txtSaleNumber.setFocusable(true);
                txtSaleNumber.setEnabled(true);
                txtSaleTotal.setFocusableInTouchMode(false);
                txtSaleTotal.setFocusable(false);
                txtSaleNumber.requestFocus();
                txtSaleNumber.selectAll();
            }else{
//                txtSaleBatch.setFocusableInTouchMode(false);
//                txtSaleBatch.setFocusable(false);
//                txtSaleNumber.setFocusableInTouchMode(false);
//                txtSaleNumber.setFocusable(false);
//                txtSaleNumber.setEnabled(false);
//                txtSaleTotal.setFocusableInTouchMode(true);
//                txtSaleTotal.setFocusable(true);
//                txtSaleTotal.requestFocus();
//                txtSaleTotal.selectAll();

                txtSaleTotal.setEnabled(true);
                txtSaleTotal.requestFocus();
                txtSaleTotal.setFocusableInTouchMode(true);
                txtSaleTotal.setFocusable(true);
            }
        }
    }

    private boolean addDataToDetail() {
        SplitBarcode bar = m_cSplitBarcode;
        Double S = 0.0;
        try {
            JSONArray bodys = jsBody.getJSONArray("dbBody");
            Log.d("TAG", "dbBody: " + bodys);
            boolean isFind = false;
            for (int i=0; i < bodys.length(); i++) {
                JSONObject temp = bodys.getJSONObject(i);
                Log.d(TAG, "InvCode: "+temp.getString("invcode"));
                        if (temp.getString("invcode").equals(m_mapSaleBaseInfo.get("invcode").toString())) {
                            isFind = true;
                            String empty = "";
//                            String rowno =temp.getString("rowno");
//                            if (){
//
//                            }
                            Double doneqty = 0.0;
                            if (!temp.getString("ntotaloutinvnum").isEmpty() && !temp.getString("ntotaloutinvnum").toLowerCase().equals("null")) {
                                String rowno =temp.getString("rowno");
                                doneqty = temp.getDouble("ntotaloutinvnum");
                                doneqty = doneqty + Double.parseDouble(txtSaleTotal.getText().toString());
                                Log.d(TAG, "ScanedToGet: " + doneqty.toString());
                                if (doneqty > temp.getInt("doneqty")) {
                                    Toast.makeText(this, "�������Ѿ�����Ӧ��������,���ʳ���!",
                                            Toast.LENGTH_LONG).show();
                                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                                    IniDetail();
                                    txtBarcode.setText("");
                                    txtBarcode.requestFocus();
                                    return false;
                                }

                            }
                            if (saveScanDetail(bar.FinishBarCode, empty, txtSaleTotal.getText().toString()) == false) {
                                txtBarcode.setText("");
                                txtBarcode.requestFocus();
                                return false;
                            }
                            ScanedBarcode.add(bar.FinishBarCode);
                            MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
                            temp.put("ntotaloutinvnum", doneqty);
                            break;
                }
            }

            if (isFind == false) {
                IniDetail();
                Toast.makeText(this, "���������ڱ���ɨ��������", Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return false;
            }


        } catch (Exception ex) {
            Toast.makeText(this, "�����޷���ӵ���ϸ", Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        JSONArray arrays;
        try {
            arrays = jsBody.getJSONArray("dbBody");
            number = 0.0;
            ntotaloutinvnum = 0.0;
            for (int i = 0; i < arrays.length(); i++) {
                String sshouldinnum = ((JSONObject) (arrays.get(i)))
                        .getString("doneqty");
                String sinnum = ((JSONObject) (arrays.get(i)))
                        .getString("ntotaloutinvnum");
                number = number + Double.valueOf(sshouldinnum);
                if (!sinnum.toLowerCase().equals("null") && !sinnum.isEmpty())
                    ntotaloutinvnum = ntotaloutinvnum + Double.valueOf(sinnum);
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();

            Toast.makeText(this, "�޷���ȡ������Ϣ", Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        }
        tvSalecount.setText("����" + number + " | " + "��ɨ" + ntotaloutinvnum
                + " | " + "δɨ" + (number - ntotaloutinvnum));

        txtBarcode.requestFocus();
        txtBarcode.setText("");
        txtBarcode.setSelectAllOnFocus(true);

        return true;
    }

    //����ɨ����ϸ
    private boolean saveScanDetail(String serino, String Free1, String TotalBox)
            throws JSONException {
        if (jsSerino == null) {
            jsSerino = new JSONObject();
        }
        if (!jsSerino.has("Serino")) {
            JSONArray  serinos = new JSONArray();
            JSONObject temp    = new JSONObject();
            jsSerino.put("Serino", serinos);
            temp.put("serino", serino);
            temp.put("box", TotalBox);
            temp.put("invcode", m_mapSaleBaseInfo.get("invcode").toString());
            temp.put("invname", m_mapSaleBaseInfo.get("invname").toString());
            temp.put("batch", m_mapSaleBaseInfo.get("batch").toString());
            temp.put("sno", m_mapSaleBaseInfo.get("serino").toString());
            temp.put("invtype", m_mapSaleBaseInfo.get("invtype").toString());
            temp.put("invspec", m_mapSaleBaseInfo.get("invspec").toString());
            temp.put("vfree4", m_mapSaleBaseInfo.get("vfree4").toString());
//            ***************���
            temp.put("barcodetype", m_mapSaleBaseInfo.get("barcodetype").toString());
            temp.put("barcode", m_mapSaleBaseInfo.get("barcode").toString());
            temp.put("barqty", m_mapSaleBaseInfo.get("barqty").toString());
            temp.put("opqty", TotalBox);
            temp.put("isDoPacked", isPacked);
            serinos.put(temp);


        } else {
            JSONArray serinos = jsSerino.getJSONArray("Serino");

            for (int i = 0; i < serinos.length(); i++) {
                JSONObject temp = new JSONObject();
                temp = serinos.getJSONObject(i);
                if (temp.getString("serino").equals(serino)&&(temp.getBoolean("isDoPacked")==false)) {
                    TotalBox = String.valueOf(Double.parseDouble(temp.getString("box").toString())
                            + Double.parseDouble(TotalBox));
                    temp.put("box", TotalBox);
                    return true;
                }
                if (temp.getString("serino").equals(serino)&&(temp.getBoolean("isDoPacked")==true)) {
                    TotalBox = String.valueOf(Double.parseDouble(temp.getString("box").toString())
                            + Double.parseDouble(TotalBox));
                    temp.put("box", TotalBox);
                    return true;
                }
            }
            JSONObject temp = new JSONObject();
            temp.put("serino", serino);
            temp.put("box", TotalBox);
            temp.put("invcode", m_mapSaleBaseInfo.get("invcode").toString());
            temp.put("invname", m_mapSaleBaseInfo.get("invname").toString());
            temp.put("batch", m_mapSaleBaseInfo.get("batch").toString());
            temp.put("sno", m_mapSaleBaseInfo.get("serino").toString());
            temp.put("invtype", m_mapSaleBaseInfo.get("invtype").toString());
            temp.put("invspec", m_mapSaleBaseInfo.get("invspec").toString());
            temp.put("vfree4", m_mapSaleBaseInfo.get("vfree4").toString());
            //            ***************chaib
            temp.put("barcodetype", m_mapSaleBaseInfo.get("barcodetype").toString());
            temp.put("barcode", m_mapSaleBaseInfo.get("barcode").toString());
            temp.put("barqty", m_mapSaleBaseInfo.get("barqty").toString());
            temp.put("opqty", TotalBox);
            temp.put("isDoPacked", isPacked);
            serinos.put(temp);
        }
        Log.d(TAG, "ScanSerial: " + jsSerino.toString());
        return true;
    }

    /**
     * ��ȡ���������Ϣ �����ֲ��CORP,WAREHOUSEID,CALBODYID,CINVBASID,INVENTORYID
     */
    private void getInvBaseVFree4() {
        HashMap<String, String> para = new HashMap<String, String>();
        para.put("FunctionName", "GetInvFreeByInvCodeAndLot");
        para.put("CORP", CORP);
        para.put("BATCH", m_cSplitBarcode.cBatch);
        para.put("WAREHOUSEID", WAREHOUSEID);
        para.put("CALBODYID", CALBODYID);
        para.put("CINVBASID", CINVBASID);
        para.put("INVENTORYID", INVENTORYID);
        para.put("TableName", "customs");
        RequestThread rstThread = new RequestThread(para, mHandler, 2);
        Thread        tds       = new Thread(rstThread);
        tds.start();
    }


    private void LoadSaleOutBody() throws ParseException, IOException {
        if (BillCode == null || BillCode.equals("") || CSALEID == null || CSALEID.equals("")) {
            Toast.makeText(this, "����ȷ����Ҫɨ��Ķ�����", Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        JSONObject para         = new JSONObject();
        String     FunctionName = "";
        FunctionName = "CommonQuery";
        if (ScanType.equals("���۳���")) {
            try {
                para.put("FunctionName", "GetSaleOutBodyNew");
                para.put("BillCode", BillCode);
                para.put("CSALEID", CSALEID);
                para.put("CorpPK", "4100");
                para.put("TableName", "dbBody");
                Log.d(TAG, "GetBillBodyDetailInfo: " + BillCode);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(SalesDeliveryDetail.this, "�޷���ȡ������Ϣ",
                        Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
            }
            try {
                if (!MainLogin.getwifiinfo()) {
                    Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
                            .show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }
                jsBody = Common.DoHttpQuery(para, FunctionName, "");
                Log.d(TAG, "GetBillBodyDetailInfo: " + jsBody.toString());
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }

            try {
                if (jsBody == null) {
                    Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    return;
                }

                if (!jsBody.has("Status")) {
                    Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }
                if (!jsBody.getBoolean("Status")) {
                    String errMsg = "";
                    if (jsBody.has("ErrMsg")) {
                        errMsg = jsBody.getString("ErrMsg");
                    } else {
                        errMsg = getString(R.string.WangLuoChuXianWenTi);
                    }
                    Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    return;
                }

                JSONArray jsarray = jsBody.getJSONArray("dbBody");
//                invcode = new String[jsarray.length()];
                for (int i = 0; i < jsarray.length(); i++) {
                    JSONObject tempJso = jsarray.getJSONObject(i);
//                    invcode[i] =tempJso.getString("invcode");
//                    Log.d(TAG, "LoadSaleOutBody: "+invcode[i].toString());
                    CALBODYID = tempJso.getString("cadvisecalbodyid");
                    CINVBASID = tempJso.getString("cinvbasdocid");
                    INVENTORYID = tempJso.getString("cinventoryid");
                }
                listTaskBody = new ArrayList<Map<String, Object>>();
                // purBody
                Map<String, Object> map;

                if (jsBody == null) {
                    Toast.makeText(this, R.string.MeiYouDeDaoBiaoTiShuJu, Toast.LENGTH_LONG).show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return;
                }
//                JSONArray arrays = jsBody.getJSONArray("dbBody");
//                Double total = 0.0;
//                for (int i = 0; i < arrays.length(); i++) {
//                    map = new HashMap<String, Object>();
//                    map.put("invname",
//                            ((JSONObject) (arrays.get(i))).getString("invname"));
//                    map.put("invcode",
//                            ((JSONObject) (arrays.get(i))).getString("invcode"));
//                    map.put("invspec",
//                            ((JSONObject) (arrays.get(i))).getString("invspec"));
//                    map.put("ntotaloutinvnum",
//                            ((JSONObject) (arrays.get(i))).getString("ntotaloutinvnum"));
//                    map.put("invtype",
//                            ((JSONObject) (arrays.get(i))).getString("invtype"));
//                    map.put("doneqty",
//                            ((JSONObject) (arrays.get(i))).getString("doneqty"));
//                    lstTaskBody.add(map);
//                    total = ((JSONObject) (arrays.get(i))).getDouble("doneqty");
//                    TOTAL = TOTAL+total;
//                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }


        } else {
            return;
        }


    }

    private void scanErr() {
        AlertDialog.Builder bulider =
                new AlertDialog.Builder(this).setTitle(R.string.CuoWu).setMessage("���ݼ��س��ִ���" + "\r\n" + "�˳���ģ�鲢���ٴγ��Լ��ػ���");

        bulider.setPositiveButton(R.string.QueRen, listenExit).setCancelable(false).create().show();
        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        return;
    }


    private DialogInterface.OnClickListener listenExit = new
            DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int whichButton) {
                    finish();
                    System.gc();
                }
            };

    //ע�����
    private void initView() {
        txtBarcode.setOnKeyListener(myTxtListener);
        txtSaleNumber.setOnKeyListener(myTxtListener);
        txtSaleCustoms.setOnKeyListener(myTxtListener);
        txtSaleTotal.setOnKeyListener(myTxtListener);
        txtBarcode.addTextChangedListener(new CustomTextWatcher(txtBarcode));
        txtSaleNumber.addTextChangedListener(new CustomTextWatcher(txtSaleNumber));
        txtSaleTotal.addTextChangedListener(new CustomTextWatcher(txtSaleTotal));
//        this.txtBarcode.addTextChangedListener(watchers);
        switch_m.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    packed.setText("����");
                    txtBarcode.requestFocus();
                    isPacked = true;
                } else {
                    packed.setText("������");
                    isPacked = false;
                }
                IniDetail();
            }

        });
    }

    private void IniDetail() {
//        currentObj = null;
        txtSaleInvName.setText("");
        txtSaleInvCode.setText("");
        txtSaleBatch.setText("");
        txtSaleType.setText("");
        txtSaleSpec.setText("");
        txtSaleTotal.setText("");
        txtSaleUnit.setText("");
        txtSaleNumber.setText("");
        txtBarcode.setText("");
        txtBarcode.setFocusable(true);
        txtSaleWeight.setText("");
        txtSaleCustoms.setText("");
        txtSaleCustoms.setEnabled(false);
        txtSaleNumber.setEnabled(false);
        txtSaleTotal.setEnabled(false);
        txtSaleWeight.setEnabled(false);

    }

    @OnClick({R.id.btnTask, R.id.btnDetail, R.id.btnReturn})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btnTask:
                try {
                    ShowTaskDig();
                } catch (JSONException e) {
                    e.printStackTrace();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                }
                break;
            case R.id.btnDetail:
                try {
                    ShowDetailDig();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnReturn:
                if (jsSerino != null) {
                    try {
                        Intent intent = new Intent();
                        intent.putExtra("body", jsBody.toString());
                        Log.d("TAG", "ReturnScanedbody: " + jsBody);
                        intent.putExtra("serino", jsSerino.toString());
                        Log.d(TAG, "Return: " + jsSerino.toString());
                        intent.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
                        SalesDeliveryDetail.this.setResult(24, intent);
//                        SalesDeliveryDetail.this.finish();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
//                    Toast.makeText(SalesDeliveryDetail.this, "û��ɨ�赽����", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
        }
    }

    private void ShowTaskDig() throws JSONException {
        lstTaskBody = new ArrayList<Map<String, Object>>();
        // purBody
        Map<String, Object> map;

        if (jsBody == null) {
            Toast.makeText(this, R.string.MeiYouDeDaoBiaoTiShuJu, Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }
        JSONArray arrays = jsBody.getJSONArray("dbBody");

        for (int i = 0; i < arrays.length(); i++) {
            map = new HashMap<String, Object>();
            map.put("InvName",
                    ((JSONObject) (arrays.get(i))).getString("invname"));
            map.put("InvCode",
                    ((JSONObject) (arrays.get(i))).getString("invcode"));
            map.put("Invspec",
                    ((JSONObject) (arrays.get(i))).getString("invspec"));
            map.put("Invtype",
                    ((JSONObject) (arrays.get(i))).getString("invtype"));
            String sinnum = ((JSONObject) (arrays.get(i))).getString("ntotaloutinvnum");
            if (sinnum.toLowerCase().equals("null") || sinnum.isEmpty())
                sinnum = "0.0";
            map.put("InvNum",
                    sinnum + " / " + Double.valueOf(((JSONObject) (arrays.get(i))).getString("doneqty")));
            lstTaskBody.add(map);
        }

        listTaskAdapter = new SimpleAdapter(
                SalesDeliveryDetail.this, lstTaskBody,
                R.layout.sale_out_task_detail,
                new String[]{"InvName", "InvNum", "InvCode", "Invspec", "Invtype"},
                new int[]{R.id.txtTranstaskInvName, R.id.txtTranstaskInvNum,
                        R.id.txtTranstaskInvCode, R.id.txtSpec,
                        R.id.txtType});
        new AlertDialog.Builder(SalesDeliveryDetail.this).setTitle("Դ����Ϣ")
                .setAdapter(listTaskAdapter, null)
                .setPositiveButton(R.string.QueRen, null).show();

    }

    private void ShowDetailDig() throws JSONException {
        lstDetailBody = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        if (jsSerino == null || !jsSerino.has("Serino")) {
            Toast.makeText(this, "��û��ɨ�赽�ļ�¼", Toast.LENGTH_SHORT).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }
        JSONArray arrays = jsSerino.getJSONArray("Serino");

        for (int i = 0; i < arrays.length(); i++) {
            map = new HashMap<String, Object>();
            String sSerial  = ((JSONObject) (arrays.get(i))).getString("sno");//���к�
            String sBatch   = ((JSONObject) (arrays.get(i))).getString("batch");
            String sInvCode = ((JSONObject) (arrays.get(i))).getString("invcode");
            String serino   = ((JSONObject) (arrays.get(i))).getString("serino");//����
            String sTotal   = ((JSONObject) (arrays.get(i))).getString("box");
            String invtype  = ((JSONObject) (arrays.get(i))).getString("invtype");
            String invspec  = ((JSONObject) (arrays.get(i))).getString("invspec");
            map.put("invcode", sInvCode);
            map.put("serino", serino);
            map.put("sno", sSerial);
            map.put("invname", ((JSONObject) (arrays.get(i))).getString("invname"));
            map.put("batch", sBatch);
            map.put("invtype", invtype);
            map.put("invspec", invspec);
            map.put("total", sTotal);
            lstDetailBody.add(map);
        }
        Log.d("TAG", "lstTaskBody: " + lstDetailBody);
        Log.d("TAG", "lstTaskBody: " + lstDetailBody.size());
        listItemAdapter = new SimpleAdapter(
                SalesDeliveryDetail.this, lstDetailBody,// ����Դ
                R.layout.item_sale_out_details,// ListItem��XMLʵ��
                // ��̬������ImageItem��Ӧ������
                new String[]{"invname", "invcode", "invspec", "invtype", "batch", "total"},
                // ImageItem��XML�ļ������һ��ImageView,����TextView ID
                new int[]{R.id.name,
                        R.id.encoding, R.id.spec,
                        R.id.type, R.id.lot, R.id.qty});

        DeleteButton = new AlertDialog.Builder(this).setTitle(getString(R.string.SaoMiaoMingXiXinXi))
                .setSingleChoiceItems(listItemAdapter, 0, buttonDelOnClick)
                .setPositiveButton(R.string.QueRen, null).create();
        // MOD CAIXY END

        DeleteButton.getListView().setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> arg0,
                                                   View parent, int position, long arg3) {
                        // TODO Auto-generated method stub
                        // When clicked, show a toast with the TextView text

                        ConfirmDelItem(position);
//                        jsSerino.remove("Serino");
//                        jsSerino.remove(String.valueOf(position));
//                        listItemAdapter.notifyDataSetChanged();
                        IniDetail();
                        return false;
                    }
                });

        DeleteButton.show();

    }


    private class ButtonOnClick implements DialogInterface.OnClickListener {
        public int index;

        public ButtonOnClick(int index) {
            this.index = index;
        }

        @Override
        public void onClick(@NonNull DialogInterface dialog, int whichButton) {
            if (whichButton >= 0) {
                index = whichButton;
            } else {
                if (dialog.equals(DeleteButton)) {
                    if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                        return;
                    } else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
                        // �����������ɾ������
                        // ConfirmDelItem(index);
                    }
                }
            }

        }

    }

    private void ConfirmDelItem(final int index) {
        ButtonOnClickDelconfirm buttondel = new ButtonOnClickDelconfirm(index);
        SelectButton = new AlertDialog.Builder(this).setTitle(R.string.QueRenShanChu)
                .setMessage(R.string.NiQueRenShanChuGaiXingWeiJiLuMa)
                .setPositiveButton(R.string.QueRen, buttondel)
                .setNegativeButton(R.string.QuXiao, null).show();
    }

    private class ButtonOnClickDelconfirm implements
            DialogInterface.OnClickListener {
        public int index;

        public ButtonOnClickDelconfirm(int index) {
            this.index = index;
        }

        @Override
        public void onClick(DialogInterface dialog, int whichButton) {
            if (whichButton >= 0) {
                index = whichButton;
            } else {

                if (whichButton == DialogInterface.BUTTON_POSITIVE) {

                    Map<String, Object> mapTemp = lstDetailBody
                            .get(index);
                    String invcode     = (String) mapTemp.get("invcode");
                    String batch       = (String) mapTemp.get("batch");
                    String sno         = (String) mapTemp.get("sno");
                    String serino      = (String) mapTemp.get("serino");
                    String totals      = (String) mapTemp.get("total");
                    Double ScanedTotal = Double.parseDouble(mapTemp.get("total").toString());

                    if (ScanedBarcode != null || ScanedBarcode.size() > 0) {
                        for (int si = 0; si < ScanedBarcode.size(); si++) {
                            String RemoveBarCode = ScanedBarcode.get(si).toString();
                            if (RemoveBarCode.equals(serino)) {
                                ScanedBarcode.remove(si);
                                si--;
                            }
                        }
                    }

                    JSONArray arrays;
                    try {
                        arrays = jsSerino.getJSONArray("Serino");

                        HashMap<String, Object> Temp    = new HashMap<String, Object>();
                        JSONArray               serinos = new JSONArray();

                        for (int i = 0; i < arrays.length(); i++) {
                            String serino1 = ((JSONObject) (arrays.get(i)))
                                    .getString("serino");
                            if (!serino1.equals(serino)) {
                                JSONObject temp = new JSONObject();
                                temp = arrays.getJSONObject(i);
                                serinos.put(temp);
                            }
                        }

                        jsSerino = new JSONObject();

                        if (serinos.length() > 0) {
                            jsSerino.put("Serino", serinos);
                        }
                        JSONArray bodys    = jsBody.getJSONArray("dbBody");
                        JSONArray bodynews = new JSONArray();
                        // JSONArray serinos = new JSONArray();
                        for (int i = 0; i < bodys.length(); i++) {
                            JSONObject temp = bodys.getJSONObject(i);

                            String invcodeold = ((JSONObject) (bodys.get(i)))
                                    .getString("invcode");
                            if (invcodeold.equals(invcode)) {
                                Double doneqty = temp.getDouble("ntotaloutinvnum");
                                temp.put("ntotaloutinvnum", doneqty - ScanedTotal);
//                                break;
                            }

                            bodynews.put(temp);
                        }

                        jsBody = new JSONObject();
                        jsBody.put("Status", "true");
                        jsBody.put("dbBody", bodynews);

                        //}

                        JSONArray arraysCount;
                        try {
                            arraysCount = jsBody.getJSONArray("dbBody");
                            number = 0.0;
                            ntotaloutinvnum = 0.0;
                            for (int i = 0; i < arraysCount.length(); i++) {
                                String sshouldinnum = ((JSONObject) (arraysCount
                                        .get(i))).getString("doneqty");
                                String sinnum = ((JSONObject) (arraysCount
                                        .get(i))).getString("ntotaloutinvnum");

                                number = number
                                        + Double.valueOf(sshouldinnum);
                                if (!sinnum.toLowerCase().equals("null") && !sinnum.isEmpty())
                                    ntotaloutinvnum = ntotaloutinvnum + Double.valueOf(sinnum);
                            }
                        } catch (JSONException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                        tvSalecount.setText("����" + number + " | " + "��ɨ"
                                + ntotaloutinvnum + " | " + "δɨ"
                                + (number - ntotaloutinvnum));
                        //SaveScanedBody();//д�뱾��
                        IniDetail();

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(SalesDeliveryDetail.this, e.getMessage(),
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                    }

                    DeleteButton.cancel();

                } else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
                    return;
                }
            }
        }
    }


}