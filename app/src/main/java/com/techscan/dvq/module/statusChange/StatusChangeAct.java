package com.techscan.dvq.module.statusChange;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.techscan.dvq.login.MainLogin;
import com.techscan.dvq.OtherOrderList;
import com.techscan.dvq.R;
import com.techscan.dvq.bean.PurGood;
import com.techscan.dvq.common.SaveThread;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.module.statusChange.scan.SCScanAct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;
import static com.techscan.dvq.common.Utils.showResultDialog;
import static com.techscan.dvq.common.Utils.showToast;


public class StatusChangeAct extends Activity {

    @InjectView(R.id.ed_bill_type)
    EditText mEdBillType;
    @InjectView(R.id.btn_bill_type)
    ImageButton mBtnBillType;
    @InjectView(R.id.ed_source_bill)
    EditText mEdSourceBill;
    @InjectView(R.id.btn_source_bill)
    ImageButton mBtnSourceBill;
    @InjectView(R.id.ed_select_wh)
    EditText mEdSelectWh;
    @InjectView(R.id.btn_select_wh)
    ImageButton mBtnSelectWh;

    @InjectView(R.id.sacn)
    Button mSacn;
    @InjectView(R.id.save)
    Button mSave;
    @InjectView(R.id.back)
    Button mBack;

    String result = "";
    String OrderNo = "";
    String OrderID = "";
    String WarehouseName = "";
    String WarehouseID = "";
    String AccID = "";
    String pk_corp = "";
    List<PurGood> taskList;
    ProgressDialog progressDialog;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_change);
        ButterKnife.inject(this);
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("��̬ת��");
        activity = this;
        mBtnBillType.setVisibility(View.INVISIBLE);
        mBtnSelectWh.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    @OnClick({R.id.btn_bill_type, R.id.btn_source_bill, R.id.btn_select_wh,
            R.id.sacn, R.id.save, R.id.back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_bill_type:
                break;
            case R.id.btn_source_bill:
                ShowOrderList("");
                break;
            case R.id.btn_select_wh:
                break;
            case R.id.sacn:
                ShowScanDetail();
                break;
            case R.id.save:
                if (!checkSaveInfo()) {
                    Utils.showToast(activity, "��˶Ե���!");
                    return;
                }

                if (null == taskList || taskList.size() < 0) {
                    Utils.showToast(activity, "û����Ҫ���������");
                    return;
                }

                try {
                    saveInfo();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showProgressDialog("��ʾ", "���ڱ���,���Ժ�...");
                break;
            case R.id.back:
                if (taskList != null && taskList.size() > 0) {
                    AlertDialog.Builder bulider =
                            new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("����δ�����Ƿ��˳�");
                    bulider.setNegativeButton(R.string.QuXiao, null);
                    bulider.setPositiveButton(R.string.QueRen, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SCScanAct.taskList.clear();
                            SCScanAct.detailList.clear();
                            dialog.dismiss();
                            finish();
                        }
                    }).create().show();
                } else {
                    SCScanAct.taskList.clear();
                    SCScanAct.detailList.clear();
                    finish();
                }
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
                    JSONObject info = (JSONObject) msg.obj;
                    if (null == info) {
                        Log.d("TAG", "info=null ");
                        progressDialogDismiss();
                        showResultDialog(activity, "�����ύʧ��!");
                        return;
                    }
                    try {
                        if (info.getBoolean("Status")) {
                            Log.d("TAG", "����" + info.toString());
                            showResultDialog(activity, info.getString("ErrMsg"));
                            taskList.clear();
                            SCScanAct.taskList.clear();
                            SCScanAct.detailList.clear();
                            changeAllEdToEmpty();
                            mEdSourceBill.requestFocus();
                        } else {
                            showResultDialog(activity, info.getString("ErrMsg"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialogDismiss();
                    break;
            }
        }
    };

    private void changeAllEdToEmpty() {
        mEdSourceBill.setText("");
        mEdSelectWh.setText("");
    }

    private void saveInfo() throws JSONException {
        String CFIRSTBILLBID = null;
        String CFIRSTBILLHID = null;
        final JSONObject table = new JSONObject();
        JSONObject saveOut = new JSONObject();
        JSONObject saveOutHead = new JSONObject();
        saveOutHead.put("CWAREHOUSEID", WarehouseID);
        saveOutHead.put("PK_CALBODY", MainLogin.objLog.STOrgCode);
        saveOutHead.put("PK_CORP", MainLogin.objLog.STOrgCode);
        saveOutHead.put("CLASTMODIID", MainLogin.objLog.UserID);
        saveOutHead.put("COPERATORID", MainLogin.objLog.UserID);
        saveOutHead.put("CDISPATCHERID", "0001TC100000000011Q8");
        JSONObject saveOutBody = new JSONObject();
        JSONArray saveOutArray = new JSONArray();
        for (int i = 0; i < taskList.size(); i++) {
            PurGood purGood = taskList.get(i);
            if (purGood.getFbillrowflag().equals("3")) {
                saveOutBody.put("CSOURCEBILLBID", purGood.getCsourcebillbid());
                saveOutBody.put("CSOURCEBILLHID", purGood.getCsourcebillhid());
                CFIRSTBILLBID = purGood.getCsourcebillbid();
                CFIRSTBILLHID = purGood.getCsourcebillhid();
                saveOutBody.put("CFIRSTBILLBID", CFIRSTBILLBID);
                saveOutBody.put("CFIRSTBILLHID", CFIRSTBILLHID);
                saveOutBody.put("CBODYWAREHOUSEID", WarehouseID);
                saveOutBody.put("INVCODE", purGood.getInvcode());
                saveOutBody.put("CINVBASID", purGood.getPk_invbasdoc());
                saveOutBody.put("CINVENTORYID", purGood.getCinventoryid());
                saveOutBody.put("NSHOULDOUTNUM", purGood.getNshouldinnum());
                saveOutBody.put("NSHOULDOUTNUM", "100.00");
                saveOutBody.put("NOUTNUM", purGood.getNum_task());
                saveOutBody.put("PK_BODYCALBODY", "1011TC100000000000KV");
                saveOutBody.put("VSOURCEBILLCODE", purGood.getSourceBill());
                saveOutBody.put("VSOURCEROWNO", purGood.getVsourcerowno());
                saveOutBody.put("VBATCHCODE", purGood.getVbatchcode());
                saveOutBody.put("CSOURCETYPE", "4N");
            }
        }
        saveOutArray.put(saveOutBody);
        saveOut.put("Head", saveOutHead);
        saveOut.put("Body", saveOutArray);
        saveOut.put("GUIDS", UUID.randomUUID().toString());
        table.put("SaveOut", saveOut);

        JSONObject saveIn = new JSONObject();
        JSONObject saveInHead = new JSONObject();
        saveInHead.put("CWAREHOUSEID", WarehouseID);     //�ֿ�
        saveInHead.put("PK_CALBODY", MainLogin.objLog.STOrgCode);   //�����֯
        saveInHead.put("PK_CORP", MainLogin.objLog.STOrgCode);   //�����֯
        saveInHead.put("CLASTMODIID", MainLogin.objLog.UserID);  //������id  ��
        saveInHead.put("COPERATORID", MainLogin.objLog.UserID);  //������id   ��
        saveInHead.put("CDISPATCHERID", "0001TC100000000011QO");//�շ����,�޿���˵����д��
        JSONObject saveInBody = new JSONObject();
        JSONArray saveInArray = new JSONArray();
        for (int i = 0; i < taskList.size(); i++) {
            PurGood purGood = taskList.get(i);
            if (purGood.getFbillrowflag().equals("2")) {
                saveInBody.put("CSOURCEBILLBID", purGood.getCsourcebillbid());
                saveInBody.put("CSOURCEBILLHID", purGood.getCsourcebillhid());
                saveInBody.put("CFIRSTBILLBID", CFIRSTBILLBID);
                saveInBody.put("CFIRSTBILLHID", CFIRSTBILLHID);
                saveInBody.put("CBODYWAREHOUSEID", WarehouseID);
                saveInBody.put("INVCODE", purGood.getInvcode());
                saveInBody.put("CINVBASID", purGood.getPk_invbasdoc());
                saveInBody.put("CINVENTORYID", purGood.getCinventoryid());
//                saveInBody.put("NSHOULDOUTNUM", purGood.getNshouldinnum());
                saveInBody.put("NSHOULDOUTNUM", "100.00");
                saveInBody.put("NINNUM", purGood.getNum_task());
                saveInBody.put("VSOURCEBILLCODE", purGood.getSourceBill());
                saveInBody.put("PK_BODYCALBODY", "1011TC100000000000KV");
                saveInBody.put("VSOURCEBILLCODE", purGood.getSourceBill());
                saveInBody.put("VSOURCEROWNO", purGood.getVsourcerowno());
                saveInBody.put("VBATCHCODE", purGood.getVbatchcode());
                saveInBody.put("CSOURCETYPE", "4N");
            }
        }
        saveInArray.put(saveInBody);
        saveIn.put("Head", saveInHead);
        saveIn.put("Body", saveInArray);
        saveIn.put("GUIDS", UUID.randomUUID().toString());
        table.put("SaveIn", saveIn);
        Log.d("TAG", "saveInfo: " + table.toString());
        SaveThread saveThread = new SaveThread(table, "SaveOtherInOutBill", mHandler, 1);
        Thread thread = new Thread(saveThread);
        thread.start();
    }

    /**
     * ���浥�ݵ�dialog
     */
    private void showProgressDialog(String title, String message) {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���ý���������ʽΪԲ��ת���Ľ�����
        progressDialog.setCancelable(false);// �����Ƿ����ͨ�����Back��ȡ��
        progressDialog.setCanceledOnTouchOutside(false);// �����ڵ��Dialog���Ƿ�ȡ��Dialog������
        // progressDialog.setIcon(R.drawable.ic_launcher);
        // ������ʾ��title��ͼ�꣬Ĭ����û�еģ����û������title�Ļ�ֻ����Icon�ǲ�����ʾͼ���
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
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

    private boolean checkSaveInfo() {
        return !TextUtils.isEmpty(mEdSourceBill.getText().toString())
                && !TextUtils.isEmpty(mEdSelectWh.getText().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // �ش�����<----OtherOrderList.class����ShowOrderList(); ������ȥ
        if (requestCode == 99 && resultCode == 1) {
            result = data.getStringExtra("result");
            OrderNo = data.getStringExtra("OrderNo");
            OrderID = data.getStringExtra("OrderID");
            WarehouseName = data.getStringExtra("WarehouseName");
            WarehouseID = data.getStringExtra("WarehouseID");
            AccID = data.getStringExtra("AccID");
            pk_corp = data.getStringExtra("pk_corp");
            mEdSourceBill.setText(OrderNo);
            mEdSelectWh.setText(WarehouseName);
            Log.d("TAG", "result: " + result);// result: 1
            Log.d("TAG", "OrderNo: " + OrderNo);// OrderNo: ZH17062800001
            Log.d("TAG", "OrderID: " + OrderID);//OrderID: 1011AA1000000004KP6R
            Log.d("TAG", "WarehouseName: " + WarehouseName);// WarehouseName: ���湤����Ʒ��
            Log.d("TAG", "WarehouseID: " + WarehouseID);// WarehouseID: 1011TC100000000000LF
            Log.d("TAG", "AccID: " + AccID);// AccID: B
            Log.d("TAG", "pk_corp: " + pk_corp);// pk_corp: 1011
        }
        // �ش�����<----OtherStockInDetail.class����ShowScanDetail(); ������ȥ
        if (requestCode == 93 && resultCode == 7) {
            taskList = data.getParcelableArrayListExtra("taskList");
            Log.d(TAG, "onActivityResult: " + taskList);
            Log.d(TAG, "onActivityResult: " + taskList.toArray());
        }
    }

    /**
     * ѡ����Ҫת����order
     *
     * @param lsBillCode
     */
    private void ShowOrderList(String lsBillCode) {
        if (this.mEdBillType.getText().toString().equals("")) {
            showToast(StatusChangeAct.this, "��ѡ�񵥾�����");
            return;
        }
        Intent otherOrder = new Intent(this, OtherOrderList.class);
        otherOrder.putExtra("OrderType", "4N");
        otherOrder.putExtra("Typename", this.mEdBillType.getText().toString());
        otherOrder.putExtra("BillCode", lsBillCode);
        startActivityForResult(otherOrder, 99);
    }

    //��ɨ�����
    private void ShowScanDetail() {

        if (OrderID == null || OrderID.equals("")) {
            showToast(StatusChangeAct.this, "��ѡ����Դ�ĵ��ݺ�");
        } else {
            Intent otherOrderDetail = new Intent(this, SCScanAct.class);
            otherOrderDetail.putExtra("OrderID", OrderID);
            otherOrderDetail.putExtra("BillNo", OrderNo);
            otherOrderDetail.putExtra("OrderType", "4N");
            otherOrderDetail.putExtra("AccID", AccID);
            otherOrderDetail.putExtra("m_WarehouseID", WarehouseID);
            otherOrderDetail.putExtra("pk_corp", pk_corp);
            startActivityForResult(otherOrderDetail, 93);
        }
    }
}

