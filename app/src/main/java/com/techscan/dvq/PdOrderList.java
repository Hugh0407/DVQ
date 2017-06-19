package com.techscan.dvq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
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
//
//import org.lee.android.R;
//import org.lee.android.MyListView4.MyAdapter;
//import org.lee.android.MyListView4.ViewHolder;
//import org.lee.android.R;


public class PdOrderList extends Activity{	
	
	//ADD CAIXY TEST START
//	private SoundPool sp;//����һ��SoundPool
//	private int MainLogin.music;//����һ��int������suondID
	//ADD CAIXY TEST END 
	Button btPdOrderListReturn =null;
	
	private List<Map<String, Object>> getData(JSONObject jas) 
			throws JSONException 
	{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;
		
		JSONObject tempJso = null;
		
		if(!jas.has("Status"))
		{
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return null;
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
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END

			return null;
		}
		JSONArray jsarray= jas.getJSONArray("dbHead");
		
		for(int i = 0;i<jsarray.length();i++)
		{
			tempJso = jsarray.getJSONObject(i);
			map = new HashMap<String, Object>();
			map.put("No", tempJso.getString("vcode"));
			map.put("From", tempJso.getString("cwarehousename") + "     ");
			map.put("To",tempJso.getString("cotherwhname"));
			map.put("BillId",tempJso.getString("cbillid"));
			if(tempJso.has("accid"))
			{
			map.put("AccID", tempJso.getString("accid"));
			}
			else
			{
				map.put("AccID", tempJso.getString("AccID"));
			}
			
			if(tempJso.getString("coutcorpid").equals(tempJso.getString("cincorpid")))
			{
				map.put("Dcorp", "  ");
			}
			else
			{
				map.put("Dcorp", "��");
			}
			map.put("statusE", "  ");
			
			map.put("OrgId", tempJso.getString("coutcbid"));
			map.put("companyID", tempJso.getString("coutcorpid"));
			map.put("warehouseID", tempJso.getString("coutwhid"));
			
//			 intent.putExtra("OrgId", OrgId);
//             intent.putExtra("companyID", companyID);
//             intent.putExtra("vcode", vCode);
//             intent.putExtra("warehouseID", warehouseID);
//			String OrgId=map.get("coutcbid").toString();
//         	String companyID=map.get("coutcorpid").toString();
//         	String warehouseID=map.get("cwarehouseid").toString();
//         	String vCode=map.get("vcode").toString();
			list.add(map);
		}
		return list;
	}
	public List<Map<String, Object>> mData;
	private Handler handler=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pd_order_list);
		
		this.setTitle("����������ϸ");
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()  
                .detectNetwork()
                .penaltyLog()  
                .build());  

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().
				detectLeakedClosableObjects().penaltyLog().penaltyDeath().build()); 

		//ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//��һ������Ϊͬʱ���������������������ڶ����������ͣ�����Ϊ��������
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
//		//ADD CAIXY END
		btPdOrderListReturn = (Button)findViewById(R.id.btPdOrderListReturn);
		btPdOrderListReturn.setOnClickListener(ButtonOnClickListener);
		
		
		JSONObject para = new JSONObject();
		Intent intent = this.getIntent(); 
		String FunctionName="";
		String BillCode = "";
		if(intent.hasExtra("BillCode"))
		{
			BillCode=intent.getCharSequenceExtra("BillCode").toString();
			String acc=BillCode.substring(0,1);
			String Bill=BillCode.substring(1);
			
			FunctionName="CommonQuery";//;
			try {
				para.put("BillCode", Bill);
				para.put("accId", acc);
				para.put("FunctionName", "GetAdjustOrderBillHeadOnlyByCode");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
			}
			
		}
		else
		{
			FunctionName="GetAdjustOrderBillHead";
			try {
				para.put("BillCode", "null");
				para.put("Wh-CodeA", MainLogin.objLog.WhCodeA);
				para.put("Wh-CodeB", MainLogin.objLog.WhCodeB);
				
				
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				//Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				e2.printStackTrace();
			}
		}
		
		try {
			para.put("TableName",  "dbHead");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
	        if(!BillCode.equals(""))
	        {
	        	this.finish();
	        }
			return;
		}	
		
		JSONObject jas;
		try {
	        if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return ;
	        }
			jas = Common.DoHttpQuery(para, FunctionName, "");
		} catch (Exception ex)
		{
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
	        if(!BillCode.equals(""))
	        {
	        	this.finish();
	        }
			return;
		}
		try 
		{	
			if(jas==null)
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
		        if(!BillCode.equals(""))
		        {
		        	this.finish();
		        }
				return;
			}
			if(!jas.has("Status"))
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		        if(!BillCode.equals(""))
		        {
		        	this.finish();
		        }
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
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
		        if(!BillCode.equals(""))
		        {
		        	this.finish();
		        }
				return;
			}
			
			mData = getData(jas);
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
	        if(!BillCode.equals(""))
	        {
	        	this.finish();
	        }
			return;
		}
		catch (Exception ex)
		{
			//Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
	        if(!BillCode.equals(""))
	        {
	        	this.finish();
	        }
			return;
		}
		
		ListView list = (ListView) findViewById(R.id.pdlist);  
				
        SimpleAdapter listItemAdapter = new SimpleAdapter(this,mData,//����Դ   
                R.layout.vlistpds,//ListItem��XMLʵ��  
                //��̬������ImageItem��Ӧ������          
                new String[] {"No","From", "To","AccID","Dcorp","statusE"}, 
                //ImageItem��XML�ļ������һ��ImageView,����TextView ID  
                new int[] {R.id.listpdorder,R.id.listfromware,R.id.listtoware,R.id.listaccid, R.id.listpddcorp, R.id.listpdbillstatus}  
            ); 
        
        //list.addHeaderView()
        
        list.setOnItemClickListener((OnItemClickListener) itemListener);		
        list.setAdapter(listItemAdapter);  
        
        
        if(!BillCode.equals(""))
        {
 			Map<String,Object> map=(Map<String, Object>) mData.get(0);
            String orderNo = map.get("No").toString();
            String billId=map.get("BillId").toString();
            String AccId=map.get("AccID").toString();
            
        	String OrgId=map.get("OrgId").toString();
        	String companyID=map.get("companyID").toString();
        	String vCode=map.get("No").toString();
        	String warehouseID=map.get("warehouseID").toString();
 
         	if(!Common.CheckUserRole(AccId,"1001","40081014"))
         	{
         		MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
         		Toast.makeText(PdOrderList.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian, Toast.LENGTH_LONG).show();
         		this.finish();
         		return;
         	}


         	if(!Common.CheckUserWHRole(AccId,"1001",warehouseID))
         	{
         		MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
         		Toast.makeText(PdOrderList.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian, Toast.LENGTH_LONG).show();
         		this.finish();
         		return;
         	}
         	
            Intent intentx = new Intent();      
            intentx.putExtra("result", orderNo);// �ѷ������ݴ���Intent  
            intentx.putExtra("BillId", billId);
            intentx.putExtra("AccID", AccId);
            intentx.putExtra("OrgId", OrgId);
            intentx.putExtra("companyID", companyID);
            intentx.putExtra("vcode", vCode);
            intentx.putExtra("warehouseID", warehouseID);
            
            PdOrderList.this.setResult(1, intentx);// ���ûش����ݡ�resultCodeֵ��1�����ֵ�������ڽ��������ֻش����ݵ���Դ��������ͬ�Ĵ���      
            this.finish();
        }
        
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pd_order_list, menu);
		return true;
	}

	
	
	private OnClickListener ButtonOnClickListener = new OnClickListener()
    {
  		
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
  			{			//btnSDScanReturn
	  			case id.btPdOrderListReturn:
	  				finish();					
					break;
  			}
		}	    	
    };
    
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

//	private List.. itemListener = new 
//			DialogInterface.OnClickListener()
	
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
             String orderNo = map.get("No").toString();
             String billId=map.get("BillId").toString();
             String AccId=map.get("AccID").toString();
             
         	String OrgId=map.get("OrgId").toString();
         	String companyID=map.get("companyID").toString();
         	String vCode=map.get("No").toString();
         	String warehouseID=map.get("warehouseID").toString();
         	
         	
         	if(!Common.CheckUserRole(AccId,"1001","40081014"))
         	{
         		MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
         		Toast.makeText(PdOrderList.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian, Toast.LENGTH_LONG).show();
         		return;
         	}


         	if(!Common.CheckUserWHRole(AccId,"1001",warehouseID))
         	{
         		MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
         		Toast.makeText(PdOrderList.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian, Toast.LENGTH_LONG).show();
         		return;
         	}
             
             Intent intent = new Intent();      
             intent.putExtra("result", orderNo);// �ѷ������ݴ���Intent  
             intent.putExtra("BillId", billId);
             intent.putExtra("AccID", AccId);
             intent.putExtra("OrgId", OrgId);
             intent.putExtra("companyID", companyID);
             intent.putExtra("vcode", vCode);
             intent.putExtra("warehouseID", warehouseID);
             
             PdOrderList.this.setResult(1, intent);// ���ûش����ݡ�resultCodeֵ��1�����ֵ�������ڽ��������ֻش����ݵ���Դ��������ͬ�Ĵ���      
             PdOrderList.this.finish();// �ر��Ӵ���ChildActivity 
             
             //Toast.makeText(PdOrderList.this, errMsg, Toast.LENGTH_SHORT).show();
             
		}
	};
	
}




//
//Runnable runnable=new Runnable()
//{
//   public void run() 
//    {
//        try 
//        {
//            Thread.sleep(2000);
//            //xmlwebData����������xml�е�����
//            //persons=XmlwebData.getData(path);
//            //������Ϣ������persons��϶��󴫵ݹ�ȥ
//            handler.sendMessage(handler.obtainMessage(0, persons));
//        } 
//        catch (InterruptedException e) 
//        {
//            e.printStackTrace();
//        }
//    }
//}


//
//try 
//         {
//            //�����߳�
//            new Thread(runnable).start();
//            //handler���߳�֮���ͨ�ż����ݴ���
//           handler=new Handler()
//           {
//               public void handleMessage(Message msg) 
//             {
//                   if(msg.what==0)
//                  {
//                       //msg.obj�ǻ�ȡhandler������Ϣ����������
//                     //@SuppressWarnings("unchecked")
//                       // ArrayList<Person> person=(ArrayList<Person>) msg.obj;
//                     //��ListView������
//                       // BinderListData(person);
//               		this.getIntent().getByteExtra(name, defaultValue)
//               		String jasstr= EncodingUtils.getString(this.getIntent().getByteArrayExtra("myData"),"utf-8");
//                    }
//                }
//            };
//        } 
//         catch (Exception e) 
//         {
//           e.printStackTrace();
//         }




//
//String LoginString =mySharedPreferences.getString("Address", "");
//String CompanyCode = mySharedPreferences.getString("CompanyCode", "");
//
//String lgUser = MainLogin.objLog.LoginUser;
//String lgPwd = MainLogin.objLog.Password;
//
//
//JSONObject para = new JSONObject();
//try {
//	para.put("BillCode", "null");
//} catch (JSONException e2) {
//	// TODO Auto-generated catch block
//	e2.printStackTrace();
//}
//try {
//	para.put("TableName",  "dbHead");
//} catch (JSONException e2) {
//	// TODO Auto-generated catch block
//	e2.printStackTrace();
//}		
//
//LoginString =  MainLogin.objLog.LoginString;
//
//HttpEntity entity = null;
//try 
//{
//	entity = new StringEntity(para.toString(), "gb2312");
//} 
//catch (UnsupportedEncodingException e1) 
//{
//	// TODO Auto-generated catch block
//	//e1.printStackTrace();
//	Toast.makeText(this, e1.getMessage(), Toast.LENGTH_LONG).show();
//	return;
//}  
//catch(Exception ex)
//{
//	Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
//	return;
//}
//	
//HttpPost httpPost = new HttpPost(LoginString);
//httpPost.setEntity(entity);		
////httpPost.addHeader("Self-Test", "AdjustToHead");
//httpPost.addHeader("Self-Test","GetAdjustOrderBillHead");
//httpPost.addHeader("User-Code", lgUser);
//httpPost.addHeader("User-Pwd", lgPwd);	
//httpPost.addHeader("User-Company", CompanyCode);
//httpPost.addHeader("Data-Source", "design");
//			
//HttpResponse httpResponse = null;		
//try 
//{
//	DefaultHttpClient defaults=new DefaultHttpClient();
//	HttpConnectionParams.setConnectionTimeout(defaults.getParams(), 30000);
//	HttpConnectionParams.setSoTimeout(defaults.getParams(),30000);
//	httpResponse = defaults.execute(httpPost);			
//} 
//catch (ClientProtocolException e) 
//{
//	e.printStackTrace();
//	Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//	return;
//} 
//catch (IOException e)
//{
//	e.printStackTrace();
//	Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//	return;
//}
//catch(Exception e)
//{
//	e.printStackTrace();
//	Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//	return;
//}
//String result="";
//String jasstr="";
//if (httpResponse.getStatusLine().getStatusCode() == 200)
//{
//	
//	try {
//		result = EntityUtils.toString(httpResponse.getEntity());
//	} catch (ParseException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	
//	byte[] bytes=null;
//	try
//	{
//	 jasstr = EncodingUtils.getString(EncodingUtils.getBytes(result, "iso8859-1"),"GB2312");
//		//bytes=EncodingUtils.getBytes(result, "iso8859-1");
//	}catch(Exception ex)
//	{
//		Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
//		return;
//	}
//}	
