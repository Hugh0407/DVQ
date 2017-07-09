package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
//    JSONObject jsBoxTotal;
    JSONObject jsSerino;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_out_scan_detail);
        ButterKnife.inject(this);
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("���۳���ɨ����ϸ");
        initView();
        Intent intent = this.getIntent();
        BillCode = intent.getStringExtra("BillCode");
        PK_CORP = intent.getStringExtra("PK_CORP");
        CSALEID = intent.getStringExtra("CSALEID");
        ScanType = intent.getStringExtra("ScanType");

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
                if(!ntotalnum.toLowerCase().equals("null") && !ntotalnum.isEmpty())
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

    private View.OnKeyListener myTxtListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int arg1, KeyEvent arg2) {
            switch (v.getId()) {
                case R.id.txtPurBarcode:
                    if (arg1 == 66 && arg2.getAction() == KeyEvent.ACTION_UP) {
                        ScanDetail(txtBarcode.getText().toString());
                        txtBarcode.requestFocus();
                        txtBarcode.setText("");
                        return true;
                    }
            }
            return false;
        }

    };

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
            Toast.makeText(this, "ɨ��Ĳ�����ȷ��Ʒ����", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return false;
        }

        m_cSplitBarcode = bar;

        if (!bar.BarcodeType.equals("P") && !bar.BarcodeType.equals("TP"))
            bar.creatorOk = false;

        String FinishBarCode = bar.FinishBarCode;

        if (ScanedBarcode != null || ScanedBarcode.size() > 0) {
            for (int si = 0; si < ScanedBarcode.size(); si++) {
                String BarCode = ScanedBarcode.get(si).toString();

                if (BarCode.equals(FinishBarCode)) {
                    Toast.makeText(this, "�������Ѿ���ɨ�����,�����ٴ�ɨ��", Toast.LENGTH_LONG)
                            .show();
                    // ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // ADD CAIXY TEST END
                    return false;
                }
            }
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
    //��ȡ�õ����ݼ��ص�ҳ��ؼ���
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

        Double ldTotal = (Double) m_mapSaleBaseInfo.get("quantity") * (Integer)m_mapSaleBaseInfo.get("number");
        txtSaleTotal.setText(ldTotal.toString());
        m_mapSaleBaseInfo.put("total",ldTotal);
        if(m_mapSaleBaseInfo.get("barcodetype").toString().equals("TP")) {
            txtSaleBatch.setFocusableInTouchMode(false);
            txtSaleBatch.setFocusable(false);
            txtSaleNumber.setFocusableInTouchMode(false);
            txtSaleNumber.setFocusable(false);
            txtSaleTotal.setFocusableInTouchMode(false);
            txtSaleTotal.setFocusable(false);
            ScanedToGet();
        }
        else if(m_mapSaleBaseInfo.get("barcodetype").toString().equals("P") ) {
            txtSaleBatch.setFocusableInTouchMode(false);
            txtSaleBatch.setFocusable(false);
            txtSaleNumber.setFocusableInTouchMode(true);
            txtSaleNumber.setFocusable(true);
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
                    // Ѱ�ҵ��˶�Ӧ���
                    Double doneqty = 0.0;
                    if(!temp.getString("nconfirmnum").isEmpty() &&
                            !temp.getString("nconfirmnum").toLowerCase().equals("null")) {
                        doneqty = temp.getDouble("nconfirmnum");
                        if (doneqty  >= temp.getInt("nnumber")) {
                            Toast.makeText(this, "�������Ѿ�����Ӧ��������,��������!",
                                    Toast.LENGTH_LONG).show();
                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END

                            txtBarcode.setText("");
                            txtBarcode.requestFocus();
                            return false;
                        }
                    }

                    if(bar.BarcodeType.equals("P") || bar.BarcodeType.equals("TP")) {
                        Double ldTotal = (Double) m_mapSaleBaseInfo.get("quantity") * (Integer)m_mapSaleBaseInfo.get("number");
                        txtSaleTotal.setText(ldTotal.toString());
                    }

                    if (ScanSerial(bar.FinishBarCode, Free1, txtSaleTotal.getText().toString()) == false) {
                        txtBarcode.setText("");
                        txtBarcode.requestFocus();
                        return false;
                    }
                    ScanedBarcode.add(bar.FinishBarCode);
                    MainLogin.sp.play(MainLogin.music2, 1, 1, 0, 0, 1);

//                    int doneqty = temp.getInt("doneqty");
//                    temp.put("doneqty", doneqty + 1);

                    //Double doneqty = temp.getDouble("doneqty");
                    //temp.put("doneqty", doneqty + Double.parseDouble(txtPurTotal.getText().toString()));
                    temp.put("nconfirmnum", doneqty + Double.parseDouble(txtSaleTotal.getText().toString()));
                    break;
                }
            }

            if (isFind == false) {
                Toast.makeText(this, "���������ڱ���ɨ��������", Toast.LENGTH_LONG).show();
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
            arrays = jsBody.getJSONArray("PurBody");
            number = 0.0;
            ntotaloutinvnum = 0.0;
            for (int i = 0; i < arrays.length(); i++) {
                String sshouldinnum = ((JSONObject) (arrays.get(i)))
                        .getString("nordernum");
                String sinnum = ((JSONObject) (arrays.get(i)))
                        .getString("nconfirmnum");

                number = number + Double.valueOf(sshouldinnum);
                if(!sinnum.toLowerCase().equals("null") && !sinnum.isEmpty())
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
        tvSalecount.setText("����" + number + " | " + "��ɨ" + ntotaloutinvnum
                + " | " + "δɨ" + (number - ntotaloutinvnum) );

        txtBarcode.requestFocus();
        txtBarcode.setText("");
        txtBarcode.setSelectAllOnFocus(true);

        return true;
    }

    private boolean ScanSerial(String serino, String Free1, String TotalBox)
            throws JSONException {
        if (jsSerino == null)
            jsSerino = new JSONObject();

        if (!jsSerino.has("Serino")) {
            JSONArray serinos = new JSONArray();
            jsSerino.put("Serino", serinos);
            JSONObject temp = new JSONObject();
            temp.put("serino", serino);
            temp.put("box", TotalBox);
            temp.put("invcode", m_mapSaleBaseInfo.get("invcode").toString());
            temp.put("invname", m_mapSaleBaseInfo.get("invname").toString());
            temp.put("batch", m_mapSaleBaseInfo.get("batch").toString());
            temp.put("sno", m_mapSaleBaseInfo.get("serino").toString());
            // caixy ��Ҫ���Ӳ���
            temp.put("vfree1", Free1);

            serinos.put(temp);
            return true;
        } else {
            JSONArray serinos = jsSerino.getJSONArray("Serino");

            for (int i = 0; i < serinos.length(); i++) {
                JSONObject temp = new JSONObject();
                temp = serinos.getJSONObject(i);
                if (temp.getString("serino").equals(serino)) {
                    //temp.put("box", TotalBox);
                    // Toast.makeText(this, "�������Ѿ���ɨ�����,�����ٴ�ɨ��",
                    // Toast.LENGTH_LONG).show();
                    // //ADD CAIXY TEST START
                    // MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    // //ADD CAIXY TEST END
                    // return false;
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
            // caixy ��Ҫ���Ӳ���
            temp.put("vfree1", Free1);

            serinos.put(temp);
        }

        return true;
    }

    private void LoadSaleOutBody() throws ParseException, IOException {
        if (BillCode == null || BillCode.equals("") || CSALEID == null || CSALEID.equals("")) {
            Toast.makeText(this, "����ȷ����Ҫɨ��Ķ�����", Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
            return;
        }

        JSONObject para = new JSONObject();
        String FunctionName = "";
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


    private void initView() {
        txtBarcode.setOnKeyListener(myTxtListener);
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
    }

    @OnClick({R.id.btnTask, R.id.btnDetail, R.id.btnReturn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnTask:
                break;
            case R.id.btnDetail:
                break;
            case R.id.btnReturn:
                break;
        }
    }
}
