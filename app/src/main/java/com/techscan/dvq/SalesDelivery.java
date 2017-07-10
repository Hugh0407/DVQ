package com.techscan.dvq;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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


public class SalesDelivery extends Activity {
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
    String CWAREHOUSEID = "";    //�����֯
    String CSENDWAREID = "";
    String sBillCodes = "";//���ݺŲ�ѯ
    String sBeginDate = "";//�Ƶ����ڿ�ʼ��ѯ
    String sEndDate = "";//�Ƶ����ڽ�����ѯ
    JSONObject table = null;
    JSONObject jsBody = null;
    JSONObject jsBoxTotal = null;
    JSONObject jsSerino = null;
    List<SaleOutGoods> saleOutGoodsLists = null;
    String tmpWHStatus = "";//�ֿ��Ƿ����û�λ
    EditText txtSalesDelPDOrder;
    private writeTxt writeTxt;
    EditText txtSalesDelRdcl;//��������
    EditText txtSalesDelCD;
    TextView tvCustomer;
    ImageButton btnSalesDelPDOrder;
    Button btnSalesDelExit;
    Button btnSalesDelScan;
    Button btnSalesDelSave;
    boolean NoScanSave = false;
    private ArrayList<String> ScanedBarcode = new ArrayList<String>();

    String sBillAccID = "";
    String sBillCorpPK = "";
    String sBillCode = "";
    String SaleFlg = "";
    String csaleid = "";

    TextView tvSalesDelWare;
    TextView tvSalesDelWH;
    EditText txtSalesDelWH;
    ImageButton btnSalesDelWH;

    int TaskCount = 0;
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
//	String sCompanyCode="";
//	String sOrgCode="";

    TextView tvSaleOutSelect;
    ImageButton btnSaleOutSelect;
    private UUID uploadGuid = null;
    private String[] BillTypeNameList = null;

    private String[] WHNameList = null;
    //	private String[] WHCodeList = null;
    private String[] WHIDList = null;
    private String[] CDIDList = null;
    private String[] CDNameList = null;
    private AlertDialog CDSelectButton = null;
    private AlertDialog BillTypeSelectButton = null;
    private AlertDialog WHSelectButton = null;
    private ButtonOnClick buttonOnClick = new ButtonOnClick(0);
    private String tmpWarehousePK = "";
    private String tmpCorpPK = "";
    private String tmpBillCode = "";
    private String tmpCustName = "";
    private String tmpBillDate = "";
    private String rdflag = "1";
    private String GetBillBFlg = "1";
    //�����ñ�ͷ��Ϣ
    private JSONObject jsonSaveHead = null;
    private JSONObject jsonBillHead = null;
    //��������
    private JSONObject jsonBillBodyTask = null;
    private JSONObject jsonBillBodyTask2 = null;
    private String tmpAccID = "";
    private List<Map<String, Object>> lstSaveBody = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_delivery);
        this.setTitle("���۳���");
        initView();
        SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_SETTING,
                Activity.MODE_PRIVATE);

        WhNameA = sharedPreferences.getString("WhCode", "");
        WhNameB = sharedPreferences.getString("AccId", "");
//				sCompanyCode=sharedPreferences.getString("CompanyCode", "");
//		        sOrgCode=sharedPreferences.getString("OrgCode", "");
        ClearBillDetailInfoShow();
        SetBillType();


        tvSaleOutSelect.requestFocus();

        jsonSaveHead = new JSONObject();
    }

    private void GetWHPosStatus() throws JSONException {
        JSONObject para = new JSONObject();
        para.put("FunctionName", "GetWHPosStatus");
        para.put("WareHouse", tmpWarehousePK);

        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }
        JSONObject rev = null;
        try {
            rev = Common.DoHttpQuery(para, "CommonQuery", tmpAccID);
        } catch (ParseException e) {

            Toast.makeText(this, "��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        } catch (IOException e) {

            Toast.makeText(this, "��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }

        if (rev == null) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }


        if (rev.getBoolean("Status")) {
            JSONArray val = rev.getJSONArray("position");
            if (val.length() < 1) {
                Toast.makeText(this, "��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }

            String WHStatus;
            JSONObject temp = val.getJSONObject(0);

            WHStatus = temp.getString("csflag");

            tmpWHStatus = WHStatus;
            return;
        } else {
            Toast.makeText(this, "��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 88) {
            switch (resultCode) {
                case 1:         // ���ǵ������ⵥ�����б��صĵط�
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        if (bundle != null) {
                            SerializableMap ResultMap = new SerializableMap();
                            ResultMap = (SerializableMap) bundle.get("ResultBillInfo");
                            Map<String, Object> mapBillInfo = ResultMap.getMap();
                            //�󶨱����ñ�ͷ
                            jsonSaveHead = new JSONObject();
                            //wuqiong
                            try {
                                jsonSaveHead = Common.MapTOJSONOBject(mapBillInfo);
//                                Log.d(TAG, "onActivityResult: "+jsonSaveHead.toString());
                            } catch (JSONException e) {
                                Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                //ADD CAIXY TEST START
                                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                                //ADD CAIXY TEST END
                            }
                            try {
                                SaleFlg = jsonSaveHead.getString("saleflg");
                            } catch (JSONException e2) {
                                // TODO Auto-generated catch block
                                e2.printStackTrace();
                            }

                            //����ʾ������Ϣ
                            if (!BindingBillDetailInfo(mapBillInfo)) {
                                return;
                            }
                            try {
                                //��ñ�ͷ��Ϣ
                                GetBillHeadDetailInfo(SaleFlg);
                                //��ñ�����Ϣ
                                GetBillBodyDetailInfo(SaleFlg);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                            try {
                                GetTaskCount();
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
//                            if(GetBillBFlg.equals("1"))
//                            {
//
//                                SetRDCL();
//                                tmpCdTypeID = "";
//                                try {
//                                    GetSalesDelWH();
//                                } catch (JSONException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                }
//                            }
                        }
                    }
                    //ADD BY WUQIONG 2015/04/27
                    break;
//                case 2:         // �����շ���𷵻صĵط�
//                    if (data != null)
//                    {
//                        Bundle bundle = data.getExtras();
//                        if (bundle != null)
//                        {
//
//                            tmprdCode = data.getStringExtra("Code");
//                            tmprdID = data.getStringExtra("RdID");
//                            tmprdName = data.getStringExtra("Name");
//                            txtSalesDelRdcl.setText(tmprdName);
//
//                        }
//                        else
//                        {
//                            txtSalesDelRdcl.setText("");
//                        }
//
//                    }
                //ADD BY WUQIONG 2015/04/27
//                    break;
                default:
                    break;
            }
        } else if (requestCode == 86) {
            if (resultCode == 6) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        saleOutGoodsLists = bundle.getParcelableArrayList("SaleGoodsLists");
//                        SerializableList ResultBodyList = new SerializableList();
//                        ResultBodyList = (SerializableList)bundle.get("SaveBodyList");
//                        lstSaveBody = ResultBodyList.getList();
//                        ScanedBarcode = bundle.getStringArrayList("ScanedBarcode");

//                        try {
////                            jsonBillBodyTask = new JSONObject(bundle.getString("ScanTaskJson"));
//                        } catch (JSONException e) {
//                            // TODO Auto-generated catch block
//                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//                            //ADD CAIXY TEST START
//                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                            //ADD CAIXY TEST END
//                            e.printStackTrace();
//                        }
                    }
                }
            }
        }
        //�������ڲ�ѯby XuHu
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
            if (resultCode!=24){
                return;
            }
            Bundle bundle = data.getExtras();
            String saleBox = bundle.getString("box");

            String saleSerinno = bundle.getString("serino");
            String dbBody = bundle.getString("body");
            ScanedBarcode = bundle.getStringArrayList("ScanedBarcode");

            try
            {

                this.jsBody = new JSONObject(dbBody);
//                this.jsHead = new JSONObject(headJS);
                Log.d(TAG, "AAAAAA: "+jsBody.toString());
                this.jsSerino = new JSONObject(saleSerinno);
                Log.d(TAG, "AAAAAA: "+jsSerino.toString());
//				this.jsBoxTotal = new JSONObject(boxJS);
                this.jsBoxTotal = null;
            }
            catch (JSONException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();

                Toast.makeText(SalesDelivery.this, e.getMessage() ,
                        Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
            }
        }

        //�ֿ��б���Ϣ
        else if (requestCode == 97) {
            if (resultCode == 13) {
                String warehousePK1 = data.getStringExtra("result1");
                String warehousecode = data.getStringExtra("result2");
                String warehouseName = data.getStringExtra("result3");
                CWAREHOUSEID = warehousePK1;
                txtSalesDelWH.setText(warehouseName);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Map<String, Object> GetBillDetailInfoByBillCode(String sAccID, String sCorpPK, String sBillCode, String sSaleFlg) {
        SaleFlg = sSaleFlg;
        JSONObject para = new JSONObject();
        Map<String, Object> mapBillInfo = new HashMap<String, Object>();

        try {
            if (tvSaleOutSelect.getText().toString().equals("���۳���")) {
                para.put("FunctionName", "GetSalereceiveHead");
                para.put("CorpPK", sCorpPK);
                para.put("BillCode", sBillCode);
            }

        } catch (JSONException e2) {
            // TODO Auto-generated catch block
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            e2.printStackTrace();
            return null;
        }
        try {
            para.put("TableName", "dbHead");
        } catch (JSONException e2) {
            // TODO Auto-generated catch block
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return null;
        }

        JSONObject jas;
        try {
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return null;
            }
            jas = Common.DoHttpQuery(para, "CommonQuery", sAccID);
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return null;
        }

        //��ȡ�õĵ�����Ϣ�󶨵�ListView��
        try {
            if (jas == null) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return null;
            }

            if (!jas.getBoolean("Status")) {
                String errMsg = "";
                if (jas.has("ErrMsg")) {
                    errMsg = jas.getString("ErrMsg");
                } else {
                    errMsg = getString(R.string.WangLuoChuXianWenTi);
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return null;
            }

            //�󶨵�map
            mapBillInfo = new HashMap<String, Object>();
            JSONArray jsarray = jas.getJSONArray("dbHead");

            jas = jsarray.getJSONObject(0);

            if (tvSaleOutSelect.getText().toString().equals("���۳���")) {
                //mapBillInfo.put("pk_corp", jas.getString("pk_corp"));//��˾PK
                mapBillInfo.put("pk_corp", jas.getString("pk_corp"));
                mapBillInfo.put("custname", jas.getString("custname"));
                mapBillInfo.put("pk_cubasdoc", jas.getString("pk_cubasdoc"));
                mapBillInfo.put("pk_cumandoc", jas.getString("pk_cumandoc"));
                mapBillInfo.put("billID", jas.getString("csalereceiveid"));
                mapBillInfo.put("billCode", jas.getString("vreceivecode"));
                mapBillInfo.put("AccID", sAccID);
                mapBillInfo.put("vdef11", jas.getString("vdef11"));
                mapBillInfo.put("vdef12", jas.getString("vdef12"));
                mapBillInfo.put("vdef13", jas.getString("vdef13"));
                mapBillInfo.put("saleflg", "");
                if (sAccID.equals("A"))
                    mapBillInfo.put("coperatorid", MainLogin.objLog.UserID);//������
                else
                    mapBillInfo.put("coperatorid", MainLogin.objLog.UserIDB);//������
                mapBillInfo.put("ctransporttypeid", jas.getString("ctransporttypeid"));//���䷽ʽID
                mapBillInfo.put("cbiztype", jas.getString("cbiztype"));
            }

//            if(tvSaleOutSelect.getText().toString().equals("�˻�����"))
//            {
//                //mapBillInfo.put("pk_corp", jas.getString("pk_corp"));
//                mapBillInfo.put("pk_corp", jas.getString("pk_corp"));
//                mapBillInfo.put("custname", jas.getString("custname"));
//                mapBillInfo.put("pk_cubasdoc", jas.getString("pk_cubasdocc"));
//                mapBillInfo.put("pk_cumandoc", jas.getString("ccustomerid"));
//                mapBillInfo.put("billID", jas.getString("cgeneralhid"));
//                mapBillInfo.put("billCode", jas.getString("vbillcode"));
//                mapBillInfo.put("AccID", sAccID);
//                mapBillInfo.put("vdef11", jas.getString("vuserdef11"));
//                mapBillInfo.put("vdef12", jas.getString("vuserdef12"));
//                mapBillInfo.put("vdef13", jas.getString("vuserdef13"));
//                mapBillInfo.put("saleflg", "");
//                if(sAccID.equals("A"))
//                {
//                    mapBillInfo.put("coperatorid", MainLogin.objLog.UserID);//������
//                    mapBillInfo.put("ctransporttypeid", "0001AA100000000003U7");//���䷽ʽID
//                }
//                else
//                {
//                    mapBillInfo.put("coperatorid", MainLogin.objLog.UserIDB);//������
//                    mapBillInfo.put("ctransporttypeid", "0001DD10000000000XQT");//���䷽ʽID
//                }
//                mapBillInfo.put("cbiztype", jas.getString("cbiztype"));
//            }

//            if(tvSaleOutSelect.getText().toString().equals("�˻ز���"))
//            {
//                if(sSaleFlg.equals("T"))
//                {
//                    mapBillInfo.put("pk_corp", jas.getString("pk_corp"));
//                    mapBillInfo.put("custname", jas.getString("custname"));
//                    mapBillInfo.put("pk_cubasdoc", jas.getString("pk_cubasdoc"));
//                    mapBillInfo.put("pk_cumandoc", jas.getString("pk_cumandoc"));
//                    mapBillInfo.put("billID", jas.getString("pk_take"));
//                    mapBillInfo.put("billCode", jas.getString("vreceiptcode"));
//                    mapBillInfo.put("AccID", sAccID);
//                    mapBillInfo.put("vdef11", jas.getString("vdef11"));
//                    mapBillInfo.put("vdef12", jas.getString("vdef12"));
//                    mapBillInfo.put("vdef13", jas.getString("vdef13"));
//                    mapBillInfo.put("saleflg", "T");
//
//                    if(sAccID.equals("A"))
//                    {
//                        mapBillInfo.put("coperatorid", MainLogin.objLog.UserID);//������
//                        mapBillInfo.put("ctransporttypeid", "0001AA100000000003U7");//���䷽ʽID
//                    }
//                    else
//                    {
//                        mapBillInfo.put("coperatorid", MainLogin.objLog.UserIDB);//������
//                        mapBillInfo.put("ctransporttypeid", "0001DD10000000000XQT");//���䷽ʽID
//                    }
//                    mapBillInfo.put("cbiztype", jas.getString("cbiztype"));
//                }
//                else if (sSaleFlg.equals("D"))
//                {
//                    mapBillInfo.put("pk_corp", jas.getString("pk_corp"));
//                    mapBillInfo.put("custname", jas.getString("custname"));
//                    mapBillInfo.put("pk_cubasdoc", jas.getString("pk_cubasdoc"));
//                    mapBillInfo.put("pk_cumandoc", jas.getString("ccustomerid"));
//                    mapBillInfo.put("billID", jas.getString("csaleid"));
//                    mapBillInfo.put("billCode", jas.getString("vreceiptcode"));
//                    mapBillInfo.put("AccID", sAccID);
//                    mapBillInfo.put("vdef11", jas.getString("vdef11"));
//                    mapBillInfo.put("vdef12", jas.getString("vdef12"));
//                    mapBillInfo.put("vdef13", jas.getString("vdef13"));
//                    mapBillInfo.put("saleflg", "D");
//
//                    if(sAccID.equals("A"))
//                    {
//                        mapBillInfo.put("coperatorid", MainLogin.objLog.UserID);//������
//                        mapBillInfo.put("ctransporttypeid", "0001AA100000000003U7");//���䷽ʽID
//                    }
//                    else
//                    {
//                        mapBillInfo.put("coperatorid", MainLogin.objLog.UserIDB);//������
//                        mapBillInfo.put("ctransporttypeid", "0001DD10000000000XQT");//���䷽ʽID
//                    }
//                    mapBillInfo.put("cbiztype", jas.getString("cbiztype"));
//                }
//            }

            //�����ñ�ͷJSONObject����---����
            return mapBillInfo;
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return null;
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return null;
        }
    }

    private void SetRDCL() {
        if (tvSaleOutSelect.getText().toString().equals("���۳���")) {
            tmprdCode = "202";
            if (tmpAccID.equals("A")) {
                tmprdID = "0001AA100000000003VD";    //
            } else if (tmpAccID.equals("B")) {
                tmprdID = "0001DD10000000000XR8";    //
            }
//			tmprdName = "���۳���";
//			txtSalesDelRdcl.setText(tmprdName);
        }

//        if(tvSaleOutSelect.getText().toString().equals("�˻�����"))
//        {
//            tmprdCode = "210";
//            if (tmpAccID.equals("A")) {
//                tmprdID = "0001AA100000000003VL";        //
//            } else if (tmpAccID.equals("B")) {
//                tmprdID = "0001DD10000000000XRG";    //
//            }
//            tmprdName = "�����˻�";
//            txtSalesDelRdcl.setText(tmprdName);
//        }

//        if(tvSaleOutSelect.getText().toString().equals("�˻ز���"))
//        {
//            tmprdCode = "210";
//            if (tmpAccID.equals("A")) {
//                tmprdID = "0001AA100000000003VL";        //
//            } else if (tmpAccID.equals("B")) {
//                tmprdID = "0001DD10000000000XRG";    //
//            }
//            tmprdName = "�����˻�";
//            txtSalesDelRdcl.setText(tmprdName);
//        }
    }


    //���ݶ�����ͷ�õ�������ϸ
    private void GetBillBodyDetailInfo2(String sSaleFlg) {
        GetBillBFlg = "0";
        if (tmpAccID == null || tmpAccID.equals(""))
            return;

        JSONObject para = new JSONObject();
        //Map<String,Object> mapBillBody = new HashMap<String,Object>();
        try {

            if (tvSaleOutSelect.getText().toString().equals("���۳���")) {
                para.put("FunctionName", "GetSalereceiveBody");
                para.put("BillCode", tmpBillCode);
                para.put("CorpPK", "4100");
            }

            if (tvSaleOutSelect.getText().toString().equals("�˻�����")) {
                para.put("FunctionName", "GetSaledB");
                para.put("BillCode", tmpBillCode);
                para.put("CorpPK", tmpCorpPK);
            }

            if (tvSaleOutSelect.getText().toString().equals("�˻ز���")) {
                if (sSaleFlg.equals("T")) {
                    para.put("FunctionName", "GetSaleTakeBody");
                    para.put("BillCode", tmpBillCode);
                    para.put("CorpPK", tmpCorpPK);
                } else if (sSaleFlg.equals("D"))

                {
                    para.put("FunctionName", "GetSaleOutBody");
                    para.put("BillCode", tmpBillCode);
                    para.put("CorpPK", tmpCorpPK);
                }
            }


        } catch (JSONException e2) {
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            e2.printStackTrace();
            return;
        }
        try {
            para.put("TableName", "dbBody");
        } catch (JSONException e2) {
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }

        JSONObject jas;
        try {
            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }
            jas = Common.DoHttpQuery(para, "CommonQuery", tmpAccID);
            //txtSalesDelManualNo.setEnabled(true);
//			txtSalesDelPos.setEnabled(true);
            txtSalesDelRdcl.setEnabled(true);

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }
        try {
            if (jas == null) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
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
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }
            jsonBillBodyTask2 = new JSONObject();
            //jsonBillBodyTask = jas;
            //��Ҫ�޸����ݽṹ

            JSONArray jsarray = jas.getJSONArray("dbBody");

            JSONArray NewBodyarray = new JSONArray();
            JSONObject NewBodJSON = null;

            for (int i = 0; i < jsarray.length(); i++) {
                JSONObject tempJso = jsarray.getJSONObject(i);
                NewBodJSON = new JSONObject();

                if (tvSaleOutSelect.getText().toString().equals("���۳���")) {
                    NewBodJSON.put("vfree1", tempJso.getString("vfree1"));
                    NewBodJSON.put("pk_measdoc", tempJso.getString("pk_measdoc"));
                    NewBodJSON.put("measname", tempJso.getString("measname"));
                    NewBodJSON.put("invcode", tempJso.getString("invcode"));
                    NewBodJSON.put("invname", tempJso.getString("invname"));
                    NewBodJSON.put("invspec", tempJso.getString("invspec"));
                    NewBodJSON.put("invtype", tempJso.getString("invtype"));
                    NewBodJSON.put("billcode", tmpBillCode);
                    NewBodJSON.put("batchcode", tempJso.getString("vbatchcode"));
                    NewBodJSON.put("invbasdocid", tempJso.getString("cinvbasdocid"));
                    NewBodJSON.put("invmandocid", tempJso.getString("cinvmandocid"));
                    String number = tempJso.getString("nnumber");
                    String outnumber = tempJso.getString("ntotaloutinvnum");
                    if (!outnumber.equals("null")) {
                        outnumber = outnumber.replaceAll("\\.0", "");
                    } else {
                    }
                    if (!number.equals("null")) {
                        number = number.replaceAll("\\.0", "");
                    } else {
                    }
                    //int shouldoutnum = Integer.valueOf(number).intValue() - Integer.valueOf(outnumber).intValue();
                    NewBodJSON.put("number", number);
                    NewBodJSON.put("outnumber", outnumber);
                    NewBodJSON.put("sourcerowno", tempJso.getString("vsourcerowno"));
                    NewBodJSON.put("sourcehid", tempJso.getString("csourcebillid"));
                    NewBodJSON.put("sourcebid", tempJso.getString("csourcebillbodyid"));
                    NewBodJSON.put("sourcehcode", tempJso.getString("vsourcereceivecode"));
                    NewBodJSON.put("sourcetype", tempJso.getString("vsourcetype"));
                    NewBodJSON.put("crowno", tempJso.getString("crowno"));
                    NewBodJSON.put("billhid", tempJso.getString("csalereceiveid"));
                    NewBodJSON.put("billbid", tempJso.getString("csalereceiveid_bid"));
                    NewBodJSON.put("billhcode", tmpBillCode);
                    NewBodJSON.put("billtype", "4331");
                    NewBodJSON.put("ddeliverdate", tempJso.getString("ddeliverdate"));
                    NewBodJSON.put("pk_defdoc6", tempJso.getString("pk_defdoc6"));
                    NewBodJSON.put("def6", tempJso.getString("vdef6"));
                }

                NewBodyarray.put(NewBodJSON);
            }
            jsonBillBodyTask2.put("Status", true);
            jsonBillBodyTask2.put("dbBody", NewBodyarray);
            GetBillBFlg = "1";


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }

    }

    //��ñ�����Ϣ
    private void GetBillBodyDetailInfo(String sSaleFlg) {
        GetBillBFlg = "0";
//        if(tmpAccID==null || tmpAccID.equals(""))
//            return;

        JSONObject para = new JSONObject();
        //Map<String,Object> mapBillBody = new HashMap<String,Object>();
        try {

            if (tvSaleOutSelect.getText().toString().equals("���۳���")) {
                para.put("FunctionName", "GetSaleOutBodyNew");
                para.put("BillCode", tmpBillCode);
                para.put("CSALEID", csaleid);
                para.put("CorpPK", "4100");
                Log.d(TAG, "GetBillBodyDetailInfo: " + tmpBillCode);
            }

        } catch (JSONException e2) {
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            e2.printStackTrace();
            return;
        }
        try {
            para.put("TableName", "dbBody");
        } catch (JSONException e2) {
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
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
            Log.d(TAG, "GetBillBodyInfo: " + jas.toString());

        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }
        try {
            if (jas == null) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
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
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }
            jsonBillBodyTask = new JSONObject();
            //jsonBillBodyTask = jas;
            //��Ҫ�޸����ݽṹ

            JSONArray jsarray = jas.getJSONArray("dbBody");

            JSONArray newBodyArray = new JSONArray();
            JSONObject newBodyJSON = null;

            for (int i = 0; i < jsarray.length(); i++) {
                JSONObject tempJso = jsarray.getJSONObject(i);
                newBodyJSON = new JSONObject();

                if (tvSaleOutSelect.getText().toString().equals("���۳���")) {
                    newBodyJSON.put("measname", tempJso.getString("measname"));
                    newBodyJSON.put("invcode", tempJso.getString("invcode"));
                    newBodyJSON.put("invname", tempJso.getString("invname"));
                    newBodyJSON.put("invspec", tempJso.getString("invspec"));
                    newBodyJSON.put("invtype", tempJso.getString("invtype"));
                    newBodyJSON.put("crowno", tempJso.getString("crowno"));
                    newBodyJSON.put("batchcode", "");
                    //���۶�������ID
                    newBodyJSON.put("csourcebillbodyid", tempJso.getString("corder_bid"));
                    //��������ID
                    newBodyJSON.put("csourcebillid", tempJso.getString("csaleid"));
                    newBodyJSON.put("pk_sendcorp", tempJso.getString("pk_corp"));
                    //ע���ַ
                    newBodyJSON.put("vreceiveaddress", tempJso.getString("vreceiveaddress"));
                    //���ID
                    newBodyJSON.put("cinvmandocid", tempJso.getString("cinventoryid"));
                    //���鷢�������֯
                    newBodyJSON.put("csendcalbodyid", tempJso.getString("cadvisecalbodyid"));
                    newBodyJSON.put("billcode", tmpBillCode);
                    //�����������
                    newBodyJSON.put("cinvbasdocid", tempJso.getString("cinvbasdocid"));
                    //creceeiptareaid 
//                    newBodyJSON.put("invmandocid", tempJso.getString("cinvmandocid"));
                    String number = tempJso.getString("nnumber");
                    if (!number.equals("null")) {
                        number = number.replaceAll("\\.0", "");
                    } else {
                    }
                    newBodyJSON.put("number", number);
                    newBodyJSON.put("sourcehid", tempJso.getString("csourcebillid"));
                    newBodyJSON.put("sourcebid", tempJso.getString("csourcebillbodyid"));
//                    newBodyJSON.put("billhid", tempJso.getString("csalereceiveid"));
//                    newBodyJSON.put("billbid", tempJso.getString("csalereceiveid_bid"));
                    newBodyJSON.put("billcode", tmpBillCode);
//                    newBodyJSON.put("billtype", "4331");
                    newBodyJSON.put("ddeliverdate", tempJso.getString("ddeliverdate"));
                }
                newBodyArray.put(newBodyJSON);
            }

            jsonBillBodyTask.put("Status", true);
            jsonBillBodyTask.put("dbBody", newBodyArray);

            GetBillBFlg = "1";


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }

    }

    //��ȡ������ͷ��Ϣ
    private void GetBillHeadDetailInfo(String sSaleFlg) {
        GetBillBFlg = "0";
//        if(tmpAccID==null || tmpAccID.equals(""))
//            return;

        JSONObject para = new JSONObject();
        //Map<String,Object> mapBillBody = new HashMap<String,Object>();
        try {

            if (tvSaleOutSelect.getText().toString().equals("���۳���")) {
                para.put("FunctionName", "GetSaleOutHeadNew");
                para.put("CSALEID", csaleid);
                para.put("BillCode", tmpBillCode);
                para.put("CorpPK", "4100");
                Log.d(TAG, "GetBillHeadDetailInfo: " + csaleid);
            }

        } catch (JSONException e2) {
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            e2.printStackTrace();
            return;
        }
        try {
            para.put("TableName", "dbHead");
        } catch (JSONException e2) {
            Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
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
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }
        try {
            if (jas == null) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
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
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }
            jsonBillHead = new JSONObject();
            //jsonBillBodyTask = jas;
            //��Ҫ�޸����ݽṹ

            JSONArray jsarray = jas.getJSONArray("dbHead");

            JSONArray newHeadArray = new JSONArray();
            JSONObject newHeadJSON = null;

            for (int i = 0; i < jsarray.length(); i++) {
                JSONObject tempJso = jsarray.getJSONObject(i);
                newHeadJSON = new JSONObject();

                if (tvSaleOutSelect.getText().toString().equals("���۳���")) {
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
            GetBillBFlg = "1";
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        } catch (Exception ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }

    }


    //�󶨶�����ͷ��Ϣ
    private boolean BindingBillDetailInfo(Map<String, Object> mapBillInfo) {
//		String CompanyCode = "";
//		if (tmpAccID.equals("A")) {
//			CompanyCode = sCompanyCode;
//		} else if (tmpAccID.equals("B")) {
//			CompanyCode = "1";
//		}
//        tmpAccID = mapBillInfo.get("AccID").toString();
//        //tmpWarehousePK = mapBillInfo.get("pk_stordoc").toString();
//        tmpCorpPK = mapBillInfo.get("pk_corp").toString();
        csaleid = mapBillInfo.get("Csaleid").toString();
        tmpBillCode = mapBillInfo.get("BillCode").toString();
        tmpCustName = mapBillInfo.get("CustName").toString();
        tmpBillDate = mapBillInfo.get("BillDate").toString();
        txtSalesDelPDOrder.setText(tmpBillCode);
        txtSalesDelCD.setText(tmpCustName);
        txtSalesDelRdcl.setText(tmpBillDate);

//		if(!Common.CheckUserRole(tmpAccID, tmpCorpPK, "40080802"))
//		{
//			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//			Toast.makeText(SalesDelivery.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian, Toast.LENGTH_LONG).show();
//			return false;
//		}

        return true;
    }

    //��ն�����ͷ��Ϣ
    private void ClearBillDetailInfoShow() {
//		tvSalesDelBillCodeName.setText("");
//		tvSalesDelAccIDName.setText("");

//		tvSalesDelCorpName.setText("");

//		tvSalesDelBillCode.setText("");
//		tvSalesDelAccID.setText("");
        //tvSalesDelWare.setText("");
//		tvSalesDelCorp.setText("");
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

//				txtSalesDelPos.requestFocus();
            }
//���ͷ�ʽ
//			if(dialog.equals(CDSelectButton))
//			{
//				txtSalesDelCD.setText(CDNameList[index].toString());
//				tmpCdTypeID = CDIDList[index].toString();
////				txtSalesDelPos.requestFocus();
//			}
        }


    }


    private void SetCDtype() {

        if ((tmpAccID == null) || (tmpAccID.equals(""))) {
            Toast.makeText(this, "������Ϣû�л�ò���ѡ�����䷽ʽ", Toast.LENGTH_SHORT).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            this.txtSalesDelPDOrder.requestFocus();

            return;
        }
        if (tvSaleOutSelect.getText().toString().equals("���۳���")) {
            this.CDNameList = new String[2];
            this.CDIDList = new String[2];
            this.CDNameList[0] = "����";
            this.CDNameList[1] = "�ͻ�";
            if (tmpAccID.equals("A")) {
                CDIDList[0] = "0001AA100000000003U4";
                CDIDList[1] = "0001AA100000000003U5";
            } else if (tmpAccID.equals("B")) {
                CDIDList[0] = "0001DD10000000000XQQ";
                CDIDList[1] = "0001DD10000000000XQR";
            }
        }
        if (tvSaleOutSelect.getText().toString().equals("�˻�����")) {
            CDNameList = new String[1];
            CDIDList = new String[1];
            CDNameList[0] = "�˻�";
            if (tmpAccID.equals("A")) {
                CDIDList[0] = "0001AA100000000003U7";
            } else if (tmpAccID.equals("B")) {
                CDIDList[0] = "0001DD10000000000XQT";
            }
        }
        if (tvSaleOutSelect.getText().toString().equals("�˻ز���")) {
            CDNameList = new String[1];
            CDIDList = new String[1];
            CDNameList[0] = "�˻�";
            if (tmpAccID.equals("A")) {
                CDIDList[0] = "0001AA100000000003U7";
            } else if (tmpAccID.equals("B")) {
                CDIDList[0] = "0001DD10000000000XQT";
            }
        }
//		showCDChoiceDialog();
    }

//	private void showCDChoiceDialog()
//	{
//		this.CDSelectButton = new AlertDialog.Builder(this).setTitle("ѡ�����䷽ʽ").setSingleChoiceItems(this.CDNameList, -1, this.buttonOnClick).setNegativeButton(R.string.QuXiao, this.buttonOnClick).show();
//	}

    private void showSingleChoiceDialog() {

        BillTypeSelectButton = new AlertDialog.Builder(this).setTitle("ѡ�񵥾�����").setSingleChoiceItems(
                BillTypeNameList, -1, buttonOnClick).setNegativeButton(R.string.QuXiao, buttonOnClick).show();
    }


    private void showWHChoiceDialog() {

        WHSelectButton = new AlertDialog.Builder(this).setTitle(R.string.XuanZeCangKu).setSingleChoiceItems(
                WHNameList, -1, buttonOnClick).setNegativeButton(R.string.QuXiao, buttonOnClick).show();
    }

    private void SetBillType() {
        BillTypeNameList = new String[1];//���õ�����������
        //��ʼ���õ�����������
        BillTypeNameList[0] = "���۳���   (ɨ�����۶���)";
//		BillTypeNameList[1]="�˻�����   (ɨ���ͻ���)";
//		BillTypeNameList[2]="�˻ز���   (ɨ���˻���)";
    }

    public class OnClickListener implements android.view.View.OnClickListener {

        public void onClick(View v) {

            switch (v.getId()) {
                case id.btnSaleOutSelect:
                    if (lstSaveBody == null || lstSaveBody.size() < 1) {

                    } else {
                        Toast.makeText(SalesDelivery.this, R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG).show();
                        //ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        //ADD CAIXY TEST END
                        tvSaleOutSelect.setText("");
                        break;
                    }
                    showSingleChoiceDialog();
                    break;
                //����ͼ��
                case id.btnSalesDelPDOrder:
                    try {
                        Intent intent = new Intent(SalesDelivery.this, SaleChooseTime.class);
//                        String sBeginDate ="";
//                        String sEndDate ="";
//                        String sBillCode = "";
//                        intent.putExtra("sBeginDate",sBeginDate);
//                        intent.putExtra("sBeginDate",sEndDate);
//                        intent.putExtra("sBillCode",sBillCode);
                        startActivityForResult(intent, 44);
                        txtSalesDelWH.requestFocus();

//						if(tvSaleOutSelect.getText().toString().equals("�˻�����"))
//						{
//							BillCodeKey= tvSaleOutSelecttvSaleOutSelect.getText().toString();
//							if(BillCodeKey.length()<5)
//							{
//								Toast.makeText(SalesDelivery.this, "��������5λ�ؼ���", Toast.LENGTH_LONG).show();
//								//ADD CAIXY TEST START
//								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//								//ADD CAIXY TEST END
//								txtSalesDelPDOrder.setText("");
//								break;
//							}
//						}

                    } catch (ParseException e) {
                        Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        //ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        //ADD CAIXY TEST END
                    }
// catch (IOException e) {
//                        Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                        //ADD CAIXY TEST START
//                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                        //ADD CAIXY TEST END
//                    } catch (JSONException e) {
//                        Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                        //ADD CAIXY TEST START
//                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                        //ADD CAIXY TEST END
//                    }
//                    catch(Exception  e){
//                        Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                        //ADD CAIXY TEST START
//                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                        //ADD CAIXY TEST END
//                    }
                    break;

//			case id.btnSalesDelRdcl:
//				try {
//					if(jsonSaveHead == null || jsonSaveHead.length() < 1)
//					{
//						txtSalesDelRdcl.setText("");
//						Toast.makeText(SalesDelivery.this,
//								R.string.DanJuXinXiMeiYouBuNengXuanZeShouFaLeiBie, Toast.LENGTH_LONG).show();
//						//ADD CAIXY TEST START
//						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//						//ADD CAIXY TEST END
//						txtSalesDelPDOrder.requestFocus();
//						return;
//					}
//					btnSalesDelRdclClick("");
//				} catch (ParseException e) {
//					Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
//					e.printStackTrace();
//					//ADD CAIXY TEST START
//					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//					//ADD CAIXY TEST END
//				} catch (IOException e) {
//					Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
//					e.printStackTrace();
//					//ADD CAIXY TEST START
//					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//					//ADD CAIXY TEST END
//				} catch (JSONException e) {
//					Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
//					e.printStackTrace();
//					//ADD CAIXY TEST START
//					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//					//ADD CAIXY TEST END
//				}
//				catch(Exception  e){
//					Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
//					e.printStackTrace();
//					//ADD CAIXY TEST START
//					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//					//ADD CAIXY TEST END
//				}
//				break;

                case id.btnSalesDelScan:

//					if(jsonSaveHead == null || jsonSaveHead.length() < 1)
//					{
//						Toast.makeText(SalesDelivery.this,"û��ȡ�ö�����ͷ", Toast.LENGTH_LONG).show();
//
//						//ADD CAIXY TEST START
//						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//						//ADD CAIXY TEST END
//						return;
//					}
                    if (tvSaleOutSelect.getText().toString() == null || tvSaleOutSelect.getText().toString().equals("")) {
                        Toast.makeText(SalesDelivery.this, "��������Դ����",
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        return;
                    }

                    if (txtSalesDelPDOrder.getText().toString() == null || txtSalesDelPDOrder.getText().toString().equals("")) {
                        Toast.makeText(SalesDelivery.this, "û��ѡ�񵥾ݺ�",
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        return;
                    }

                    if (txtSalesDelWH.getText().toString() == null || txtSalesDelWH.getText().toString().equals("")) {
                        Toast.makeText(SalesDelivery.this, "�ֿ�û��ѡ��",
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        return;
                    }

                    if (jsonBillBodyTask == null || jsonBillBodyTask.length() < 1) {
                        Toast.makeText(SalesDelivery.this, "û��ȡ�ö�������",
                                Toast.LENGTH_LONG).show();

                        // ADD CAIXY TEST START
//                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        return;
                    }


//                    try {
//                        GetWHPosStatus();
//                    } catch (JSONException e) {
//                        Toast.makeText(SalesDelivery.this,"��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
//                        //ADD CAIXY TEST START
//                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                        //ADD CAIXY TEST END
//
//                    }

//                    if(tmpWHStatus.equals(""))
//                    {
//                        return;
//                    }
//					if(tmpWHStatus.equals("Y"))
//					{
//						if(tmpposCode.equals(""))
//						{
//							Toast.makeText(SalesDelivery.this,R.string.QingShuRuHuoWeiHao, Toast.LENGTH_LONG).show();
//
//							//ADD CAIXY TEST START
//							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//							//ADD CAIXY TEST END
//							return;
//						}
//					}

                    Intent intDeliveryScan = new Intent(SalesDelivery.this, SalesDeliveryDetail.class);
                    intDeliveryScan.putExtra("BillCode", tmpBillCode);
                    intDeliveryScan.putExtra("PK_CORP", PK_CORP);
                    intDeliveryScan.putExtra("CSALEID", csaleid);
                    intDeliveryScan.putExtra("ScanType", tvSaleOutSelect.getText().toString());
                    intDeliveryScan.putExtra("TaskJonsBody", jsonBillBodyTask.toString());
                    SerializableList lstScanSaveDetial = new SerializableList();
                    lstScanSaveDetial.setList(lstSaveBody);
                    intDeliveryScan.putExtra("lstScanSaveDetial", lstScanSaveDetial);
                    String sTaskCount = TaskCount + "";
                    intDeliveryScan.putExtra("TaskCount", sTaskCount);
//                    intDeliveryScan.putExtra("tmpCorpPK",tmpCorpPK);
                    intDeliveryScan.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);

                    startActivityForResult(intDeliveryScan, 42);

//                    Intent deliveryScan = new Intent(SalesDelivery.this, SalesDeliveryScan.class);
//                    deliveryScan.putExtra("BillCode", tmpBillCode);
//                    deliveryScan.putExtra("PK_CORP", PK_CORP);
//                    deliveryScan.putExtra("CSALEID", csaleid);
//                    deliveryScan.putExtra("ScanType", tvSaleOutSelect.getText().toString());
//                    deliveryScan.putExtra("TaskJonsBody", jsonBillBodyTask.toString());
//                    SerializableList scanSaveDetial = new SerializableList();
//                    scanSaveDetial.setList(lstSaveBody);
//                    deliveryScan.putExtra("lstScanSaveDetial", scanSaveDetial);
//                    String taskCount = TaskCount + "";
//                    deliveryScan.putExtra("TaskCount", taskCount);
////                    intDeliveryScan.putExtra("tmpCorpPK",tmpCorpPK);
//                    deliveryScan.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
//                    startActivityForResult(deliveryScan, 86);

                    break;
                case id.btnSalesDelSave:
                    try
                    {
                    if (tvSaleOutSelect.getText().toString() == null || tvSaleOutSelect.getText().toString().equals("")) {
                        Toast.makeText(SalesDelivery.this, "��������Դ����",
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        return;
                    }

                    if (txtSalesDelPDOrder.getText().toString() == null || txtSalesDelPDOrder.getText().toString().equals("")) {
                        Toast.makeText(SalesDelivery.this, "û��ѡ�񵥾ݺ�",
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        return;
                    }

                    if (txtSalesDelWH.getText().toString() == null || txtSalesDelWH.getText().toString().equals("")) {
                        Toast.makeText(SalesDelivery.this, "�ֿ�û��ѡ��",
                                Toast.LENGTH_LONG).show();
                        // ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        // ADD CAIXY TEST END
                        return;
                    }
                        saveInfo();
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(SalesDelivery.this, R.string.WangLuoChuXianWenTi ,
                                Toast.LENGTH_LONG).show();
                        //ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    }


//                    if (saleOutGoodsLists != null && saleOutGoodsLists.size() > 0) {
//                        try {
//                               (saleOutGoodsLists);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    } else {
//                        Log.d("TAG", "onClick: saleOutGoodsLists== null ");
//                    }

//                    try {
//
////                        SaveTransData();
//                    } catch (Exception e) {
//                        Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                        //ADD CAIXY TEST START
//                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END

//                    } catch (ParseException e) {
//                        Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                        //ADD CAIXY TEST START
//                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                        //ADD CAIXY TEST END
//
//                    } catch (IOException e) {
//                        Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                        //ADD CAIXY TEST START
//                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                        //ADD CAIXY TEST END
//
//                    }
                    break;
                //���ذ�ť
                case id.btnSalesDelExit:
                    Exit();
                    break;

                case id.btnSalesDelWH:
                    try {

                        if (tvSaleOutSelect.getText().toString() == null || tvSaleOutSelect.getText().toString().equals("")) {
                            Toast.makeText(SalesDelivery.this, "��������Դ����",
                                    Toast.LENGTH_LONG).show();

                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return;
                        }

                        if (txtSalesDelPDOrder.getText().toString() == null || txtSalesDelPDOrder.getText().toString().equals("")) {
                            Toast.makeText(SalesDelivery.this, "û��ѡ�񵥾ݺ�",
                                    Toast.LENGTH_LONG).show();

                            // ADD CAIXY TEST START
                            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                            // ADD CAIXY TEST END
                            return;
                        }
                        //��ȡ�ֿ�
//                        GetSalesDelWH();
                        btnWarehouseClick();
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Toast.makeText(SalesDelivery.this, "�����뵥����Դ", Toast.LENGTH_SHORT).show();
                        //ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    }

                    break;

//				case id.btnSalesDelCD:
//					SetCDtype();
//					break;

            }
        }
    }
    private  void saveInfo(){
        try {
            if (SaveSaleOrder() == true) {
                //SaveOk();
    //            IniActivyMemor();// TODO: 2017/7/10 XUHU
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //��������
    private boolean SaveSaleOrder() throws JSONException,
            ParseException, IOException {
        table = new JSONObject();
        JSONObject tableHead = new JSONObject();
        tableHead.put("CBIZTYPE", CBIZTYPE);
        tableHead.put("COPERATORID", MainLogin.objLog.UserID);
        tableHead.put("CRECEIPTTYE", "4331");
        tableHead.put("CSALECORPID", CSALECORPID);
        tableHead.put("PK_CORP", MainLogin.objLog.STOrgCode);
        tableHead.put("VBILLCODE", "");
        tableHead.put("VDEF1", VDEF1);
        tableHead.put("VDEF2", VDEF2);
        tableHead.put("VDEF5", VDEF5);
        tableHead.put("NOTOTALNUMBER","200.00");// TODO: 2017/7/4
        table.put("Head", tableHead);
        JSONObject tableBody = new JSONObject();
        JSONArray bodyArray = new JSONArray();
        if (NoScanSave == false) {
            JSONArray arrays = jsSerino.getJSONArray("Serino");
            for (int i = 0; i < arrays.length(); i++) {
//                String sSerial = ((JSONObject) (arrays.get(i))).getString("sno");
                String sBatch = ((JSONObject) (arrays.get(i))).getString("batch");
//                String sInvCode = ((JSONObject) (arrays.get(i))).getString("invcode");
//                String serino = ((JSONObject) (arrays.get(i))).getString("serino");
//                serino = serino.replace("\n", "");
                String totalnum = ((JSONObject) (arrays.get(i))).getString("box");
                totalnum = Double.valueOf(totalnum).toString();
//                String sbarcode = serino;
                JSONObject map = new JSONObject();
                map.put("VBATCHCODE",sBatch);
                map.put("NNUMBER",totalnum);

            }
        }

        if (NoScanSave == false) {
            //JSONArray heads = jsHead.getJSONArray("PurGood");
            JSONArray bodys = jsBody.getJSONArray("dbBody");
            JSONArray arraysSerino = jsSerino.getJSONArray("Serino");

//			if (tmpBillStatus.equals("N")) {
            int y = 0;
            for (int j=0; j<arraysSerino.length(); j++) {

                for (int i = 0; i < bodys.length(); i++) {

                    if(arraysSerino.getJSONObject(j).getString("invcode").toLowerCase().equals(
                            bodys.getJSONObject(i).getString("invcode")))
                    //Double ldDoneQty = 0.0;
                    {
//                        CROWNO
//                                CBIZTYPE //��ͷ
//                        CCUSTBASDOCID--��Ҫ�Լ�ȥ��ȡ
//                        CCUSTMANDOCID ,CCUSTOMERID ��ͷ
//                            CINVBASDOCID,CINVBASDOCID
//                        CINVMANDOCID,CINVENTORYID
//                        CRECEEIPTAREAID ��
//                        CRECEIVECUSTBASID,CCUSTBASDOCID--��Ҫ�Լ�ȥ��ȡ
//                            CSENDCALBODYID,CADVISECALBODYID
//                        CSENDWAREID,  --�ֿ�
//                        CSOURCEBILLBODYID,CORDER_BID
//                        CSOURCEBILLID,CSALEID
//                        NNUMBER
//                                NTOTALOUTINVNUM
//                        PK_SENDCORP,PK_CORP
//                        VBATCHCODE
//                                VRECEIVEADDRESS,VRECEIVEADDRESS
//                        VRECEIVEPERSON,--�û���
                        JSONObject object = new JSONObject();
                        object.put("CROWNO", bodys.getJSONObject(i).getString("crowno"));
                        object.put("CBIZTYPE", CBIZTYPE);//��ͷ
                        object.put("CCUSTBASDOCID", CCUSTBASDOCID);
                        object.put("CCUSTMANDOCID", CCUSTOMERID);//��ͷcustomerID
                        object.put("CINVBASDOCID",bodys.getJSONObject(i).getString("cinvbasdocid"));
                        object.put("CINVMANDOCID", bodys.getJSONObject(i).getString("cinventoryid"));
                        object.put("CRECEIVEAREAID", "");
                        object.put("CRECEIVECUSTBASID",CCUSTBASDOCID);//�Լ���ȡ
                        object.put("CSENDCALBODYID",bodys.getJSONObject(i).getString("cadvisecalbodyid"));
                        object.put("CSENDWAREID", CWAREHOUSEID);//�ֿ�
                        object.put("CSOURCEBILLBODYID", bodys.getJSONObject(i).getString("corder_bid"));
                        object.put("CSOURCEBILLID",bodys.getJSONObject(i).getString("csaleid"));
                        object.put("NNUMBER", arraysSerino.getJSONObject(j).getDouble("box"));
                        object.put("NTOTALOUTINVNUM",bodys.getJSONObject(i).getString("nnumber"));
                        object.put("PK_SENDCORP", bodys.getJSONObject(i).getString("pk_corp"));
                        object.put("VBATCHCODE", arraysSerino.getJSONObject(j).getDouble("batch"));
                        object.put("VRECEIVEADDRESS",bodys.getJSONObject(i).getString("vreceiveaddress"));
                        object.put("VRECEIVEPERSON",MainLogin.objLog.LoginUser);
                        bodyArray.put(object);
                        y++;
                    }
                }
            }
            tableBody.put("ScanDetails", bodyArray);
            table.put("Body", tableBody);
            table.put("GUIDS", UUID.randomUUID().toString());
            Log.d(TAG, "XXXXXX: " + table.toString());

        }
        return true;
    }

    /**
     * ��ȡ�ֿ���Ϣ
     * by Xuhu
     *
     */
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
            Log.d(TAG, "btnWarehouseClick: " + rev.toString());

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

//	 private boolean GetBaseWhCdByNameAndAccID(String WhName) throws JSONException, ParseException, IOException
//	 {
//
////			 if(array!=null)
////				{array.removeAll(array);}
////				if(listItemAdapter!=null)
////				{
////				listItemAdapter.notifyDataSetChanged();
////				lvListView.setAdapter(listItemAdapter);
////				}
//		    	JSONObject serList = null;
//				JSONObject para = new JSONObject();
//
//
//
//				para.put("FunctionName", "GetBaseWhCodeByNameAndAccID");
//				para.put("TableName","WhCodeByName");
//				para.put("WhName", WhName.toUpperCase().replace("\n", ""));
//
//				serList = Common.
//						DoHttpQuery(para, "CommonQuery", "");
//				if(serList==null)
//				{
////					Toast.makeText(this, serList.getString("��ȡ�ֿ�����з����˴���"),
////							Toast.LENGTH_LONG).show();
//					Toast.makeText(this,"���������������!���Ժ�����", Toast.LENGTH_LONG).show();
//
//
//					//ADD CAIXY TEST START
//					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//					//ADD CAIXY TEST END
//					return false;
//				}
//				JSONArray arys = serList.getJSONArray("WhCodeByName");
//
//
//
////				for(int i=0;i< arys.length();i++)
////				{
////					//storcode  �ֿ����		storname  �ֿ�����
////					if (sWhCode.equals(""))
////					{
////						sWhCode = sWhCode + "'"+arys.getJSONObject(i).get("storcode").toString()+"'";
////						sWhCode2 = sWhCode2 + arys.getJSONObject(i).get("storcode").toString();
////						sWhName = sWhName + arys.getJSONObject(i).get("storname").toString();
////					}
////					else
////					{
////						sWhCode = sWhCode + ",'"+arys.getJSONObject(i).get("storcode").toString()+"'";
////						sWhCode2 = sWhCode2 + ","+arys.getJSONObject(i).get("storcode").toString();
////						sWhName = sWhName + ","+arys.getJSONObject(i).get("storname").toString();
////					}
////				}
//				return true;
//
//
//	 }
    //GetSalesDelWH
    //����ACCID ����ҳ��ֿ�����ȡ�òֿ��б�

    private void GetSalesDelWH() throws JSONException {

        try {
//			if(tmpAccID==null || tmpAccID.equals(""))
//			{
//				Toast.makeText(this, "������Ϣû�л�ò���ѡ��ֿ�", Toast.LENGTH_LONG).show();
//				//ADD CAIXY TEST START
//				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//				//ADD CAIXY TEST END
//				txtSalesDelPDOrder.requestFocus();
//				return;
//			}

            if (!tvSaleOutSelect.getText().toString().equals("���۳���")) {
                JSONArray JsonArrays = (JSONArray) this.jsonBillBodyTask.get("dbBody");
                for (int i = 0; i < JsonArrays.length(); i++) {
                    String Batch = ((JSONObject) JsonArrays.get(i)).getString("batchcode");
                    if (Batch.equals("null")) {
                        Toast.makeText(this, "������û���������κŵ�����,�뵽ϵͳ���������κź��ٽ�����ز���", Toast.LENGTH_LONG).show();
                        //ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        //ADD CAIXY TEST END
                        tvSaleOutSelect.requestFocus();
                        return;
                    }
                }
            }


            //���ӿ��ƣ��Ѿ�ɨ�������޷��޸Ĳֿ�
            if (lstSaveBody == null || lstSaveBody.size() < 1) {

            } else {
                Toast.makeText(SalesDelivery.this, "�������Ѿ���ɨ��,�޷��޸Ĳֿ⡣��Ҫ�޸Ĳֿ��������ɨ����ϸ", Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                tvSaleOutSelect.setText("");
                return;
            }


            JSONObject serList = null;
            JSONObject para = new JSONObject();

            String sWHName = "";
            String scorpCode = "";
            if (tmpAccID.equals("A")) {
                //CompanyCode=sharedPreferences.getString("CompanyCode", "");
                //OrgCode=sharedPreferences.getString("OrgCode", "");
                scorpCode = tmpCorpPK;
                sWHName = WhNameA;
            } else {
                scorpCode = tmpCorpPK;
                sWHName = WhNameB;
            }


            para.put("FunctionName", "GetBaseWhCodeByNameAndCorp");
            para.put("TableName", "WhCodeByName");
            para.put("Corp", scorpCode);
            para.put("WhName", sWHName.toUpperCase().replace("\n", ""));

            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }

            serList = Common.
                    DoHttpQuery(para, "CommonQuery", tmpAccID);
            if (serList == null) {
//					Toast.makeText(this, serList.getString("��ȡ�ֿ�����з����˴���"),
//							Toast.LENGTH_LONG).show();
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }

            if (serList.getBoolean("Status")) {
                JSONArray arys = serList.getJSONArray("WhCodeByName");

                WHNameList = new String[arys.length()];//���òֿ�������������
                //WHCodeList =new String[arys.length()];//���òֿ�CODE����
                WHIDList = new String[arys.length()];//���òֿ�ID����


                for (int i = 0; i < arys.length(); i++) {
                    //storcode  �ֿ����		storname  �ֿ�����      pk_stordoc �ֿ�ID
                    String storname = arys.getJSONObject(i).get("storname").toString();
                    //String storcode = arys.getJSONObject(i).get("storcode").toString();
                    String pk_stordoc = arys.getJSONObject(i).get("pk_stordoc").toString();
                    WHNameList[i] = storname;
                    //WHCodeList[i]=storcode;
                    WHIDList[i] = pk_stordoc;
                }

                showWHChoiceDialog();
            } else {
//					Toast.makeText(this, serList.getString("�Ҳ�����زֿ���Ϣ"),
//							Toast.LENGTH_LONG).show();
                Toast.makeText(this, "�Ҳ�����زֿ���Ϣ", Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
        }
    }


    //�ҵ���λID���ջ�λ��
    private void FindPositionByCode(String posCode) throws JSONException {
        String lsCompanyCode = "";

        try {
            if (tmpWarehousePK == null || tmpWarehousePK.equals("")) {
                Toast.makeText(this, "�ֿ⻹û��ȷ��,������ɨ���λ", Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
//				txtSalesDelPos.setText("");
                txtSalesDelPDOrder.requestFocus();
                tmpposCode = "";
                tmpposName = "";
                tmpposID = "";
                return;
            }

            if (tmpAccID.equals("A")) {
                lsCompanyCode = tmpCorpPK;
            } else {
                lsCompanyCode = tmpCorpPK;
            }

            posCode = posCode.trim();
            posCode = posCode.replace("\n", "");
            posCode = posCode.toUpperCase();

            JSONObject para = new JSONObject();
            para.put("FunctionName", "GetBinCodeInfo");
            para.put("CompanyCode", lsCompanyCode);
            para.put("STOrgCode", MainLogin.objLog.STOrgCode);
            para.put("WareHouse", tmpWarehousePK);
            para.put("BinCode", posCode);
            para.put("TableName", "position");

            if (!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }

            JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", tmpAccID);

            if (rev == null) {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }


            if (rev.getBoolean("Status")) {
                JSONArray val = rev.getJSONArray("position");
                if (val.length() < 1) {
                    Toast.makeText(this, "��ȡ��λʧ��", Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//					txtSalesDelPos.setText("");
//					txtSalesDelPos.requestFocus();
                    tmpposCode = "";
                    tmpposName = "";
                    tmpposID = "";
                    //ADD CAIXY TEST END
                    return;
                }
                String jposName, jposCode, jposID;
                JSONObject temp = val.getJSONObject(0);

                jposName = temp.getString("csname");
                jposCode = temp.getString("cscode");
                jposID = temp.getString("pk_cargdoc");

                tmpposCode = jposCode;
                tmpposName = jposName;
                tmpposID = jposID;
//				txtSalesDelPos.setText(tmpposCode);
                return;
            } else {
                Toast.makeText(this, "��ȡ��λʧ��", Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
//				txtSalesDelPos.setText("");
//				txtSalesDelPos.requestFocus();
                tmpposCode = "";
                tmpposName = "";
                tmpposID = "";
                return;

            }

        } catch (JSONException e) {

            Toast.makeText(this,
                    e.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            e.printStackTrace();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
        }
    }

    //�򿪶����б���
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


        if (tvSaleOutSelect.getText().toString().equals("δѡ��")) {
            Toast.makeText(SalesDelivery.this, "��������δѡ��,��ѡ�񵥾�����", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            txtSalesDelPDOrder.requestFocus();
            return;
        }
        if (tvSaleOutSelect.getText().toString().equals("���۳���")) {
            Intent ViewGrid = new Intent(this, SaleBillInfoOrderList.class);
            ViewGrid.putExtra("FunctionName", "���۳���");//GetSalereceiveHead
            ViewGrid.putExtra("sBeginDate", sBeginDate);
            ViewGrid.putExtra("sBillCodes", sBillCodes);
            ViewGrid.putExtra("sEndDate", sEndDate);
            startActivityForResult(ViewGrid, 88);
        }
//		if(tvSaleOutSelect.getText().toString().equals("�˻�����"))
//		{
//			Intent ViewGrid = new Intent(this, SaleBillInfoOrderList.class);
//			ViewGrid.putExtra("BillCodeKey", BillCodeKey);
//			ViewGrid.putExtra("FunctionName", "�˻�����");
//			startActivityForResult(ViewGrid,88);
//		}
//		if(tvSaleOutSelect.getText().toString().equals("�˻ز���"))
//		{
//			Intent ViewGrid = new Intent(this, SaleBillInfoOrderList.class);
//			ViewGrid.putExtra("FunctionName", "�˻ز���");//GetSaleTakeHead//GetSaleOutHead
//			startActivityForResult(ViewGrid,88);
//		}

    }

    //���շ������
//    private void btnSalesDelRdclClick(String Code) throws ParseException, IOException, JSONException
//    {
//        Intent ViewGrid = new Intent(this,VlistRdcl.class);
//        ViewGrid.putExtra("FunctionName", "GetRdcl");
//        ViewGrid.putExtra("AccID", tmpAccID);
//        ViewGrid.putExtra("rdflag", rdflag);
//        ViewGrid.putExtra("rdcode", Code);
//        startActivityForResult(ViewGrid,88);
//    }

    //�˳���ť
    private void Exit() {
        if (saleOutGoodsLists.size() > 0) {
            AlertDialog.Builder bulider =
                    new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("ɨ�赥��δ���棬ȷ���˳���?");
            bulider.setNegativeButton(R.string.QuXiao, null);
            bulider.setPositiveButton(R.string.QueRen, listenExit).create().show();
        } else {
            finish();
        }

    }

    //�˳���ť�Ի����¼�
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
        if (keyCode == KeyEvent.KEYCODE_MENU) {//����meu���¼�			//do something...
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {//���ط��ذ�ť�¼�			//do something...
            return false;
        }
        return true;
    }


    //EditText�����س��ļ����¼�
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


    private void GetTaskCount() throws JSONException {
        this.TaskCount = 0;
        tmpWHStatus = "";
        if ((this.jsonBillBodyTask == null) || (this.jsonBillBodyTask.equals(""))) {
            return;
        }


        JSONArray JsonArrays = (JSONArray) this.jsonBillBodyTask.get("dbBody");
        for (int i = 0; i < JsonArrays.length(); i++) {
            String Batch = ((JSONObject) JsonArrays.get(i)).getString("batchcode");
            if (Batch.equals("null")) {

            }
            String nnum = ((JSONObject) JsonArrays.get(i)).getString("number");
            String ntranoutnum = ((JSONObject) JsonArrays.get(i)).getString("outnumber");
            String snnum = "0";
            if (!ntranoutnum.equals("null")) {
                snnum = ntranoutnum.replaceAll("\\.0", "");
            }
            int shouldinnum = Integer.valueOf(nnum).intValue() - Integer.valueOf(snnum).intValue();
            TaskCount = (TaskCount + shouldinnum);
        }

    }


    //��������
    private void SaveTransData() throws JSONException, ParseException, IOException {
        JSONObject sendJsonSave = new JSONObject();
        JSONArray sendJsonArrBody = new JSONArray();
        JSONArray sendJsonArrBodyLocation = new JSONArray();
        HashMap<String, Object> sendMapHead = new HashMap<String, Object>();
        HashMap<String, Object> sendMapBody = new HashMap<String, Object>();
        if ((this.jsonSaveHead == null) || (this.jsonSaveHead.length() < 1)) {
            Toast.makeText(this, R.string.WuKeBaoCunShuJu, Toast.LENGTH_SHORT).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }
        if ((this.lstSaveBody == null) || (this.lstSaveBody.size() < 1)) {
            Toast.makeText(this, R.string.WuKeBaoCunShuJu, Toast.LENGTH_SHORT).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }


        for (int j = 0; j < lstSaveBody.size(); j++) {
            sendMapBody = (HashMap<String, Object>) lstSaveBody.get(j);

//            if(!sendMapBody.get("spacenum").toString().equals("1"))
//            {
//                Toast.makeText(this, R.string.YouWeiSaoWanDeFenBao, Toast.LENGTH_LONG).show();
//                //ADD CAIXY TEST START
//                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//                //ADD CAIXY TEST END
//                return;
//            }

            //String sHBillCode = sendMapHead.get("No").toString();
            String sBBillCode = sendMapBody.get("BillCode").toString();

//			if(tvSalesDelBillCode.getText().equals(sBBillCode))
//			{
//				JSONObject jsonSaveBody = new JSONObject();
//
//				jsonSaveBody = Common.MapTOJSONOBject(sendMapBody);
//				sendJsonArrBody.put(jsonSaveBody);
//
//				if(tmpWHStatus.equals("Y"))
//				{
//					JSONObject saveJsonBodyLocation = new JSONObject();
//					saveJsonBodyLocation.put("csourcebillbid", sendMapBody.get("billbid").toString());
//					saveJsonBodyLocation.put("cspaceidf", tmpposID);
//					saveJsonBodyLocation.put("spacenum", sendMapBody.get("spacenum").toString());
//					sendJsonArrBodyLocation.put(saveJsonBodyLocation);
//				}
//				sendJsonSave.put("ScanDetail", sendJsonArrBody);
//				sendJsonSave.put("ScanDetailLocation", sendJsonArrBodyLocation);
//			}
        }

        if ((sendJsonSave == null) || (sendJsonSave.length() < 0x1)) {
            Toast.makeText(this, R.string.WuKeBaoCunShuJu, Toast.LENGTH_SHORT).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }


        String ErrMsg = "";


        if (tvSaleOutSelect.getText().toString().startsWith("�˻�")) {
            jsonBillBodyTask2 = null;
            GetBillBodyDetailInfo2(SaleFlg);

            if ((this.jsonBillBodyTask2 == null) || (this.jsonBillBodyTask2.equals(""))) {
                Toast.makeText(this, "����ɨ���������ٴμ�鵥�ݺ��ٽ��б���", Toast.LENGTH_SHORT).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            } else {
                JSONArray jsarray = jsonBillBodyTask2.getJSONArray("dbBody");
                for (int i = 0; i < jsarray.length(); i++) {
                    JSONObject tempJso = jsarray.getJSONObject(i);
                    String outnumber = tempJso.getString("outnumber");
                    String number = tempJso.getString("number");

                    if (outnumber == null || outnumber.equals("") || outnumber.equals("null")) {
                        outnumber = "0";
                    }

                    String invcode = tempJso.getString("invcode");
                    String batchcode = tempJso.getString("batchcode");

                    int shouldoutnum = Integer.valueOf(number).intValue() - Integer.valueOf(outnumber).intValue();

                    for (int j = 0; j < sendJsonArrBody.length(); j++) {
                        String AccID = (String) ((JSONObject) sendJsonArrBody.get(j)).get("AccID");
                        String Scinvcode = (String) ((JSONObject) sendJsonArrBody.get(j)).get("InvCode");
                        String Scbatchcode = (String) ((JSONObject) sendJsonArrBody.get(j)).get("batchcode");
                        if (Scinvcode.equals(invcode) & batchcode.equals(Scbatchcode)) {
                            shouldoutnum--;
                            if (shouldoutnum < 0) {
                                ErrMsg = ErrMsg + Scinvcode + "," + Scbatchcode + " " + "\r\n";
                            }
                        }
                    }
                }
            }
        }

        if (!ErrMsg.equals("")) {
            Toast.makeText(this, ErrMsg + "����ɨ���������ٴμ�鵥�ݺ��ٽ��б���", Toast.LENGTH_SHORT).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }


        if (uploadGuid == null) {
            uploadGuid = UUID.randomUUID();
        }
        sendJsonSave.put("Head", jsonSaveHead);
        sendJsonSave.put("GUIDS", uploadGuid.toString());

        sendJsonSave.put("tmpWHStatus", tmpWHStatus);

        sendJsonSave.put("RdID", tmprdID.toString());
//        tmpOutManualNo = txtSalesDelManualNo.getText().toString().toUpperCase();
//        sendJsonSave.put("ManualNo", tmpOutManualNo);
        sendJsonSave.put("WarehousePK", tmpWarehousePK);
        sendJsonSave.put("CdTypeID", tmpCdTypeID);
        sendJsonSave.put("SalesType", tvSaleOutSelect.getText());

        if (!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }
        /////////////////////////////////
        JSONObject jas = Common.DoHttpQuery(sendJsonSave, "SaveSaleOutBill", tmpAccID);

        if (jas == null) {
            Toast.makeText(SalesDelivery.this, R.string.DanJuZaiBaoCunGuoChengZhongChuXianWenTi, Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }

        if (!jas.has("Status")) {
            Toast.makeText(SalesDelivery.this, "���ݱ�������г���������," +
                    "�볢���ٴ��ύ�򵽵���ϵͳ��ȷ�Ϻ��پ����Ƿ��������!", Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }


        boolean loginStatus = jas.getBoolean("Status");

        if (loginStatus == true) {
//			String lsResultBillCode = jas.getString("BillCode");
            String lsResultBillCode = "";

            if (jas.has("BillCode")) {
                lsResultBillCode = jas.getString("BillCode");
            } else {
                Toast.makeText(this, "���ݱ�������г���������," +
                        "�볢���ٴ��ύ�򵽵���ϵͳ��ȷ�Ϻ��پ����Ƿ��������!", Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }

            Map<String, Object> mapResultBillCode = new HashMap<String, Object>();
            mapResultBillCode.put("BillCode", lsResultBillCode);
            ArrayList<Map<String, Object>> lstResultBillCode = new ArrayList<Map<String, Object>>();
            lstResultBillCode.add(mapResultBillCode);
            //Toast.makeText(StockTransContent.this, "���ݱ���ɹ�", Toast.LENGTH_LONG).show();
            //IniActivyMemor();
            //return;

            uploadGuid = null;
            //ADD BY WUQIONG START
            //tmpmanualNo=null;
            //ADD BY WUQIONG END
            SimpleAdapter listItemAdapter = new SimpleAdapter(SalesDelivery.this, lstResultBillCode,//����Դ
                    android.R.layout.simple_list_item_1,
                    new String[]{"BillCode"},
                    new int[]{android.R.id.text1}
            );
            new AlertDialog.Builder(SalesDelivery.this).setTitle(R.string.DanJuBaoCunChengGong)
                    .setAdapter(listItemAdapter, null)
                    .setPositiveButton(R.string.QueRen, null).show();

            //����ɹ����ʼ��������ڴ�����

            //д��log�ļ�
            writeTxt = new writeTxt();

            Date day = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            SimpleDateFormat dfd = new SimpleDateFormat("yyyy-MM-dd");

            String BillCode = lsResultBillCode;
            String BillType = "4C";
            String UserID = MainLogin.objLog.UserID;

            String LogName = BillType + UserID + dfd.format(day) + ".txt";
            String LogMsg = df.format(day) + " " + tmpAccID + " " + BillCode;

            writeTxt.writeTxtToFile(LogName, LogMsg);
            //д��log�ļ�

            InitActiveMemor();
            this.tvSaleOutSelect.setText("δѡ��");
            return;
        } else {
            String ErrMsg1 = jas.getString("ErrMsg");
            Toast.makeText(SalesDelivery.this, ErrMsg1, Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }

        /////////////////////////////////


    }

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
//		this.txtSalesDelPos.setText("");
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

        //this.tmpOutManualNo = "";
        this.tmpCdTypeID = "";
//		this.tvSalesDelBillCodeName.setText("");
//		this.tvSalesDelAccIDName.setText("");
//		this.tvSalesDelCorpName.setText("");
//		this.tvSalesDelBillCode.setText("");
//		this.tvSalesDelAccID.setText("");
//		this.tvSalesDelCorp.setText("");
        this.ScanedBarcode = new ArrayList<String>();

        this.txtSalesDelPDOrder.requestFocus();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.sales_delivery, menu);
//        return true;
//    }


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

//        btnSalesDelPDOrder.setFocusable(true);
//        txtSalesDelWH.setFocusable(true);
        btnSalesDelExit.setFocusable(false);
        btnSalesDelScan.setFocusable(false);
        btnSalesDelSave.setFocusable(false);
//		btnSalesDelCD.setFocusable(false);
    }

    private void SaveInfo(List<SaleOutGoods> saleOutGoodsLists) throws JSONException {
        final JSONObject table = new JSONObject();
        JSONObject tableHead = new JSONObject();
        tableHead.put("CBIZTYPE", CBIZTYPE);
        tableHead.put("COPERATORID", MainLogin.objLog.UserID);
        tableHead.put("CRECEIPTTYE", "4331");
        tableHead.put("CSALECORPID", CSALECORPID);
        tableHead.put("PK_CORP", MainLogin.objLog.STOrgCode);

        tableHead.put("VBILLCODE", "");

        tableHead.put("VDEF1", VDEF1);
        tableHead.put("VDEF2", VDEF2);
        tableHead.put("VDEF5", VDEF5);
        tableHead.put("NOTOTALNUMBER","200.00");// TODO: 2017/7/4

        table.put("Head", tableHead);
        JSONObject tableBody = new JSONObject();
        JSONArray bodyArray = new JSONArray();
        for (SaleOutGoods saleOutGoods : saleOutGoodsLists) {
            JSONObject object = new JSONObject();


            float price = saleOutGoods.getQty();
            DecimalFormat decimalFormat = new DecimalFormat(".00");//���췽�����ַ���ʽ�������С������2λ,����0����.
            String qty = decimalFormat.format(price);//format ���ص����ַ���
//            CROWNO
//            CBIZTYPE,CBIZTYPE
//            CCUSTBASDOCID,CCUSTBASDOCID
//            CCUSTMANDOCID ,CCUSTOMERID
//            CINVBASDOCID,CINVBASDOCID
//            CINVMANDOCID,CINVENTORYID
//            CRECEIVEAREAID,CRECEIPTAREID
//            CRECEIVECUSTBASID,CCUSTBASDOCID
//            CSENDCALBODYID,CADVISECALBODYID
//            CSENDWAREID,  --�ֿ�
//            CSOURCEBILLBODYID,CORDER_BID
//            CSOURCEBILLID,CSALEID
//            NNUMBER
//            NTOTALOUTINVNUM
//            PK_SENDCORP,PK_CORP
//            VBATCHCODE
//            VRECEIVEADDRESS,VRECEIVEADDRESS
//            VRECEIVEPERSON,--�û���
            object.put("CROWNO", saleOutGoods.getCROWNO());
            object.put("CBIZTYPE", CBIZTYPE);//��ͷ
            object.put("CCUSTBASDOCID", CCUSTBASDOCID);
            object.put("CCUSTMANDOCID", CCUSTOMERID);//��ͷcustomerID
            object.put("CINVBASDOCID", saleOutGoods.getCINVBASDOCID());
            object.put("CINVMANDOCID", saleOutGoods.getCINVENTORYID());
            object.put("CRECEIVEAREAID", "");
            object.put("CRECEIVECUSTBASID",CCUSTBASDOCID);//�Լ���ȡ
            object.put("CSENDCALBODYID", saleOutGoods.getCADVISECALBODYID());
            object.put("CSENDWAREID", CWAREHOUSEID);//�ֿ�
            object.put("CSOURCEBILLBODYID", saleOutGoods.getCORDER_BID());
            object.put("CSOURCEBILLID",saleOutGoods.getCSALEID());
//            Log.d(TAG, "SaveInfo: "+saleOutGoods.getCSALEID());
            object.put("NNUMBER", qty);
            object.put("NTOTALOUTINVNUM",saleOutGoods.getNum());
            object.put("PK_SENDCORP", saleOutGoods.getPK_CORP());
            object.put("VBATCHCODE", saleOutGoods.getBatch());
            object.put("VRECEIVEADDRESS", saleOutGoods.getVRECEIVEADDRESS());
            object.put("VRECEIVEPERSON",MainLogin.objLog.LoginUser);
            bodyArray.put(object);
        }
        tableBody.put("ScanDetails", bodyArray);
        table.put("Body", tableBody);
        table.put("GUIDS", UUID.randomUUID().toString());
        Log.d(TAG, "SaveInfo: " + table.toString());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jas = Common.DoHttpQuery(table, "SaveSaleReceive", "A");
                    if (jas != null) {
                        Log.d(TAG, "����" + jas.toString());
                    } else {
                        Log.d(TAG, "null ");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
