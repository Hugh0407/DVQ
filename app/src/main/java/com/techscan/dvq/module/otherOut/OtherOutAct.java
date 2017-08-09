package com.techscan.dvq.module.otherOut;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.techscan.dvq.ListWarehouse;
import com.techscan.dvq.R;
import com.techscan.dvq.VlistRdcl;
import com.techscan.dvq.bean.Goods;
import com.techscan.dvq.common.Base64Encoder;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SaveThread;
import com.techscan.dvq.common.SoundHelper;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.login.MainLogin;
import com.techscan.dvq.module.materialOut.DepartmentListAct;
import com.techscan.dvq.module.materialOut.StorgListAct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.techscan.dvq.common.Utils.HANDER_DEPARTMENT;
import static com.techscan.dvq.common.Utils.HANDER_SAVE_RESULT;
import static com.techscan.dvq.common.Utils.HANDER_STORG;
import static com.techscan.dvq.common.Utils.showResultDialog;
import static com.techscan.dvq.common.Utils.showToast;

public class OtherOutAct extends Activity {

    @Nullable
    @InjectView(R.id.ed_bill_num)
    EditText    edBillNum;
    @Nullable
    @InjectView(R.id.ed_bill_date)
    EditText    edBillDate;
    @Nullable
    @InjectView(R.id.ed_wh)
    EditText    edWh;
    @Nullable
    @InjectView(R.id.btn_refer_wh)
    ImageButton btnReferWh;
    @Nullable
    @InjectView(R.id.ed_org)
    EditText    edOrg;
    @Nullable
    @InjectView(R.id.btn_refer_org)
    ImageButton btnReferOrg;
    @Nullable
    @InjectView(R.id.ed_lei_bie)
    EditText    edLeiBie;
    @Nullable
    @InjectView(R.id.btn_refer_lei_bie)
    ImageButton btnReferLeiBie;
    @Nullable
    @InjectView(R.id.ed_dep)
    EditText    edDep;
    @Nullable
    @InjectView(R.id.btn_refer_dep)
    ImageButton btnReferDep;
    @Nullable
    @InjectView(R.id.ed_remark)
    EditText    edRemark;
    @Nullable
    @InjectView(R.id.btn_scan)
    Button      btnScan;
    @Nullable
    @InjectView(R.id.btn_save)
    Button      btnSave;
    @Nullable
    @InjectView(R.id.btn_back)
    Button      btnBack;

    @Nullable
    Activity activity;
    private String TAG = this.getClass().getSimpleName();

    @Nullable
    List<Goods> tempList;
    HashMap<String, String> checkInfo;

    String CDISPATCHERID = "";//�շ����code
    String CDPTID        = "";  //����id
    String CWAREHOUSEID  = "";    //�����֯
    String PK_CALBODY    = "";    //�����֯ id
    String CUSER;   //��¼Ա��id
    String PK_CORP;         //��˾
    String VBILLCOD;        //���ݺ�

    int      year;
    int      month;
    int      day;
    Calendar mycalendar;
    @Nullable
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_out);
        ButterKnife.inject(this);
        activity = this;
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    @OnClick({R.id.btn_refer_wh, R.id.btn_refer_org, R.id.btn_refer_lei_bie,
            R.id.btn_refer_dep, R.id.btn_scan, R.id.btn_save, R.id.btn_back})
    public void onViewClicked(@NonNull View view) {
        switch (view.getId()) {
            case R.id.btn_refer_wh:
                btnWarehouseClick();
                break;
            case R.id.btn_refer_org:
                btnReferSTOrgList();
                break;
            case R.id.btn_refer_lei_bie:
                btnRdclClick("");
                break;
            case R.id.btn_refer_dep:
                btnReferDepartment();
                break;
            case R.id.btn_scan:
                if (checkSaveInfo()) {
                    Intent in = new Intent(activity, OtherOutScanAct.class);
                    in.putExtra("CWAREHOUSEID", CWAREHOUSEID);
                    in.putExtra("PK_CALBODY", PK_CALBODY);
                    startActivityForResult(in, 95);
                    if (tempList != null) {
                        tempList.clear();
                    }
                }
                break;
            case R.id.btn_save:
                if (checkSaveInfo()) {
                    if (tempList != null && tempList.size() > 0) {
                        saveInfo(tempList);
                        showProgressDialog();
                    } else {
                        showToast(activity, "û����Ҫ���������");
                    }
                }
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private void init() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("��������");
        mycalendar = Calendar.getInstance();//��ʼ��Calendar��������
        year = mycalendar.get(Calendar.YEAR); //��ȡCalendar�����е���
        month = mycalendar.get(Calendar.MONTH);//��ȡCalendar�����е���
        day = mycalendar.get(Calendar.DAY_OF_MONTH);//��ȡ����µĵڼ���
        edBillDate.setText(MainLogin.appTime);
        edBillDate.setInputType(InputType.TYPE_NULL);
        edBillDate.setOnFocusChangeListener(myFocusListener);
        edBillDate.setOnKeyListener(mOnKeyListener);
        edWh.setOnKeyListener(mOnKeyListener);
        edOrg.setOnKeyListener(mOnKeyListener);
        edLeiBie.setOnKeyListener(mOnKeyListener);
        edDep.setOnKeyListener(mOnKeyListener);
        checkInfo = new HashMap<String, String>();
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //���ϳ�������֯�Ļش����� <----StorgListAct.class
        if (requestCode == 94 && resultCode == 6) {
            String pk_areacl  = data.getStringExtra("pk_areacl");
            String bodyname   = data.getStringExtra("bodyname");
            String pk_calbody = data.getStringExtra("pk_calbody");
            PK_CALBODY = pk_calbody;
            edOrg.setText(bodyname);
            edWh.requestFocus();
            checkInfo.put("Organization", bodyname);
        }
        //�ֿ�Ļش����� <----ListWarehouse.class
        if (requestCode == 97 && resultCode == 13) {
            String warehousePK1  = data.getStringExtra("result1");
            String warehousecode = data.getStringExtra("result2");
            String warehouseName = data.getStringExtra("result3");
            CWAREHOUSEID = warehousePK1;
            edWh.setText(warehouseName);
            edLeiBie.requestFocus();
            checkInfo.put("Warehouse", warehouseName);
        }

        // �շ����Ļش����� <----VlistRdcl.class
        if (requestCode == 98 && resultCode == 2) {
            String code  = data.getStringExtra("Code");
            String name  = data.getStringExtra("Name");
            String AccID = data.getStringExtra("AccID");
            String RdIDA = data.getStringExtra("RdIDA");    //��Ҫ�ش���id
            String RdIDB = data.getStringExtra("RdIDB");
            CDISPATCHERID = RdIDA;
            edLeiBie.setText(name);
            edDep.requestFocus();
            checkInfo.put("LeiBie", name);
        }
        //������Ϣ�Ļش����� <----DepartmentListAct.class
        if (requestCode == 96 && resultCode == 4) {
            String deptname   = data.getStringExtra("deptname");
            String pk_deptdoc = data.getStringExtra("pk_deptdoc");
            String deptcode   = data.getStringExtra("deptcode");
            CDPTID = pk_deptdoc;
            edDep.requestFocus();
            edDep.setText(deptname);
            checkInfo.put("Department", deptname);
        }

        //ɨ����ϸ�Ļش����� <----OtherOutScanAct.class
        if (requestCode == 95 && resultCode == 5) {
            Bundle bundle = data.getExtras();
            tempList = bundle.getParcelableArrayList("overViewList");
        }
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
                case HANDER_DEPARTMENT:
                    JSONObject json = (JSONObject) msg.obj;
                    try {
                        if (json.getBoolean("Status")) {
                            JSONArray  val  = json.getJSONArray("department");
                            JSONObject temp = new JSONObject();
                            temp.put("department", val);
                            Intent ViewGrid = new Intent(activity, DepartmentListAct.class);
                            ViewGrid.putExtra("myData", temp.toString());
                            startActivityForResult(ViewGrid, 96);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HANDER_STORG:
                    JSONObject storg = (JSONObject) msg.obj;
                    try {
                        if (storg.getBoolean("Status")) {
                            JSONArray  val  = storg.getJSONArray("STOrg");
                            JSONObject temp = new JSONObject();
                            temp.put("STOrg", val);
                            Intent StorgList = new Intent(activity, StorgListAct.class);
                            StorgList.putExtra("STOrg", temp.toString());
                            startActivityForResult(StorgList, 94);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HANDER_SAVE_RESULT:
                    JSONObject saveResult = (JSONObject) msg.obj;
                    try {
                        if (saveResult != null) {
                            if (saveResult.getBoolean("Status")) {
                                Log.d(TAG, "����" + saveResult.toString());
                                showResultDialog(activity, saveResult.getString("ErrMsg"));
                                OtherOutScanAct.ovList.clear();
                                OtherOutScanAct.detailList.clear();
                                tempList.clear();
                                changeAllEdToEmpty();
                                edBillNum.requestFocus();
                            } else {
                                showResultDialog(activity, saveResult.getString("ErrMsg"));
                            }
                        } else {
                            showResultDialog(activity, "�����ύʧ��!");
                        }
                        progressDialogDismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * ����ֿ��б����
     *
     * @throws JSONException
     */
    private void btnWarehouseClick() {
        String lgUser      = MainLogin.objLog.LoginUser;
        String lgPwd       = MainLogin.objLog.Password;
        String LoginString = MainLogin.objLog.LoginString;

        JSONObject para = new JSONObject();

        try {
            para.put("FunctionName", "GetWareHouseList");
            para.put("CompanyCode", MainLogin.objLog.CompanyCode);
            para.put("STOrgCode", MainLogin.objLog.STOrgCode);
            para.put("TableName", "warehouse");

            JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "");
            if (rev == null) {
                // ����ͨѶ����
                showToast(this, "��������ͨѶ����");
                SoundHelper.playWarning();
                return;
            }
            if (rev.getBoolean("Status")) {
                JSONArray  val  = rev.getJSONArray("warehouse");
                JSONObject temp = new JSONObject();
                temp.put("warehouse", val);
                Intent ViewGrid = new Intent(this, ListWarehouse.class);
                ViewGrid.putExtra("myData", temp.toString());
                startActivityForResult(ViewGrid, 97);
            } else {
                String Errmsg = rev.getString("ErrMsg");
                showToast(this, Errmsg);
                SoundHelper.playWarning();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showToast(this, e.getMessage());
            SoundHelper.playWarning();
        } catch (IOException e) {
            e.printStackTrace();
            showToast(this, e.getMessage());
            SoundHelper.playWarning();
        }
    }

    /**
     * ��ȡ�����б���Ϣ����������
     */
    private void btnReferDepartment() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetDeptList");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("TableName", "department");
        RequestThread requestThread = new RequestThread(parameter, mHandler, HANDER_DEPARTMENT);
        Thread        td            = new Thread(requestThread);
        td.start();
    }

    /**
     * ��ȡ�����֯���յ���������
     */
    private void btnReferSTOrgList() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetSTOrgList");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("TableName", "STOrg");
        RequestThread requestThread = new RequestThread(parameter, mHandler, HANDER_STORG);
        Thread        td            = new Thread(requestThread);
        td.start();
    }

    // ���շ������
    private void btnRdclClick(String Code) {
        Intent ViewGrid = new Intent(this, VlistRdcl.class);
        ViewGrid.putExtra("FunctionName", "GetRdcl");
        // ViewGrid.putExtra("AccID", "A");
        // ViewGrid.putExtra("rdflag", "1");
        // ViewGrid.putExtra("rdcode", "202");
        ViewGrid.putExtra("AccID", "");
        ViewGrid.putExtra("rdflag", "1");
        ViewGrid.putExtra("rdcode", "");
        startActivityForResult(ViewGrid, 98);
    }

    private void changeAllEdToEmpty() {
        edBillNum.setText("");
//        edBillDate.setText("");
//        edWh.setText("");
//        edOrg.setText("");
//        edLeiBie.setText("");
//        edDep.setText("");
//        edRemark.setText("");
    }

    /**
     * ����ͷ��Ϣ�Ƿ���ȷ
     */
    private boolean checkSaveInfo() {
        if (checkInfo.size() == 0) {
            showToast(activity, "������Ϣ����ȷ��˶�");
            return false;
        }
        if (TextUtils.isEmpty(edBillNum.getText().toString())) {
            showToast(activity, "���ݺŲ���Ϊ��");
            edBillNum.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(edBillDate.getText().toString())) {
            showToast(activity, "���ڲ���Ϊ��");
            edBillDate.requestFocus();
            return false;
        }
        if (!edWh.getText().toString().equals(checkInfo.get("Warehouse"))) {
            showToast(activity, "�ֿ���Ϣ����ȷ");
            edWh.requestFocus();
            return false;
        }
//        if (!edOrg.getText().toString().equals(checkInfo.get("Organization"))) {
//            showToast(activity, "��֯��Ϣ����ȷ");
//            edOrg.requestFocus();
//            return false;
//        }
        if (!edLeiBie.getText().toString().equals(checkInfo.get("LeiBie"))) {
            showToast(activity, "�շ������Ϣ����ȷ");
            edLeiBie.requestFocus();
            return false;
        }
        if (!edDep.getText().toString().equals(checkInfo.get("Department"))) {
            showToast(activity, "������Ϣ����ȷ");
            edDep.requestFocus();
            return false;
        }
        return true;
    }

    /**
     * ���浥�ݵ�dialog
     */
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���ý���������ʽΪԲ��ת���Ľ�����
        progressDialog.setCancelable(false);// �����Ƿ����ͨ�����Back��ȡ��
        progressDialog.setCanceledOnTouchOutside(false);// �����ڵ��Dialog���Ƿ�ȡ��Dialog������
        // progressDialog.setIcon(R.drawable.ic_launcher);
        // ������ʾ��title��ͼ�꣬Ĭ����û�еģ����û������title�Ļ�ֻ����Icon�ǲ�����ʾͼ���
        progressDialog.setTitle("���浥��");
        progressDialog.setMessage("���ڱ��棬��ȴ�...");
        progressDialog.show();
        Thread thread = new Thread(new Runnable() {
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
        });
        thread.start();
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
     * ���浥����Ϣ
     *
     * @param goodsList
     */
    private void saveInfo(@NonNull List<Goods> goodsList) {
        try {
            final JSONObject table     = new JSONObject();
            JSONObject       tableHead = new JSONObject();
            tableHead.put("VBILLCODE", edBillNum.getText().toString());  //���ݺ�
            tableHead.put("CDISPATCHERID", CDISPATCHERID);              //�շ����code
            tableHead.put("CDPTID", CDPTID);                            //����id
            tableHead.put("CUSER", MainLogin.objLog.UserID);            //�û�id
            tableHead.put("CWAREHOUSEID", CWAREHOUSEID);                //�����֯
            tableHead.put("PK_CALBODY", PK_CALBODY);                    //�ֿ�id
            tableHead.put("PK_CORP", MainLogin.objLog.STOrgCode);       //��֯���
            String login_user = MainLogin.objLog.LoginUser.toString();
            String cuserName  = Base64Encoder.encode(login_user.getBytes("gb2312"));
            tableHead.put("CUSERNAME", cuserName);     //�û�����
            if (edRemark.getText().toString().isEmpty()) {
                edRemark.setText("");
            }
            tableHead.put("VNOTE", edRemark.getText().toString());
            tableHead.put("FREPLENISHFLAG", "N");    //N����,�Ƿ��˻���־λ
            table.put("Head", tableHead);
            JSONObject tableBody = new JSONObject();
            JSONArray  bodyArray = new JSONArray();
            for (Goods c : goodsList) {
                JSONObject object = new JSONObject();
                object.put("CINVBASID", c.getPk_invbasdoc());
                object.put("CINVENTORYID", c.getPk_invmandoc());
                object.put("NOUTNUM", Utils.formatDecimal(c.getQty()));
                object.put("CINVCODE", c.getEncoding());
                object.put("BLOTMGT", "1");
                object.put("COSTOBJECT", c.getPk_invmandoc());
                object.put("PK_BODYCALBODY", PK_CALBODY);
                object.put("PK_CORP", MainLogin.objLog.STOrgCode);
                object.put("VBATCHCODE", c.getLot());
                object.put("VFREE4", c.getManual());    //�����ֲ��
                bodyArray.put(object);
            }
            tableBody.put("ScanDetails", bodyArray);
            table.put("Body", tableBody);
            table.put("GUIDS", UUID.randomUUID().toString());
            table.put("OPDATE", edBillDate.getText().toString());
            Log.d(TAG, "saveInfo: " + table.toString());
            SaveThread saveThread = new SaveThread(table, "SaveOtherOut", mHandler, HANDER_SAVE_RESULT);
            Thread     thread     = new Thread(saveThread);
            thread.start();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    private DatePickerDialog.OnDateSetListener Datelistener    = new DatePickerDialog.OnDateSetListener() {
        /**params��view�����¼����������
         * params��myyear����ǰѡ�����
         * params��monthOfYear����ǰѡ�����
         * params��dayOfMonth����ǰѡ�����
         */
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {
            //�޸�year��month��day�ı���ֵ���Ա��Ժ󵥻���ťʱ��DatePickerDialog����ʾ��һ���޸ĺ��ֵ
            year = myyear;
            month = monthOfYear;
            day = dayOfMonth;
            updateDate();
            edOrg.requestFocus();//ѡ�����ں󽫽����������ֿ��EdText��
        }

        //��DatePickerDialog�ر�ʱ������������ʾ
        private void updateDate() {
            //��TextView����ʾ����
            edBillDate.setText(year + "-" + (month + 1) + "-" + day);
        }

    };
    @NonNull
    private View.OnFocusChangeListener         myFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpd = new DatePickerDialog(activity, Datelistener, year, month, day);
                dpd.show();//��ʾDatePickerDialog���
            }
        }
    };

    /**
     * �س����ĵ���¼�
     */
    @NonNull
    View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(@NonNull View v, int keyCode, @NonNull KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.bill_num:
                        edWh.requestFocus();
                        return true;
                    case R.id.wh:
                        edLeiBie.requestFocus();
                        return true;
//                    case organization:
//                        edLeiBie.requestFocus();
//                        return true;
                    case R.id.lei_bie:
                        edDep.requestFocus();
                        return true;
                }
            }
            return false;
        }
    };
}
