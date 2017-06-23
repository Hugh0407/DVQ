package com.techscan.dvq.materialOut;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.techscan.dvq.MainLogin;
import com.techscan.dvq.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentListAct extends Activity {
    //ADD CAIXY TEST START
//	private SoundPool sp;//����һ��SoundPool
//	private int MainLogin.music;//����һ��int������suondID
    //ADD CAIXY TEST END
    Button btnBack = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_list);
        //ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//��һ������Ϊͬʱ���������������������ڶ����������ͣ�����Ϊ��������
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
//		//ADD CAIXY END
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("�����б�");
        String jasstr = this.getIntent().getStringExtra("myData");
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(ButtonOnClickListener);

        try {
            JSONObject jas = new JSONObject(jasstr);
            mData = getData(jas);
            ListView list = (ListView) findViewById(R.id.listwarehouse);

            SimpleAdapter listItemAdapter = new SimpleAdapter(this, mData,//����Դ
                    R.layout.vlistwh,//ListItem��XMLʵ��
                    //��̬������ImageItem��Ӧ������
                    new String[]{"deptcode", "deptname"},
                    //ImageItem��XML�ļ������һ��ImageView,����TextView ID
                    new int[]{R.id.vlistwarehousecode, R.id.vlistwarehousename}
            );
            list.setOnItemClickListener(itemListener);
            list.setAdapter(listItemAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
            //Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            //ADD CAIXY TEST START
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            //ADD CAIXY TEST END
        }
    }

    private View.OnClickListener ButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                    finish();
                    break;
            }
        }
    };

    public List<Map<String, Object>> mData;

    private List<Map<String, Object>> getData(JSONObject jas) throws JSONException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        JSONObject tempJso;
        if (!jas.has("department")) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return null;
        }
        JSONArray jsarray = jas.getJSONArray("department");

        for (int i = 0; i < jsarray.length(); i++) {
            tempJso = jsarray.getJSONObject(i);
            map = new HashMap<String, Object>();
            map.put("deptname", tempJso.getString("deptname"));
            map.put("pk_deptdoc", tempJso.getString("pk_deptdoc"));
            map.put("deptcode", tempJso.getString("deptcode"));
            list.add(map);
        }
        return list;
    }

    private ListView.OnItemClickListener itemListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Adapter adapter = arg0.getAdapter();
            Map<String, Object> map = (Map<String, Object>) adapter.getItem(arg2);

            String deptname = map.get("deptname").toString();
            String pk_deptdoc = map.get("pk_deptdoc").toString();
            String deptcode = map.get("deptcode").toString();

            Intent intent = new Intent();
            intent.putExtra("deptname", deptname);// �ѷ������ݴ���Intent
            intent.putExtra("pk_deptdoc", pk_deptdoc);// �ѷ������ݴ���Intent
            intent.putExtra("deptcode", deptcode);// �ѷ������ݴ���Intent
            DepartmentListAct.this.setResult(4, intent);// ���ûش����ݡ�resultCodeֵ��1�����ֵ�������ڽ��������ֻش����ݵ���Դ��������ͬ�Ĵ���
            DepartmentListAct.this.finish();// �ر��Ӵ���ChildActivity
        }
    };
}