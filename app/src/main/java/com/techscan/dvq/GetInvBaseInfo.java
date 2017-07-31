package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.techscan.dvq.common.Common;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SplitBarcode;
import com.techscan.dvq.login.MainLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by walter on 2017/6/29.
 */

public class GetInvBaseInfo {

    public HashMap<String,Object> mapInvBaseInfo = null;

    /**
     * ��ȡ���������Ϣ
     *
     *
     */
    public GetInvBaseInfo(SplitBarcode cSplitBarcode, Handler mHandler) {
        mapInvBaseInfo = new HashMap<String, Object>();
        mapInvBaseInfo.put("barcodetype",cSplitBarcode.BarcodeType);
        mapInvBaseInfo.put("batch",cSplitBarcode.cBatch);
        mapInvBaseInfo.put("serino",cSplitBarcode.cSerino);
        mapInvBaseInfo.put("quantity",cSplitBarcode.dQuantity);
        mapInvBaseInfo.put("number",cSplitBarcode.iNumber);
        mapInvBaseInfo.put("taxflag",cSplitBarcode.TaxFlag);
        mapInvBaseInfo.put("outsourcing",cSplitBarcode.Outsourcing);
        mapInvBaseInfo.put("barcode",cSplitBarcode.FinishBarCode);
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
        parameter.put("InvCode", cSplitBarcode.cInvCode);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread td = new Thread(requestThread);
        td.start();
    }

    /**
     * ͨ����ȡ����json �����õ�������Ϣ,�����õ�UI��
     *
     * @param json
     * @throws JSONException
     */
    String pk_invbasdoc = "";
    String pk_invmandoc = "";

    public void SetInvBaseToParam(JSONObject json) throws JSONException {
        //Log.d(TAG, "SetInvBaseToUI: " + json);
        if (json.getBoolean("Status")) {
            JSONArray val = json.getJSONArray("baseInfo");
            //mapInvBaseInfo = null;
            for (int i = 0; i < val.length(); i++) {
                JSONObject tempJso = val.getJSONObject(i);
                //mapInvBaseInfo = new HashMap<String, Object>();
                mapInvBaseInfo.put("invname", tempJso.getString("invname"));   //��������
                mapInvBaseInfo.put("invcode", tempJso.getString("invcode"));   //���Ϻ�
                mapInvBaseInfo.put("measname", tempJso.getString("measname"));   //��λ
                mapInvBaseInfo.put("pk_invbasdoc", tempJso.getString("pk_invbasdoc"));//����bas PK
                //pk_invbasdoc = tempJso.getString("pk_invbasdoc");
                mapInvBaseInfo.put("pk_invmandoc", tempJso.getString("pk_invmandoc"));//����man PK
                //pk_invmandoc = tempJso.getString("pk_invmandoc");
                mapInvBaseInfo.put("invtype", tempJso.getString("invtype"));   //�ͺ�
                mapInvBaseInfo.put("invspec", tempJso.getString("invspec"));   //���
                mapInvBaseInfo.put("oppdimen", tempJso.getString("oppdimen"));
            }

        }
    }

    public static class SaleBillInfoOrderList extends Activity {

        //private Button btnSaleBillInfoReturn;
        private ListView lvSaleBillInfoOrderList;

        private String fsFunctionName = "";
        private String fsBillCode     = "";
        private String sDate;
        private String sEndDate;
        private String sBillCodes;
        List<Map<String, Object>> list                          = new ArrayList<Map<String, Object>>();
        Button                    btSaleBillInfoOrderListReturn = null;

        //ADD CAIXY TEST START
    //	private SoundPool sp;//����һ��SoundPool
    //	private int MainLogin.music;//����һ��int������suondID
        //ADD CAIXY TEST END

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_sale_bill_info_order_list);

            //����title
            ActionBar actionBar = this.getActionBar();
            actionBar.setTitle("������ϸ");
    //		Drawable TitleBar = this.getResources().getDrawable(R.drawable.bg_barbackgroup);
    //		actionBar.setBackgroundDrawable(TitleBar);
    //		actionBar.show();

            btSaleBillInfoOrderListReturn = (Button) findViewById(R.id.btSaleBillInfoOrderListReturn);
            btSaleBillInfoOrderListReturn.setOnClickListener(ButtonOnClickListener);

            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
                                               .detectNetwork()
                                               .penaltyLog()
                                               .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
                    detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

            //ȡ�ò�ѯ�õ��ݱ���
            Intent myIntent = this.getIntent();
            if (myIntent.hasExtra("FunctionName"))
                fsFunctionName = myIntent.getStringExtra("FunctionName");
            if (myIntent.hasExtra("BillCodeKey"))
                fsBillCode = myIntent.getStringExtra("BillCodeKey");
            if (myIntent.hasExtra("sBeginDate"))
                sDate = myIntent.getStringExtra("sBeginDate");
            if (myIntent.hasExtra("sEndDate"))
                sEndDate = myIntent.getStringExtra("sEndDate");
            if (myIntent.hasExtra("sBillCodes"))
                sBillCodes = myIntent.getStringExtra("sBillCodes");


            //ȡ�ÿؼ�
            //btnSaleBillInfoReturn = (Button)findViewById(R.id.btnSaleBillInfoReturn);
            //btnSaleBillInfoReturn.setOnClickListener(ButtonClickListener);
            lvSaleBillInfoOrderList = (ListView) findViewById(R.id.SaleBillInfoOrderList);

            //ADD CAIXY START
    //		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//��һ������Ϊͬʱ���������������������ڶ����������ͣ�����Ϊ��������
    //		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
            //ADD CAIXY END

            //ȡ���Լ�����ʾ������ϸ
            GetAndBindingBillInfoDetail();

        }

        private View.OnClickListener ButtonOnClickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {            //btnSDScanReturn
                    case R.id.btSaleBillInfoOrderListReturn:
                        finish();
                        break;
                }
            }
        };

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
    //		getMenuInflater().inflate(R.menu.sale_bill_info_order_list, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        //ȡ���Լ�����ʾ������ϸ
        private void GetAndBindingBillInfoDetail() {
            //ȡ�����е�����Ϣ
            if (fsFunctionName.equals("")) {
                Toast.makeText(this, "û�в�ѯ�õ��ݱ���", Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }

            int x = 1;

            if (fsFunctionName.equals("���۳���")) {
                fsFunctionName = "GetSaleOrderList";
            }
            //���ó�� TODO: 2017/7/31
            if (fsFunctionName.equals("���ó��")) {
                fsFunctionName = "GetSaleOrderList";
            }

            for (int i = 0; i < x; i++) {

                if (x == 2) {
                    if (i == 0) {
                        fsFunctionName = "GetSaleTakeHead";
                    } else {
                        fsFunctionName = "GetSaleOutHead";
                    }
                }

                JSONObject para = new JSONObject();
                try {
                    para.put("FunctionName", fsFunctionName);
                    para.put("CorpPK", "4100");
                    para.put("STOrgCode", MainLogin.objLog.STOrgCode);
                    para.put("BillCode", sBillCodes);
                    para.put("sDate", sDate);
                    para.put("sEndDate", sEndDate);


                } catch (JSONException e2) {
                    // TODO Auto-generated catch block
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
                    // TODO Auto-generated catch block
                    Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    return;
                }

                JSONObject jas;
                try {
                    if (!MainLogin.getwifiinfo()) {
                        Toast.makeText(this, "WIFI�źŲ�!�뱣�����糩ͨ", Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }
                    jas = Common.DoHttpQuery(para, "CommonQuery", "");
                } catch (Exception ex) {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                    //ADD CAIXY TEST START
                    MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                    //ADD CAIXY TEST END
                    return;
                }

                //��ȡ�õĵ�����Ϣ�󶨵�ListView��
                try {
                    if (jas == null) {
                        Toast.makeText(this, "���������������!���Ժ�����", Toast.LENGTH_LONG).show();
                        //ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        //ADD CAIXY TEST END
                        return;
                    }

                    if (!jas.has("Status")) {
                        Toast.makeText(this, "���������������!���Ժ�����", Toast.LENGTH_LONG).show();
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        return;
                    }


                    if (!jas.getBoolean("Status")) {
                        String errMsg = "";
                        if (jas.has("ErrMsg")) {
                            errMsg = jas.getString("ErrMsg");
                        } else {
                            errMsg = "���������������!���Ժ�����";
                        }
                        Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                        //ADD CAIXY TEST START
                        MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                        //ADD CAIXY TEST END
                        return;
                    }
                    //�󶨵�ListView
                    BindingBillInfoData(jas);
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

        }

        //�󶨵�ListView
        private void BindingBillInfoData(JSONObject jsonBillInfo) throws JSONException {

            Map<String, Object> map;

            JSONObject tempJso = null;

            if (!jsonBillInfo.has("Status")) {
                Toast.makeText(this, "���������������!���Ժ�����", Toast.LENGTH_LONG).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
            }

            if (!jsonBillInfo.getBoolean("Status")) {
                String errMsg = "";
                if (jsonBillInfo.has("ErrMsg")) {
                    errMsg = jsonBillInfo.getString("ErrMsg");
                } else {
                    errMsg = "���������������!���Ժ�����";
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                list = null;
            }

            JSONArray jsarray = jsonBillInfo.getJSONArray("dbHead");

            for (int i = 0; i < jsarray.length(); i++) {
                tempJso = jsarray.getJSONObject(i);
                map = new HashMap<String, Object>();
                if (fsFunctionName.equals("GetSaleOrderList"))//���۳��ⵥ
                {
                    map.put("BillDate", tempJso.getString("dbilldate"));
                    map.put("BillCode", tempJso.getString("vreceiptcode"));
                    map.put("CustName", tempJso.getString("custname"));
                    map.put("Csaleid", tempJso.getString("csaleid"));
                    map.put("saleflg", "");

    //				map.put("pk_corp", tempJso.getString("pk_corp"));
    //				map.put("custname", tempJso.getString("custname"));
    //				map.put("pk_cubasdoc", tempJso.getString("pk_cubasdoc"));
    //				map.put("pk_cumandoc", tempJso.getString("pk_cumandoc"));
    //				map.put("billID", tempJso.getString("csalereceiveid"));
    //				map.put("billCode", tempJso.getString("vreceivecode"));
    //				map.put("dmakedate",tempJso.getString("dmakedate"));
    //				map.put("AccID", tempJso.getString("AccID"));
    //				map.put("vdef11", tempJso.getString("vdef11"));
    //				map.put("vdef12", tempJso.getString("vdef12"));
    //				map.put("vdef13", tempJso.getString("vdef13"));
    //				map.put("saleflg", "");
    //				if(tempJso.getString("AccID").equals("A"))
    //					map.put("coperatorid", MainLogin.objLog.UserID);//������
    //				else
    //					map.put("coperatorid", MainLogin.objLog.UserIDB);//������
    //				map.put("ctransporttypeid", tempJso.getString("ctransporttypeid"));//���䷽ʽID
    //
    //				map.put("cbiztype", tempJso.getString("cbiztype"));
                }

                list.add(map);
            }
            SimpleAdapter listItemAdapter = null;

            listItemAdapter = new SimpleAdapter(this, list,
                                                R.layout.vlistsaledel,
                                                new String[]{"BillCode", "CustName", "BillDate"},
                                                new int[]{R.id.tvBillCode,
                                                        R.id.tvCustomer,
                                                        R.id.tvBillDate});

            if (listItemAdapter == null)
                return;
            lvSaleBillInfoOrderList.setAdapter(listItemAdapter);
            lvSaleBillInfoOrderList.setOnItemClickListener(itemListener);

        }

        //ListView��Item��������¼�
        private ListView.OnItemClickListener itemListener = new
                ListView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                            long arg3) {

                        Adapter             adapter = arg0.getAdapter();
                        Map<String, Object> map     = (Map<String, Object>) adapter.getItem(arg2);

                        SerializableMap ResultMap = new SerializableMap();
                        ResultMap.setMap(map);
    //					String lsAccID = map.get("AccID").toString();
    //					String lsPk_Corp = map.get("pk_corp").toString();


    //					if(!Common.CheckUserRole(lsAccID, lsPk_Corp, "40080802"))
    //					{
    //						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    //						Toast.makeText(SaleBillInfoOrderList.this, "û��ʹ�øõ��ݵ�Ȩ��", Toast.LENGTH_LONG).show();
    //						return;
    //					}
                        Intent intent = new Intent();
                        intent.putExtra("ResultBillInfo", ResultMap);

                        SaleBillInfoOrderList.this.setResult(1, intent);
                        SaleBillInfoOrderList.this.finish();

                    }

                };

        //button��ť�ļ����¼�
        private Button.OnClickListener ButtonClickListener = new
                Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
    //				case R.id.btnSaleBillInfoReturn:
    //					SaleBillInfoOrderList.this.finish();
    //					break;
                        }

                    }

                };

    }
}
