package com.techscan.dvq.module.materialOut;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.techscan.dvq.common.Common;
import com.techscan.dvq.ListWarehouse;
import com.techscan.dvq.R;
import com.techscan.dvq.VlistRdcl;
import com.techscan.dvq.bean.Goods;
import com.techscan.dvq.common.Base64Encoder;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SaveThread;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.login.MainLogin;
import com.techscan.dvq.module.materialOut.scan.MaterialOutScanAct;

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

public class MaterialOutAct extends Activity {


    @InjectView(R.id.bill_num)
    EditText    billNum;
    @InjectView(R.id.bill_date)
    EditText    billDate;
    @InjectView(R.id.wh)
    EditText    wh;
    @InjectView(R.id.refer_wh)
    ImageButton referWh;
    @InjectView(R.id.organization)
    EditText    organization;
    @InjectView(R.id.refer_organization)
    ImageButton referOrganization;
    @InjectView(R.id.lei_bie)
    EditText    leiBie;
    @InjectView(R.id.refer_lei_bie)
    ImageButton referLeiBie;
    @InjectView(R.id.department)
    EditText    department;
    @InjectView(R.id.refer_department)
    ImageButton referDepartment;
    @InjectView(R.id.remark)
    EditText    remark;
    @InjectView(R.id.btn_scan)
    Button      btnScan;
    @InjectView(R.id.btn_save)
    Button      btnSave;
    @InjectView(R.id.btn_back)
    Button      btnBack;

    private String TAG = this.getClass().getSimpleName();

    List<Goods>             tempList;
    HashMap<String, String> checkInfo;

    String CDISPATCHERID     = "";//�շ����code
    String CDPTID            = "";  //����id
    String CWAREHOUSEID      = "";    //�����֯
    String PK_CALBODY        = "";      //�ֿ�id
    String CUSER;   //��¼Ա��id
    String PK_CORP;         //��˾
    String VBILLCOD;        //���ݺ�

    int      year;
    int      month;
    int      day;
    Calendar mycalendar;
    Activity mActivity;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_out);
        ButterKnife.inject(this);
        mActivity = this;
        init();
    }


    /**
     * ���еĵ���¼�
     *
     * @param view
     */
    @OnClick({R.id.bill_date, R.id.btn_scan, R.id.refer_wh, R.id.refer_organization, R.id.refer_lei_bie, R.id.refer_department, R.id.btn_save, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.refer_wh:
                btnWarehouseClick();
                break;
            case R.id.refer_organization:
                btnReferSTOrgList();
                break;
            case R.id.refer_lei_bie:
                btnRdclClick("");
                break;
            case R.id.btn_scan:
                if (isAllEdNotEmpty()) {
                    Intent in = new Intent(mActivity, MaterialOutScanAct.class);
                    in.putExtra("CWAREHOUSEID", CWAREHOUSEID);
                    in.putExtra("PK_CALBODY", PK_CALBODY);
                    startActivityForResult(in, 95);
                    if (tempList != null) {
                        tempList.clear();
                    }
                } else {
                    showToast(mActivity, "���Ⱥ˶���Ϣ���ٽ���ɨ��");
                }
                break;
            case R.id.btn_save:
                if (checkSaveInfo()) {
                    if (tempList != null && tempList.size() > 0) {
                        saveInfo(tempList);
                        showProgressDialog();
                    } else {
                        showToast(mActivity, "û����Ҫ���������");
                    }
                }
                break;
            case R.id.btn_back:
                if (tempList != null && tempList.size() > 0) {
                    AlertDialog.Builder bulider =
                            new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("����δ�����Ƿ��˳�");
                    bulider.setNegativeButton(R.string.QuXiao, null);
                    bulider.setPositiveButton(R.string.QueRen, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MaterialOutScanAct.ovList.clear();
                            MaterialOutScanAct.detailList.clear();
                            dialog.dismiss();
                            finish();
                        }
                    }).create().show();
                } else {
                    MaterialOutScanAct.ovList.clear();
                    MaterialOutScanAct.detailList.clear();
                    finish();
                }
                break;
            case R.id.refer_department:
                btnReferDepartment();
                break;
            case R.id.bill_date:
                year = mycalendar.get(Calendar.YEAR); //��ȡCalendar�����е���
                month = mycalendar.get(Calendar.MONTH);//��ȡCalendar�����е���
                day = mycalendar.get(Calendar.DAY_OF_MONTH);//��ȡ����µĵڼ���
                DatePickerDialog dpd = new DatePickerDialog(mActivity, Datelistener, year, month, day);
                dpd.show();//��ʾDatePickerDialog���
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    /**
     * ����ͷ��Ϣ�Ƿ���ȷ
     */
    private boolean checkSaveInfo() {
        if (checkInfo.size() == 0) {
            showToast(mActivity, "������Ϣ����ȷ��˶�");
            return false;
        }
        if (TextUtils.isEmpty(billNum.getText().toString())) {
            showToast(mActivity, "���ݺŲ���Ϊ��");
            billNum.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(billDate.getText().toString())) {
            showToast(mActivity, "���ڲ���Ϊ��");
            billDate.requestFocus();
            return false;
        }
        if (!wh.getText().toString().equals(checkInfo.get("Warehouse"))) {
            showToast(mActivity, "�ֿ���Ϣ����ȷ");
            wh.requestFocus();
            return false;
        }
        if (!organization.getText().toString().equals(checkInfo.get("Organization"))) {
            showToast(mActivity, "��֯��Ϣ����ȷ");
            organization.requestFocus();
            return false;
        }
        if (!leiBie.getText().toString().equals(checkInfo.get("LeiBie"))) {
            showToast(mActivity, "�շ������Ϣ����ȷ");
            leiBie.requestFocus();
            return false;
        }
        if (!department.getText().toString().equals(checkInfo.get("Department"))) {
            showToast(mActivity, "������Ϣ����ȷ");
            department.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //�ֿ�Ļش����� <----ListWarehouse.class
        if (requestCode == 97 && resultCode == 13) {
            String warehousePK1  = data.getStringExtra("result1");
            String warehousecode = data.getStringExtra("result2");
            String warehouseName = data.getStringExtra("result3");
            CWAREHOUSEID = warehousePK1;
            wh.requestFocus();
            wh.setText(warehouseName);
            organization.requestFocus();
            checkInfo.put("Warehouse", warehouseName);
        }
        //���ϳ�������֯�Ļش����� <----StorgListAct.class
        if (requestCode == 94 && resultCode == 6) {
            String pk_areacl  = data.getStringExtra("pk_areacl");
            String bodyname   = data.getStringExtra("bodyname");
            String pk_calbody = data.getStringExtra("pk_calbody");
            organization.requestFocus();
            organization.setText(bodyname);
            leiBie.requestFocus();
            PK_CALBODY = pk_calbody;
            checkInfo.put("Organization", bodyname);
        }
        // �շ����Ļش����� <----VlistRdcl.class
        if (requestCode == 98 && resultCode == 2) {
            String code  = data.getStringExtra("Code");
            String name  = data.getStringExtra("Name");
            String AccID = data.getStringExtra("AccID");
            String RdIDA = data.getStringExtra("RdIDA");    //��Ҫ�ش���id
            String RdIDB = data.getStringExtra("RdIDB");
            CDISPATCHERID = RdIDA;
            leiBie.requestFocus();
            leiBie.setText(name);
            department.requestFocus();
            checkInfo.put("LeiBie", name);
        }
        //������Ϣ�Ļش����� <----DepartmentListAct.class
        if (requestCode == 96 && resultCode == 4) {
            String deptname   = data.getStringExtra("deptname");
            String pk_deptdoc = data.getStringExtra("pk_deptdoc");
            String deptcode   = data.getStringExtra("deptcode");
            CDPTID = pk_deptdoc;
            department.requestFocus();
            department.setText(deptname);
            checkInfo.put("Department", deptname);
        }

        //ɨ����ϸ�Ļش����� <----MaterialOutScanAct.class
        if (requestCode == 95 && resultCode == 5) {
            Bundle bundle = data.getExtras();
            tempList = bundle.getParcelableArrayList("overViewList");
        }

    }

    @Override
    public void onBackPressed() {

    }

    /**
     * ��ʼ������
     * ��ʼ��ԭʼ����
     */
    private void init() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("���ϳ���");
        mycalendar = Calendar.getInstance();//��ʼ��Calendar��������
        year = mycalendar.get(Calendar.YEAR); //��ȡCalendar�����е���
        month = mycalendar.get(Calendar.MONTH);//��ȡCalendar�����е���
        day = mycalendar.get(Calendar.DAY_OF_MONTH);//��ȡ����µĵڼ���
        billDate.setText(MainLogin.appTime);
        billDate.setInputType(InputType.TYPE_NULL);
        billDate.setOnFocusChangeListener(myFocusListener);
        billDate.setOnKeyListener(mOnKeyListener);
        wh.setOnKeyListener(mOnKeyListener);
        organization.setOnKeyListener(mOnKeyListener);
        leiBie.setOnKeyListener(mOnKeyListener);
        department.setOnKeyListener(mOnKeyListener);
        checkInfo = new HashMap<String, String>();
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
                case HANDER_DEPARTMENT:
                    JSONObject json = (JSONObject) msg.obj;
                    try {
                        if (json.getBoolean("Status")) {
                            JSONArray  val  = json.getJSONArray("department");
                            JSONObject temp = new JSONObject();
                            temp.put("department", val);
                            Intent ViewGrid = new Intent(mActivity, DepartmentListAct.class);
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
                            Intent StorgList = new Intent(mActivity, StorgListAct.class);
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
                                showResultDialog(mActivity, saveResult.getString("ErrMsg"));
                                MaterialOutScanAct.ovList.clear();
                                MaterialOutScanAct.detailList.clear();
                                tempList.clear();
                                changeAllEdToEmpty();
                                billNum.requestFocus();
                            } else {
                                showResultDialog(mActivity, saveResult.getString("ErrMsg"));
                            }
                        } else {
                            showResultDialog(mActivity, "�����ύʧ��!");
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

    private void changeAllEdToEmpty() {
        billNum.setText("");
        //
//        billDate.setText("");
//        wh.setText("");
//        organization.setText("");
//        leiBie.setText("");
//        department.setText("");
//        remark.setText("");
    }

    /**
     * ���浥�ݵ�dialog
     */
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(mActivity);
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

    private boolean isAllEdNotEmpty() {
        return (!TextUtils.isEmpty(billNum.getText().toString())
                && !TextUtils.isEmpty(billDate.getText().toString())
                && !TextUtils.isEmpty(wh.getText().toString())
                && !TextUtils.isEmpty(organization.getText().toString())
                && !TextUtils.isEmpty(leiBie.getText().toString())
                && !TextUtils.isEmpty(department.getText().toString()));
    }

    /**
     * ���浥����Ϣ
     *
     * @param goodsList
     */
    private void saveInfo(List<Goods> goodsList) {

        try {
            final JSONObject table     = new JSONObject();
            JSONObject       tableHead = new JSONObject();
            tableHead.put("VBILLCODE", billNum.getText().toString());  //���ݺ�
            tableHead.put("CDISPATCHERID", CDISPATCHERID);              //�շ����code
            tableHead.put("CDPTID", CDPTID);                            //����id
            tableHead.put("CUSER", MainLogin.objLog.UserID);            //�û�id
            tableHead.put("CWAREHOUSEID", CWAREHOUSEID);                //�����֯
            tableHead.put("PK_CALBODY", PK_CALBODY);                    //�ֿ�id
            tableHead.put("PK_CORP", MainLogin.objLog.STOrgCode);       //��֯���
            String login_user = MainLogin.objLog.LoginUser.toString();
            String cuserName  = Base64Encoder.encode(login_user.getBytes("gb2312"));
            tableHead.put("CUSERNAME", cuserName);     //�û�����
            if (remark.getText().toString().isEmpty()) {
                remark.setText("");
            }
            tableHead.put("VNOTE", remark.getText().toString());
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
                object.put("COSTOBJECT", c.getPk_invmandoc_cost());
                object.put("PK_BODYCALBODY", PK_CALBODY);
                object.put("PK_CORP", MainLogin.objLog.STOrgCode);
                object.put("VBATCHCODE", c.getLot());
                object.put("VFREE4", c.getManual());    //�����ֲ��
                bodyArray.put(object);
            }
            tableBody.put("ScanDetails", bodyArray);
            table.put("Body", tableBody);
            table.put("GUIDS", UUID.randomUUID().toString());
            table.put("OPDATE", MainLogin.appTime);
            Log.d(TAG, "saveInfo: " + table.toString());
            SaveThread saveThread = new SaveThread(table, "SaveMaterialOut", mHandler, HANDER_SAVE_RESULT);
            Thread     thread     = new Thread(saveThread);
            thread.start();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
//            if (!MainLogin.getwifiinfo()) {
//                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
//                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                return;
//            }

            JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "");
            if (rev == null) {
                // ����ͨѶ����
                Utils.showToast(this, "��������ͨѶ����");
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
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
                Toast.makeText(this, Errmsg, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showToast(this, e.getMessage());
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        } catch (IOException e) {
            e.printStackTrace();
            Utils.showToast(this, e.getMessage());
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        }
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
            wh.requestFocus();//ѡ�����ں󽫽����������ֿ��EdText��
        }

        //��DatePickerDialog�ر�ʱ������������ʾ
        private void updateDate() {
            //��TextView����ʾ����
            billDate.setText(year + "-" + (month + 1) + "-" + day);
        }

    };
    private View.OnFocusChangeListener         myFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpd = new DatePickerDialog(mActivity, Datelistener, year, month, day);
                dpd.show();//��ʾDatePickerDialog���
            }
        }
    };

    /**
     * �س����ĵ���¼�
     */
    View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.bill_num:
                        billDate.requestFocus();
                        return true;
                    case R.id.wh:
                        organization.requestFocus();
                        return true;
                    case R.id.organization:
                        leiBie.requestFocus();
                        return true;
                    case R.id.lei_bie:
                        department.requestFocus();
                        return true;
                }
            }
            return false;
        }
    };

}
