package com.techscan.dvq;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.bean.SaleOutGoods;
import com.techscan.dvq.common.SaveThread;
import com.techscan.dvq.common.Utils;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;
import static com.techscan.dvq.common.Utils.HANDER_SAVE_RESULT;
import static com.techscan.dvq.common.Utils.showResultDialog;


public class SalesDelivery extends Activity {
    String NTOTALNUMBER = "";
    String  CCUSTBASDOCID ="";
    String  CRECEIVECUSTBASID ="";
    String CCUSTOMERID = "";
     String CBIZTYPE = "";
    String  CSALECORPID = "";
     String PK_CORP = "";
    String  VDEF1 = "";
    String VDEF2 = "";
    String VDEF5 = "";
    String NOTOTALNUMBER = "";
    String CWAREHOUSEID = "";    //库存组织
    String CSENDWAREID = "";
    String sBillCodes = "";//单据号查询
    String sBeginDate = "";//制单日期开始查询
    String sEndDate = "";//制单日期结束查询
    String CheckBillCode = "";
    JSONObject table = null;
    JSONObject jsBody = null;
    JSONObject jsBoxTotal = null;
    JSONObject jsSerino = null;
    List<SaleOutGoods> saleOutGoodsLists = null;
    String tmpWHStatus = "";//仓库是否启用货位
    EditText txtSalesDelPDOrder;
    private writeTxt writeTxt;
    EditText txtSalesDelRdcl;//单据日期
    EditText txtSalesDelCD;
    EditText txtSaleOrderNo;
    TextView tvCustomer;
    ImageButton btnSalesDelPDOrder;
    Button btnSalesDelExit;
    Button btnSalesDelScan;
    Button btnSalesDelSave;
    boolean NoScanSave = false;
    HashMap<String, String> checkInfo = new HashMap<String, String>();
    private ArrayList<String> ScanedBarcode = new ArrayList<String>();
    ProgressDialog progressDialog;
    String sBillAccID = "";
    String sBillCorpPK = "";
    String sBillCode = "";
    String SaleFlg = "";
    String csaleid = "";
    List<SaleOutGoods> tempList;
    EditText txtSalesDelWH;
    ImageButton btnSalesDelWH;

    String tmprdCode = "";
    String tmprdID = "";
    String tmprdName = "";
    String tmpposCode = "";
    String tmpposName = "";
    String tmpposID = "";
    public final static String PREFERENCE_SETTING = "Setting";
    String tmpCdTypeID;
    String WhNameA = "";
    String WhNameB = "";

    TextView tvSaleOutSelect;
    ImageButton btnSaleOutSelect;
    private UUID uploadGuid = null;
    private String[] BillTypeNameList = null;

    private String[] WHNameList = null;
    private String[] WHIDList = null;
    private String[] CDIDList = null;
    private String[] CDNameList = null;
    private AlertDialog BillTypeSelectButton = null;
    private AlertDialog WHSelectButton = null;
    private ButtonOnClick buttonOnClick = new ButtonOnClick(0);
    private String tmpWarehousePK = "";
    private String tmpCorpPK = "";
    private String tmpBillCode = "";
    private String tmpCustName = "";
    private String tmpBillDate = "";
    //保存用表头信息
    private JSONObject jsonSaveHead = null;
    private JSONObject jsonBillHead = null;
    //表体任务
    private JSONObject jsonBillBodyTask = null;
    private JSONObject jsonBillBodyTask2 = null;
    private JSONObject jsTotal=null;
    private String tmpAccID = "";
    private List<Map<String, Object>> lstSaveBody = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_delivery);
        this.setTitle("销售出库");
        initView();
        tvSaleOutSelect.requestFocus();
        jsonSaveHead = new JSONObject();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 88) {
            switch (resultCode) {
                case 1:         // 这是调拨出库单单号列表返回的地方
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            SerializableMap ResultMap = new SerializableMap();
                            ResultMap = (SerializableMap) bundle.get("ResultBillInfo");
                            Map<String, Object> mapBillInfo = ResultMap.getMap();
                            //绑定保存用表头
                            jsonSaveHead = new JSONObject();
                            //wuqiong
                            try {
                                jsonSaveHead = Common.MapTOJSONOBject(mapBillInfo);
                            } catch (JSONException e) {
                                Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            }
                            try {
                                CheckBillCode = jsonSaveHead.getString("BillCode");
                                checkInfo.put("BillCode",CheckBillCode);
                                SaleFlg = jsonSaveHead.getString("saleflg");
                            } catch (JSONException e2) {
                                // TODO Auto-generated catch block
                                e2.printStackTrace();
                            }
                            //绑定显示订单信息
                            if (!BindingBillDetailInfo(mapBillInfo)) {
                                return;
                            }
                            try {
                                //获得表头信息
                                GetBillHeadDetailInfo(SaleFlg);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        else if (requestCode == 44) {
            if (resultCode == 4) {

                if (data != null) {
                    try {
                        sBillCodes = data.getStringExtra("sBillCodes");
                        sBeginDate = data.getStringExtra("sBeginDate");
                        sEndDate = data.getStringExtra("sEndDate");
                        String BillCodeKey = "";
                        btnSalesDelPDOrderClick(BillCodeKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        else if (requestCode==42){
            if (resultCode==24){
                if (data != null) {
                    try
                    {
                     Bundle bundle = data.getExtras();
                    String saleTotalBox = bundle.getString("box");
                    String saleSerinno = bundle.getString("serino");
                    String dbBody = bundle.getString("body");
                    ScanedBarcode = bundle.getStringArrayList("ScanedBarcode");
                    this.jsBody = new JSONObject(dbBody);
                    Log.d(TAG, "AAAAAA: "+jsBody.toString());
                    this.jsSerino = new JSONObject(saleSerinno);
                    Log.d(TAG, "AAAAAA: "+jsSerino.toString());
                    this.jsBoxTotal = null;

                } catch (JSONException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(SalesDelivery.this, e.getMessage() ,
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    }
            }
            }
        }
        //仓库列表信息
        else if (requestCode == 97) {
            if (resultCode == 13) {
                String warehousePK1 = data.getStringExtra("result1");
                String warehousecode = data.getStringExtra("result2");
                String warehouseName = data.getStringExtra("result3");
                CWAREHOUSEID = warehousePK1;
                txtSalesDelWH.setText(warehouseName);
                checkInfo.put("WHName",warehouseName);
                Log.d(TAG, "Check: " +checkInfo.toString());
            }
        }
        else if (requestCode == 42) {
            if (resultCode == 5) {
                Bundle bundle = data.getExtras();
                tempList = bundle.getParcelableArrayList("overViewList");
                Log.d(TAG, "Check: " +checkInfo.toString());
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //获取订单表头信息
    private void GetBillHeadDetailInfo(String sSaleFlg) {

        JSONObject para = new JSONObject();
        try {

            if (tvSaleOutSelect.getText().toString().equals("销售出库")) {
                para.put("FunctionName", "GetSaleOutHeadNew");
                para.put("CSALEID", csaleid);
                para.put("BillCode", tmpBillCode);
                para.put("CorpPK", "4100");
                Log.d(TAG, "GetBillHeadDetailInfo: " + csaleid);
            }
        } catch (JSONException e2) {
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            e2.printStackTrace();
            return;
        }
        try {
            para.put("TableName", "dbHead");
        } catch (JSONException e2) {
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }

        JSONObject jas;
        try {
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            jas = Common.DoHttpQuery(para, "CommonQuery", "");
            Log.d(TAG, "GetBillHeadDetailInfo: " + jas.toString());

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }
        try {
            if (jas == null) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }

            if (!jas.has("Status")) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            if (!jas.getBoolean("Status")) {
                String errMsg = "";
                if (jas.has("ErrMsg")) {
                    errMsg = jas.getString("ErrMsg");
                } else {
                    errMsg = getString(R.string.WangLuoChuXianWenTi);
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            jsonBillHead = new JSONObject();
            JSONArray jsarray = jas.getJSONArray("dbHead");
            JSONArray newHeadArray = new JSONArray();
            JSONObject newHeadJSON = null;
            for (int i = 0; i < jsarray.length(); i++) {
                JSONObject tempJso = jsarray.getJSONObject(i);
                newHeadJSON = new JSONObject();
                if (tvSaleOutSelect.getText().toString().equals("销售出库")) {
                    newHeadJSON.put("VDEF1", tempJso.getString("vdef1"));
                    newHeadJSON.put("VDEF2", tempJso.getString("vdef2"));
                    newHeadJSON.put("VDEF5", tempJso.getString("vdef5"));
                    newHeadJSON.put("cbiztype", tempJso.getString("cbiztype"));
                    newHeadJSON.put("CSALECORPID", tempJso.getString("csalecorpid"));
                    newHeadJSON.put("PK_CORP", tempJso.getString("pk_corp"));
                    newHeadJSON.put("cdeptid", tempJso.getString("cdeptid"));
                    newHeadJSON.put("ccalbodyid", tempJso.getString("ccalbodyid"));
                    newHeadJSON.put("coperatorid", tempJso.getString("coperatorid"));
                    newHeadJSON.put("ccustomerid", tempJso.getString("ccustomerid"));
                    newHeadJSON.put("vreceiveaddress", tempJso.getString("vreceiveaddress"));
                    newHeadJSON.put("creceiptcorpid", tempJso.getString("creceiptcorpid"));
                    newHeadJSON.put("csaleid", tempJso.getString("csaleid"));
                    newHeadJSON.put("billcode", tmpBillCode);
                    newHeadJSON.put("cdeptid", tempJso.getString("cdeptid"));
                    newHeadJSON.put("capproveid", tempJso.getString("capproveid"));
                    newHeadJSON.put("ccalbodyid", tempJso.getString("ccalbodyid"));
                    newHeadJSON.put("creceiptcustomerid", tempJso.getString("creceiptcustomerid"));
                    newHeadJSON.put("nheadsummny", tempJso.getString("nheadsummny"));
                    newHeadJSON.put("creceipttype", tempJso.getString("creceipttype"));
                    CBIZTYPE = tempJso.getString("cbiztype").toString();
                    CSALECORPID = tempJso.getString("csalecorpid").toString();
                    PK_CORP = tempJso.getString("pk_corp").toString();
                    VDEF1 = tempJso.getString("vdef1").toString();
                    VDEF2 = tempJso.getString("vdef2").toString();
                    VDEF5 = tempJso.getString("vdef5").toString();
                    if (!TextUtils.isEmpty(VDEF5)){
                        VDEF5 = "";
                    }
                    CCUSTOMERID  = tempJso.getString("ccustomerid").toString();
                    CCUSTBASDOCID = tempJso.getString("ccustbasdocid").toString();
                }
                newHeadArray.put(newHeadJSON);
            }
            jsonBillHead.put("Status", true);
            jsonBillHead.put("dbBody", newHeadArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }

    }


    //绑定订单表头信息
    private boolean BindingBillDetailInfo(Map<String, Object> mapBillInfo) {
        csaleid = mapBillInfo.get("Csaleid").toString();
        tmpBillCode = mapBillInfo.get("BillCode").toString();
        tmpCustName = mapBillInfo.get("CustName").toString();
        tmpBillDate = mapBillInfo.get("BillDate").toString();
        txtSalesDelPDOrder.setText(tmpBillCode);
        txtSalesDelCD.setText(tmpCustName);
        txtSalesDelRdcl.setText(tmpBillDate);
        return true;
    }


    private class ButtonOnClick implements DialogInterface.OnClickListener {

        public int index;

        public ButtonOnClick(int index) {
            this.index = index;
        }

        public void onClick(DialogInterface dialog, int whichButton) {
            if (whichButton >= 0) {
                index = whichButton;
                dialog.cancel();
            } else {
                return;
            }
            if (dialog.equals(BillTypeSelectButton)) {

                String BillTypeName = BillTypeNameList[index].toString();

                BillTypeName = BillTypeName.substring(0, 4);

                tvSaleOutSelect.setText(BillTypeName);

                InitActiveMemor();
            }
            if (dialog.equals(WHSelectButton)) {

                if (!Common.CheckUserWHRole(tmpAccID, tmpCorpPK, WHIDList[index].toString())) {
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    Toast.makeText(SalesDelivery.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian, Toast.LENGTH_LONG).show();
                    return;
                }


                txtSalesDelWH.setText(WHNameList[index].toString());
                tmpWarehousePK = WHIDList[index].toString();

//				txtSalesDelPos.setText("");
                txtSalesDelPDOrder.requestFocus();
                tmpposCode = "";
                tmpposName = "";
                tmpposID = "";

                if ((tmpCdTypeID == null) || (tmpCdTypeID.equals(""))) {
//                    SetCDtype();
                }

            }

        }


    }


    private void showSingleChoiceDialog() {

        BillTypeSelectButton = new AlertDialog.Builder(this).setTitle("选择单据类型").setSingleChoiceItems(
                BillTypeNameList, -1, buttonOnClick).setNegativeButton(R.string.QuXiao, buttonOnClick).show();
    }


    private void SetBillType() {
        BillTypeNameList = new String[1];//设置单据类型数量
        //开始设置单据类型名字
        BillTypeNameList[0] = "销售出库   (扫描销售订单)";
//		BillTypeNameList[1]="退回再送   (扫描送货单)";
//		BillTypeNameList[2]="退回不送   (扫描退货单)";
    }

    public class OnClickListener implements android.view.View.OnClickListener {

        public void onClick(View v) {

            switch (v.getId()) {
                case id.btnSaleOutSelect:
                    if (lstSaveBody == null || lstSaveBody.size() < 1) {

                    } else {
                        Toast.makeText(SalesDelivery.this, R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        tvSaleOutSelect.setText("");
                        break;
                    }
                    showSingleChoiceDialog();
                    break;
                //单据图标
                case id.btnSalesDelPDOrder:
                    try {
                        Intent intent = new Intent(SalesDelivery.this, SaleChooseTime.class);
                        startActivityForResult(intent, 44);
                        txtSalesDelWH.requestFocus();


                    } catch (ParseException e) {
                        Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        //ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        //ADD CAIXY TEST END
                    }
                    break;

                case id.btnSalesDelScan:

                    if (tvSaleOutSelect.getText().toString() == null || tvSaleOutSelect.getText().toString().equals("")) {
                        Toast.makeText(SalesDelivery.this, "请输入来源单据",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }

                    if (txtSalesDelPDOrder.getText().toString() == null || txtSalesDelPDOrder.getText().toString().equals("")) {
                        Toast.makeText(SalesDelivery.this, "没有选择单据号",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }

                    if (txtSalesDelWH.getText().toString() == null || txtSalesDelWH.getText().toString().equals("")) {
                        Toast.makeText(SalesDelivery.this, "仓库没有选择",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }
                    SaleScan();
                    break;
                case id.btnSalesDelSave:
                    try
                    {
                        if (jsBody==null||jsBody.equals("")){
                            Toast.makeText(SalesDelivery.this, "没有要保存的数据",
                                    Toast.LENGTH_LONG).show();
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            return;

                        }
                    if (tvSaleOutSelect.getText().toString() == null || tvSaleOutSelect.getText().toString().equals("")) {
                        Toast.makeText(SalesDelivery.this, "请输入来源单据",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }

                    if (txtSalesDelPDOrder.getText().toString() == null || (!txtSalesDelPDOrder.getText().toString().equals(checkInfo.get("BillCode")))) {
                        Toast.makeText(SalesDelivery.this, "没有选择单据号",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }

                    if (txtSalesDelWH.getText().toString() == null || (!txtSalesDelWH.getText().toString().equals(checkInfo.get("WHName")))) {
                        Toast.makeText(SalesDelivery.this, "仓库没有选择",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }
//                        if (!checkOrder(txtSaleOrderNo.getText().toString())){
//                            Toast.makeText(SalesDelivery.this, "要输入字母和数字的组合数，",
//                                    Toast.LENGTH_LONG).show();
//                            // ADD CAIXY TEST START
//                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                            // ADD CAIXY TEST END
//                            return;
//                        }

//                        saveInfo(tempList);
                        saveInfo();// TODO: 2017/7/16
                        showProgressDialog();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(SalesDelivery.this, R.string.WangLuoChuXianWenTi ,
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    }
                    break;
                //返回按钮
                case id.btnSalesDelExit:
                    Exit();
                    break;

                case id.btnSalesDelWH:
                    try {

                        if (tvSaleOutSelect.getText().toString() == null || tvSaleOutSelect.getText().toString().equals("")) {
                            Toast.makeText(SalesDelivery.this, "请输入来源单据",
                                    Toast.LENGTH_LONG).show();
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            return;
                        }

                        if (txtSalesDelPDOrder.getText().toString() == null || txtSalesDelPDOrder.getText().toString().equals("")) {
                            Toast.makeText(SalesDelivery.this, "没有选择单据号",
                                    Toast.LENGTH_LONG).show();
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            return;
                        }
                        btnWarehouseClick();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(SalesDelivery.this, "请输入单据来源", Toast.LENGTH_SHORT).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    }
                    break;

            }
        }
    }

//    private boolean checkOrder(String str){
//        //
//        Pattern pattern = Pattern.compile("^[a-zA-Z0-9]{6,10}$");
//        Matcher order = pattern.matcher(str);
//        return order.matches();
//    }
    /**
     * 保存弹出提醒
     */

    private void showProgressDialog() {
        Log.d(TAG, "showProgressDialog: "+"000");
        progressDialog = new ProgressDialog(SalesDelivery.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        progressDialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        progressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        // progressDialog.setIcon(R.drawable.ic_launcher);
        // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
        progressDialog.setTitle("保存单据");
        progressDialog.setMessage("正在保存，请等待...");
        progressDialog.show();
        new Thread(new Runnable() {

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
        }).start();
    }
    private  void saveInfo(){
        try {
            if (SaveSaleOrder() == true) {
                jsTotal = null;
                jsSerino = null;
                jsBody = null;
                jsonSaveHead = null;
                jsonBillHead = null;
                changeAllEdToEmpty();
                txtSalesDelPDOrder.requestFocus();
                //SaveOk();
    //            IniActivyMemor();// TODO: 2017/7/10 XUHU
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //保存数据
    private boolean SaveSaleOrder() throws JSONException,
            ParseException, IOException {
        table = new JSONObject();
        JSONArray arrayss = null;
        JSONArray arrayMerge = null;
        arrayss = jsSerino.getJSONArray("Serino");
        arrayMerge = merge(arrayss);
        jsTotal = new JSONObject();
        jsTotal.put("Serino",arrayMerge);
        JSONObject map = new JSONObject();
        Double notnum = 0.0;
            JSONArray arrays = jsTotal.getJSONArray("Serino");
            for (int i = 0; i < arrays.length(); i++) {
//                String totalnum = ((JSONObject) (arrays.get(i))).getString("box");
//                totalnum = Double.valueOf(totalnum).toString();
                Double box = arrays.getJSONObject(i).getDouble("box");
                DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                String totalBox = decimalFormat.format(box);
                notnum +=Double.valueOf(totalBox);
                NTOTALNUMBER = decimalFormat.format(notnum);
            }

        Log.d(TAG, "GGGG: "+map.toString());
        JSONObject tableHead = new JSONObject();
        tableHead.put("RECEIVECODE",tmpBillCode);
        tableHead.put("CBIZTYPE", CBIZTYPE);
        tableHead.put("COPERATORID", MainLogin.objLog.UserID);
        tableHead.put("CRECEIPTTYE", "4331");
        tableHead.put("CSALECORPID", CSALECORPID);
        tableHead.put("PK_CORP", MainLogin.objLog.STOrgCode);
        tableHead.put("VBILLCODE", "");
        tableHead.put("VDEF1", VDEF1);
        tableHead.put("VDEF2", VDEF2);
        tableHead.put("VDEF5", VDEF5);
        tableHead.put("NTOTALNUMBER",NTOTALNUMBER);
        tableHead.put("NOTOTALNUMBER","200.00");// TODO: 2017/7/4
        table.put("Head", tableHead);
        JSONObject tableBody = new JSONObject();
        JSONArray bodyArray = new JSONArray();

            JSONArray bodys = jsBody.getJSONArray("dbBody");
            JSONArray arraysSerino = jsTotal.getJSONArray("Serino");
            int y = 0;
            for (int j=0; j<arraysSerino.length(); j++) {

                for (int i = 0; i < bodys.length(); i++) {

                    if(arraysSerino.getJSONObject(j).getString("invcode").toLowerCase().equals(
                            bodys.getJSONObject(i).getString("invcode")))
                    {
                        Double box = arraysSerino.getJSONObject(j).getDouble("box");
                        DecimalFormat decimalFormat = new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                        String totalBox = decimalFormat.format(box);//format 返回的是字符串
                        JSONObject object = new JSONObject();
                        object.put("CROWNO", bodys.getJSONObject(i).getString("crowno"));
                        object.put("VSOURCEROWNO", bodys.getJSONObject(i).getString("crowno"));
                        object.put("VSOURCERECEIVECODE", tmpBillCode);
                        object.put("VRECEIVEPOINTID", bodys.getJSONObject(i).getString("crecaddrnode"));
                        object.put("CRECEIVECUSTID", bodys.getJSONObject(i).getString("creceiptcorpid"));
                        object.put("CRECEIVEAREAID", bodys.getJSONObject(i).getString("creceiptareaid"));
//                        object.put("DDELIVERDATE", bodys.getJSONObject(i).getString("ddeliverdate"));
                        object.put("CBIZTYPE", CBIZTYPE);//表头
                        object.put("CCUSTBASDOCID", CCUSTBASDOCID);
                        object.put("CCUSTMANDOCID", CCUSTOMERID);//表头customerID
                        object.put("CINVBASDOCID",bodys.getJSONObject(i).getString("cinvbasdocid"));
                        object.put("CINVMANDOCID", bodys.getJSONObject(i).getString("cinventoryid"));
                        object.put("CRECEIVECUSTBASID",CCUSTBASDOCID);//自己获取
                        object.put("CSENDCALBODYID",bodys.getJSONObject(i).getString("cadvisecalbodyid"));
                        object.put("CSENDWAREID", CWAREHOUSEID);//仓库
                        object.put("CSOURCEBILLBODYID", bodys.getJSONObject(i).getString("corder_bid"));
                        object.put("CSOURCEBILLID",bodys.getJSONObject(i).getString("csaleid"));
                        object.put("NNUMBER", totalBox);
                        object.put("PK_SENDCORP", bodys.getJSONObject(i).getString("pk_corp"));
                        object.put("VBATCHCODE", arraysSerino.getJSONObject(j).getString("batch"));
                        object.put("VRECEIVEADDRESS",bodys.getJSONObject(i).getString("vreceiveaddress"));
                        object.put("VRECEIVEPERSON",MainLogin.objLog.LoginUser);
                        bodyArray.put(object);
                    }
                }
            }
            tableBody.put("ScanDetails", bodyArray);
            table.put("Body", tableBody);
            table.put("GUIDS", UUID.randomUUID().toString());
            Log.d(TAG, "XXXXXX: " + table.toString());
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return false;
            }
            SaveThread saveThread = new SaveThread(table, "SaveSaleReceive", mHandler, HANDER_SAVE_RESULT);
            Thread thread = new Thread(saveThread);
            thread.start();

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
                case HANDER_SAVE_RESULT:
                    JSONObject saveResult = (JSONObject) msg.obj;
                    try {
                        if (saveResult != null) {
                            if (saveResult.getBoolean("Status")) {
                                Log.d(TAG, "保存" + saveResult.toString());
                                showResultDialog(SalesDelivery.this, saveResult.getString("ErrMsg"));
//                                MaterialOutScanAct.ovList.clear();
//                                MaterialOutScanAct.detailList.clear();

                            } else {
                                showResultDialog(SalesDelivery.this, saveResult.getString("ErrMsg"));
                            }
                        } else {
                            showResultDialog(SalesDelivery.this, "数据提交失败!");
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
     * progressDialog 消失
     */
    private void progressDialogDismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 清空
     */
    private  void changeAllEdToEmpty(){
        txtSalesDelPDOrder.setText("");
        txtSalesDelWH.setText("");
        txtSalesDelCD.setText("");
        txtSalesDelRdcl.setText("");
        txtSaleOrderNo.setText("");

    }

    /**
     * 获取仓库信息
     * by Xuhu
     * 点击仓库列表参照
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
            Log.d(TAG, "btnWarehouseClick: " + rev.toString());

            if (rev == null) {
                // 网络通讯错误
                Toast.makeText(this, "错误！网络通讯错误", Toast.LENGTH_LONG).show();
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



    //打开订单列表画面
    private void btnSalesDelPDOrderClick(String BillCodeKey) throws ParseException, IOException, JSONException {

        if (lstSaveBody == null || lstSaveBody.size() < 1) {

        } else {
            Toast.makeText(SalesDelivery.this, R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            txtSalesDelPDOrder.setText("");
            return;
        }


        if (tvSaleOutSelect.getText().toString().equals("未选择")) {
            Toast.makeText(SalesDelivery.this, "单据类型未选择,请选择单据类型", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            txtSalesDelPDOrder.requestFocus();
            return;
        }
        if (tvSaleOutSelect.getText().toString().equals("销售出库")) {
            Intent ViewGrid = new Intent(this, SaleBillInfoOrderList.class);
            ViewGrid.putExtra("FunctionName", "销售出库");//GetSalereceiveHead
            ViewGrid.putExtra("sBeginDate", sBeginDate);
            ViewGrid.putExtra("sBillCodes", sBillCodes);
            ViewGrid.putExtra("sEndDate", sEndDate);
            startActivityForResult(ViewGrid, 88);
        }

    }

    private void SaleScan(){
        Intent intDeliveryScan = new Intent(SalesDelivery.this, SalesDeliveryDetail.class);
        intDeliveryScan.putExtra("BillCode", tmpBillCode);
        intDeliveryScan.putExtra("PK_CORP", PK_CORP);
        intDeliveryScan.putExtra("CSALEID", csaleid);
        intDeliveryScan.putExtra("ScanType", tvSaleOutSelect.getText().toString());
        if (jsBody!=null){
            intDeliveryScan.putExtra("jsbody",jsBody.toString());
        }  if (jsSerino!=null){
            intDeliveryScan.putExtra("jsserino",jsSerino.toString());
        }
        intDeliveryScan.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
        startActivityForResult(intDeliveryScan, 42);
    }

    //退出按钮
    private void Exit() {
        if (jsBody!=null) {
            AlertDialog.Builder bulider =
                    new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("扫描单据未保存，确认退出吗?");
            bulider.setNegativeButton(R.string.QuXiao, null);
            bulider.setPositiveButton(R.string.QueRen, listenExit).create().show();
        } else {
            finish();
        }

    }

    //退出按钮对话框事件
    private DialogInterface.OnClickListener listenExit = new
            DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int whichButton) {
                    dialog.dismiss();
                    finish();
//                    System.gc();
                }
            };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {//拦截meu键事件			//do something...
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {//拦截返回按钮事件			//do something...
            return false;
        }
        return true;
    }


    //EditText输入后回车的监听事件
    private OnKeyListener EditTextOnKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int arg1, KeyEvent arg2) {
            if (arg1 == arg2.KEYCODE_ENTER && arg2.getAction() == KeyEvent.ACTION_UP)//&& arg2.getAction() == KeyEvent.ACTION_DOWN
            {
                switch (v.getId()) {
//                    case id.txtSalesDelPDOrder:
//                        txtSalesDelWH.requestFocus();
//                        return true;
                }

            }
            return false;
        }
    };




    private void InitActiveMemor() {
        this.tmpAccID = "";
        this.tmpposCode = "";
        this.tmpposName = "";
        this.tmpposID = "";
        this.tmpCorpPK = "";
        this.tmpBillCode = "";
        this.tmpAccID = "";
        this.tmpCorpPK = "";
        this.tmpWHStatus = "";
        this.lstSaveBody = null;
        this.uploadGuid = null;
        this.jsonSaveHead = null;
        this.jsonBillBodyTask = null;
        this.jsonBillBodyTask2 = null;
        this.lstSaveBody = null;
        this.jsonBillBodyTask = new JSONObject();
        this.jsonSaveHead = new JSONObject();
        this.txtSalesDelPDOrder.setText("");
        this.txtSalesDelRdcl.setText("");
        this.txtSalesDelWH.setText("");
        this.txtSalesDelCD.setText("");
        this.tmprdCode = "";
        this.tmprdID = "";
        this.tmprdName = "";
        this.tmpposCode = "";
        this.tmpposName = "";
        this.tmpposID = "";
        this.sBillAccID = "";
        this.sBillCorpPK = "";
        this.sBillCode = "";
        this.SaleFlg = "";
        this.tmpCdTypeID = "";
        this.ScanedBarcode = new ArrayList<String>();
        this.txtSalesDelPDOrder.requestFocus();
    }



    private void initView() {

        saleOutGoodsLists = new ArrayList<SaleOutGoods>();
        txtSalesDelPDOrder = (EditText) findViewById(id.txtSalesDelPDOrder);
        btnSalesDelPDOrder = (ImageButton) findViewById(id.btnSalesDelPDOrder);
        btnSalesDelPDOrder.setOnClickListener(new OnClickListener());

        tvSaleOutSelect = (EditText) findViewById(id.tvSaleOutSelect);
        btnSaleOutSelect = (ImageButton) findViewById(id.btnSaleOutSelect);
        btnSaleOutSelect.setOnClickListener(new OnClickListener());

        txtSalesDelWH = (EditText) findViewById(id.txtSalesDelWH);
        btnSalesDelWH = (ImageButton) findViewById(id.btnSalesDelWH);
        btnSalesDelWH.setOnClickListener(new OnClickListener());

//        txtSaleOrderNo = (EditText) findViewById(id.tvSaleOrderNo);
        btnSalesDelScan = (Button) findViewById(id.btnSalesDelScan);
        btnSalesDelScan.setOnClickListener(new OnClickListener());
        btnSalesDelSave = (Button) findViewById(id.btnSalesDelSave);
        btnSalesDelSave.setOnClickListener(new OnClickListener());
        btnSalesDelExit = (Button) findViewById(id.btnSalesDelExit);
        btnSalesDelExit.setOnClickListener(new OnClickListener());

        tvCustomer = (TextView) findViewById(id.tvCustomer);

        txtSalesDelRdcl = (EditText) findViewById(id.txtSalesDelRdcl);


        txtSalesDelCD = (EditText) findViewById(id.txtSalesDelCD);


        txtSalesDelRdcl.setOnKeyListener(EditTextOnKeyListener);
        txtSalesDelPDOrder.setOnKeyListener(EditTextOnKeyListener);

        txtSalesDelCD.setFocusable(false);
        txtSalesDelCD.setFocusableInTouchMode(false);
        txtSalesDelRdcl.setFocusable(false);
        txtSalesDelRdcl.setFocusableInTouchMode(false);
        tvSaleOutSelect.setFocusableInTouchMode(false);
        btnSalesDelExit.setFocusable(false);
        btnSalesDelScan.setFocusable(false);
        btnSalesDelSave.setFocusable(false);
    }

    /**
     * 根据批次sku相同合并数量
     */
    public  JSONArray merge(JSONArray array) {

        JSONArray arrayTemp = new JSONArray();
        int num = 0;
        for(int i = 0;i < array.length();i++) {
            if (num == 0) {
                try {
                    arrayTemp.put(array.get(i));
                    num++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    int numJ = 0;
                    Log.d(TAG, "Merge: "+arrayTemp.length());
                    for (int j = 0; j < arrayTemp.length(); j++) {
                        JSONObject newJsonObjectI = (JSONObject) array.get(i);
                        JSONObject newJsonObjectJ = (JSONObject) arrayTemp.get(j);
                        String invcode = newJsonObjectI.get("invcode").toString();
                        String invname = newJsonObjectI.get("invname").toString();
                        String batch = newJsonObjectI.get("batch").toString();
                        String box = newJsonObjectI.get("box").toString();
                        String sno = newJsonObjectI.get("sno").toString();
                        String invtype = newJsonObjectI.get("invtype").toString();
                        String invspec = newJsonObjectI.get("invspec").toString();
                        String serino = newJsonObjectI.get("serino").toString();

                        String invcodeJ = newJsonObjectJ.get("invcode").toString();
                        String batchJ = newJsonObjectJ.get("batch").toString();
                        String boxJ = newJsonObjectJ.get("box").toString();

                        if (invcode.equals(invcodeJ)&&batch.equals(batchJ)) {
                            double newValue = Double.parseDouble(box) + Double.parseDouble(boxJ);
                            JSONObject newObject = new JSONObject();
                            arrayTemp.remove(j);
                            newObject.put("invcode", invcode);
                            newObject.put("batch", batch);
                            newObject.put("invname", invname);
                            newObject.put("serino", serino);
                            newObject.put("sno", sno);
                            newObject.put("invtype", invtype);
                            newObject.put("invspec", invspec);
                            newObject.put("box", String.valueOf(newValue));
                            arrayTemp.put(newObject);
                            break;
                        }

                        numJ++;

                        String a = numJ+"";
                        Log.d(TAG, "Merge: "+a);


                    }
                    if (numJ - 1 == arrayTemp.length() - 1) {
                        arrayTemp.put(array.get(i));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
        Log.d(TAG, "DDDDD: "+arrayTemp.toString());
        return arrayTemp;
    }


//    private void saveInfo(List<SaleOutGoods> goodsList) throws JSONException {
//        table = new JSONObject();
//        JSONObject tableHead = new JSONObject();
//    tableHead.put("CBIZTYPE", CBIZTYPE);
//    tableHead.put("COPERATORID", MainLogin.objLog.UserID);
//    tableHead.put("CRECEIPTTYE", "4331");
//    tableHead.put("CSALECORPID", CSALECORPID);
//    tableHead.put("PK_CORP", MainLogin.objLog.STOrgCode);
//    tableHead.put("VBILLCODE", "");
//    tableHead.put("VDEF1", VDEF1);
//    tableHead.put("VDEF2", VDEF2);
//    tableHead.put("VDEF5", VDEF5);
//    tableHead.put("NTOTALNUMBER",NTOTALNUMBER);
//    tableHead.put("NOTOTALNUMBER","200.00");// TODO: 2017/7/4
//    table.put("Head", tableHead);
//        JSONObject tableBody = new JSONObject();
//        JSONArray bodyArray = new JSONArray();
//        double ss =0;
//        for (SaleOutGoods c : goodsList) {
//            JSONObject object = new JSONObject();
//            float u = c.getQty();
//            ss+=u;
//            NTOTALNUMBER =ss+"";
//            object.put("CROWNO", c.getCrowno());
//                object.put("CBIZTYPE", CBIZTYPE);//表头
//                object.put("CCUSTBASDOCID", CCUSTBASDOCID);
//                object.put("CCUSTMANDOCID", CCUSTOMERID);//表头customerID
//                object.put("CINVBASDOCID",c.getCinvbasdocid());
//                object.put("CINVMANDOCID", c.getCinventoryid());
//                object.put("CRECEIVEAREAID", "");
//                object.put("CRECEIVECUSTBASID",CCUSTBASDOCID);//自己获取
//                object.put("CSENDCALBODYID",c.getCadvisecalbodyid());
//                object.put("CSENDWAREID", CWAREHOUSEID);//仓库
//                object.put("CSOURCEBILLBODYID", c.getCorder_bid());
//                object.put("CSOURCEBILLID",c.getCsaleid());
//                object.put("NNUMBER", Utils.formatDecimal(c.getQty()));
//                object.put("NTOTALOUTINVNUM",c.getNumber());
//                object.put("PK_SENDCORP", c.getPk_corp());
//                object.put("VBATCHCODE", c.getBatch());
//                object.put("VRECEIVEADDRESS",c.getVreceiveaddress());
//                object.put("VRECEIVEPERSON",MainLogin.objLog.LoginUser);
//                bodyArray.put(object);
//            bodyArray.put(object);
//        }
//        tableBody.put("ScanDetails", bodyArray);
//        table.put("Body", tableBody);
//        table.put("GUIDS", UUID.randomUUID().toString());
//        Log.d(TAG, "saveInfo: " + table.toString());
//        SaveThread saveThread = new SaveThread(table, "SaveSaleReceive", mHandler, HANDER_SAVE_RESULT);
//        Thread thread = new Thread(saveThread);
//        thread.start();
//
//    }
}
