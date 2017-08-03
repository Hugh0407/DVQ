package com.techscan.dvq.module.multilateraltrade;

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
import android.widget.Toast;

import com.techscan.dvq.GetInvBaseInfo;
import com.techscan.dvq.ListWarehouse;
import com.techscan.dvq.R;
import com.techscan.dvq.SerializableMap;
import com.techscan.dvq.common.Base64Encoder;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SaveThread;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.login.MainLogin;
import com.techscan.dvq.module.materialOut.DepartmentListAct;
import com.techscan.dvq.module.multilateraltrade.scan.MultilateralTradeDetail;
import com.techscan.dvq.module.saleout.SaleChooseTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;
import static com.techscan.dvq.R.string.WangLuoChuXiangWenTi;
import static com.techscan.dvq.common.Utils.HANDER_DEPARTMENT;
import static com.techscan.dvq.common.Utils.HANDER_SAVE_RESULT;
import static com.techscan.dvq.common.Utils.showResultDialog;

public class MultilateralTrade extends Activity {

    @InjectView(R.id.txtMultilateralTrade)
    EditText txtMultilateralTrade;
    @InjectView(R.id.txtDocumentNumber)
    EditText txtDocumentNumber;
    @InjectView(R.id.imageDocumentNumber)
    ImageButton imageDocumentNumber;
    @InjectView(R.id.txtWareHouse)
    EditText txtWareHouse;
    @InjectView(R.id.imageWareHouse)
    ImageButton imageWareHouse;
    @InjectView(R.id.imageDepartment)
    ImageButton imageDepartment;
    @InjectView(R.id.txtOutCompany)
    EditText txtOutCompany;
    @InjectView(R.id.txtOutOrg)
    EditText txtOutOrg;
    @InjectView(R.id.txtInCompany)
    EditText txtInCompany;
    @InjectView(R.id.txtInOrg)
    EditText txtInOrg;
    @InjectView(R.id.txtBillDate)
    EditText txtBillDate;
    @InjectView(R.id.txtDepartment)
    EditText txtDepartment;
    @InjectView(R.id.btnScan)
    Button btnScan;
    @InjectView(R.id.btnSave)
    Button btnSave;
    @InjectView(R.id.btnExit)
    Button btnExit;

    String sBillCodes = "";
    String sBeginDate = "";
    String sEndDate = "";
    String CheckBillCode = "";
    String SaleFlg = "";
    private String tmpBillCode = "";
    private String tmpCustName = "";
    private String tmpBillDate = "";
    String CBILLID = "";
    String CBIZTYPE = "";
    String  CSALECORPID = "";
    String PK_CORP = "";
    String  VDEF1 = "";
    String VDEF2 = "";
    String VDEF5 = "";
    String  CCUSTBASDOCID ="";
    String  CRECEIVECUSTBASID ="";
    String CCUSTOMERID = "";
    String CWAREHOUSEID = "";//�ֿ�id
    String NTOTALNUMBER = "";
    String CDPTID = "";//����id
    String outCompany = "";//������˾
    String inCompany = "";//���빫˾
    String COUTCOMPANYID="";
    String CINCOMPANYID="";
    HashMap<String, String> checkInfo = new HashMap<String, String>();
    private ArrayList<String> ScanedBarcode = new ArrayList<String>();
    private JSONObject jsonSaveHead = null;
    private JSONObject jsonBillHead = null;
    JSONObject table = null;
    JSONObject jsBody = null;
    JSONObject jsSerino = null;
    private JSONObject jsTotal=null;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multilateral_trade);
        this.setTitle("���ó��");
//        initView();
        ButterKnife.inject(this);
//        txtCustomer.setFocusable(false);
//        txtCustomer.setFocusableInTouchMode(false);
        txtBillDate.setFocusable(false);
        txtBillDate.setFocusableInTouchMode(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 44) {
            if (resultCode == 4) {
                if (data != null) {
                        try {
                            sBillCodes = data.getStringExtra("sBillCodes");
                            sBeginDate = data.getStringExtra("sBeginDate");
                            sEndDate = data.getStringExtra("sEndDate");
                            String BillCodeKey = "";
                            btnSalesDelPDOrderClick(BillCodeKey);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }
        }
          else if (requestCode == 88) {
            switch (resultCode) {
                case 1:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            SerializableMap ResultMap = new SerializableMap();
                            ResultMap = (SerializableMap) bundle.get("ResultBillInfo");
                            Map<String, Object> mapBillInfo = ResultMap.getMap();
                            try {
                                jsonSaveHead = new JSONObject();
                                jsonSaveHead = Common.MapTOJSONOBject(mapBillInfo);
                                CheckBillCode = jsonSaveHead.getString("vbillcode");
                                checkInfo.put("BillCode",CheckBillCode);
                                SaleFlg = jsonSaveHead.getString("saleflg");
//                                BindingBillDetailInfo(mapBillInfo);
                            } catch (Exception e) {
                                Toast.makeText(MultilateralTrade.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            }
                            try {
                                //��ñ�ͷ��Ϣ
                                GetBillHeadDetailInfo(SaleFlg);
//                                BindingBillDetailInfo(mapBillInfo);
                                //��ñ�����Ϣ
//                                GetBillBodyDetailInfo(SaleFlg);
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
                    } catch (JSONException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(MultilateralTrade.this, e.getMessage() ,
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    }
                }
            }
        }
        //�ֿ��б���Ϣ
        else if (requestCode == 97) {
            if (resultCode == 13) {
                String warehousePK1 = data.getStringExtra("result1");
                String warehousecode = data.getStringExtra("result2");
                String warehouseName = data.getStringExtra("result3");
                CWAREHOUSEID = warehousePK1;
                txtWareHouse.setText(warehouseName);
                checkInfo.put("WHName",warehouseName);
//                try {
//                    //��ñ�ͷ��Ϣ
//                    GetBillHeadDetailInfo(SaleFlg);
//                    //��ñ�����Ϣ
////                                GetBillBodyDetailInfo(SaleFlg);
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                }
            }

        }
        else if (requestCode == 96 && resultCode == 4) {
            String deptname   = data.getStringExtra("deptname");
            String pk_deptdoc = data.getStringExtra("pk_deptdoc");
            String deptcode   = data.getStringExtra("deptcode");
            CDPTID = pk_deptdoc;
//            department.requestFocus();
            txtDepartment.setText(deptname);
            checkInfo.put("Department", deptname);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.imageDocumentNumber, R.id.imageWareHouse,R.id.imageDepartment, R.id.btnScan, R.id.btnSave, R.id.btnExit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageDocumentNumber:
                try{
                    Intent intent = new Intent(MultilateralTrade.this, SaleChooseTime.class);
                    startActivityForResult(intent, 44);
//                    txtWareHouse.requestFocus();

                }catch (Exception e){
                    Toast.makeText(MultilateralTrade.this, WangLuoChuXiangWenTi, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                }
                break;
            case R.id.imageWareHouse:
                try {
                    if (txtMultilateralTrade.getText().toString() == null || txtMultilateralTrade.getText().toString().equals("")) {
                        Toast.makeText(MultilateralTrade.this, "��������Դ����",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }
//                    if (txtDocumentNumber.getText().toString() == null || txtDocumentNumber.getText().toString().equals("")) {
//                        Toast.makeText(MultilateralTrade.this, "û��ѡ�񵥾ݺ�",
//                                Toast.LENGTH_LONG).show();
//                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                        return;
//                    }
                    //��ȡ�ֿ�
                    btnWarehouseClick();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MultilateralTrade.this, "�����뵥����Դ", Toast.LENGTH_SHORT).show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                }
                break;
            case R.id.imageDepartment:
//              Toast.makeText(MultilateralTrade.this,"QQQ",Toast.LENGTH_SHORT).show();
                btnReferDepartment();
            break;
            case R.id.btnScan:
                if (txtMultilateralTrade.getText().toString() == null || txtMultilateralTrade.getText().toString().equals("")) {
                    Toast.makeText(MultilateralTrade.this, "��������Դ����",
                            Toast.LENGTH_LONG).show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }

                if (txtDocumentNumber.getText().toString() == null || txtDocumentNumber.getText().toString().equals("")) {
                    Toast.makeText(MultilateralTrade.this, "û��ѡ�񵥾ݺ�",
                            Toast.LENGTH_LONG).show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }
                if (txtWareHouse.getText().toString() == null || txtWareHouse.getText().toString().equals("")) {
                    Toast.makeText(MultilateralTrade.this, "�ֿ�û��ѡ��",
                            Toast.LENGTH_LONG).show();
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    return;
                }
                SaleScan();
                break;
            case R.id.btnSave:
                try
                {
                    if (jsBody==null||jsBody.equals("")){
                        Toast.makeText(MultilateralTrade.this, "û��Ҫ���������",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;

                    }
                    if (txtMultilateralTrade.getText().toString() == null || txtMultilateralTrade.getText().toString().equals("")) {
                        Toast.makeText(MultilateralTrade.this, "��������Դ����",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }

                    if (txtDocumentNumber.getText().toString() == null || (!txtDocumentNumber.getText().toString().equals(checkInfo.get("BillCode")))) {
                        Toast.makeText(MultilateralTrade.this, "û��ѡ�񵥾ݺ�",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }

                    if (txtWareHouse.getText().toString() == null || (!txtWareHouse.getText().toString().equals(checkInfo.get("WHName")))) {
                        Toast.makeText(MultilateralTrade.this, "�ֿ�û��ѡ��",
                                Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }
                    SaveSaleOrder();
                    showProgressDialog();
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MultilateralTrade.this, R.string.WangLuoChuXianWenTi ,
                            Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                }

                break;
            case R.id.btnExit:
                Exit();
                break;
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
 * ɨ�谴ť
 */

    private void SaleScan(){
        Intent intDeliveryScan = new Intent(MultilateralTrade.this, MultilateralTradeDetail.class);
        intDeliveryScan.putExtra("BillCode", tmpBillCode);
        intDeliveryScan.putExtra("PK_CORP", PK_CORP);
        intDeliveryScan.putExtra("CBILLID", CBILLID);
        intDeliveryScan.putExtra("CWAREHOUSEID", CWAREHOUSEID);
        intDeliveryScan.putExtra("ScanType", txtMultilateralTrade.getText().toString());
        if (jsBody!=null){
            intDeliveryScan.putExtra("jsbody",jsBody.toString());
        }  if (jsSerino!=null){
            intDeliveryScan.putExtra("jsserino",jsSerino.toString());
        }
        intDeliveryScan.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
        startActivityForResult(intDeliveryScan, 42);
    }

    //�˳���ť
    private void Exit() {
        if (jsSerino == null || jsSerino.length() < 1) {
            deleteInfo();
            finish();
        } else {
            AlertDialog.Builder bulider =
                    new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("����δ�����Ƿ��˳�");
            bulider.setNegativeButton(R.string.QuXiao, null);
            bulider.setPositiveButton(R.string.QueRen, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    deleteInfo();
                    finish();
                }
            }).create().show();
        }

    }

    private void initView(){
//        txtCustomer.setFocusable(false);
//        txtCustomer.setFocusableInTouchMode(false);
        txtBillDate.setFocusable(false);
        txtBillDate.setFocusableInTouchMode(false);
    }

    /**
     * �򿪶����б�����
     */

    private void btnSalesDelPDOrderClick(String BillCodeKey) throws ParseException, IOException, JSONException {
            Intent ViewGrid = new Intent(this, GetInvBaseInfo.SaleBillInfoOrderList.class);
            ViewGrid.putExtra("FunctionName", "���ó��");//GetSalereceiveHead
            ViewGrid.putExtra("sBeginDate", sBeginDate);
            ViewGrid.putExtra("sBillCodes", sBillCodes);
            ViewGrid.putExtra("sEndDate", sEndDate);
            ViewGrid.putExtra("whId",CWAREHOUSEID);
            startActivityForResult(ViewGrid, 88);

    }

    /**
     * �󶨶�����ͷ��Ϣ
     */
    private boolean BindingBillDetailInfo(Map<String, Object> mapBillInfo) {
        String outOrg = "";
        String inOrg = "";
        CBILLID = mapBillInfo.get("cbillid").toString();
        COUTCOMPANYID = mapBillInfo.get("coutcompanyid").toString();
        CINCOMPANYID = mapBillInfo.get("coincompanyid").toString();
        tmpBillCode = mapBillInfo.get("vbillcode").toString();
        outOrg = mapBillInfo.get("coutcompanyname").toString();
        inOrg = mapBillInfo.get("coincompanycode").toString();
        tmpBillDate = mapBillInfo.get("BillDate").toString();
        txtOutCompany.setText("");
        txtOutOrg.setText(outOrg);
        txtInCompany.setText("");
        txtInOrg.setText(inOrg);
        txtDocumentNumber.setText(tmpBillCode);
        txtBillDate.setText(tmpBillDate);
        txtBillDate.setFocusable(false);
        txtBillDate.setFocusableInTouchMode(false);
        return true;
    }

    /**
     * ��ȡ������ͷ��Ϣ
     */

    private void GetBillHeadDetailInfo(String sSaleFlg) {
        JSONObject para = new JSONObject();
        try {
            para.put("FunctionName", "GetAdjustOrderBillHead");
            para.put("BILLCODE", CheckBillCode);
            para.put("COMPANYCODE",  MainLogin.objLog.CompanyCode);
            para.put("WHID",CWAREHOUSEID);
//            para.put("BillCode", tmpBillCode);
            para.put("STORGCODE",  MainLogin.objLog.STOrgCode);
            para.put("TableName", "dbHead");
        } catch (JSONException e2) {
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            e2.printStackTrace();
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
                if (txtMultilateralTrade.getText().toString().equals("���ó��")) {
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
            jsonBillHead.put("dbHead", newHeadArray);
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

    /**
     * ��ȡ�ֿ���Ϣ
     * by Xuhu
     * ����ֿ��б�����
     */
    private void btnWarehouseClick() throws JSONException {
           JSONObject para = new JSONObject();
        try {
            para.put("FunctionName", "GetWareHouseList");
            para.put("CompanyCode", MainLogin.objLog.CompanyCode);
            para.put("STOrgCode", MainLogin.objLog.STOrgCode);
            para.put("TableName", "warehouse");
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", "");
            Log.d(TAG, "btnWarehouseClick: " + rev.toString());
            if (rev == null) {
                Toast.makeText(this, "��������ͨѶ����", Toast.LENGTH_LONG).show();
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
                Toast.makeText(this, Errmsg, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        }


    }
    private  void deleteInfo() {
        try {
            jsTotal = null;
            jsSerino = null;
            jsBody = null;
            jsonSaveHead = null;
            jsonBillHead = null;
            changeAllEdToEmpty();
            txtDocumentNumber.requestFocus();
            //SaveOk();
            //            IniActivyMemor();// TODO: 2017/7/10 XUHU
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //��������
    private void SaveSaleOrder() throws JSONException,
            org.apache.http.ParseException, IOException {
        if (txtMultilateralTrade.getText().toString().equals("���ó��")) {
            table = new JSONObject();
            JSONArray arrayss = null;
            JSONArray arrayMerge = null;
            arrayss = jsSerino.getJSONArray("Serino");
            arrayMerge = merge(arrayss);
            jsTotal = new JSONObject();
            jsTotal.put("Serino", arrayMerge);
            JSONObject map = new JSONObject();
            Double notnum = 0.0;
            JSONArray arrays = jsTotal.getJSONArray("Serino");
            for (int i = 0; i < arrays.length(); i++) {
//                String totalnum = ((JSONObject) (arrays.get(i))).getString("box");
//                totalnum = Double.valueOf(totalnum).toString();
                Double box = arrays.getJSONObject(i).getDouble("box");
                DecimalFormat decimalFormat = new DecimalFormat(".00");//���췽�����ַ���ʽ�������С������2λ,����0����.
                String totalBox = decimalFormat.format(box);
                notnum += Double.valueOf(totalBox);
//                map.put("NNUMBER",notnum);
                NTOTALNUMBER = decimalFormat.format(notnum);
            }
//            ===��ͷ
//            COTHERCALBODYID == CINCBID
//            COTHERCORPID == CINCORPID
//            COTHERWHID == B.CINVWH ID
//            COUTCALBODYID == COUTCBID
//            COUTCORPID == COUTCORPID
//            CWAREHOUSEID == �ֿ�ID
//            DBILLDATE
//            PK_CALBODY = '��֯'
//            PK_CORP = '��˾'
//            VBILLCODE = ''
//            VUSERDEF1 = VDEF1
//            VUSERDEF2 = VDEF2
            JSONObject tableHead = new JSONObject();
            tableHead.put("COTHERCALBODYID", tmpBillCode);//�Է������֯PK
            tableHead.put("COTHERCORPID", tmpBillCode);//�Է���˾ID
            tableHead.put("COTHERWHID", tmpBillCode);//�����ֿ�ID
            tableHead.put("COUTCALBODYID", tmpBillCode);//���������֯ID
            tableHead.put("COUTCORPID", tmpBillCode);//������˾ID

            tableHead.put("CWAREHOUSEID", CWAREHOUSEID);//�ֿ�id
            tableHead.put("PK_CALBODY", MainLogin.objLog.STOrgCode);//��֯
            tableHead.put("PK_CORP", MainLogin.objLog.CompanyCode);//��˾
            tableHead.put("VBILLCODE", CheckBillCode);//���ݺ�
            String vd1 = Base64Encoder.encode(VDEF1.getBytes("gb2312"));
            String vd2 = Base64Encoder.encode(VDEF2.getBytes("gb2312"));
            tableHead.put("VUSERDEF1", vd1);//
            tableHead.put("VUSERDEF2", vd2);//





//            tableHead.put("RECEIVECODE", tmpBillCode);
//            tableHead.put("CBIZTYPE", CBIZTYPE);
//            tableHead.put("COPERATORID", MainLogin.objLog.UserID);
//            tableHead.put("CRECEIPTTYE", "4331");
//            tableHead.put("CSALECORPID", CSALECORPID);
//            tableHead.put("PK_CORP", MainLogin.objLog.STOrgCode);
//            tableHead.put("VBILLCODE", "");
//            String login_user = MainLogin.objLog.LoginUser.toString();
//            String cuserName = Base64Encoder.encode(login_user.getBytes("gb2312"));
//            tableHead.put("USERNAME", cuserName);
//            String vd3 = Base64Encoder.encode(VDEF5.getBytes("gb2312"));
//            tableHead.put("VDEF5", vd3);
//            tableHead.put("NTOTALNUMBER", NTOTALNUMBER);
            table.put("Head", tableHead);
            JSONObject tableBody = new JSONObject();
            JSONArray bodyArray = new JSONArray();

            JSONArray bodys = jsBody.getJSONArray("dbBody");
            JSONArray arraysSerino = jsTotal.getJSONArray("Serino");
            int y = 0;
            for (int j = 0; j < arraysSerino.length(); j++) {

                for (int i = 0; i < bodys.length(); i++) {

                    if (arraysSerino.getJSONObject(j).getString("invcode").toLowerCase().equals(
                            bodys.getJSONObject(i).getString("invcode"))) {
                        Double box = arraysSerino.getJSONObject(j).getDouble("box");
                        DecimalFormat decimalFormat = new DecimalFormat(".00");//���췽�����ַ���ʽ�������С������2λ,����0����.
                        String totalBox = decimalFormat.format(box);//format ���ص����ַ���
                        JSONObject object = new JSONObject();
//
//                        CBODYWAREHOUSEID = '�ֿ�'
//                        CFIRSTBILLBID == CBILL_BID
//                        CFIRSTBILLHID == CBILLID
//                        CFIRSTTYPE = CTYPECODE
//                        CINVBASID = CINVBASID
//                        CINVENTORYID = COUTINVID
//                        CQUOTEUNITID = CQUOTEUNITID
//                        CRECEIEVEID =
//                                CRECEIVEAREAID = PK_ARRIVEAREA
//                        CSOURCEBILLBID = CBILL_BID
//                        CSOURCEBILLHID = CBILLID
//                        CSOURCETYPE = CTYPECODE
                        object.put("CBODYWAREHOUSEID", bodys.getJSONObject(i).getString("crowno"));//���ֿ�
                        object.put("CFIRSTBILLBID", bodys.getJSONObject(i).getString("crowno"));//Դͷ���ݱ���ID
                        object.put("CFIRSTBILLHID", bodys.getJSONObject(i).getString("crowno"));//Դͷ���ݱ�ͷID
                        object.put("CFIRSTTYPE", bodys.getJSONObject(i).getString("crowno"));//Դͷ��������
                        object.put("CINVBASID", bodys.getJSONObject(i).getString("crowno"));//�������ID
                        object.put("CINVENTORYID", bodys.getJSONObject(i).getString("crowno"));//���ID
                        object.put("CQUOTEUNITID", bodys.getJSONObject(i).getString("crowno"));//���ۼ�����λID
                        object.put("CRECEIEVEID", bodys.getJSONObject(i).getString("crowno"));//�ջ���λ
                        object.put("CRECEIVEAREAID", bodys.getJSONObject(i).getString("crowno"));//�ջ�����
                        object.put("CSOURCEBILLBID", bodys.getJSONObject(i).getString("crowno"));//��Դ���ݱ������к�
                        object.put("CSOURCEBILLHID", bodys.getJSONObject(i).getString("crowno"));//��Դ���ݱ�ͷ���к�
                        object.put("CSOURCETYPE", bodys.getJSONObject(i).getString("crowno"));//��Դ��������


                        object.put("CROWNO", bodys.getJSONObject(i).getString("crowno"));
                        object.put("VFREE4", arraysSerino.getJSONObject(j).getString("vfree4"));//�����ֲ��
                        object.put("VSOURCEROWNO", bodys.getJSONObject(i).getString("crowno"));
                        object.put("VSOURCERECEIVECODE", tmpBillCode);
                        object.put("VRECEIVEPOINTID", bodys.getJSONObject(i).getString("crecaddrnode"));
                        object.put("CRECEIVECUSTID", bodys.getJSONObject(i).getString("creceiptcorpid"));
                        object.put("CRECEIVEAREAID", bodys.getJSONObject(i).getString("creceiptareaid"));
//                        object.put("DDELIVERDATE", bodys.getJSONObject(i).getString("ddeliverdate"));
                        object.put("CBIZTYPE", CBIZTYPE);//��ͷ
                        object.put("CCUSTBASDOCID", CCUSTBASDOCID);
                        object.put("CCUSTMANDOCID", CCUSTOMERID);//��ͷcustomerID
                        object.put("CINVBASDOCID", bodys.getJSONObject(i).getString("cinvbasdocid"));
                        object.put("CINVMANDOCID", bodys.getJSONObject(i).getString("cinventoryid"));
                        object.put("CRECEIVECUSTBASID", CCUSTBASDOCID);//�Լ���ȡ
                        object.put("CSENDCALBODYID", bodys.getJSONObject(i).getString("cadvisecalbodyid"));
                        object.put("CSENDWAREID", CWAREHOUSEID);//�ֿ�
                        object.put("CSOURCEBILLBODYID", bodys.getJSONObject(i).getString("corder_bid"));
                        object.put("CSOURCEBILLID", bodys.getJSONObject(i).getString("csaleid"));
                        object.put("NNUMBER", totalBox);
                        object.put("PK_SENDCORP", bodys.getJSONObject(i).getString("pk_corp"));
                        object.put("VBATCHCODE", arraysSerino.getJSONObject(j).getString("batch"));
                        String add = bodys.getJSONObject(i).getString("vreceiveaddress");
                        String adds = Base64Encoder.encode(add.getBytes("gb2312"));
                        object.put("VRECEIVEADDRESS", adds);
                        object.put("VRECEIVEPERSON", MainLogin.objLog.LoginUser);
                        bodyArray.put(object);
                        y++;
                    }
                }
            }
            tableBody.put("ScanDetails", bodyArray);
            table.put("Body", tableBody);
            table.put("GUIDS", UUID.randomUUID().toString());
            Log.d(TAG, "SaveSaleOrder: " + MainLogin.appTime);
            table.put("OPDATE", MainLogin.appTime);
            Log.d(TAG, "XXXXXX: " + table.toString());
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            SaveThread saveThread = new SaveThread(table, "SaveSaleReceive", mHandler, HANDER_SAVE_RESULT);
            Thread thread = new Thread(saveThread);
            thread.start();
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
                case HANDER_DEPARTMENT:
                    JSONObject json = (JSONObject) msg.obj;
                    try {
                        if (json.getBoolean("Status")) {
                            JSONArray  val  = json.getJSONArray("department");
                            JSONObject temp = new JSONObject();
                            temp.put("department", val);
                            Intent ViewGrid = new Intent(MultilateralTrade.this, DepartmentListAct.class);
                            ViewGrid.putExtra("myData", temp.toString());
                            startActivityForResult(ViewGrid, 96);
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
                                showResultDialog(MultilateralTrade.this, saveResult.getString("ErrMsg"));
                                deleteInfo();
                                Log.d(TAG, "messageTrue" + saveResult.getString("ErrMsg"));
                            } else {
                                showResultDialog(MultilateralTrade.this, saveResult.getString("ErrMsg"));
                                Log.d(TAG, "messageFALSE" + saveResult.getString("ErrMsg"));
                            }
                        } else {
                            showResultDialog(MultilateralTrade.this, "�����ύʧ��!");
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            return;
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
     * progressDialog ��ʧ
     */
    private void progressDialogDismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * ���
     */
    private  void changeAllEdToEmpty(){
        txtWareHouse.setText("");
        txtDocumentNumber.setText("");
        txtBillDate.setText("");
//        txtCustomer.setText("");

    }

    /**
     * ��������sku��ͬ�ϲ�����
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
                        String vfree4 = newJsonObjectI.get("vfree4").toString();

                        String invcodeJ = newJsonObjectJ.get("invcode").toString();
                        String batchJ = newJsonObjectJ.get("batch").toString();
                        String boxJ = newJsonObjectJ.get("box").toString();

                        if (invcode.equals(invcodeJ)&&batch.equals(batchJ)) {
                            double newValue = Double.parseDouble(box) + Double.parseDouble(boxJ);
                            JSONObject newObject = new JSONObject();
//                            if (Build.VERSION.SDK_INT >= 19) {
//                               arrayTemp.remove(j);
//                            }else{
//
//                            }
                            Utils.removeJsonArray(j,arrayTemp);
                            newObject.put("invcode", invcode);
                            newObject.put("batch", batch);
                            newObject.put("invname", invname);
                            newObject.put("serino", serino);
                            newObject.put("sno", sno);
                            newObject.put("invtype", invtype);
                            newObject.put("invspec", invspec);
                            newObject.put("vfree4", vfree4);
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


    /**
     * ���浯������
     */

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(MultilateralTrade.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���ý���������ʽΪԲ��ת���Ľ�����
        progressDialog.setCancelable(false);// �����Ƿ����ͨ�����Back��ȡ��
        progressDialog.setCanceledOnTouchOutside(false);// �����ڵ��Dialog���Ƿ�ȡ��Dialog������
        // progressDialog.setIcon(R.drawable.ic_launcher);
        // ������ʾ��title��ͼ�꣬Ĭ����û�еģ����û������title�Ļ�ֻ����Icon�ǲ�����ʾͼ���
        progressDialog.setTitle("���浥��");
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

}