package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.techscan.dvq.login.MainLogin;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillInfoOrderList extends Activity {

    private Button btnBillInfoReturn;
    private ListView lvBillInfoOrderList;
    private String fsFunctionName = "";


    //ADD CAIXY TEST START
    private SoundPool sp;//��һ��SoundPool
    private int music;//����һ��int������suondID
    private String WhNameA = "";
    private String WhNameB = "";
    private String corpincode = "";
    private String billcodeKey="";
    //ADD CAIXY TEST END

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_info_order_list);

        //����title
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle(R.string.orderInfo);
//		Drawable TitleBar = this.getResources().getDrawable(R.drawable.bg_barbackgroup);
//		actionBar.setBackgroundDrawable(TitleBar);
//		actionBar.show();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());

        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
                detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());

        //ȡ�ò�ѯ�õ��ݱ���
        Intent myIntent = this.getIntent();
        if(myIntent.hasExtra("FunctionName"))
            fsFunctionName = myIntent.getStringExtra("FunctionName");

        //add caixy s
        if(myIntent.hasExtra("WhNameA"))
            WhNameA = myIntent.getStringExtra("WhNameA");
        if(myIntent.hasExtra("WhNameB"))
            WhNameB = myIntent.getStringExtra("WhNameB");
        if(myIntent.hasExtra("corpincode"))
            corpincode = myIntent.getStringExtra("corpincode");
        if(myIntent.hasExtra("billcodeKey"))
            billcodeKey = myIntent.getStringExtra("billcodeKey");
        //add caixy e

        //ȡ�ÿؼ�
        btnBillInfoReturn = (Button)findViewById(R.id.btnBillInfoReturn);
        btnBillInfoReturn.setOnClickListener(ButtonClickListener);
        lvBillInfoOrderList = (ListView)findViewById(R.id.BillInfoOrderList);

        //ADD CAIXY START
        sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//��һ������Ϊͬʱ���������������������ڶ����������ͣ�����Ϊ��������
        music = sp.load(this, R.raw.xxx, 1); //����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
        //ADD CAIXY END

        //ȡ���Լ�����ʾ������ϸ
        GetAndBindingBillInfoDetail();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.bill_info_order_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
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
    private void GetAndBindingBillInfoDetail()
    {
        //ȡ�����е�����Ϣ
        if(fsFunctionName.equals(""))
        {
            Toast.makeText(this, R.string.MeiYouChaXunDaoDanJuBiaoMing, Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            sp.play(music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            return;
        }

        JSONObject para = new JSONObject();
        try {
            para.put("FunctionName", fsFunctionName);
            //add caixy s
            para.put("WhNameA", WhNameA);
            para.put("WhNameB", WhNameB);
            para.put("corpincode", corpincode);
            para.put("billcodeKey", billcodeKey);
            //add caixy e
            para.put("CorpPK", "");
            para.put("BillCode", "");
            //para.put("Wh-CodeA", MainLogin.objLog.WhCodeA);
            //para.put("Wh-CodeB", MainLogin.objLog.WhCodeB);


        } catch (JSONException e2) {
            return;
        }
        try {
            para.put("TableName",  "dbHead");
        } catch (JSONException e2) {
            return;
        }

        JSONObject jas;
        try {
            if(!MainLogin.getwifiinfo()) {
                Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
                sp.play(music, 1, 1, 0, 0, 1);
                return;
            }
            jas = Common.DoHttpQuery(para, "CommonQuery", "");
        } catch (Exception ex)
        {
            return;
        }

        //��ȡ�õĵ�����Ϣ�󶨵�ListView��
        try
        {
            if(jas==null)
            {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                sp.play(music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }
            if(!jas.has("Status"))
            {
                Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                sp.play(music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }
            if(!jas.getBoolean("Status"))
            {
                String errMsg = "";
                if(jas.has("ErrMsg"))
                {
                    errMsg = jas.getString("ErrMsg");
                }
                else
                {
                    errMsg = getString(R.string.WangLuoChuXianWenTi);
                }
                Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
                //ADD CAIXY TEST START
                sp.play(music, 1, 1, 0, 0, 1);
                //ADD CAIXY TEST END
                return;
            }
            //�󶨵�ListView
            BindingBillInfoData(jas);
        }
        catch (JSONException e)
        {
            return;
        }
        catch (Exception ex)
        {
            return;
        }
    }

    //�󶨵�ListView
    private void BindingBillInfoData(@Nullable JSONObject jsonBillInfo) throws JSONException
    {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        JSONObject tempJso = null;

        if(jsonBillInfo == null)
            return;
        if(!jsonBillInfo.has("Status"))
            return;
        if(!jsonBillInfo.getBoolean("Status"))
        {
            String errMsg = "��ȡ����ʱ��������,���ٴγ���";
            if(jsonBillInfo.has("Status"))
            {
                errMsg = jsonBillInfo.getString("ErrMsg");
            }

            Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            sp.play(music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
            list = null;
        }
        if(!jsonBillInfo.has("dbHead"))
            return;
        JSONArray jsarray= jsonBillInfo.getJSONArray("dbHead");

        for(int i = 0;i<jsarray.length();i++)
        {
            tempJso = jsarray.getJSONObject(i);
            map = new HashMap<String, Object>();
            if(fsFunctionName.equals("GetAdjustOutBillHead"))//�������ⵥ
            {
                map.put("BillCode", tempJso.getString("vbillcode"));//���ݺ�
                map.put("WHOut", tempJso.getString("cwarehousename") + "     ");//�����ֿ���
                map.put("WHIn",tempJso.getString("cotherwhname"));//����ֿ���
                //map.put("BillID",tempJso.getString("vbillcode"));
                map.put("AccID", tempJso.getString("AccID"));
                map.put("CorpSOut", tempJso.getString("corpoutshortname") + "     ");//������˾���
                map.put("CorpSIn", tempJso.getString("corpinshortname"));//���빫˾���
                map.put("CorpOut", tempJso.getString("corpoutname"));//������˾����
                map.put("CorpIn", tempJso.getString("corpinname"));//���빫˾����

                //�����ñ�ͷJSONObject����---��ʼ
                map.put("pk_corp", tempJso.getString("cothercorpid"));//��˾ID//caixy
                if(tempJso.getString("AccID").equals("A"))
                    map.put("coperatorid", MainLogin.objLog.UserID);//������
                else
                    map.put("coperatorid", MainLogin.objLog.UserIDB);//������

                map.put("pk_calbody", tempJso.getString("cothercalbodyid"));//��������֯
                map.put("pk_stordoc", tempJso.getString("cotherwhid"));//����ֿ�
                map.put("fallocflag", tempJso.getString("fallocflag"));//�������ͱ�־
                map.put("cbiztype", tempJso.getString("cbiztype"));//ҵ������ID
                map.put("pk_outstordoc", tempJso.getString("cwarehouseid"));//�����ֿ�
                map.put("pk_outcalbody", tempJso.getString("pk_calbody"));//���������֯
                map.put("pk_outcorp", tempJso.getString("pk_corp"));//������˾
                map.put("vcode", tempJso.getString("vbillcode"));//���ݺ�
                map.put("cgeneralhid", tempJso.getString("cgeneralhid"));//����ID
                //cgeneralhid
                //������JSONObject����---����
            }

            list.add(map);
        }
        SimpleAdapter listItemAdapter = null;
        if(fsFunctionName.equals("GetAdjustOutBillHead"))//�������ⵥ
        {
            listItemAdapter = new SimpleAdapter(this,list,
                    R.layout.vlisttransin,
                    new String[] {"BillCode","WHOut", "WHIn","AccID","CorpSOut","CorpSIn"},
                    new int[] {R.id.listtransinpdorder,
                            R.id.listtransinfromware,
                            R.id.listtransintoware,
                            R.id.listtransinaccid,
                            R.id.listtransincorpoutname,
                            R.id.listtransincorpinname});
        }

        if(listItemAdapter == null)
            return;
        lvBillInfoOrderList.setAdapter(listItemAdapter);
        lvBillInfoOrderList.setOnItemClickListener(itemListener);

    }

    //ListView��Item��������¼�
    @NonNull
    private ListView.OnItemClickListener itemListener = new
            ListView.OnItemClickListener()
            {

                @Override
                public void onItemClick(@NonNull AdapterView<?> arg0, View arg1, int arg2,
                                        long arg3) {

                    Adapter adapter=arg0.getAdapter();
                    Map<String,Object> map=(Map<String, Object>) adapter.getItem(arg2);

                    SerializableMap ResultMap = new SerializableMap();
                    ResultMap.setMap(map);

                    Intent intent = new Intent();
                    intent.putExtra("ResultBillInfo", ResultMap);

                    BillInfoOrderList.this.setResult(1, intent);
                    BillInfoOrderList.this.finish();

                }

            };

    //button��ť�ļ����¼�
    @NonNull
    private Button.OnClickListener ButtonClickListener = new
            Button.OnClickListener()
            {

                @Override
                public void onClick(@NonNull View v) {
                    switch(v.getId())
                    {
                        case R.id.btnBillInfoReturn:
                            BillInfoOrderList.this.finish();
                            break;
                    }

                }

            };

}
