package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.common.Utils;

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

    String ScanType = "";
    String BillCode = "";
    String CSALEID = "";
    String PK_CORP = "";
    JSONObject jsBody;
    JSONObject jsBoxTotal;
    JSONObject jsSerino;
    JSONObject jsTotal;
    String weight = "";
    String num = "";
    Double number;
    Double ntotaloutinvnum;
    @InjectView(R.id.TextView31)
    TextView TextView31;
    @InjectView(R.id.txtBarcode)
    EditText txtBarcode;
    @InjectView(R.id.TextView33)
    TextView TextView33;
    @InjectView(R.id.txtSaleInvCode)
    EditText txtSaleInvCode;
    @InjectView(R.id.txtSaleInvName)
    EditText txtSaleInvName;
    @InjectView(R.id.txtSaleType)
    EditText txtSaleType;
    @InjectView(R.id.txtSaleSpec)
    EditText txtSaleSpec;
    @InjectView(R.id.txtSaleBatch)
    EditText txtSaleBatch;
    @InjectView(R.id.txtSaleNumber)
    EditText txtSaleNumber;
    @InjectView(R.id.txtSaleWeight)
    EditText txtSaleWeight;
    @InjectView(R.id.txtSaleTotal)
    EditText txtSaleTotal;
    @InjectView(R.id.txtSaleUnit)
    EditText txtSaleUnit;
    @InjectView(R.id.tvSalecount)
    TextView tvSalecount;
    @InjectView(R.id.btnTask)
    Button btnTask;
    @InjectView(R.id.btnDetail)
    Button btnDetail;
    @InjectView(R.id.btnReturn)
    Button btnReturn;
    private GetSaleBaseInfo objSaleBaseInfo = null;
    private HashMap<String, Object> m_mapSaleBaseInfo = null;
    private SplitBarcode m_cSplitBarcode = null;
    private ArrayList<String> ScanedBarcode = new ArrayList<String>();
    List<Map<String, Object>> lstTaskBody = null;
    private AlertDialog DeleteButton = null;
    private AlertDialog SelectButton = null;
    private ButtonOnClick buttonDelOnClick = new ButtonOnClick(0);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_out_scan_detail);
        ButterKnife.inject(this);
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("销售出库扫描明细");
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
                ReScanErr();
                return;
            }
            arrays = jsBody.getJSONArray("dbBody");
            for (int i = 0; i < arrays.length(); i++) {
                String totalNumber = ((JSONObject) (arrays.get(i)))
                        .getString("nnumber");
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

        tvSalecount.setText("总量" + number + " | " + "已扫" + ntotaloutinvnum
                + " | " + "未扫" + (number - ntotaloutinvnum));

    }

    private View.OnKeyListener myTxtListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.txtBarcode:
                        ScanDetail(txtBarcode.getText().toString());
                        txtBarcode.requestFocus();
                        txtBarcode.setText("");
                        return true;
                    case R.id.txtSaleNumber:
                        if (TextUtils.isEmpty(txtSaleNumber.getText())) {
                            Utils.showToast(SalesDeliveryDetail.this, "数量不能为空");
//                            txtSaleNumber.requestFocus();
                            return true;
                        }
                        if (!isNumber(txtSaleNumber.getText().toString())) {
                            Utils.showToast(SalesDeliveryDetail.this, "数量不正确");
                            txtSaleNumber.setText("");
//                            txtSaleNumber.requestFocus();
                            return true;
                        }
                        if (Float.valueOf(txtSaleNumber.getText().toString()) <= 0) {
                            Utils.showToast(SalesDeliveryDetail.this, "数量不正确");
//                            txtSaleNumber.requestFocus();
                            return true;
                        }
                        m_mapSaleBaseInfo.put("number", Integer.valueOf(txtSaleNumber.getText().toString()));
                        ScanedToGet();
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
//                        txtSaleNumber.setText("");
                        return;
                    }
                    if (!isNumber(txtSaleNumber.getText().toString())) {
                        Utils.showToast(SalesDeliveryDetail.this, "数量不正确");
                        txtSaleNumber.requestFocus();
                        return;
                    }
                    if (Float.valueOf(txtSaleNumber.getText().toString()) < 0) {
                        Utils.showToast(SalesDeliveryDetail.this, "数量不正确");
                        txtSaleNumber.requestFocus();
                        return;
                    }

                    num = txtSaleNumber.getText().toString();

                    if (TextUtils.isEmpty(num)) {
                        num = "0";
                    }
                    weight = txtSaleWeight.getText().toString();
                    Log.d(TAG, "afterTextChanged: " + number);
                    Log.d(TAG, "afterTextChanged: " + weight);
                    float a = Float.valueOf(num);
                    float b = Float.valueOf(weight);
                    Log.d(TAG, "afterTextChanged: " + "");
                    txtSaleTotal.setText(String.valueOf(a * b));
                    m_mapSaleBaseInfo.put("number", Integer.valueOf(txtSaleNumber.getText().toString()));
//                    ScanedToGet();
                    break;
            }
        }
    }

    /**
     * 判断是否都是数字，使用正则表达式
     *
     * @param str
     * @return
     */
    public boolean isNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }


    private boolean ScanDetail(String Scanbarcode) {
        if (Scanbarcode == null || Scanbarcode.equals(""))
            return false;

        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return false;
        }

        SplitBarcode bar = new SplitBarcode(Scanbarcode);

        if (bar.creatorOk == false) {
            Toast.makeText(this, "扫描的不是正确货品条码", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return false;
        }
        m_cSplitBarcode = bar;

        //判断条码类型是否正确
        if (bar.BarcodeType.equals("P") || bar.BarcodeType.equals("TP")) {
            String FinishBarCode = bar.FinishBarCode;
            if (bar.BarcodeType.equals("TP")) {
                if (ScanedBarcode != null || ScanedBarcode.size() > 0) {
                    for (int si = 0; si < ScanedBarcode.size(); si++) {
                        String BarCode = ScanedBarcode.get(si).toString();
                        Log.d(TAG, "BBBB: " + BarCode);
                        if (BarCode.equals(FinishBarCode)) {
                            Toast.makeText(this, "该条码已经被扫描过了,不能再次扫描", Toast.LENGTH_SHORT)
                                    .show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return false;
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, "条码类型不匹配", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return false;
        }


        IniDetail();
        try {
            //currentObj = new Inventory(bar.cInvCode, "BADV", bar.AccID);
            objSaleBaseInfo = new GetSaleBaseInfo(bar, mHandler, PK_CORP);
//            objSaleBaseInfo = new GetSaleBaseInfo(bar, mHandler,PK_CORP);
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return false;
        }
        return true;
    }

    /**
     * 网络请求后的线程通信
     * msg.obj 是从子线程传递过来的数据
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

                            Log.d("TAG", "handleMessage: TEST");
                            Log.d("TAG", "json: " + json);
                            objSaleBaseInfo.SetSaleBaseToParam(json);
                            m_mapSaleBaseInfo = objSaleBaseInfo.mapSaleBaseInfo;
                            SetInvBaseToUI();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("TAG", "handleMessage: NULL");
                        return;
                    }
                    break;
            }
        }
    };

    //把取得的数据加载到页面控件上
    private void SetInvBaseToUI() {
        txtSaleInvCode.setText(m_mapSaleBaseInfo.get("invcode").toString());
        txtSaleInvName.setText(m_mapSaleBaseInfo.get("invname").toString());
        txtSaleType.setText(m_mapSaleBaseInfo.get("invtype").toString());
        txtSaleSpec.setText(m_mapSaleBaseInfo.get("invspec").toString());
        txtSaleBatch.setText(m_mapSaleBaseInfo.get("batch").toString());
        txtSaleUnit.setText(m_mapSaleBaseInfo.get("measname").toString());
        txtBarcode.setText(m_mapSaleBaseInfo.get("barcode").toString());
        txtSaleWeight.setText(m_mapSaleBaseInfo.get("quantity").toString());
        txtSaleNumber.setText(m_mapSaleBaseInfo.get("number").toString());

        Double ldTotal = (Double) m_mapSaleBaseInfo.get("quantity") * (Integer) m_mapSaleBaseInfo.get("number");
        txtSaleTotal.setText(ldTotal.toString());
        m_mapSaleBaseInfo.put("total", ldTotal);
        if (m_mapSaleBaseInfo.get("barcodetype").toString().equals("TP")) {
            txtSaleBatch.setFocusableInTouchMode(false);
            txtSaleBatch.setFocusable(false);
            txtSaleNumber.setFocusableInTouchMode(false);
            txtSaleNumber.setFocusable(false);
            txtSaleTotal.setFocusableInTouchMode(false);
            txtSaleTotal.setFocusable(false);
            ScanedToGet();
        } else if (m_mapSaleBaseInfo.get("barcodetype").toString().equals("P")) {
            txtSaleBatch.setFocusableInTouchMode(false);
            txtSaleBatch.setFocusable(false);
            txtSaleNumber.setFocusableInTouchMode(true);
            txtSaleNumber.setFocusable(true);
            txtSaleNumber.setEnabled(true);
            txtSaleTotal.setFocusableInTouchMode(false);
            txtSaleTotal.setFocusable(false);
            txtSaleNumber.requestFocus();
            txtSaleNumber.selectAll();
        }
    }

    private boolean ScanedToGet() {
        SplitBarcode bar = m_cSplitBarcode;
        try {
            JSONArray bodys = jsBody.getJSONArray("dbBody");
            Log.d("TAG", "dbBody: " + bodys);
            boolean isFind = false;
            for (int i = 0; i < bodys.length(); i++) {
                JSONObject temp = bodys.getJSONObject(i);
                if (temp.getString("invcode").equals(m_mapSaleBaseInfo.get("invcode").toString())) {
                    isFind = true;
                    String Free1 = "";
                    // 寻找到了对应存货
                    Double doneqty = 0.0;
                    if (!temp.getString("ntotaloutinvnum").isEmpty() && !temp.getString("ntotaloutinvnum").toLowerCase().equals("null")) {
                        doneqty = temp.getDouble("ntotaloutinvnum");
                        if (bar.BarcodeType.equals("P") || bar.BarcodeType.equals("TP")) {
                            Double ldTotal = (Double) m_mapSaleBaseInfo.get("quantity") * (Integer) m_mapSaleBaseInfo.get("number");
                            txtSaleTotal.setText(ldTotal.toString());
                        }
                        doneqty = doneqty + Double.parseDouble(txtSaleTotal.getText().toString());
                        Log.d(TAG, "ScanedToGet: " + doneqty.toString());
                        if (doneqty > temp.getInt("nnumber")) {
                            Toast.makeText(this, "这个存货已经超过应发数量了,不允出库!",
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            IniDetail();
                            txtBarcode.setText("");
                            txtBarcode.requestFocus();
                            return false;
                        }
                    }

                    if (ScanSerial(bar.FinishBarCode, Free1, txtSaleTotal.getText().toString()) == false) {
                        txtBarcode.setText("");
                        txtBarcode.requestFocus();
                        return false;
                    }
                    ScanedBarcode.add(bar.FinishBarCode);
                    MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);
                    Log.d(TAG, "ScanedToGet: " + doneqty.toString());
                    temp.put("ntotaloutinvnum", doneqty);
                    break;
                }
            }


            if (isFind == false) {
                IniDetail();
                Toast.makeText(this, "这个存货不在本次扫描任务中", Toast.LENGTH_LONG).show();

                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return false;
            }


        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
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
                        .getString("nnumber");
                String sinnum = ((JSONObject) (arrays.get(i)))
                        .getString("ntotaloutinvnum");
                number = number + Double.valueOf(sshouldinnum);
                if (!sinnum.toLowerCase().equals("null") && !sinnum.isEmpty())
                    ntotaloutinvnum = ntotaloutinvnum + Double.valueOf(sinnum);
            }
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();

            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
        }
        tvSalecount.setText("总量" + number + " | " + "已扫" + ntotaloutinvnum
                + " | " + "未扫" + (number - ntotaloutinvnum));

        txtBarcode.requestFocus();
        txtBarcode.setText("");
        txtBarcode.setSelectAllOnFocus(true);

        return true;
    }

    private boolean ScanSerial(String serino, String Free1, String TotalBox)
            throws JSONException {
        if (jsSerino == null) {
            jsSerino = new JSONObject();
        }
        if (!jsSerino.has("Serino")) {
            JSONArray serinos = new JSONArray();
            JSONObject temp = new JSONObject();
            temp.put("serino", serino);
            temp.put("box", TotalBox);
            temp.put("invcode", m_mapSaleBaseInfo.get("invcode").toString());
            temp.put("invname", m_mapSaleBaseInfo.get("invname").toString());
            temp.put("batch", m_mapSaleBaseInfo.get("batch").toString());
            temp.put("sno", m_mapSaleBaseInfo.get("serino").toString());
            temp.put("invtype", m_mapSaleBaseInfo.get("invtype").toString());
            temp.put("invspec", m_mapSaleBaseInfo.get("invspec").toString());
            serinos.put(temp);
            jsSerino.put("Serino", serinos);
        } else {
            JSONArray serinos = jsSerino.getJSONArray("Serino");

//            for (int i = 0; i < serinos.length(); i++) {
//                JSONObject temp = new JSONObject();
//                temp = serinos.getJSONObject(i);
////                if (temp.getString("serino").equals(serino)) {
////                    return true;
////                }
//            }
            JSONObject temp = new JSONObject();
            temp.put("serino", serino);
            temp.put("box", TotalBox);
            temp.put("invcode", m_mapSaleBaseInfo.get("invcode").toString());
            temp.put("invname", m_mapSaleBaseInfo.get("invname").toString());
            temp.put("batch", m_mapSaleBaseInfo.get("batch").toString());
            temp.put("sno", m_mapSaleBaseInfo.get("serino").toString());
            temp.put("invtype", m_mapSaleBaseInfo.get("invtype").toString());
            temp.put("invspec", m_mapSaleBaseInfo.get("invspec").toString());
            serinos.put(temp);
            jsSerino.put("Serino", serinos);

        }
        return true;
    }

    private void LoadSaleOutBody() throws ParseException, IOException {
        if (BillCode == null || BillCode.equals("") || CSALEID == null || CSALEID.equals("")) {
            Toast.makeText(this, "请先确认需要扫描的订单号", Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        JSONObject para = new JSONObject();
        String FunctionName = "";
        FunctionName = "CommonQuery";
        if (ScanType.equals("销售出库")) {
            try {
                para.put("FunctionName", "GetSaleOutBodyNew");
                para.put("BillCode", BillCode);
                para.put("CSALEID", CSALEID);
                para.put("CorpPK", "4100");
                para.put("TableName", "dbBody");
                Log.d(TAG, "GetBillBodyDetailInfo: " + BillCode);
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(SalesDeliveryDetail.this, e.getMessage(),
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

    private void ReScanErr() {
        AlertDialog.Builder bulider =
                new AlertDialog.Builder(this).setTitle(R.string.CuoWu).setMessage("数据加载出现错误" + "\r\n" + "退出该模块并且再次尝试加载缓存");

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


    private void initView() {
        txtBarcode.setOnKeyListener(myTxtListener);
        txtSaleNumber.setOnKeyListener(myTxtListener);
        txtBarcode.addTextChangedListener(new CustomTextWatcher(txtBarcode));
        txtSaleNumber.addTextChangedListener(new CustomTextWatcher(txtSaleNumber));
//        this.txtBarcode.addTextChangedListener(watchers);
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
        txtSaleNumber.setEnabled(false);
        txtSaleTotal.setEnabled(false);
        txtSaleWeight.setEnabled(false);

    }

    @OnClick({R.id.btnTask, R.id.btnDetail, R.id.btnReturn})
    public void onViewClicked(View view) {
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
                Log.d(TAG, "OOO: " + jsSerino.toString());
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
                    Toast.makeText(SalesDeliveryDetail.this, "没有扫描到数据", Toast.LENGTH_SHORT).show();
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
                    sinnum + " / " + Double.valueOf(((JSONObject) (arrays.get(i))).getString("nnumber")));
            // map.put("DoneQty", )
            lstTaskBody.add(map);
        }

        SimpleAdapter listItemAdapter = new SimpleAdapter(
                SalesDeliveryDetail.this, lstTaskBody,
                R.layout.sale_out_task_detail,
                new String[]{"InvName", "InvNum", "InvCode", "Invspec", "Invtype"},
                new int[]{R.id.txtTranstaskInvName, R.id.txtTranstaskInvNum,
                        R.id.txtTranstaskInvCode, R.id.txtSpec,
                        R.id.txtType});
        new AlertDialog.Builder(SalesDeliveryDetail.this).setTitle("源单信息")
                .setAdapter(listItemAdapter, null)
                .setPositiveButton(R.string.QueRen, null).show();

    }

    private void ShowDetailDig() throws JSONException {
        lstTaskBody = new ArrayList<Map<String, Object>>();
//        Log.d("TAG", "jsSerino: " + jsSerino.toString());
        Map<String, Object> map;
        if (jsSerino == null || !jsSerino.has("Serino")) {
            Toast.makeText(this, "还没有扫描到的记录", Toast.LENGTH_SHORT).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }
        JSONArray arrays = jsSerino.getJSONArray("Serino");

        for (int i = 0; i < arrays.length(); i++) {
            map = new HashMap<String, Object>();
            String sSerial = ((JSONObject) (arrays.get(i))).getString("sno");//序列号
            String sBatch = ((JSONObject) (arrays.get(i))).getString("batch");
            String sInvCode = ((JSONObject) (arrays.get(i))).getString("invcode");
            String serino = ((JSONObject) (arrays.get(i))).getString("serino");//条码
            String sTotal = ((JSONObject) (arrays.get(i))).getString("box");
            String invtype = ((JSONObject) (arrays.get(i))).getString("invtype");
            String invspec = ((JSONObject) (arrays.get(i))).getString("invspec");
            map.put("invcode", sInvCode);
            map.put("serino", serino);
            map.put("sno", sSerial);
            map.put("invname", ((JSONObject) (arrays.get(i))).getString("invname"));
            map.put("batch", sBatch);
            map.put("invtype", invtype);
            map.put("invspec", invspec);
            map.put("total", sTotal);
            lstTaskBody.add(map);
        }
        Log.d("TAG", "lstTaskBody: " + lstTaskBody);
        SimpleAdapter listItemAdapter = new SimpleAdapter(
                SalesDeliveryDetail.this, lstTaskBody,// 数据源
                R.layout.item_sale_out_details,// ListItem的XML实现
                // 动态数组与ImageItem对应的子项
                new String[]{"invname", "invcode", "invspec", "invtype", "batch", "total"},
                // ImageItem的XML文件里面的一个ImageView,两个TextView ID
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
        public void onClick(DialogInterface dialog, int whichButton) {
            if (whichButton >= 0) {
                index = whichButton;
            } else {
                if (dialog.equals(DeleteButton)) {
                    if (whichButton == DialogInterface.BUTTON_POSITIVE) {
                        return;
                    } else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
                        // 这里进行数据删除处理
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
                .setPositiveButton(R.string.QueRen, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> mapTemp = (Map<String, Object>) lstTaskBody
                                .get(index);
                        String invcode = (String) mapTemp.get("invcode");
                        String batch = (String) mapTemp.get("batch");
                        String sno = (String) mapTemp.get("sno");
                        String serino = (String) mapTemp.get("serino");
                        String totals = (String) mapTemp.get("total");
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

                            HashMap<String, Object> Temp = new HashMap<String, Object>();
                            JSONArray serinos = new JSONArray();

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
                            JSONArray bodys = jsBody.getJSONArray("dbBody");
                            JSONArray bodynews = new JSONArray();
                            // JSONArray serinos = new JSONArray();
                            for (int i = 0; i < bodys.length(); i++) {
                                JSONObject temp = bodys.getJSONObject(i);

                                String invcodeold = ((JSONObject) (bodys.get(i)))
                                        .getString("invcode");
                                if (invcodeold.equals(invcode)) {
                                    Double doneqty = temp.getDouble("ntotaloutinvnum");
                                    temp.put("ntotaloutinvnum", doneqty - ScanedTotal);
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
                                            .get(i))).getString("nnumber");
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
                            tvSalecount.setText("总量" + number + " | " + "已扫"
                                    + ntotaloutinvnum + " | " + "未扫"
                                    + (number - ntotaloutinvnum));
                            //SaveScanedBody();//写入本地
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

                    }
                })
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
//                index = whichButton;
            } else {

                if (whichButton == DialogInterface.BUTTON_POSITIVE) {

                    Map<String, Object> mapTemp = (Map<String, Object>) lstTaskBody
                            .get(index);
                    String invcode = (String) mapTemp.get("invcode");
                    String batch = (String) mapTemp.get("batch");
                    String sno = (String) mapTemp.get("sno");
                    String serino = (String) mapTemp.get("serino");
                    String totals = (String) mapTemp.get("total");
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

                        HashMap<String, Object> Temp = new HashMap<String, Object>();
                        JSONArray serinos = new JSONArray();

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
                        JSONArray bodys = jsBody.getJSONArray("dbBody");
                        JSONArray bodynews = new JSONArray();
                        // JSONArray serinos = new JSONArray();
                        for (int i = 0; i < bodys.length(); i++) {
                            JSONObject temp = bodys.getJSONObject(i);

                            String invcodeold = ((JSONObject) (bodys.get(i)))
                                    .getString("invcode");
                            if (invcodeold.equals(invcode)) {
                                Double doneqty = temp.getDouble("ntotaloutinvnum");
                                temp.put("ntotaloutinvnum", doneqty - ScanedTotal);
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
                                        .get(i))).getString("nnumber");
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
                        tvSalecount.setText("总量" + number + " | " + "已扫"
                                + ntotaloutinvnum + " | " + "未扫"
                                + (number - ntotaloutinvnum));
                        //SaveScanedBody();//写入本地
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