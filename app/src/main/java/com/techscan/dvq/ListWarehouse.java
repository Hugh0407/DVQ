package com.techscan.dvq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.techscan.dvq.R.id;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListWarehouse extends Activity {
	//ADD CAIXY TEST START
//	private SoundPool sp;//����һ��SoundPool
//	private int MainLogin.music;//����һ��int������suondID
	//ADD CAIXY TEST END 
	Button btlist_warehouseReturn =null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_warehouse);
		//ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//��һ������Ϊͬʱ���������������������ڶ����������ͣ�����Ϊ��������
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
//		//ADD CAIXY END
		
		String jasstr= this.getIntent().getStringExtra("myData");
		btlist_warehouseReturn = (Button)findViewById(R.id.btlist_warehouseReturn);
		btlist_warehouseReturn.setOnClickListener(ButtonOnClickListener);
		
		try 
		{
			
			JSONObject jas=new JSONObject(jasstr);
			mData = getData(jas);
			ListView list = (ListView) findViewById(R.id.listwarehouse);  
			
	        SimpleAdapter listItemAdapter = new SimpleAdapter(this,mData,//����Դ   
	                R.layout.vlistwh,//ListItem��XMLʵ��  
	                //��̬������ImageItem��Ӧ������          
	                new String[] {"storcode","storname"},   
	                //ImageItem��XML�ļ������һ��ImageView,����TextView ID  
	                new int[] {R.id.vlistwarehousecode,R.id.vlistwarehousename}  
	            ); 
	        
	        list.setOnItemClickListener((OnItemClickListener) itemListener);
			
	        list.setAdapter(listItemAdapter);  
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
			//Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
		}
	}

	private OnClickListener ButtonOnClickListener = new OnClickListener()
    {
  		
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
  			{			//btnSDScanReturn
	  			case id.btlist_warehouseReturn:
	  				finish();					
					break;
  			}
		}	    	
    };
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_warehouse, menu);
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
	
	public List<Map<String, Object>> mData;
	
	private List<Map<String, Object>> getData(JSONObject jas)throws JSONException 
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		
		JSONObject tempJso = null;

//		if(!jas.getBoolean("Status"))
//		{
//			String errMsg = jas.getString("ErrMsg");
//
//			Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
//			return null;
//		}
		//��֪��Ϊʲôע�͵ģ����ڽ����жϽڵ��Ƿ����
		if(!jas.has("warehouse"))
		{
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return null;
		}
		JSONArray jsarray= jas.getJSONArray("warehouse");
		
		for(int i = 0;i<jsarray.length();i++)
		{
			tempJso = jsarray.getJSONObject(i);
			map = new HashMap<String, Object>();
			map.put("pk_stordoc", tempJso.getString("pk_stordoc"));
			map.put("storcode", tempJso.getString("storcode"));
			map.put("storname", tempJso.getString("storname"));

			list.add(map);
		}
		return list;
	}
	private ListView.OnItemClickListener itemListener = new
			ListView.OnItemClickListener()
	{
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) 
		{
			// TODO Auto-generated method stub
			 Adapter adapter=arg0.getAdapter();
             @SuppressWarnings("unchecked")
             Map<String,Object> map=(Map<String, Object>) adapter.getItem(arg2);
             
             String warehousePK1 =    map.get("pk_stordoc").toString();
             String warehousecode =  map.get("storcode").toString();
             String warehouseName =  map.get("storname").toString();
             
             Intent intent = new Intent();      
             intent.putExtra("result1", warehousePK1);// �ѷ������ݴ���Intent  
             intent.putExtra("result2", warehousecode);// �ѷ������ݴ���Intent    
             intent.putExtra("result3", warehouseName);// �ѷ������ݴ���Intent    
             ListWarehouse.this.setResult(13, intent);// ���ûش����ݡ�resultCodeֵ��1�����ֵ�������ڽ��������ֻش����ݵ���Դ��������ͬ�Ĵ���      
             ListWarehouse.this.finish();// �ر��Ӵ���ChildActivity 

		}
	};
	
	
}
