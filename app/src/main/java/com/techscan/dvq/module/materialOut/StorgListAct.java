package com.techscan.dvq.module.materialOut;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.techscan.dvq.login.MainLogin;
import com.techscan.dvq.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorgListAct extends Activity {

    @Nullable
    Button btnBack = null;
    @Nullable
    public List<Map<String, Object>> mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storg_list_act);
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("�����֯�б�");
        btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(ButtonOnClickListener);

        String jasstr = this.getIntent().getStringExtra("STOrg");
        try {
            JSONObject jas = new JSONObject(jasstr);
            mData = getData(jas);
            ListView list = (ListView) findViewById(R.id.listwarehouse);

            SimpleAdapter listItemAdapter = new SimpleAdapter(this, mData,//����Դ
                    R.layout.vlistwh,//ListItem��XMLʵ��
                    //��̬������ImageItem��Ӧ������
                    new String[]{"bodyname", "pk_calbody"},
                    //ImageItem��XML�ļ������һ��ImageView,����TextView ID
                    new int[]{R.id.vlistwarehousename, R.id.vlistwarehousecode}
            );
            list.setOnItemClickListener(itemListener);
            list.setAdapter(listItemAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
        }
    }

    private List<Map<String, Object>> getData(@NonNull JSONObject jas) throws JSONException {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        JSONObject tempJso;
        if (!jas.has("STOrg")) {
            Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_SHORT).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return null;
        }
        JSONArray jsarray = jas.getJSONArray("STOrg");

        for (int i = 0; i < jsarray.length(); i++) {
            tempJso = jsarray.getJSONObject(i);
            map = new HashMap<String, Object>();
            map.put("pk_areacl", tempJso.getString("pk_areacl"));
            map.put("bodyname", tempJso.getString("bodyname"));
            map.put("pk_calbody", tempJso.getString("pk_calbody"));
            list.add(map);
        }
        return list;
    }

    @NonNull
    private ListView.OnItemClickListener itemListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(@NonNull AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            Adapter adapter = arg0.getAdapter();
            Map<String, Object> map = (Map<String, Object>) adapter.getItem(arg2);

            String pk_areacl = map.get("pk_areacl").toString();
            String bodyname = map.get("bodyname").toString();
            String pk_calbody = map.get("pk_calbody").toString();

            Intent intent = new Intent();
            intent.putExtra("pk_areacl", pk_areacl);// �ѷ������ݴ���Intent
            intent.putExtra("bodyname", bodyname);// �ѷ������ݴ���Intent
            intent.putExtra("pk_calbody", pk_calbody);// �ѷ������ݴ���Intent
            StorgListAct.this.setResult(6, intent);// ���ûش����ݡ�resultCodeֵ��1�����ֵ�������ڽ��������ֻش����ݵ���Դ��������ͬ�Ĵ���
            StorgListAct.this.finish();// �ر��Ӵ���ChildActivity
        }
    };

    @NonNull
    private View.OnClickListener ButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(@NonNull View v) {
            switch (v.getId()) {
                case R.id.btn_back:
                    finish();
                    break;
            }
        }
    };
}
