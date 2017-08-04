package com.techscan.dvq;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.techscan.dvq.login.MainLogin;
import com.techscan.dvq.login.SettingActivity;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends Activity implements OnClickListener,OnKeyListener
{
    private final String PREFERENCE_NAME = "survey";
    @Nullable
    public static String CompanyCode     ="";
    @NonNull
    public static String LoginTime       ="";
    @Nullable
    public static String LoginString     ="";
    @NonNull
    public static String LoginUser       ="";
    @NonNull
    public static String LoingPassword   ="";
	
	//ADD CAIXY TEST START
//	private SoundPool sp;//����һ��SoundPool
//	private int MainLogin.music;//����һ��int������suondID
	//ADD CAIXY TEST END 
	
	@Override
	public boolean onKey(@NonNull View v, int keyCode, @NonNull KeyEvent event)
	{
		if(v.getId()==R.id.etInput)
		{
		if(keyCode==KeyEvent.KEYCODE_ENTER &&event.getAction() == KeyEvent.ACTION_UP)
		{
			if(v.getId()==R.id.etInput)
			{
				  EditText text=(EditText)findViewById(R.id.txtPwd);
				  text.requestFocus();
				  return true;							 
			}
		}
		return false;
		}
		return false;
		
	}


	
	//�����Ի���İ�ť�¼�����	
    @NonNull
    private DialogInterface.OnClickListener listener = new
			DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog,
							int whichButton)
		{
			EditText ss=(EditText)findViewById(R.id.etInput);
			Intent abcd = new Intent(MainActivity.this,Second.class);
			abcd.putExtra("hello", ss.getText().toString());
			startActivityForResult(abcd,R.layout.activity_second);						 
		}
	};

    @NonNull
    private DialogInterface.OnClickListener listenExit = new
				DialogInterface.OnClickListener()
	{
		public void onClick(DialogInterface dialog,
				int whichButton)
		{
				MainActivity.this.finish();
										  
		}
	};
	
	//��¼��ť
	private void BtnSettingOK()
	{
		SharedPreferences mySharedPreferences = getSharedPreferences(
                SettingActivity.PREFERENCE_SETTING, Activity.MODE_PRIVATE);
		
		MainActivity.LoginString =mySharedPreferences.getString("Address", "");
		MainActivity.CompanyCode=mySharedPreferences.getString("CompanyCode", "");
		EditText ss=(EditText)findViewById(R.id.txtUserName);
		EditText pwds=(EditText)findViewById(R.id.txtPassword);
		
		if(ss.equals("")||pwds.equals(""))
		{
			AlertDialog.Builder bulider = new AlertDialog.Builder(this).
					setTitle(R.string.TiXing).setMessage("�������û���������");
			   bulider.setPositiveButton(R.string.QueDing, null).create().show();
			   return;
		}
		if(MainActivity.LoginString.equals("")||
				MainActivity.CompanyCode.equals(""))
		{
			   AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(R.string.TiXing)
					   .setMessage("�������÷���·���͹�˾����");
			   bulider.setPositiveButton(R.string.QueDing, listener).create().show();
			   return;
		}
		
		//���÷���			
		HttpPost httpPost = new HttpPost(MainActivity.LoginString);
		httpPost.addHeader("Self-Test", "V");
		httpPost.addHeader("User-Code", ss.getText().toString());
		httpPost.addHeader("User-Pwd", pwds.getText().toString());
		HttpResponse httpResponse = null;
		
		try 
		{
				DefaultHttpClient defaults=new DefaultHttpClient();
				HttpConnectionParams.setConnectionTimeout(defaults.getParams(), 3000);
				HttpConnectionParams.setSoTimeout(defaults.getParams(),3000);
				httpResponse = defaults.execute(httpPost);
		} 
		catch (ClientProtocolException e) 
		{
			e.printStackTrace();
			//Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		} 
		catch (IOException e)
		{
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
		
		if (httpResponse.getStatusLine().getStatusCode() == 200)
		{
			try 
			{
				String result = EntityUtils.toString(httpResponse.getEntity());		
				String aaa=	EncodingUtils.getString(EncodingUtils.getBytes(result, "ISO8859-1"),"gb2312");
		
				try 
					{			
						JSONObject jas=new JSONObject(aaa);
						boolean status= jas.getBoolean("Status");
						if(status==true)
							{
								MainActivity.LoginTime ="";
								MainActivity.LoginUser=ss.getText().toString();
								MainActivity.LoingPassword=pwds.getText().toString();
								Intent abcd = 
										new Intent(MainActivity.this,Second.class);
								startActivity(abcd);
							}
						else
							{
								String ErrMsg=jas.getString("ErrMsg");
								Toast.makeText(this, ErrMsg, Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								return;
							}
					} 
				catch (JSONException e)
				{
					e.printStackTrace();
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
				}
			} 
			catch (ParseException e) 
			{
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
			} 
		catch (IOException e) 
		{
				// 
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
		}
		}
	}
	@Override
	public void onClick(@NonNull View v)
	{
		if(v.getId() ==R.id.btSettingFOk)
		{
			
//			SharedPreferences mySharedPreferences = getSharedPreferences(
//					SettingActivity.PREFERENCE_SETTING, Activity.MODE_PRIVATE);
//			
//			MainActivity.LoginString =mySharedPreferences.getString("Address", "");
//			MainActivity.CompanyCode =mySharedPreferences.getString("CompanyCode", "");
//			EditText ss=(EditText)findViewById(R.id.etInput);
//			EditText pwds=(EditText)findViewById(R.id.txtPwd);
//			//String 
//			if(ss.equals("")||pwds.equals(""))
//			{
//				AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle("����").setMessage("�������û���������");
//				   bulider.setPositiveButton("ȷ��", null).create().show();
//				   return;
//			}
//			if(MainActivity.LoginString.equals("")||MainActivity.CompanyCode.equals(""))
//			{
//				   AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle("����").setMessage("�������÷���·���͹�˾����");
//				   bulider.setPositiveButton("ȷ��", listener).create().show();
//				   return;
//			}
//			else
//			{
//				
//			}
//			HttpPost httpPost = new HttpPost(MainActivity.LoginString);
//			httpPost.addHeader("Self-Test", "V");
//			httpPost.addHeader("User-Code", ss.getText().toString());
//			httpPost.addHeader("User-Pwd", pwds.getText().toString());
//			HttpResponse httpResponse = null;
//			
//
//			try {
//				DefaultHttpClient defaults=new DefaultHttpClient();
//				HttpConnectionParams.setConnectionTimeout(defaults.getParams(), 3000);
//				HttpConnectionParams.setSoTimeout(defaults.getParams(),3000);
//				httpResponse = defaults.execute(httpPost);
//			} catch (ClientProtocolException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//				return;
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//				return;
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
//				return;
//			}
//			if (httpResponse.getStatusLine().getStatusCode() == 200)
//			{
//				try 
//				{
//					String result = EntityUtils.toString(httpResponse.getEntity());		
//					String aaa=	EncodingUtils.getString(EncodingUtils.getBytes(result, "ISO8859-1"),"gb2312");
//			
//			try {
////				JSONArray arrays =new JSONObject(aaa).getJSONArray("singers");
//				JSONObject jas=new JSONObject(aaa);
//				boolean status= jas.getBoolean("Status");
//				if(status==true)
//				{
//					MainActivity.LoginTime ="";
//					MainActivity.LoginUser=ss.getText().toString();
//					MainActivity.LoingPassword=pwds.getText().toString();
//					 Intent abcd = new Intent(MainActivity.this,Second.class);
//					 startActivity(abcd);
//				}
//				else
//				{
//					String ErrMsg=jas.getString("ErrMsg");
//					Toast.makeText(this, ErrMsg, Toast.LENGTH_LONG).show();
//					return;
//				}
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
////					Toast.makeText(this, aaa, Toast.LENGTH_LONG).show();
//				} catch (ParseException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//			}
//

		}
		else if(R.id.btExit==v.getId())
		{
			   AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage(R.string.NiQueDingYaoTuiChuMa);
			   bulider.setNegativeButton(R.string.QuXiao, null);
			   bulider.setPositiveButton(R.string.QueDing, listenExit).create().show();
			   
		}
		else if(R.id.btSetting== v.getId())
		{
			 Intent abcd = new Intent(MainActivity.this,SettingActivity.class);
				startActivityForResult(abcd,R.layout.activity_setting);
		}
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);							//���û��෽��
		setContentView(R.layout.activity_main);						//Ĭ�ϴ���
		
		
		Button btOk =(Button)findViewById(R.id.btSettingFOk);		//OK��ť 
		btOk.setOnClickListener(this);
		
		Button btExit =(Button)findViewById(R.id.btExit);			//�˳���ť
		btExit.setOnClickListener(this);
		
		Button btSetting =(Button)findViewById(R.id.btSetting);		//��¼��ť 
		btSetting.setOnClickListener(this);
		this.setTitle("��¼");
		
		//ADD CAIXY START
//		sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//��һ������Ϊͬʱ���������������������ڶ����������ͣ�����Ϊ��������
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
//		//ADD CAIXY END

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites()  
                .detectNetwork()  
                .penaltyLog()  
                .build());  

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());                
        EditText edit =(EditText)findViewById(R.id.etInput);
        edit.setOnKeyListener(this);
       edit.requestFocus();               
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
