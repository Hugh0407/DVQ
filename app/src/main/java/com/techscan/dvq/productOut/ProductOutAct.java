package com.techscan.dvq.productOut;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.techscan.dvq.Common;
import com.techscan.dvq.ListWarehouse;
import com.techscan.dvq.MainLogin;
import com.techscan.dvq.R;
import com.techscan.dvq.VlistRdcl;
import com.techscan.dvq.bean.Goods;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SaveThread;
import com.techscan.dvq.materialOut.DepartmentListAct;
import com.techscan.dvq.materialOut.StorgListACt;
import com.techscan.dvq.materialOut.scan.MaterialOutScanAct;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
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

public class ProductOutAct extends Activity {


    @InjectView(R.id.bill_num)
    EditText mBillNum;
    @InjectView(R.id.refer_bill_num)
    ImageButton mReferBillNum;
    @InjectView(R.id.bill_date)
    EditText mBillDate;
    @InjectView(R.id.wh)
    EditText mWh;
    @InjectView(R.id.refer_wh)
    ImageButton mReferWh;
    @InjectView(R.id.organization)
    EditText mOrganization;
    @InjectView(R.id.refer_organization)
    ImageButton mReferOrganization;
    @InjectView(R.id.lei_bie)
    EditText mLeiBie;
    @InjectView(R.id.refer_lei_bie)
    ImageButton mReferLeiBie;
    @InjectView(R.id.department)
    EditText mDepartment;
    @InjectView(R.id.remark)
    EditText mRemark;
    @InjectView(R.id.btnPurInScan)
    Button mBtnPurInScan;
    @InjectView(R.id.btnPurinSave)
    Button mBtnPurinSave;
    @InjectView(R.id.btnBack)
    Button mBtnBack;
    @InjectView(R.id.refer_department)
    ImageButton mReferDepartment;
    private String TAG = this.getClass().getSimpleName();
    List<Goods> tempList;

    String CDISPATCHERID = "";//�շ����code
    String CDPTID = "";  //����id
    String CUSER;   //��¼Ա��id
    String CWAREHOUSEID = "";    //�����֯
    String PK_CALBODY = "";      //�ֿ�id
    String PK_CORP;         //��˾
    String VBILLCOD;        //���ݺ�

    int year;
    int month;
    int day;
    Calendar mycalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_out);
        ButterKnife.inject(this);
        initView();
    }

    /**
     * ���еĵ���¼�
     *
     * @param view
     */
    @OnClick({R.id.refer_bill_num, R.id.refer_wh, R.id.refer_organization,
            R.id.refer_lei_bie, R.id.btnPurInScan, R.id.btnPurinSave,
            R.id.btnBack, R.id.refer_department, R.id.bill_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.refer_bill_num:
                break;
            case R.id.refer_wh:
                try {
                    btnWarehouseClick();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.refer_organization:
                btnReferSTOrgList();
                break;
            case R.id.refer_lei_bie:
                try {
                    btnRdclClick("");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnPurInScan:
                Intent in = new Intent(ProductOutAct.this, MaterialOutScanAct.class);
                startActivityForResult(in, 95);
                break;
            case R.id.btnPurinSave:
                if (tempList != null && tempList.size() > 0) {
                    try {
                        SaveInfo(tempList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.btnBack:
                if (tempList != null && tempList.size() > 0) {
                    AlertDialog.Builder bulider =
                            new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("����δ�����Ƿ��˳�");
                    bulider.setNegativeButton(R.string.QuXiao, null);
                    bulider.setPositiveButton(R.string.QueRen, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).create().show();
                } else {
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
                DatePickerDialog dpd = new DatePickerDialog(ProductOutAct.this, Datelistener, year, month, day);
                dpd.show();//��ʾDatePickerDialog���
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //�ֿ�Ļش����� <----ListWarehouse.class
        if (requestCode == 97 && resultCode == 13) {
            String warehousePK1 = data.getStringExtra("result1");
            String warehousecode = data.getStringExtra("result2");
            String warehouseName = data.getStringExtra("result3");
            CWAREHOUSEID = warehousePK1;
            mWh.setText(warehouseName);
        }
        // �շ����Ļش����� <----VlistRdcl.class
        if (requestCode == 98 && resultCode == 2) {
            String code = data.getStringExtra("Code");
            String name = data.getStringExtra("Name");
            String AccID = data.getStringExtra("AccID");
            String RdIDA = data.getStringExtra("RdIDA");    //��Ҫ�ش���id
            String RdIDB = data.getStringExtra("RdIDB");
            CDISPATCHERID = RdIDA;
            mLeiBie.setText(name);
        }
        //������Ϣ�Ļش����� <----DepartmentListAct.class
        if (requestCode == 96 && resultCode == 4) {
            String deptname = data.getStringExtra("deptname");
            String pk_deptdoc = data.getStringExtra("pk_deptdoc");
            String deptcode = data.getStringExtra("deptcode");
            CDPTID = pk_deptdoc;
            mDepartment.setText(deptname);
        }

        //ɨ����ϸ�Ļش����� <----MaterialOutScanAct.class
        if (requestCode == 95 && resultCode == 5) {
            Bundle bundle = data.getExtras();
            tempList = bundle.getParcelableArrayList("overViewList");
        }

        //���ϳ�������֯�Ļش����� <----StorgListACt.class
        if (requestCode == 94 && resultCode == 6) {
            String pk_areacl = data.getStringExtra("pk_areacl");
            String bodyname = data.getStringExtra("bodyname");
            String pk_calbody = data.getStringExtra("pk_calbody");
            mOrganization.setText(bodyname);
            PK_CALBODY = pk_calbody;
        }
    }

    /**
     * ��ʼ������
     * ��ʼ��ԭʼ����
     */
    private void initView() {
//        mOrganization.setText("C00");   // TODO: 2017/6/21 ��ʱĬ������
//        mLeiBie.setText("0105");
//        mDepartment.setText("������");
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("��Ʒ���");
        mycalendar = Calendar.getInstance();//��ʼ��Calendar��������
        year = mycalendar.get(Calendar.YEAR); //��ȡCalendar�����е���
        month = mycalendar.get(Calendar.MONTH);//��ȡCalendar�����е���
        day = mycalendar.get(Calendar.DAY_OF_MONTH);//��ȡ����µĵڼ���
        mBillDate.setOnFocusChangeListener(myFocusListener);
        mBillDate.setOnKeyListener(mOnKeyListener);
        mWh.setOnKeyListener(mOnKeyListener);
        mOrganization.setOnKeyListener(mOnKeyListener);
        mLeiBie.setOnKeyListener(mOnKeyListener);
        mDepartment.setOnKeyListener(mOnKeyListener);
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
                            JSONArray val = json.getJSONArray("department");
                            JSONObject temp = new JSONObject();
                            temp.put("department", val);
                            Intent ViewGrid = new Intent(ProductOutAct.this, DepartmentListAct.class);
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
                            JSONArray val = storg.getJSONArray("STOrg");
                            JSONObject temp = new JSONObject();
                            temp.put("STOrg", val);
                            Intent StorgList = new Intent(ProductOutAct.this, StorgListACt.class);
                            StorgList.putExtra("STOrg", temp.toString());
                            startActivityForResult(StorgList, 94);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case HANDER_SAVE_RESULT:
                    JSONObject saveResult = (JSONObject) msg.obj;
                    if (saveResult != null) {
                        Log.d(TAG, "������:" + saveResult.toString());
                    } else {
                        Log.d(TAG, "null");
                    }
                    break;
                default:
                    break;
            }
        }
    };


    /**
     * ���浥����Ϣ
     *
     * @param goodsList
     * @throws JSONException
     */
    private void SaveInfo(List<Goods> goodsList) throws JSONException {
        final JSONObject table = new JSONObject();
        JSONObject tableHead = new JSONObject();
        tableHead.put("CDISPATCHERID", CDISPATCHERID);
        tableHead.put("CDPTID", CDPTID);
        tableHead.put("CUSER", MainLogin.objLog.UserID);
        tableHead.put("CWAREHOUSEID", CWAREHOUSEID);
        tableHead.put("PK_CALBODY", PK_CALBODY);
        tableHead.put("PK_CORP", MainLogin.objLog.STOrgCode);
        tableHead.put("VBILLCODE", mBillNum.getText().toString());
        table.put("Head", tableHead);
        JSONObject tableBody = new JSONObject();
        JSONArray bodyArray = new JSONArray();
        for (Goods c : goodsList) {
            JSONObject object = new JSONObject();
            object.put("CINVBASID", c.getPk_invbasdoc());
            object.put("CINVENTORYID", c.getPk_invmandoc());
            object.put("WGDATE", mBillDate.getText().toString());    //LEOҪ�󣬽�ʱ����ӵ�������
            float c_2 = c.getQty();
            DecimalFormat decimalFormat = new DecimalFormat(".00"); //���췽�����ַ���ʽ�������С������2λ,����0����.
            String qty = decimalFormat.format(c_2);                 //format ���ص����ַ���

            object.put("NOUTNUM", qty);
            object.put("PK_BODYCALBODY", PK_CALBODY);
            object.put("PK_CORP", MainLogin.objLog.STOrgCode);
            object.put("VBATCHCODE", c.getLot());
            bodyArray.put(object);
        }
        tableBody.put("ScanDetails", bodyArray);
        table.put("Body", tableBody);
        table.put("GUIDS", UUID.randomUUID().toString());
        Log.d(TAG, "SaveInfo: " + table.toString());

        SaveThread saveThread = new SaveThread(table, "SaveMaterialOut", mHandler, HANDER_SAVE_RESULT);
        Thread thread = new Thread(saveThread);
        thread.start();
    }

    // ���շ������
    private void btnRdclClick(String Code) throws ParseException, IOException, JSONException {
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
    private void btnWarehouseClick() throws JSONException {
        String lgUser = MainLogin.objLog.LoginUser;
        String lgPwd = MainLogin.objLog.Password;
        String LoginString = MainLogin.objLog.LoginString;

        JSONObject para = new JSONObject();

        para.put("FunctionName", "GetWareHouseList");
        para.put("CompanyCode", MainLogin.objLog.CompanyCode);
        para.put("STOrgCode", MainLogin.objLog.STOrgCode);
        para.put("TableName", "warehouse");

        try {
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }

            JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "");
            if (rev == null) {
                // ����ͨѶ����
                Toast.makeText(this, "��������ͨѶ����", Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
                return;
            }
            if (rev.getBoolean("Status")) {
                JSONArray val = rev.getJSONArray("warehouse");

                JSONObject temp = new JSONObject();
                temp.put("warehouse", val);

                Intent ViewGrid = new Intent(this, ListWarehouse.class);
                ViewGrid.putExtra("myData", temp.toString());

                startActivityForResult(ViewGrid, 97);
            } else {
                String Errmsg = rev.getString("ErrMsg");
                Toast.makeText(this, Errmsg, Toast.LENGTH_LONG).show();
                // ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                // ADD CAIXY TEST END
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            // ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            // ADD CAIXY TEST END
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
        Thread td = new Thread(requestThread);
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
        Thread td = new Thread(requestThread);
        td.start();
    }


    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
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
            mWh.requestFocus(); //ѡ�����ں󽫽����������ֿ��EdText��
        }

        //��DatePickerDialog�ر�ʱ������������ʾ
        private void updateDate() {
            //��TextView����ʾ����
            mBillDate.setText(year + "-" + (month + 1) + "-" + day);
        }

    };

    private View.OnFocusChangeListener myFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpd = new DatePickerDialog(ProductOutAct.this, Datelistener, year, month, day);
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
                        mBillDate.requestFocus();
                        return true;
                    case R.id.wh:
                        mOrganization.requestFocus();
                        return true;
                    case R.id.organization:
                        mLeiBie.requestFocus();
                        return true;
                    case R.id.lei_bie:
                        mDepartment.requestFocus();
                        return true;
                }
            }
            return false;
        }
    };
}

