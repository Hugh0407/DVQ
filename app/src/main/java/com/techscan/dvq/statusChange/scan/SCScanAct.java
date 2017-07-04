package com.techscan.dvq.statusChange.scan;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.PurGood;
import com.techscan.dvq.common.RequestThread;

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

/**
 * ��̬ת��ģ���µ� ɨ�����
 */

public class SCScanAct extends Activity {

    @InjectView(R.id.ed_bar_code)
    EditText mEdBarCode;
    @InjectView(R.id.ed_encoding)
    EditText mEdEncoding;
    @InjectView(R.id.ed_name)
    EditText mEdName;
    @InjectView(R.id.ed_type)
    EditText mEdType;
    @InjectView(R.id.ed_spectype)
    EditText mEdSpectype;
    @InjectView(R.id.ed_lot)
    EditText mEdLot;
    @InjectView(R.id.ed_cost_object)
    EditText mEdCostObject;
    @InjectView(ed_num)
    EditText mEdNum;
    @InjectView(R.id.ed_weight)
    EditText mEdWeight;
    @InjectView(R.id.ed_qty)
    EditText mEdQty;
    @InjectView(R.id.ed_unit)
    EditText mEdUnit;
    @InjectView(R.id.btn_task)
    Button mBtnTask;
    @InjectView(R.id.btn_detail)
    Button mBtnDetail;
    @InjectView(R.id.btn_back)
    Button mBtnBack;

    String m_BillNo;
    String m_BillID;
    String m_BillType;
    String m_AccID;
    String m_WarehouseID;
    String m_pk_Corp;
    List<PurGood> dataList;
    ProgressDialog progressDialog;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scscan);
        ButterKnife.inject(this);
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("��̬ת��ɨ��");
        mEdBarCode.setOnKeyListener(mOnKeyListener);
        dataList = new ArrayList<PurGood>();
        initTaskData();
    }

    private void initTaskData() {
        showProgressDialog();
        m_BillNo = this.getIntent().getStringExtra("BillNo");
        m_BillID = this.getIntent().getStringExtra("OrderID");
        m_BillType = this.getIntent().getStringExtra("OrderType");
        m_AccID = this.getIntent().getStringExtra("AccID");
        m_WarehouseID = this.getIntent().getStringExtra("m_WarehouseID");
        m_pk_Corp = this.getIntent().getStringExtra("pk_corp");
        GetOtherInOutHead();
        GetOtherInOutBody();
    }


    @OnClick({R.id.btn_task, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_task:
                ScAdapter scAdapter = new ScAdapter(SCScanAct.this, dataList);
                showDialog(dataList, scAdapter, "������Ϣ");
                break;
            case R.id.btn_detail:

                break;
            case R.id.btn_back:
                finish();
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
                    JSONObject jsonHead = (JSONObject) msg.obj;
                    try {
                        if (jsonHead != null) {
                            Log.d("TAG", "jsonHead: " + jsonHead.toString());
                            if (jsonHead.getBoolean("Status")) {
                                Log.d("TAG", "jsonHead: ");
                            }
                        } else {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    JSONObject jsonBody = (JSONObject) msg.obj;
                    try {
                        if (jsonBody != null && jsonBody.getBoolean("Status")) {
                            JSONArray jsonArray = jsonBody.getJSONArray("PurBody");
                            PurGood purGood;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                purGood = new PurGood();
                                purGood.setSourceBill(m_BillNo);
                                purGood.setNshouldinnum(0 + "/" + object.getString("nshouldinnum"));
                                purGood.setInvcode(object.getString("invcode"));
                                purGood.setInvname(object.getString("invname"));
                                purGood.setVbatchcode(object.getString("vbatchcode"));
                                dataList.add(purGood);
                            }
                            progressDialogDismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private void showDialog(List list, BaseAdapter adapter, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SCScanAct.this);
        builder.setTitle(title);
        if (list.size() > 0) {
            View view = LayoutInflater.from(SCScanAct.this).inflate(R.layout.dialog_scan_details, null);
            ListView lv = (ListView) view.findViewById(R.id.lv);
            lv.setAdapter(adapter);
            builder.setView(view);
        } else {
            builder.setMessage("û��ɨ������");
        }
        builder.setPositiveButton("ȷ��", null);
        builder.show();
    }

    private void GetOtherInOutHead() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetOtherInOutHead");
        parameter.put("BillType", m_BillType);
        parameter.put("accId", m_AccID);
        parameter.put("BillCode", m_BillNo);
        parameter.put("pk_corp", m_pk_Corp);
        parameter.put("TableName", "PurGood");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread td = new Thread(requestThread);
        td.start();
    }

    private void GetOtherInOutBody() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetOtherInOutBody");
        parameter.put("BillID", m_BillID);
        parameter.put("accId", m_AccID);
        parameter.put("TableName", "PurBody");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 2);
        Thread td = new Thread(requestThread);
        td.start();
    }

    /**
     * ���浥�ݵ�dialog
     */
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(SCScanAct.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���ý���������ʽΪԲ��ת���Ľ�����
        progressDialog.setCancelable(false);// �����Ƿ����ͨ�����Back��ȡ��
        progressDialog.setCanceledOnTouchOutside(false);// �����ڵ��Dialog���Ƿ�ȡ��Dialog������
        // progressDialog.setIcon(R.drawable.ic_launcher);
        // ������ʾ��title��ͼ�꣬Ĭ����û�еģ����û������title�Ļ�ֻ����Icon�ǲ�����ʾͼ���
        progressDialog.setTitle("���浥��");
        // dismiss����
//        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//
//            @Override
//            public void onDismiss(DialogInterface progressDialog) {
//                // TODO Auto-generated method stub
//
//            }
//        });
        // ����Key�¼������ݸ�dialog
//        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//
//            @Override
//            public boolean onKey(DialogInterface progressDialog, int keyCode,
//                                 KeyEvent event) {
//                // TODO Auto-generated method stub
//                return false;
//            }
//        });
        // ����cancel�¼�
//        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//
//            @Override
//            public void onCancel(DialogInterface progressDialog) {
//                // TODO Auto-generated method stub
//
//            }
//        });
        //���ÿɵ���İ�ť�����������(Ĭ�������)
//        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "ȷ��",
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface progressDialog, int which) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
//        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "ȡ��",
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface progressDialog, int which) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
//        progressDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "����",
//                new DialogInterface.OnClickListener() {
//
//                    @Override
//                    public void onClick(DialogInterface progressDialog, int which) {
//                        // TODO Auto-generated method stub
//
//                    }
//                });
        progressDialog.setMessage("���ڱ��棬��ȴ�...");
        progressDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (progressDialog.isShowing()) {
                        Thread.sleep(30 * 1000);
                        // cancel��dismiss�������ʶ���һ���ģ����Ǵ���Ļ��ɾ��Dialog,Ψһ��������
                        // ����cancel������ص�DialogInterface.OnCancelListener���ע��Ļ�,dismiss��������ص�
                        progressDialog.cancel();
                        // progressDialog.dismiss();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    /**
     * progressDialog ��ʧ
     */
    private void progressDialogDismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
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

                        return true;
                    case R.id.ed_lot:

                        return true;
                    case R.id.ed_qty:

                        return true;
                    case ed_num:

                        return true;
                }
            }
            return false;
        }
    };
}
