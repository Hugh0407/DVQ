package com.techscan.dvq.module.productIn;

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
import android.widget.Toast;

import com.techscan.dvq.ListWarehouse;
import com.techscan.dvq.R;
import com.techscan.dvq.VlistRdcl;
import com.techscan.dvq.bean.Goods;
import com.techscan.dvq.common.Base64Encoder;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SaveThread;
import com.techscan.dvq.common.SoundHelper;
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
import static com.techscan.dvq.common.Utils.formatDecimal;
import static com.techscan.dvq.common.Utils.showResultDialog;
import static com.techscan.dvq.common.Utils.showToast;

public class ProductInAct extends Activity {


    @Nullable
    @InjectView(R.id.bill_num)
    EditText    mBillNum;
    @Nullable
    @InjectView(R.id.bill_date)
    EditText    mBillDate;
    @Nullable
    @InjectView(R.id.wh)
    EditText    mWh;
    @Nullable
    @InjectView(R.id.refer_wh)
    ImageButton mReferWh;
    @Nullable
    @InjectView(R.id.organization)
    EditText    mOrganization;
    @Nullable
    @InjectView(R.id.refer_organization)
    ImageButton mReferOrganization;
    @Nullable
    @InjectView(R.id.lei_bie)
    EditText    mLeiBie;
    @Nullable
    @InjectView(R.id.refer_lei_bie)
    ImageButton mReferLeiBie;
    @Nullable
    @InjectView(R.id.department)
    EditText    mDepartment;
    @Nullable
    @InjectView(R.id.remark)
    EditText    mRemark;
    @Nullable
    @InjectView(R.id.btnPurInScan)
    Button      mBtnPurInScan;
    @Nullable
    @InjectView(R.id.btnPurinSave)
    Button      mBtnPurinSave;
    @Nullable
    @InjectView(R.id.btnBack)
    Button      mBtnBack;
    @Nullable
    @InjectView(R.id.refer_department)
    ImageButton mReferDepartment;

    private String TAG = this.getClass().getSimpleName();
    String CDISPATCHERID = "";//收发类别code

    String CDPTID = "";  //部门id
    String CUSER;   //登录员工id
    String CWAREHOUSEID = "";    //库存组织
    String PK_CALBODY   = "";    //库存组织 id
    String PK_CORP;         //公司
    String VBILLCOD;        //单据号
    int    year;

    int      month;
    int      day;
    Calendar mycalendar;
    @Nullable
    List<Goods> tempList;
    HashMap<String, String> checkInfo;
    @Nullable
    ProgressDialog progressDialog;
    @Nullable
    Activity       mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_in);
        ButterKnife.inject(this);
        mActivity = this;
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    /**
     * 所有的点击事件
     *
     * @param view
     */
    @OnClick({R.id.refer_wh, R.id.refer_organization,
            R.id.refer_lei_bie, R.id.btnPurInScan, R.id.btnPurinSave,
            R.id.btnBack, R.id.refer_department, R.id.bill_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.refer_wh:
                btnWarehouseClick();
                break;
            case R.id.refer_organization:
                btnReferSTOrgList();
                break;
            case R.id.refer_lei_bie:
                btnRdclClick();
                break;
            case R.id.btnPurInScan:
                if (checkSaveInfo()) {
                    Intent in = new Intent(mActivity, ProductInScanAct.class);
                    startActivityForResult(in, 95);
                    if (tempList != null) {
                        tempList.clear();
                    }
                }
                break;
            case R.id.btnPurinSave:
                if (checkSaveInfo()) {
                    if (tempList != null && tempList.size() > 0) {
                        saveInfo(tempList);
                        showProgressDialog();
                    } else {
                        showToast(mActivity, "没有需要保存的数据");
                    }
                }
                break;
            case R.id.btnBack:
                if (tempList != null && tempList.size() > 0) {
                    AlertDialog.Builder bulider =
                            new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("数据未保存是否退出");
                    bulider.setNegativeButton(R.string.QuXiao, null);
                    bulider.setPositiveButton(R.string.QueRen, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ProductInScanAct.ovList.clear();
                            ProductInScanAct.detailList.clear();
                            dialog.dismiss();
                            finish();
                        }
                    }).create().show();
                } else {
                    ProductInScanAct.ovList.clear();
                    ProductInScanAct.detailList.clear();
                    finish();
                }
                break;
            case R.id.refer_department:
                btnReferDepartment();
                break;
            case R.id.bill_date:
                year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
                month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
                day = mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
                DatePickerDialog dpd = new DatePickerDialog(mActivity, Datelistener, year, month, day);
                dpd.show();//显示DatePickerDialog组件
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //仓库的回传数据 <----ListWarehouse.class
        if (requestCode == 97 && resultCode == 13) {
            String warehousePK1  = data.getStringExtra("result1");
            String warehousecode = data.getStringExtra("result2");
            String warehouseName = data.getStringExtra("result3");
            CWAREHOUSEID = warehousePK1;
            mWh.setText(warehouseName);
            mLeiBie.requestFocus();
            checkInfo.put("Warehouse", warehouseName);
        }
        //材料出库库存组织的回传数据 <----StorgListAct.class
        if (requestCode == 94 && resultCode == 6) {
            String pk_areacl  = data.getStringExtra("pk_areacl");
            String bodyname   = data.getStringExtra("bodyname");
            String pk_calbody = data.getStringExtra("pk_calbody");
            PK_CALBODY = pk_calbody;
            mOrganization.setText(bodyname);
            mWh.requestFocus();
            checkInfo.put("Organization", bodyname);
        }
        // 收发类别的回传数据 <----VlistRdcl.class
        if (requestCode == 98 && resultCode == 2) {
            String code  = data.getStringExtra("Code");
            String name  = data.getStringExtra("Name");
            String AccID = data.getStringExtra("AccID");
            String RdIDA = data.getStringExtra("RdIDA");    //需要回传的id
            String RdIDB = data.getStringExtra("RdIDB");
            CDISPATCHERID = RdIDA;
            mLeiBie.setText(name);
            mDepartment.requestFocus();
            checkInfo.put("LeiBie", name);
        }
        //部门信息的回传数据 <----DepartmentListAct.class
        if (requestCode == 96 && resultCode == 4) {
            String deptname   = data.getStringExtra("deptname");
            String pk_deptdoc = data.getStringExtra("pk_deptdoc");
            String deptcode   = data.getStringExtra("deptcode");
            CDPTID = pk_deptdoc;
            mDepartment.requestFocus();
            mDepartment.setText(deptname);
            checkInfo.put("Department", deptname);
        }

        //扫描明细的回传数据 <----MaterialOutScanAct.class
        if (requestCode == 95 && resultCode == 5) {
            Bundle bundle = data.getExtras();
            tempList = bundle.getParcelableArrayList("overViewList");
        }

    }

    /**
     * 初始化界面
     * 初始化原始数据
     */
    private void initView() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("成品入库");
        mycalendar = Calendar.getInstance();//初始化Calendar日历对象
        year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
        month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
        day = mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
        mBillDate.setOnFocusChangeListener(myFocusListener);
        mBillDate.setOnKeyListener(mOnKeyListener);
        mBillDate.setInputType(InputType.TYPE_NULL);
        mBillDate.setText(MainLogin.appTime);
        mWh.setOnKeyListener(mOnKeyListener);
        mOrganization.setOnKeyListener(mOnKeyListener);
        mLeiBie.setOnKeyListener(mOnKeyListener);
        mDepartment.setOnKeyListener(mOnKeyListener);
        checkInfo = new HashMap<String, String>();
    }

    /**
     * 网络请求后的线程通信
     * msg.obj 是从子线程传递过来的数据
     */
    @Nullable
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDER_DEPARTMENT:
                    JSONObject json = (JSONObject) msg.obj;
                    try {
                        if (json != null && json.getBoolean("Status")) {
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
                        if (storg != null && storg.getBoolean("Status")) {
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
                                Log.d(TAG, "保存" + saveResult.toString());
                                showResultDialog(mActivity, saveResult.getString("ErrMsg"));
                                tempList.clear();
                                ProductInScanAct.ovList.clear();
                                ProductInScanAct.detailList.clear();
                                setBarCodeToEmpty();
                                mBillNum.requestFocus();
                            } else {
                                showResultDialog(mActivity, saveResult.getString("ErrMsg"));
                            }
                        } else {
                            showResultDialog(mActivity, "数据提交失败!");
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
     * 检查表头信息是否正确
     */
    private boolean checkSaveInfo() {

        if (checkInfo.size() == 0) {
            showToast(mActivity, "单据信息不正确请核对");
            return false;
        }

//        if (TextUtils.isEmpty(mBillNum.getText().toString())) {
//            showToast(mActivity, "单据号不能为空");
//            mBillNum.requestFocus();
//            return false;
//        }

        if (TextUtils.isEmpty(mBillDate.getText().toString())) {
            showToast(mActivity, "日期不能为空");
            mBillDate.requestFocus();
            return false;
        }
        if (!mWh.getText().toString().equals(checkInfo.get("Warehouse"))) {
            showToast(mActivity, "仓库信息不正确");
            mWh.requestFocus();
            return false;
        }
        if (!mOrganization.getText().toString().equals(checkInfo.get("Organization"))) {
            showToast(mActivity, "组织信息不正确");
            mOrganization.requestFocus();
            return false;
        }
        if (!mLeiBie.getText().toString().equals(checkInfo.get("LeiBie"))) {
            showToast(mActivity, "收发类别信息不正确");
            mLeiBie.requestFocus();
            return false;
        }
        if (!mDepartment.getText().toString().equals(checkInfo.get("Department"))) {
            showToast(mActivity, "部门信息不正确");
            mDepartment.requestFocus();
            return false;
        }
        return true;
    }


    /**
     * 保存单据信息
     *
     * @param goodsList
     * @throws JSONException
     */
    private void saveInfo(List<Goods> goodsList) {
        final JSONObject table     = new JSONObject();
        JSONObject       tableHead = new JSONObject();
        try {
            tableHead.put("CDISPATCHERID", CDISPATCHERID);
            tableHead.put("CDPTID", CDPTID);
            tableHead.put("CUSER", MainLogin.objLog.UserID);
            tableHead.put("CWAREHOUSEID", CWAREHOUSEID);
            tableHead.put("PK_CALBODY", PK_CALBODY);
            tableHead.put("PK_CORP", MainLogin.objLog.STOrgCode);
            tableHead.put("VBILLCODE", mBillNum.getText().toString());
            String login_user = MainLogin.objLog.LoginUser.toString();
            String cuserName  = Base64Encoder.encode(login_user.getBytes("gb2312"));
            tableHead.put("CUSERNAME", cuserName);
            if (mRemark.getText().toString().isEmpty()) {
                mRemark.setText("");
            }
            tableHead.put("VNOTE", mRemark.getText().toString());
            tableHead.put("FREPLENISHFLAG", "N");    //N不退，Y退
            table.put("Head", tableHead);
            JSONObject tableBody = new JSONObject();
            JSONArray  bodyArray = new JSONArray();
            for (Goods c : goodsList) {
                JSONObject object = new JSONObject();
                object.put("CINVBASID", c.getPk_invbasdoc());
                object.put("CINVENTORYID", c.getPk_invmandoc());
                object.put("WGDATE", mBillDate.getText().toString());    //LEO要求，将时间添加到表体上
                object.put("NINNUM", formatDecimal(c.getQty()));
                object.put("CINVCODE", c.getEncoding());
                object.put("BLOTMGT", "1");
                object.put("PK_BODYCALBODY", PK_CALBODY);
                object.put("PK_CORP", MainLogin.objLog.STOrgCode);
                object.put("VBATCHCODE", c.getLot());
                object.put("VFREE4", c.getManual());        //海关手册号
                object.put("VFREE5", c.getProductLot());    //生产批次
                bodyArray.put(object);
            }
            tableBody.put("ScanDetails", bodyArray);
            table.put("Body", tableBody);
            table.put("GUIDS", UUID.randomUUID().toString());
            table.put("OPDATE", mBillDate.getText().toString());
            JSONArray  jsonArray = new JSONArray();
            JSONObject jsonOb;
            for (Goods good : ProductInScanAct.detailList) {
                if (good.isDoPacked()) {
                    jsonOb = new JSONObject();
                    jsonOb.put("BARCODE", good.getBarcode());
                    jsonOb.put("INVCODE", good.getEncoding());
                    jsonOb.put("OPQTY", formatDecimal(good.getQty()));
                    jsonOb.put("BARQTY", good.getBarQty());
                    jsonOb.put("BARCODETYPE", good.getCodeType());
                    jsonOb.put("GUIDS", UUID.randomUUID().toString());
                    jsonArray.put(jsonOb);
                }
            }
            table.put("JAY", jsonArray);
            Log.d(TAG, "saveInfo: " + table.toString());
            SaveThread saveThread = new SaveThread(table, "SavePrdStockIn", mHandler, HANDER_SAVE_RESULT);
            Thread     thread     = new Thread(saveThread);
            thread.start();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    // 打开收发类别画面
    private void btnRdclClick() {
        Intent ViewGrid = new Intent(this, VlistRdcl.class);
        ViewGrid.putExtra("FunctionName", "GetRdcl");
        // ViewGrid.putExtra("AccID", "A");
        // ViewGrid.putExtra("rdflag", "1");
        // ViewGrid.putExtra("rdcode", "202");
        ViewGrid.putExtra("AccID", "");
        ViewGrid.putExtra("rdflag", "0");   //0 ----》入库  1----》出库
        ViewGrid.putExtra("rdcode", "");
        startActivityForResult(ViewGrid, 98);
    }

    /**
     * 点击仓库列表参照
     *
     * @throws JSONException
     */
    private void btnWarehouseClick() {

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
                // 网络通讯错误
                Toast.makeText(this, "错误！网络通讯错误", Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
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
                showToast(mActivity, Errmsg);
                SoundHelper.playWarning();
            }

        } catch (JSONException e) {
            e.printStackTrace();
            showToast(mActivity, e.getMessage());
            SoundHelper.playWarning();
        } catch (IOException e) {
            e.printStackTrace();
            showToast(mActivity, e.getMessage());
            SoundHelper.playWarning();
        }
    }


    /**
     * 获取库存组织参照的网络请求
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
     * 获取部门列表信息的网络请求
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


    private void setBarCodeToEmpty() {
        mBillNum.setText("");
//        mBillDate.setText("");
//        mWh.setText("");
//        mOrganization.setText("");
//        mLeiBie.setText("");
//        mDepartment.setText("");
//        mRemark.setText("");
    }

    /**
     * 保存单据的dialog
     */
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        // progressDialog.setIcon(R.drawable.ic_launcher);
        // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
        progressDialog.setTitle("保存单据");
        progressDialog.setMessage("正在保存，请等待...");
        progressDialog.show();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (progressDialog.isShowing()) {
                        Thread.sleep(30 * 1000);
                        // cancel和dismiss方法本质都是一样的，都是从屏幕中删除Dialog,唯一的区别是
                        // 调用cancel方法会回调DialogInterface.OnCancelListener如果注册的话,dismiss方法不会回掉
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
     * progressDialog 消失
     */
    private void progressDialogDismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
        /**params：view：该事件关联的组件
         * params：myyear：当前选择的年
         * params：monthOfYear：当前选择的月
         * params：dayOfMonth：当前选择的日
         */
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {
            //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
            year = myyear;
            month = monthOfYear;
            day = dayOfMonth;
            updateDate();
            mOrganization.requestFocus(); //选择日期后将焦点跳到“仓库的EdText”
        }

        //当DatePickerDialog关闭时，更新日期显示
        private void updateDate() {
            //在TextView上显示日期
            mBillDate.setText(year + "-" + (month + 1) + "-" + day);
        }

    };

    @Nullable
    private View.OnFocusChangeListener myFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpd = new DatePickerDialog(mActivity, Datelistener, year, month, day);
                dpd.show();//显示DatePickerDialog组件
            }
        }
    };
    /**
     * 回车键的点击事件
     */

    View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.bill_num:
                        mWh.requestFocus();
                        return true;
                    case R.id.wh:
                        mLeiBie.requestFocus();
                        return true;
//                    case R.id.organization:
//                        mLeiBie.requestFocus();
//                        return true;
                    case R.id.lei_bie:
                        mDepartment.requestFocus();
                        return true;
                }
            }
            return false;
        }
    };
}

