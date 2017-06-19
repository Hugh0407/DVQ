package com.techscan.dvq;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SalesDelivery extends Activity {
	
	
	String tmpWHStatus = "";//�ֿ��Ƿ����û�λ
	
	TextView tvSalesDelPDOrder;
	EditText txtSalesDelPDOrder;

	private writeTxt writeTxt ;	
	TextView tvSalesDelPos;
	EditText txtSalesDelPos;
	TextView tvSalesDelRdcl;
//	ImageButton btnSalesDelRdcl;
	EditText txtSalesDelRdcl;
	EditText txtSalesDelCD;
	TextView tvSalesDelCD;
	ImageButton btnSalesDelPDOrder;
	Button btnSalesDelExit;
	Button btnSalesDelScan;
	Button btnSalesDelSave;
	ImageButton btnSalesDelCD;
//	TextView tvSalesDelManualNo;
//	EditText txtSalesDelManualNo;
	
	private ArrayList<String> ScanedBarcode = new ArrayList<String>();
	
	String sBillAccID = "";
	String sBillCorpPK = "";
	String sBillCode = "";
	String SaleFlg = "";
	
	
	TextView tvSalesDelBillCodeName;
	TextView tvSalesDelAccIDName;

	TextView tvSalesDelCorpName;
	
	TextView tvSalesDelBillCode;
	TextView tvSalesDelAccID;
	TextView tvSalesDelWare;
	TextView tvSalesDelCorp;
	
	TextView tvSalesDelWH;
	EditText txtSalesDelWH;
	ImageButton	btnSalesDelWH;
	
	
	int TaskCount = 0;
	String tmprdCode = "";
	String tmprdID = "";
	String tmprdName ="";
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
	private UUID uploadGuid=null;
	private String[] BillTypeNameList = null;
	
	private String[] WHNameList = null;
//	private String[] WHCodeList = null;
	private String[] WHIDList = null;
	
	
    private String[] CDIDList= null;
    private String[] CDNameList= null;
    private AlertDialog CDSelectButton= null;

 
    private AlertDialog BillTypeSelectButton=null;
    private AlertDialog WHSelectButton=null;
    private ButtonOnClick buttonOnClick = new ButtonOnClick(0);
    private String tmpWarehousePK = "";
    private String tmpCorpPK = "";
	private String tmpBillCode = "";
	private String rdflag = "1";
	private String GetBillBFlg = "1";
    
  //�����ñ�ͷ��Ϣ
  	private JSONObject jsonSaveHead = null;
  //��������
  	
  	
  	
  	
  	private JSONObject jsonBillBodyTask = null;
  	private JSONObject jsonBillBodyTask2 = null;
  	private String tmpAccID = "";
  	private List<Map<String, Object>> lstSaveBody = null;
    
 // ADD CAIXY TEST START
// 	private SoundPool sp;// ����һ��SoundPool
// 	private int MainLogin.music;// ����һ��int������suondID
 	// ADD CAIXY TEST END
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sales_delivery);
		
		this.setTitle("���۳���");		
		 
		txtSalesDelPDOrder = (EditText) findViewById(R.id.txtSalesDelPDOrder); 
		tvSalesDelPDOrder = (TextView) findViewById(R.id.tvSalesDelPDOrder);
		btnSalesDelPDOrder = (ImageButton) findViewById(R.id.btnSalesDelPDOrder);
		btnSalesDelPDOrder.setOnClickListener(new OnClickListener());
		
		tvSaleOutSelect = (EditText) findViewById(R.id.tvSaleOutSelect);
		btnSaleOutSelect = (ImageButton) findViewById(R.id.btnSaleOutSelect);
		btnSaleOutSelect.setOnClickListener(new OnClickListener());
		
		tvSalesDelWH = (TextView) findViewById(R.id.tvSalesDelWH);
		txtSalesDelWH = (EditText) findViewById(R.id.txtSalesDelWH);
		btnSalesDelWH = (ImageButton) findViewById(R.id.btnSalesDelWH);
		btnSalesDelWH.setOnClickListener(new OnClickListener());
		
		
		btnSalesDelScan = (Button) findViewById(R.id.btnSalesDelScan);
		btnSalesDelScan.setOnClickListener(new OnClickListener());
		btnSalesDelSave = (Button) findViewById(R.id.btnSalesDelSave);
		btnSalesDelSave.setOnClickListener(new OnClickListener());		
		btnSalesDelExit = (Button) findViewById(R.id.btnSalesDelExit);
		btnSalesDelExit.setOnClickListener(new OnClickListener());
		
		btnSalesDelCD = (ImageButton) findViewById(R.id.btnSalesDelCD);
		btnSalesDelCD.setOnClickListener(new OnClickListener());
		
		tvSalesDelBillCodeName = (TextView)findViewById(R.id.tvSalesDelBillCodeName);
		tvSalesDelAccIDName = (TextView)findViewById(R.id.tvSalesDelAccIDName);
		tvSalesDelCorpName = (TextView)findViewById(R.id.tvSalesDelCorpName);
		
		tvSalesDelBillCode = (TextView)findViewById(R.id.tvSalesDelBillCode);
		tvSalesDelAccID = (TextView)findViewById(R.id.tvSalesDelAccID);
		tvSalesDelCorp = (TextView)findViewById(R.id.tvSalesDelCorp);
		
		
		tvSalesDelPos = (TextView)findViewById(R.id.tvSalesDelPos);
		tvSalesDelRdcl = (TextView)findViewById(R.id.tvSalesDelRdcl);
		tvSalesDelCD = (TextView)findViewById(R.id.tvSalesDelCD);
		
//		btnSalesDelRdcl = (ImageButton) findViewById(R.id.btnSalesDelRdcl);
//		btnSalesDelRdcl.setOnClickListener(new OnClickListener());
		
		txtSalesDelPos = (EditText) findViewById(R.id.txtSalesDelPos);
		txtSalesDelRdcl = (EditText) findViewById(R.id.txtSalesDelRdcl);
		
		
		txtSalesDelCD = (EditText) findViewById(R.id.txtSalesDelCD);
		
		txtSalesDelPos.setOnKeyListener(EditTextOnKeyListener);
		txtSalesDelRdcl.setOnKeyListener(EditTextOnKeyListener);
		txtSalesDelPDOrder.setOnKeyListener(EditTextOnKeyListener);
		
		txtSalesDelCD.setFocusable(false);
		txtSalesDelCD.setFocusableInTouchMode(false);
		txtSalesDelRdcl.setFocusable(false);
		txtSalesDelRdcl.setFocusableInTouchMode(false);
		
		tvSaleOutSelect.setFocusableInTouchMode(false);
		
		btnSalesDelPDOrder.setFocusable(false);
		btnSalesDelExit.setFocusable(false);
		btnSalesDelScan.setFocusable(false);
		btnSalesDelSave.setFocusable(false);
		btnSalesDelCD.setFocusable(false);
		
		SharedPreferences sharedPreferences = getSharedPreferences(PREFERENCE_SETTING, 
				Activity.MODE_PRIVATE); 

				WhNameA =sharedPreferences.getString("WhCode", ""); 
				WhNameB =sharedPreferences.getString("AccId", ""); 
//				sCompanyCode=sharedPreferences.getString("CompanyCode", "");
//		        sOrgCode=sharedPreferences.getString("OrgCode", "");
		
		
		ClearBillDetailInfoShow();
		SetBillType();
		
		// ADD CAIXY START
//		sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// ��һ������Ϊͬʱ���������������������ڶ����������ͣ�����Ϊ��������
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); // ����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
		// ADD CAIXY END
		
		txtSalesDelPDOrder.requestFocus();
		
		jsonSaveHead = new JSONObject();
	}
	
	private void GetWHPosStatus() throws JSONException
    {
		JSONObject para = new JSONObject();
		para.put("FunctionName", "GetWHPosStatus");
		para.put("WareHouse", tmpWarehousePK);

		if(!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return ;
        }
		JSONObject rev = null;
		try {
			rev = Common.DoHttpQuery(para, "CommonQuery", tmpAccID);
		} catch (ParseException e) {

			Toast.makeText(this,"��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		} catch (IOException e) {
			
			Toast.makeText(this,"��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}		
		
		if(rev==null)
		{
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return ;
		}
		
		
		if(rev.getBoolean("Status"))
		{
			JSONArray val = rev.getJSONArray("position");				
			if(val.length() < 1)
			{
				Toast.makeText(this,"��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
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
		}
		else
		{
			Toast.makeText(this,"��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
			
		}
		
		
    }
	
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data)  
	{      
		if(requestCode==88)
		{
		    switch (resultCode) 
		    {          
		        case 1:         // ���ǵ������ⵥ�����б��صĵط� 
			        if (data != null) 
			        {             
			            Bundle bundle = data.getExtras();
			            if (bundle != null) 
			            {
			            	SerializableMap ResultMap = new SerializableMap();
			            	ResultMap = (SerializableMap)bundle.get("ResultBillInfo");
			            	Map<String,Object> mapBillInfo = ResultMap.getMap();			            	
			            	//�󶨱����ñ�ͷ
							jsonSaveHead = new JSONObject();
							txtSalesDelWH.setText("");
							txtSalesDelPos.setText("");
							txtSalesDelPos.setText("");
							tmpposCode = "";
							tmpposName = "";
							tmpposID = "";
							tmprdCode="";
							tmprdID = "";
							tmpWarehousePK = "";
							//wuqiong
							try {
								jsonSaveHead = Common.MapTOJSONOBject(mapBillInfo);
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
							
							//BindingBillDetailInfo(mapBillInfo);
							if(!BindingBillDetailInfo(mapBillInfo))
							{
								return;
							}
							
							GetBillBodyDetailInfo(SaleFlg);

                                
							try {
								GetTaskCount();
							} catch (JSONException e1) {
								e1.printStackTrace();
							}
							if(GetBillBFlg.equals("1"))
							{
								
								SetRDCL();
								tmpCdTypeID = "";
								try {
									GetSalesDelWH();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
			            }
			        }         
					//ADD BY WUQIONG 2015/04/27
			        break;
		        case 2:         // �����շ���𷵻صĵط� 
			        if (data != null) 
			        {             
			            Bundle bundle = data.getExtras();
			            if (bundle != null) 
			            {
				            		            
			            	tmprdCode = data.getStringExtra("Code");
			            	tmprdID = data.getStringExtra("RdID");
			            	tmprdName = data.getStringExtra("Name");
			            	txtSalesDelRdcl.setText(tmprdName);
				            
			            }
			            else
			            {
			            	txtSalesDelRdcl.setText("");
			            }
			            
			        }     
			      //ADD BY WUQIONG 2015/04/27
			        break;
		        default:              
		            //�������ڵĻش�����           
		        	//IniActivyMemor();
		        break;          
		     }  
		}
		else if(requestCode==86)
		{
			if(resultCode==6)
			{
				if(data != null)
				{
					Bundle bundle = data.getExtras();
					if(bundle != null)
					{							
						SerializableList ResultBodyList = new SerializableList();
						ResultBodyList = (SerializableList)bundle.get("SaveBodyList");
						lstSaveBody = ResultBodyList.getList();
				        ScanedBarcode = bundle.getStringArrayList("ScanedBarcode");
						
						try {
							jsonBillBodyTask = new JSONObject(bundle.getString("ScanTaskJson"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							e.printStackTrace();
						}
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);  
	}
	
	private Map<String,Object> GetBillDetailInfoByBillCode(String sAccID,String sCorpPK,String sBillCode,String sSaleFlg)
	{
		SaleFlg = sSaleFlg;
		JSONObject para = new JSONObject();
		Map<String,Object> mapBillInfo = new HashMap<String,Object>();

		try {
			if(tvSaleOutSelect.getText().toString().equals("���۳���"))
	  		{
				para.put("FunctionName", "GetSalereceiveHead");
				para.put("CorpPK", sCorpPK);
				para.put("BillCode", sBillCode);	
	  		}
			
			if(tvSaleOutSelect.getText().toString().equals("�˻�����"))
	  		{
				para.put("FunctionName", "GetSaledH");
				para.put("CorpPK", sCorpPK);
				para.put("BillCode", sBillCode);
	  		}
			
			if(tvSaleOutSelect.getText().toString().equals("�˻ز���"))
	  		{
				if(sSaleFlg.equals("T"))
				{
					para.put("FunctionName", "GetSaleTakeHead");
					para.put("CorpPK", sCorpPK);
					para.put("BillCode", sBillCode);
				}
				
				else if(sSaleFlg.equals("D"))
				{
					para.put("FunctionName", "GetSaleOutHead");
					para.put("CorpPK", sCorpPK);
					para.put("BillCode", sBillCode);
				}
				
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
			para.put("TableName",  "dbHead");
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
			if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return null;
	        }
			jas = Common.DoHttpQuery(para, "CommonQuery", sAccID);
		} catch (Exception ex)
		{
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return null;
		}
		
		//��ȡ�õĵ�����Ϣ�󶨵�ListView��
		try 
		{	
			if(jas==null)
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
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
			
			//�󶨵�map
			mapBillInfo = new HashMap<String,Object>();
			JSONArray jsarray= jas.getJSONArray("dbHead");
			
			jas = jsarray.getJSONObject(0);
			
			if(tvSaleOutSelect.getText().toString().equals("���۳���"))
	  		{
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
				if(sAccID.equals("A"))
					mapBillInfo.put("coperatorid", MainLogin.objLog.UserID);//������
				else
					mapBillInfo.put("coperatorid", MainLogin.objLog.UserIDB);//������
				mapBillInfo.put("ctransporttypeid", jas.getString("ctransporttypeid"));//���䷽ʽID
				mapBillInfo.put("cbiztype", jas.getString("cbiztype"));
	  		}
			
			if(tvSaleOutSelect.getText().toString().equals("�˻�����"))
	  		{
				//mapBillInfo.put("pk_corp", jas.getString("pk_corp"));
				mapBillInfo.put("pk_corp", jas.getString("pk_corp"));
				mapBillInfo.put("custname", jas.getString("custname"));
				mapBillInfo.put("pk_cubasdoc", jas.getString("pk_cubasdocc"));
				mapBillInfo.put("pk_cumandoc", jas.getString("ccustomerid"));
				mapBillInfo.put("billID", jas.getString("cgeneralhid"));
				mapBillInfo.put("billCode", jas.getString("vbillcode"));
				mapBillInfo.put("AccID", sAccID);
				mapBillInfo.put("vdef11", jas.getString("vuserdef11"));
				mapBillInfo.put("vdef12", jas.getString("vuserdef12"));
				mapBillInfo.put("vdef13", jas.getString("vuserdef13"));
				mapBillInfo.put("saleflg", "");
				if(sAccID.equals("A"))
				{
					mapBillInfo.put("coperatorid", MainLogin.objLog.UserID);//������
					mapBillInfo.put("ctransporttypeid", "0001AA100000000003U7");//���䷽ʽID
				}
				else
				{
					mapBillInfo.put("coperatorid", MainLogin.objLog.UserIDB);//������
					mapBillInfo.put("ctransporttypeid", "0001DD10000000000XQT");//���䷽ʽID
				}
				mapBillInfo.put("cbiztype", jas.getString("cbiztype"));
	  		}
				
			if(tvSaleOutSelect.getText().toString().equals("�˻ز���"))
	  		{
				if(sSaleFlg.equals("T"))
				{
					mapBillInfo.put("pk_corp", jas.getString("pk_corp"));
			        mapBillInfo.put("custname", jas.getString("custname"));
			        mapBillInfo.put("pk_cubasdoc", jas.getString("pk_cubasdoc"));
			        mapBillInfo.put("pk_cumandoc", jas.getString("pk_cumandoc"));
			        mapBillInfo.put("billID", jas.getString("pk_take"));
			        mapBillInfo.put("billCode", jas.getString("vreceiptcode"));
			        mapBillInfo.put("AccID", sAccID);
					mapBillInfo.put("vdef11", jas.getString("vdef11"));
					mapBillInfo.put("vdef12", jas.getString("vdef12"));
					mapBillInfo.put("vdef13", jas.getString("vdef13"));
					mapBillInfo.put("saleflg", "T");
					
					if(sAccID.equals("A"))
					{
						mapBillInfo.put("coperatorid", MainLogin.objLog.UserID);//������
						mapBillInfo.put("ctransporttypeid", "0001AA100000000003U7");//���䷽ʽID
					}
					else
					{
						mapBillInfo.put("coperatorid", MainLogin.objLog.UserIDB);//������
						mapBillInfo.put("ctransporttypeid", "0001DD10000000000XQT");//���䷽ʽID
					}
					mapBillInfo.put("cbiztype", jas.getString("cbiztype"));
				}
				else if (sSaleFlg.equals("D"))
				{
					mapBillInfo.put("pk_corp", jas.getString("pk_corp"));
			        mapBillInfo.put("custname", jas.getString("custname"));
			        mapBillInfo.put("pk_cubasdoc", jas.getString("pk_cubasdoc"));
			        mapBillInfo.put("pk_cumandoc", jas.getString("ccustomerid"));
			        mapBillInfo.put("billID", jas.getString("csaleid"));
			        mapBillInfo.put("billCode", jas.getString("vreceiptcode"));
			        mapBillInfo.put("AccID", sAccID);
					mapBillInfo.put("vdef11", jas.getString("vdef11"));
					mapBillInfo.put("vdef12", jas.getString("vdef12"));
					mapBillInfo.put("vdef13", jas.getString("vdef13"));
					mapBillInfo.put("saleflg", "D");
					
					if(sAccID.equals("A"))
					{
						mapBillInfo.put("coperatorid", MainLogin.objLog.UserID);//������
						mapBillInfo.put("ctransporttypeid", "0001AA100000000003U7");//���䷽ʽID
					}
					else
					{
						mapBillInfo.put("coperatorid", MainLogin.objLog.UserIDB);//������
						mapBillInfo.put("ctransporttypeid", "0001DD10000000000XQT");//���䷽ʽID
					}
					mapBillInfo.put("cbiztype", jas.getString("cbiztype"));
				}
	  		}

			//�����ñ�ͷJSONObject����---����
			return mapBillInfo;
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return null;
		}
		catch (Exception ex)
		{
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return null;
		}
	}
	
	
	private void SetRDCL()
	{
		if(tvSaleOutSelect.getText().toString().equals("���۳���"))
  		{
			tmprdCode = "202";
			if (tmpAccID.equals("A")) {
				tmprdID = "0001AA100000000003VD";    //
			} else if (tmpAccID.equals("B")) {
				tmprdID = "0001DD10000000000XR8";    //
			}
			tmprdName = "���۳���";
			txtSalesDelRdcl.setText(tmprdName);
  		}
		
		if(tvSaleOutSelect.getText().toString().equals("�˻�����"))
  		{
			tmprdCode = "210";
			if (tmpAccID.equals("A")) {
				tmprdID = "0001AA100000000003VL";        //
			} else if (tmpAccID.equals("B")) {
				tmprdID = "0001DD10000000000XRG";    //
			}
			tmprdName = "�����˻�";
			txtSalesDelRdcl.setText(tmprdName);
  		}
		
		if(tvSaleOutSelect.getText().toString().equals("�˻ز���"))
  		{
			tmprdCode = "210";
			if (tmpAccID.equals("A")) {
				tmprdID = "0001AA100000000003VL";        //
			} else if (tmpAccID.equals("B")) {
				tmprdID = "0001DD10000000000XRG";    //
			}
			tmprdName = "�����˻�";
			txtSalesDelRdcl.setText(tmprdName);
  		}
	}
	
	
	//���ݶ�����ͷ�õ�������ϸ
	private void GetBillBodyDetailInfo2(String sSaleFlg)
	{
		GetBillBFlg = "0";
		if(tmpAccID==null || tmpAccID.equals(""))
			return;
		
		JSONObject para = new JSONObject();
		//Map<String,Object> mapBillBody = new HashMap<String,Object>();
		try {
			
			if(tvSaleOutSelect.getText().toString().equals("���۳���"))
	  		{
				para.put("FunctionName", "GetSalereceiveBody");			
				para.put("BillCode", tmpBillCode);
				para.put("CorpPK", tmpCorpPK);
	  		}
			
			if(tvSaleOutSelect.getText().toString().equals("�˻�����"))
	  		{
				para.put("FunctionName", "GetSaledB");			
				para.put("BillCode", tmpBillCode);
				para.put("CorpPK", tmpCorpPK);
	  		}
			
			if(tvSaleOutSelect.getText().toString().equals("�˻ز���"))
	  		{
				if(sSaleFlg.equals("T"))
				{
					para.put("FunctionName", "GetSaleTakeBody");			
					para.put("BillCode", tmpBillCode);
					para.put("CorpPK", tmpCorpPK);
				}
				else if(sSaleFlg.equals("D"))
				
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
			para.put("TableName",  "dbBody");
		} catch (JSONException e2) {
			Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
		JSONObject jas;
		try {
			if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return ;
	        }
			jas = Common.DoHttpQuery(para, "CommonQuery", tmpAccID);
			//txtSalesDelManualNo.setEnabled(true);
			txtSalesDelPos.setEnabled(true);
			txtSalesDelRdcl.setEnabled(true);
			
		} catch (Exception ex)
		{
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
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
				return;
			}
			
			if(!jas.has("Status"))
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
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
				return;
			}
			jsonBillBodyTask2 = new JSONObject();
			//jsonBillBodyTask = jas;
			//��Ҫ�޸����ݽṹ
			
			JSONArray jsarray= jas.getJSONArray("dbBody");
			
			JSONArray NewBodyarray = new JSONArray();
			JSONObject NewBodJSON = null;
			
			for(int i = 0;i<jsarray.length();i++)
			{
				JSONObject tempJso = jsarray.getJSONObject(i);
				NewBodJSON = new JSONObject();
			
				if(tvSaleOutSelect.getText().toString().equals("���۳���"))
		  		{
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
	                if(!outnumber.equals("null")) {
	                    outnumber = outnumber.replaceAll("\\.0", "");
	                } else {
	                }
	                if(!number.equals("null")) {
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
				
				if(tvSaleOutSelect.getText().toString().equals("�˻�����"))
		  		{
					NewBodJSON.put("vfree1", tempJso.getString("vfree1"));
	                NewBodJSON.put("pk_measdoc", tempJso.getString("pk_measdoc"));
	                NewBodJSON.put("measname", tempJso.getString("measname"));
	                NewBodJSON.put("invcode", tempJso.getString("invcode"));
	                NewBodJSON.put("invname", tempJso.getString("invname"));
	                NewBodJSON.put("invspec", tempJso.getString("invspec"));
	                NewBodJSON.put("invtype", tempJso.getString("invtype"));
	                NewBodJSON.put("billcode", tmpBillCode);
	                NewBodJSON.put("batchcode", tempJso.getString("vbatchcode"));
	                NewBodJSON.put("invbasdocid", tempJso.getString("cinvbasid"));
	                NewBodJSON.put("invmandocid", tempJso.getString("cinventoryid"));
	                String number = tempJso.getString("noutnum");
	                String outnumber = tempJso.getString("outnum");
	                if(!outnumber.equals("null")) {
	                    outnumber = outnumber.replaceAll("\\.0", "");
	                } else {
	                }
	                if(!number.equals("null")) {
	                    number = number.replaceAll("\\.0", "");
	                } else {
	                }
	                NewBodJSON.put("number", number);
	                NewBodJSON.put("outnumber", outnumber);
	                NewBodJSON.put("sourcerowno",  tempJso.getString("vfirstrowno"));
	                NewBodJSON.put("sourcehid", tempJso.getString("cfirstbillhid"));
	                NewBodJSON.put("sourcebid", tempJso.getString("cfirstbillbid"));
	                NewBodJSON.put("sourcehcode", tempJso.getString("vfirstbillcode"));
	                NewBodJSON.put("sourcetype", tempJso.getString("cfirsttype"));
	                NewBodJSON.put("crowno", tempJso.getString("crowno"));
	                NewBodJSON.put("billhid", tempJso.getString("cgeneralhid"));
	                NewBodJSON.put("billbid", tempJso.getString("cgeneralbid"));
	                NewBodJSON.put("billhcode", tmpBillCode);
	                NewBodJSON.put("billtype", "4C");
	                NewBodJSON.put("ddeliverdate", tempJso.getString("ddeliverdate"));
	                NewBodJSON.put("pk_defdoc6", tempJso.getString("pk_defdoc6"));
	                NewBodJSON.put("def6", tempJso.getString("vuserdef6"));
		  		}
				
				if(tvSaleOutSelect.getText().toString().equals("�˻ز���"))
		  		{
					if(sSaleFlg.equals("D"))
					{
						NewBodJSON.put("vfree1", tempJso.getString("vfree1"));
		                NewBodJSON.put("pk_measdoc", tempJso.getString("pk_measdoc"));
		                NewBodJSON.put("measname", tempJso.getString("measname"));
		                NewBodJSON.put("invcode", tempJso.getString("invcode"));
		                NewBodJSON.put("invname", tempJso.getString("invname"));
		                NewBodJSON.put("invspec", tempJso.getString("invspec"));
		                NewBodJSON.put("invtype", tempJso.getString("invtype"));
		                NewBodJSON.put("billcode", tmpBillCode);
		                NewBodJSON.put("batchcode", tempJso.getString("cbatchid"));
		                NewBodJSON.put("invbasdocid", tempJso.getString("cinvbasdocid"));
		                NewBodJSON.put("invmandocid", tempJso.getString("cinventoryid"));
		                String number = tempJso.getString("nnnumber");
		                String outnumber = tempJso.getString("noutnumber");
		                if(!outnumber.equals("null")) {
		                    outnumber = outnumber.replaceAll("\\.0", "");
		                } else {
		                }
		                if(!number.equals("null")) {
		                    number = number.replaceAll("\\.0", "");
		                } else {
		                }
		                NewBodJSON.put("number", number);
		                NewBodJSON.put("outnumber", outnumber);
		                NewBodJSON.put("sourcerowno", "");
		                NewBodJSON.put("sourcehid", "");
		                NewBodJSON.put("sourcebid", "");
		                NewBodJSON.put("sourcehcode", "");
		                NewBodJSON.put("sourcetype", "");
		                NewBodJSON.put("crowno", tempJso.getString("crowno"));
		                NewBodJSON.put("billhid", tempJso.getString("csaleid"));
		                NewBodJSON.put("billbid", tempJso.getString("corder_bid"));
		                NewBodJSON.put("billhcode", tmpBillCode);
		                NewBodJSON.put("billtype", "30");
		                NewBodJSON.put("ddeliverdate", tempJso.getString("dconsigndate"));
		                NewBodJSON.put("pk_defdoc6", tempJso.getString("pk_defdoc6"));
		                NewBodJSON.put("def6", tempJso.getString("vdef6"));
					}
					else if (sSaleFlg.equals("T"))
					{
						NewBodJSON.put("vfree1", tempJso.getString("vfree1"));
		                NewBodJSON.put("pk_measdoc", tempJso.getString("pk_measdoc"));
		                NewBodJSON.put("measname", tempJso.getString("measname"));
		                NewBodJSON.put("invcode", tempJso.getString("invcode"));
		                NewBodJSON.put("invname", tempJso.getString("invname"));
		                NewBodJSON.put("invspec", tempJso.getString("invspec"));
		                NewBodJSON.put("invtype", tempJso.getString("invtype"));
		                NewBodJSON.put("billcode", tmpBillCode);
		                NewBodJSON.put("batchcode", tempJso.getString("cbatchid"));
		                NewBodJSON.put("invbasdocid", tempJso.getString("cinvbasdocid"));
		                NewBodJSON.put("invmandocid", tempJso.getString("cinventoryid"));
		                String number = tempJso.getString("ntakenumber");
		                String outnumber = tempJso.getString("ninnumber");
		                if(!outnumber.equals("null")) {
		                    outnumber = outnumber.replaceAll("\\.0", "");
		                } else {
		                }
		                if(!number.equals("null")) {
		                    number = number.replaceAll("\\.0", "");
		                } else {
		                }
		                NewBodJSON.put("number", number);
		                NewBodJSON.put("outnumber", outnumber);
		                NewBodJSON.put("sourcerowno", "");
		                NewBodJSON.put("sourcehid", tempJso.getString("csourcebillid"));
		                NewBodJSON.put("sourcebid", tempJso.getString("csourcebillbodyid"));
		                NewBodJSON.put("sourcehcode", tempJso.getString("vsourcecode"));
		                NewBodJSON.put("sourcetype", tempJso.getString("csourcebilltype"));
		                NewBodJSON.put("crowno", tempJso.getString("crowno"));
		                NewBodJSON.put("billhid", tempJso.getString("pk_take"));
		                NewBodJSON.put("billbid", tempJso.getString("pk_take_b"));
		                NewBodJSON.put("billhcode", tmpBillCode);
		                NewBodJSON.put("billtype", "3V");
		                NewBodJSON.put("ddeliverdate", "");
		                NewBodJSON.put("pk_defdoc6", tempJso.getString("pk_defdoc6"));
		                NewBodJSON.put("def6", tempJso.getString("vdef6"));
					}
					
		  		}
				NewBodyarray.put(NewBodJSON);
			}
			
			jsonBillBodyTask2.put("Status", true);
			jsonBillBodyTask2.put("dbBody", NewBodyarray);
			
			GetBillBFlg = "1";
			
			
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		catch (Exception ex)
		{
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
	}
	
	
	private void GetBillBodyDetailInfo(String sSaleFlg)
	{
		GetBillBFlg = "0";
		if(tmpAccID==null || tmpAccID.equals(""))
			return;
		
		JSONObject para = new JSONObject();
		//Map<String,Object> mapBillBody = new HashMap<String,Object>();
		try {
			
			if(tvSaleOutSelect.getText().toString().equals("���۳���"))
	  		{
				para.put("FunctionName", "GetSalereceiveBody");			
				para.put("BillCode", tmpBillCode);
				para.put("CorpPK", tmpCorpPK);
	  		}
			
			if(tvSaleOutSelect.getText().toString().equals("�˻�����"))
	  		{
				para.put("FunctionName", "GetSaledB");			
				para.put("BillCode", tmpBillCode);
				para.put("CorpPK", tmpCorpPK);
	  		}
			
			if(tvSaleOutSelect.getText().toString().equals("�˻ز���"))
	  		{
				if(sSaleFlg.equals("T"))
				{
					para.put("FunctionName", "GetSaleTakeBody");			
					para.put("BillCode", tmpBillCode);
					para.put("CorpPK", tmpCorpPK);
				}
				else if(sSaleFlg.equals("D"))
				
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
			para.put("TableName",  "dbBody");
		} catch (JSONException e2) {
			Toast.makeText(this, e2.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
		JSONObject jas;
		try {
			if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return ;
	        }
			jas = Common.DoHttpQuery(para, "CommonQuery", tmpAccID);
			//txtSalesDelManualNo.setEnabled(true);
			txtSalesDelPos.setEnabled(true);
			txtSalesDelRdcl.setEnabled(true);
			
		} catch (Exception ex)
		{
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
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
				return;
			}
			
			if(!jas.has("Status"))
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
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
				return;
			}
			jsonBillBodyTask = new JSONObject();
			//jsonBillBodyTask = jas;
			//��Ҫ�޸����ݽṹ
			
			JSONArray jsarray= jas.getJSONArray("dbBody");
			
			JSONArray NewBodyarray = new JSONArray();
			JSONObject NewBodJSON = null;
			
			for(int i = 0;i<jsarray.length();i++)
			{
				JSONObject tempJso = jsarray.getJSONObject(i);
				NewBodJSON = new JSONObject();
			
				if(tvSaleOutSelect.getText().toString().equals("���۳���"))
		  		{
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
	                if(!outnumber.equals("null")) {
	                    outnumber = outnumber.replaceAll("\\.0", "");
	                } else {
	                }
	                if(!number.equals("null")) {
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
				
				if(tvSaleOutSelect.getText().toString().equals("�˻�����"))
		  		{
					NewBodJSON.put("vfree1", tempJso.getString("vfree1"));
	                NewBodJSON.put("pk_measdoc", tempJso.getString("pk_measdoc"));
	                NewBodJSON.put("measname", tempJso.getString("measname"));
	                NewBodJSON.put("invcode", tempJso.getString("invcode"));
	                NewBodJSON.put("invname", tempJso.getString("invname"));
	                NewBodJSON.put("invspec", tempJso.getString("invspec"));
	                NewBodJSON.put("invtype", tempJso.getString("invtype"));
	                NewBodJSON.put("billcode", tmpBillCode);
	                NewBodJSON.put("batchcode", tempJso.getString("vbatchcode"));
	                NewBodJSON.put("invbasdocid", tempJso.getString("cinvbasid"));
	                NewBodJSON.put("invmandocid", tempJso.getString("cinventoryid"));
	                String number = tempJso.getString("noutnum");
	                String outnumber = tempJso.getString("outnum");
	                if(!outnumber.equals("null")) {
	                    outnumber = outnumber.replaceAll("\\.0", "");
	                } else {
	                }
	                if(!number.equals("null")) {
	                    number = number.replaceAll("\\.0", "");
	                } else {
	                }
	                NewBodJSON.put("number", number);
	                NewBodJSON.put("outnumber", outnumber);
	                NewBodJSON.put("sourcerowno",  tempJso.getString("vfirstrowno"));
	                NewBodJSON.put("sourcehid", tempJso.getString("cfirstbillhid"));
	                NewBodJSON.put("sourcebid", tempJso.getString("cfirstbillbid"));
	                NewBodJSON.put("sourcehcode", tempJso.getString("vfirstbillcode"));
	                NewBodJSON.put("sourcetype", tempJso.getString("cfirsttype"));
	                NewBodJSON.put("crowno", tempJso.getString("crowno"));
	                NewBodJSON.put("billhid", tempJso.getString("cgeneralhid"));
	                NewBodJSON.put("billbid", tempJso.getString("cgeneralbid"));
	                NewBodJSON.put("billhcode", tmpBillCode);
	                NewBodJSON.put("billtype", "4C");
	                NewBodJSON.put("ddeliverdate", tempJso.getString("ddeliverdate"));
	                NewBodJSON.put("pk_defdoc6", tempJso.getString("pk_defdoc6"));
	                NewBodJSON.put("def6", tempJso.getString("vuserdef6"));
		  		}
				
				if(tvSaleOutSelect.getText().toString().equals("�˻ز���"))
		  		{
					if(sSaleFlg.equals("D"))
					{
						NewBodJSON.put("vfree1", tempJso.getString("vfree1"));
		                NewBodJSON.put("pk_measdoc", tempJso.getString("pk_measdoc"));
		                NewBodJSON.put("measname", tempJso.getString("measname"));
		                NewBodJSON.put("invcode", tempJso.getString("invcode"));
		                NewBodJSON.put("invname", tempJso.getString("invname"));
		                NewBodJSON.put("invspec", tempJso.getString("invspec"));
		                NewBodJSON.put("invtype", tempJso.getString("invtype"));
		                NewBodJSON.put("billcode", tmpBillCode);
		                NewBodJSON.put("batchcode", tempJso.getString("cbatchid"));
		                NewBodJSON.put("invbasdocid", tempJso.getString("cinvbasdocid"));
		                NewBodJSON.put("invmandocid", tempJso.getString("cinventoryid"));
		                String number = tempJso.getString("nnnumber");
		                String outnumber = tempJso.getString("noutnumber");
		                if(!outnumber.equals("null")) {
		                    outnumber = outnumber.replaceAll("\\.0", "");
		                } else {
		                }
		                if(!number.equals("null")) {
		                    number = number.replaceAll("\\.0", "");
		                } else {
		                }
		                NewBodJSON.put("number", number);
		                NewBodJSON.put("outnumber", outnumber);
		                NewBodJSON.put("sourcerowno", "");
		                NewBodJSON.put("sourcehid", "");
		                NewBodJSON.put("sourcebid", "");
		                NewBodJSON.put("sourcehcode", "");
		                NewBodJSON.put("sourcetype", "");
		                NewBodJSON.put("crowno", tempJso.getString("crowno"));
		                NewBodJSON.put("billhid", tempJso.getString("csaleid"));
		                NewBodJSON.put("billbid", tempJso.getString("corder_bid"));
		                NewBodJSON.put("billhcode", tmpBillCode);
		                NewBodJSON.put("billtype", "30");
		                NewBodJSON.put("ddeliverdate", tempJso.getString("dconsigndate"));
		                NewBodJSON.put("pk_defdoc6", tempJso.getString("pk_defdoc6"));
		                NewBodJSON.put("def6", tempJso.getString("vdef6"));
					}
					else if (sSaleFlg.equals("T"))
					{
						NewBodJSON.put("vfree1", tempJso.getString("vfree1"));
		                NewBodJSON.put("pk_measdoc", tempJso.getString("pk_measdoc"));
		                NewBodJSON.put("measname", tempJso.getString("measname"));
		                NewBodJSON.put("invcode", tempJso.getString("invcode"));
		                NewBodJSON.put("invname", tempJso.getString("invname"));
		                NewBodJSON.put("invspec", tempJso.getString("invspec"));
		                NewBodJSON.put("invtype", tempJso.getString("invtype"));
		                NewBodJSON.put("billcode", tmpBillCode);
		                NewBodJSON.put("batchcode", tempJso.getString("cbatchid"));
		                NewBodJSON.put("invbasdocid", tempJso.getString("cinvbasdocid"));
		                NewBodJSON.put("invmandocid", tempJso.getString("cinventoryid"));
		                String number = tempJso.getString("ntakenumber");
		                String outnumber = tempJso.getString("ninnumber");
		                if(!outnumber.equals("null")) {
		                    outnumber = outnumber.replaceAll("\\.0", "");
		                } else {
		                }
		                if(!number.equals("null")) {
		                    number = number.replaceAll("\\.0", "");
		                } else {
		                }
		                NewBodJSON.put("number", number);
		                NewBodJSON.put("outnumber", outnumber);
		                NewBodJSON.put("sourcerowno", "");
		                NewBodJSON.put("sourcehid", tempJso.getString("csourcebillid"));
		                NewBodJSON.put("sourcebid", tempJso.getString("csourcebillbodyid"));
		                NewBodJSON.put("sourcehcode", tempJso.getString("vsourcecode"));
		                NewBodJSON.put("sourcetype", tempJso.getString("csourcebilltype"));
		                NewBodJSON.put("crowno", tempJso.getString("crowno"));
		                NewBodJSON.put("billhid", tempJso.getString("pk_take"));
		                NewBodJSON.put("billbid", tempJso.getString("pk_take_b"));
		                NewBodJSON.put("billhcode", tmpBillCode);
		                NewBodJSON.put("billtype", "3V");
		                NewBodJSON.put("ddeliverdate", "");
		                NewBodJSON.put("pk_defdoc6", tempJso.getString("pk_defdoc6"));
		                NewBodJSON.put("def6", tempJso.getString("vdef6"));
					}
					
		  		}
				NewBodyarray.put(NewBodJSON);
			}
			
			jsonBillBodyTask.put("Status", true);
			jsonBillBodyTask.put("dbBody", NewBodyarray);
			
			GetBillBFlg = "1";
			
			
		} 
		catch (JSONException e) 
		{
			e.printStackTrace();
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		catch (Exception ex)
		{
			Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
	}
	
	//�󶨶�����ͷ��Ϣ
	private boolean BindingBillDetailInfo(Map<String,Object> mapBillInfo)
	{
//		String CompanyCode = "";
//		if (tmpAccID.equals("A")) {
//			CompanyCode = sCompanyCode;
//		} else if (tmpAccID.equals("B")) {
//			CompanyCode = "1";
//		}
		tmpAccID = mapBillInfo.get("AccID").toString();
		//tmpWarehousePK = mapBillInfo.get("pk_stordoc").toString();
		tmpCorpPK = mapBillInfo.get("pk_corp").toString();
		tmpBillCode = mapBillInfo.get("billCode").toString();
		
		if(!Common.CheckUserRole(tmpAccID, tmpCorpPK, "40080802"))
		{
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			Toast.makeText(SalesDelivery.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian, Toast.LENGTH_LONG).show();
			return false;
		}
		
		tvSalesDelBillCodeName.setText("��  ��  �ţ�");
		tvSalesDelAccIDName.setText("��  ��  �ţ�");
		//tvSalesDelWareName.setText("�ֿ⣺");
		tvSalesDelCorpName.setText("�͡�    ����");
	
		tvSalesDelBillCode.setText(mapBillInfo.get("billCode").toString());
		tvSalesDelAccID.setText(mapBillInfo.get("AccID").toString());
		//tvSalesDelWare.setText(mapBillInfo.get("WHOut").toString());
		tvSalesDelCorp.setText(mapBillInfo.get("custname").toString());
//		
		tmpAccID = mapBillInfo.get("AccID").toString();
		//tmpWarehousePK = mapBillInfo.get("pk_stordoc").toString();
		tmpCorpPK = mapBillInfo.get("pk_corp").toString();
		tmpBillCode = mapBillInfo.get("billCode").toString();
		return true;
	}
	
	//��ն�����ͷ��Ϣ
	private void ClearBillDetailInfoShow()
	{
		tvSalesDelBillCodeName.setText("");
		tvSalesDelAccIDName.setText("");
		
		tvSalesDelCorpName.setText("");
		
		tvSalesDelBillCode.setText("");
		tvSalesDelAccID.setText("");
		//tvSalesDelWare.setText("");
		tvSalesDelCorp.setText("");
	}
	
    private class ButtonOnClick implements DialogInterface.OnClickListener
	{
		public int index;

		public ButtonOnClick(int index)
		{
			this.index = index;
		}

		
		public void onClick(DialogInterface dialog, int whichButton)
		{
			if (whichButton >= 0)
			{
				index = whichButton;
				dialog.cancel(); 				
			}
			else
			{
				return;
			}
			if(dialog.equals(BillTypeSelectButton))
			{
				
				String BillTypeName = BillTypeNameList[index].toString();
				
				BillTypeName = BillTypeName.substring(0,4);
				
				tvSaleOutSelect.setText(BillTypeName);
				
				txtSalesDelPDOrder.requestFocus();
				InitActiveMemor();
			}
			if(dialog.equals(WHSelectButton))
			{
				
				if(!Common.CheckUserWHRole(tmpAccID, tmpCorpPK, WHIDList[index].toString()))
				{
				 	MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				 	Toast.makeText(SalesDelivery.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian, Toast.LENGTH_LONG).show();
				 	return;
				}
				
				
				txtSalesDelWH.setText(WHNameList[index].toString());
				tmpWarehousePK = WHIDList[index].toString();
				
				txtSalesDelPos.setText("");
				txtSalesDelPDOrder.requestFocus();
				tmpposCode = "";
				tmpposName = "";
				tmpposID = "";
				
				if ((tmpCdTypeID == null) || (tmpCdTypeID.equals("")))
				{
					SetCDtype();
				}
				
				txtSalesDelPos.requestFocus();
			}
			
			if(dialog.equals(CDSelectButton))
			{
				txtSalesDelCD.setText(CDNameList[index].toString());
				tmpCdTypeID = CDIDList[index].toString();
				txtSalesDelPos.requestFocus();
			}
		}
	}
    
    
    
    private void SetCDtype()
    {
    	
        if((tmpAccID == null) || (tmpAccID.equals(""))) {
            Toast.makeText(this, "������Ϣû�л�ò���ѡ�����䷽ʽ", 1).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            this.txtSalesDelPDOrder.requestFocus();

            return;
        }
        if(tvSaleOutSelect.getText().toString().equals("���۳���")) {
            this.CDNameList = new String[2];
            this.CDIDList = new String[2];
            this.CDNameList[0] = "����";
            this.CDNameList[1] = "�ͻ�";
            if(tmpAccID.equals("A")) {
                CDIDList[0] = "0001AA100000000003U4";
                CDIDList[1] = "0001AA100000000003U5";
            } else if(tmpAccID.equals("B")) {
                CDIDList[0] = "0001DD10000000000XQQ";
                CDIDList[1] = "0001DD10000000000XQR";
            }
        }
        if(tvSaleOutSelect.getText().toString().equals("�˻�����")) {
            CDNameList = new String[1];
            CDIDList = new String[1];
            CDNameList[0] = "�˻�";
            if(tmpAccID.equals("A")) {
                CDIDList[0] = "0001AA100000000003U7";
            } else if(tmpAccID.equals("B")) {
                CDIDList[0] = "0001DD10000000000XQT";
            }
        }
        if(tvSaleOutSelect.getText().toString().equals("�˻ز���")) {
            CDNameList = new String[1];
            CDIDList = new String[1];
            CDNameList[0] = "�˻�";
            if(tmpAccID.equals("A")) {
                CDIDList[0] = "0001AA100000000003U7";
            } else if(tmpAccID.equals("B")) {
                CDIDList[0] = "0001DD10000000000XQT";
            }
        }
        showCDChoiceDialog();
    }
    
    private void showCDChoiceDialog()
    {
      this.CDSelectButton = new AlertDialog.Builder(this).setTitle("ѡ�����䷽ʽ").setSingleChoiceItems(this.CDNameList, -1, this.buttonOnClick).setNegativeButton(R.string.QuXiao, this.buttonOnClick).show();
    }
    
	private void showSingleChoiceDialog()
	{   

		BillTypeSelectButton=new AlertDialog.Builder(this).setTitle("ѡ�񵥾�����").setSingleChoiceItems(
				BillTypeNameList, -1, buttonOnClick).setNegativeButton(R.string.QuXiao, buttonOnClick).show();
	}
	
	
	private void showWHChoiceDialog()
	{   

		WHSelectButton=new AlertDialog.Builder(this).setTitle(R.string.XuanZeCangKu).setSingleChoiceItems(
				WHNameList, -1, buttonOnClick).setNegativeButton(R.string.QuXiao, buttonOnClick).show();
	}
	
	private void SetBillType()
	{
		BillTypeNameList =new String[3];//���õ�����������
		//��ʼ���õ�����������
		BillTypeNameList[0]="���۳���   (ɨ�豸����)";
		BillTypeNameList[1]="�˻�����   (ɨ���ͻ���)";
		BillTypeNameList[2]="�˻ز���   (ɨ���˻���)";
	}
	
	class OnClickListener implements

	android.view.View.OnClickListener {

		public void onClick(View v) {

			switch (v.getId()) {
//			case id.btSearchBillDate:
//				Message msg = new Message();
//				msg.what = SalesDelivery.SHOW_DATAPICK;
//				SalesDelivery.this.saleHandler.sendMessage(msg);
//				break;
			
			case id.btnSaleOutSelect:
				
				if(lstSaveBody == null || lstSaveBody.size() < 1)
		    	{
		    		
		    	}
		    	else
		    	{
    				Toast.makeText(SalesDelivery.this, R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					txtSalesDelPDOrder.setText("");
    				break;
		    	}
				
				showSingleChoiceDialog();
				break;
			case id.btnSalesDelPDOrder:
				try {
					String BillCodeKey = "";
					if(tvSaleOutSelect.getText().toString().equals("�˻�����"))
			  		{
						BillCodeKey= txtSalesDelPDOrder.getText().toString();
						if(BillCodeKey.length()<5)
						{
		    				Toast.makeText(SalesDelivery.this, "��������5λ�ؼ���", Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
							txtSalesDelPDOrder.setText("");
		    				break;
						}
			  		}

					btnSalesDelPDOrderClick(BillCodeKey);
			} catch (ParseException e) {
				Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
			} catch (IOException e) {
				Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
			} catch (JSONException e) {
				Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
			}
			catch(Exception  e){
				Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
				e.printStackTrace();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
			}
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
					
					if(jsonSaveHead == null || jsonSaveHead.length() < 1)
					{
					Toast.makeText(SalesDelivery.this,"û��ȡ�ö�����ͷ", Toast.LENGTH_LONG).show();

					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					return;
				}
					
				if (jsonBillBodyTask == null || jsonBillBodyTask.length() < 1) 
				{
					Toast.makeText(SalesDelivery.this, "û��ȡ�ö�������",
							Toast.LENGTH_LONG).show();

					// ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					// ADD CAIXY TEST END
					return;
				}
				
				
				try {
					GetWHPosStatus();
				} catch (JSONException e) {
					Toast.makeText(SalesDelivery.this,"��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					
				}
				
				if(tmpWHStatus.equals(""))
				{
					return;
				}
				if(tmpWHStatus.equals("Y"))
				{
					if(tmpposCode.equals(""))
					{
						Toast.makeText(SalesDelivery.this,R.string.QingShuRuHuoWeiHao, Toast.LENGTH_LONG).show();
						
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						return;
					}
				}

				
				//ADD BY WUQIONG START
				if(tmprdID==null||tmprdID.equals(""))
				{
					Toast.makeText(SalesDelivery.this,R.string.QingShuChuKuLeiBie, Toast.LENGTH_LONG).show();

					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					return;
				}
				
				
				if ((tmpCdTypeID == null) || (tmpCdTypeID.equals("")))
		        {
		          Toast.makeText(SalesDelivery.this, "���������䷽ʽ", Toast.LENGTH_LONG).show();
		          MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		          return;
		        }
				
				
					
				Intent intDeliveryScan = new Intent(SalesDelivery.this,
						SalesDeliveryScan.class);
				intDeliveryScan.putExtra("AccID", tmpAccID);
				intDeliveryScan.putExtra("TaskJonsBody",
						jsonBillBodyTask.toString());

				SerializableList lstScanSaveDetial = new SerializableList();
				lstScanSaveDetial.setList(lstSaveBody);
				intDeliveryScan.putExtra("lstScanSaveDetial", lstScanSaveDetial);
				intDeliveryScan.putExtra("Warehouse", tmpWarehousePK);
				String sTaskCount = TaskCount + "" ;
				intDeliveryScan.putExtra("TaskCount",sTaskCount);
				intDeliveryScan.putExtra("tmpCorpPK",tmpCorpPK);
				intDeliveryScan.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);
				intDeliveryScan.putExtra("ScanType",tvSaleOutSelect.getText().toString());
				

					startActivityForResult(intDeliveryScan,86);
					
					break;
				case id.btnSalesDelSave:
					try {
						SaveTransData();
				} catch (JSONException e) {
					Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					
				} catch (ParseException e) {
					Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					
				} catch (IOException e) {
					Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
					e.printStackTrace();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					
				}
				break;
				case id.btnSalesDelExit:
					Exit();
					break;
					
				case id.btnSalesDelWH:
				try {
					GetSalesDelWH();
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				}
				
				break;
				
				case id.btnSalesDelCD:
					SetCDtype();
				break;
					
			}
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
	 
	 private void GetSalesDelWH() throws JSONException
	    {

			try
			{	
				if(tmpAccID==null || tmpAccID.equals(""))
				{
					Toast.makeText(this, "������Ϣû�л�ò���ѡ��ֿ�", Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					txtSalesDelPDOrder.requestFocus();
					return;
				}
				
				if(!tvSaleOutSelect.getText().toString().equals("���۳���"))
		  		{
					JSONArray JsonArrays = (JSONArray)this.jsonBillBodyTask.get("dbBody");
		    	      for (int i = 0; i < JsonArrays.length(); i++)
		    	      {
		    	    	  String Batch = ((JSONObject)JsonArrays.get(i)).getString("batchcode");
		    	    	  if(Batch.equals("null"))
		    	    	  {
		  					Toast.makeText(this, "������û���������κŵ�����,�뵽ϵͳ���������κź��ٽ�����ز���", Toast.LENGTH_LONG).show();
							//ADD CAIXY TEST START
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							//ADD CAIXY TEST END
		  					txtSalesDelPDOrder.requestFocus();
							return;
		    	    	  }
		    	      }
		  		}
	    	    
				
				//���ӿ��ƣ��Ѿ�ɨ�������޷��޸Ĳֿ�
		  		if(lstSaveBody == null || lstSaveBody.size() < 1)
		    	{
		    		
		    	}
		    	else
		    	{
    				Toast.makeText(SalesDelivery.this, "�������Ѿ���ɨ��,�޷��޸Ĳֿ⡣��Ҫ�޸Ĳֿ��������ɨ����ϸ", Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					txtSalesDelPDOrder.setText("");
					return;
		    	}

				
				JSONObject serList = null;
				JSONObject para = new JSONObject();
				
				String sWHName = "";
				String scorpCode = "";
				if(tmpAccID.equals("A"))
				{
					//CompanyCode=sharedPreferences.getString("CompanyCode", "");
			        //OrgCode=sharedPreferences.getString("OrgCode", "");
					scorpCode = tmpCorpPK;
					sWHName = WhNameA;
				}
				else
				{
					scorpCode = tmpCorpPK;
					sWHName = WhNameB;
				}
				

				para.put("FunctionName", "GetBaseWhCodeByNameAndCorp");
				para.put("TableName","WhCodeByName");
				para.put("Corp",scorpCode);
				para.put("WhName", sWHName.toUpperCase().replace("\n", ""));
			
				if(!MainLogin.getwifiinfo()) {
		            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
		            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
		            return ;
		        }
				
				serList = Common.
						DoHttpQuery(para, "CommonQuery", tmpAccID);
				if(serList==null)
				{
//					Toast.makeText(this, serList.getString("��ȡ�ֿ�����з����˴���"), 
//							Toast.LENGTH_LONG).show();
					Toast.makeText(this,R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					return;
				}
				
				if(serList.getBoolean("Status"))
				{
					JSONArray arys = serList.getJSONArray("WhCodeByName");	
					
					WHNameList =new String[arys.length()];//���òֿ�������������
					//WHCodeList =new String[arys.length()];//���òֿ�CODE����
					WHIDList =new String[arys.length()];//���òֿ�ID����
					
					
					for(int i=0;i< arys.length();i++)
					{
						//storcode  �ֿ����		storname  �ֿ�����      pk_stordoc �ֿ�ID
						String storname = arys.getJSONObject(i).get("storname").toString();
						//String storcode = arys.getJSONObject(i).get("storcode").toString();					
						String pk_stordoc = arys.getJSONObject(i).get("pk_stordoc").toString();
						WHNameList[i]=storname;
						//WHCodeList[i]=storcode;
						WHIDList[i]=pk_stordoc;
					}
					
					showWHChoiceDialog();
				}
				else
				{
//					Toast.makeText(this, serList.getString("�Ҳ�����زֿ���Ϣ"), 
//							Toast.LENGTH_LONG).show();
					Toast.makeText(this,"�Ҳ�����زֿ���Ϣ", Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
				}
			}
			

			catch(Exception e)
			{
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
			}
	    }
	 
	
	//�ҵ���λID���ջ�λ��
    private void FindPositionByCode(String posCode) throws JSONException
    {
    	String lsCompanyCode = "";

		try
		{	
			if(tmpWarehousePK==null || tmpWarehousePK.equals(""))
			{
				Toast.makeText(this, "�ֿ⻹û��ȷ��,������ɨ���λ", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				txtSalesDelPos.setText("");
				txtSalesDelPDOrder.requestFocus();
				tmpposCode = "";
				tmpposName = "";
				tmpposID = "";
				return;
			}
			
			if(tmpAccID.equals("A"))
			{
				lsCompanyCode=tmpCorpPK;
			}else
			{
				lsCompanyCode=tmpCorpPK;
			}
	    	
			posCode = posCode.trim();
			posCode = posCode.replace("\n", "");
			posCode = posCode.toUpperCase();
			
			JSONObject para = new JSONObject();
			para.put("FunctionName", "GetBinCodeInfo");
			para.put("CompanyCode",  lsCompanyCode);
			para.put("STOrgCode",  MainLogin.objLog.STOrgCode);
			para.put("WareHouse", tmpWarehousePK);
			para.put("BinCode", posCode);
			para.put("TableName",  "position");	
			
			if(!MainLogin.getwifiinfo()) {
	            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
	            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	            return ;
	        }
			
			JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", tmpAccID);	
			
			if(rev==null)
			{
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return ;
			}
			
			
			if(rev.getBoolean("Status"))
			{
				JSONArray val = rev.getJSONArray("position");				
				if(val.length() < 1)
				{
					Toast.makeText(this, "��ȡ��λʧ��", Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					txtSalesDelPos.setText("");
					txtSalesDelPos.requestFocus();
					tmpposCode = "";
					tmpposName = "";
					tmpposID = "";
					//ADD CAIXY TEST END
					return;
				}
				String jposName,jposCode,jposID;
				JSONObject temp = val.getJSONObject(0);
				
				jposName = temp.getString("csname");
				jposCode = temp.getString("cscode");
				jposID = temp.getString("pk_cargdoc");				
				
				tmpposCode = jposCode;
				tmpposName = jposName;
				tmpposID = jposID;
				txtSalesDelPos.setText(tmpposCode);
				return;				
			}
			else
			{
				Toast.makeText(this, "��ȡ��λʧ��", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				txtSalesDelPos.setText("");
				txtSalesDelPos.requestFocus();
				tmpposCode = "";
				tmpposName = "";
				tmpposID = "";
				return;
				
			}
			
		}
		 catch (JSONException e) {
				
				Toast.makeText(this, 
						e.getMessage(), Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				e.printStackTrace();
			} 
		catch(Exception e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
		}
    }
	
	 //�򿪶����б���
  	private void btnSalesDelPDOrderClick(String BillCodeKey) throws ParseException, IOException, JSONException
  	{  	
  		
  		if(lstSaveBody == null || lstSaveBody.size() < 1)
    	{
    		
    	}
    	else
    	{
			Toast.makeText(SalesDelivery.this, R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			txtSalesDelPDOrder.setText("");
			return;
    	}
  		
  		
  		if(tvSaleOutSelect.getText().toString().equals("δѡ��"))
  		{
  			Toast.makeText(SalesDelivery.this, "��������δѡ��,��ѡ�񵥾�����", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			txtSalesDelPDOrder.requestFocus();
			return;
  		}
  		if(tvSaleOutSelect.getText().toString().equals("���۳���"))
  		{
  			Intent ViewGrid = new Intent(this, SaleBillInfoOrderList.class);
            ViewGrid.putExtra("FunctionName", "���۳���");//GetSalereceiveHead
  	  		startActivityForResult(ViewGrid,88);
  		}
  		if(tvSaleOutSelect.getText().toString().equals("�˻�����"))
  		{
  			Intent ViewGrid = new Intent(this, SaleBillInfoOrderList.class);
  			ViewGrid.putExtra("BillCodeKey", BillCodeKey);
            ViewGrid.putExtra("FunctionName", "�˻�����");
  	  		startActivityForResult(ViewGrid,88);
  		}
  		if(tvSaleOutSelect.getText().toString().equals("�˻ز���"))
  		{
  			Intent ViewGrid = new Intent(this, SaleBillInfoOrderList.class);
            ViewGrid.putExtra("FunctionName", "�˻ز���");//GetSaleTakeHead//GetSaleOutHead
  	  		startActivityForResult(ViewGrid,88);
  		}
  		
  	}
	
    //���շ������
  	private void btnSalesDelRdclClick(String Code) throws ParseException, IOException, JSONException
  	{  			
  		Intent ViewGrid = new Intent(this,VlistRdcl.class);
  		ViewGrid.putExtra("FunctionName", "GetRdcl");
  		ViewGrid.putExtra("AccID", tmpAccID);
  		ViewGrid.putExtra("rdflag", rdflag);
  		ViewGrid.putExtra("rdcode", Code);
  		startActivityForResult(ViewGrid,88);
  	}
	
	//�˳���ť
  	private void Exit()
  	{
  		 AlertDialog.Builder bulider = 
  				 new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage(R.string.NiQueDingYaoTuiChuMa);
  		 bulider.setNegativeButton(R.string.QuXiao, null);
  		 bulider.setPositiveButton(R.string.QueRen, listenExit).create().show();
  	}
  	
  	//�˳���ť�Ի����¼�
  	private DialogInterface.OnClickListener listenExit = new 
  			DialogInterface.OnClickListener()
  	{
  		public void onClick(DialogInterface dialog,
  			int whichButton)
  		{
  			finish();	
  			System.gc();
  		}
  	};
  	
	 @Override	
	 public boolean onKeyDown(int keyCode, KeyEvent event) 
	 {		if (keyCode == KeyEvent.KEYCODE_MENU) 
		 	{//����meu���¼�			//do something...	
		       return false;
			 }		
	 if (keyCode == KeyEvent.KEYCODE_BACK) 
	 {//���ط��ذ�ť�¼�			//do something...	
		 return false;
	 }		
	 return true;
	 }
	 
	 
		//EditText�����س��ļ����¼�
		private OnKeyListener EditTextOnKeyListener = new OnKeyListener()
	    {
			@Override
			public boolean onKey(View v, int arg1, KeyEvent arg2) {
				switch(v.getId())
				{
					case id.txtSalesDelPDOrder:
						if(arg1 == arg2.KEYCODE_ENTER && arg2.getAction() == KeyEvent.ACTION_UP)//&& arg2.getAction() == KeyEvent.ACTION_DOWN
						{
					  		if(tvSaleOutSelect.getText().toString().equals("δѡ��"))
					  		{
					  			Toast.makeText(SalesDelivery.this, "��������δѡ��,��ѡ�񵥾�����", Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								txtSalesDelPDOrder.setText("");
								txtSalesDelPDOrder.requestFocus();
								return false;
					  		}
					  		
							if(lstSaveBody == null || lstSaveBody.size() < 1)
					    	{
					    		
					    	}
					    	else
					    	{
			    				Toast.makeText(SalesDelivery.this, R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								txtSalesDelPDOrder.setText("");
			    				break;
					    	}
							
					  		GetBillBFlg="0";
							String lsBillCode = txtSalesDelPDOrder.getText().toString();
							txtSalesDelPDOrder.setText("");
							lsBillCode = lsBillCode.toUpperCase();
							lsBillCode = lsBillCode.replace("\n", "");
							if(lsBillCode.equals(""))
					    		return false;
							String lsBillAccID = lsBillCode.substring(0,1);
							String lsBillCorpPK = lsBillCode.substring(1,5);
							
							String SaleFlg = "";
							
							if(tvSaleOutSelect.getText().toString().equals("�˻ز���"))
					  		{
								SaleFlg = lsBillCode.substring(5,6);
								if(!SaleFlg.equals("D")&&!SaleFlg.equals("T"))
								{
									Toast.makeText(SalesDelivery.this, "ɨ������벻��ȷ", Toast.LENGTH_LONG).show();
									//ADD CAIXY TEST START
									MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
									//ADD CAIXY TEST END
									return false;
								}
								lsBillCode = lsBillCode.substring(6);
					  		}
							else
							{
								lsBillCode = lsBillCode.substring(5);
							}
							
							
							Map<String,Object> mapBillInfo = 
									GetBillDetailInfoByBillCode(lsBillAccID,lsBillCorpPK,lsBillCode,SaleFlg);
							
							sBillAccID = lsBillCode.substring(0,1);
							sBillCorpPK = lsBillCode.substring(1,5);
							
							sBillCode = lsBillCode;
							
							txtSalesDelPDOrder.setText("");
							if(mapBillInfo==null)
							{
								Toast.makeText(SalesDelivery.this, "û���ҵ����Ӧ�ĵ�����ϸ��Ϣ", Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								return false;
							}
							
							//�󶨱����ñ�ͷ
							jsonSaveHead = new JSONObject();
							try {
								jsonSaveHead = Common.MapTOJSONOBject(mapBillInfo);
							} catch (JSONException e) {
								Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
								e.printStackTrace();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
							}
							
							//����ʾ������Ϣ
							//BindingBillDetailInfo(mapBillInfo);
							if(!BindingBillDetailInfo(mapBillInfo))
							{
								return true;
							}
							
							GetBillBodyDetailInfo(SaleFlg);

							try {
								GetTaskCount();
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if(GetBillBFlg.equals("1"))
							{
								SetRDCL();
								tmpCdTypeID = "";
								try {
									GetSalesDelWH();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							return true;
						}
						break;
					case id.txtSalesDelPos:
						if(arg1 == arg2.KEYCODE_ENTER && arg2.getAction() == KeyEvent.ACTION_UP)
						{
							//txtSalesDelPos.requestFocus();
							
							if(jsonSaveHead == null || jsonSaveHead.length() < 1)							
							{
								txtSalesDelPos.setText("");
								Toast.makeText(SalesDelivery.this, 
										"������Ϣû�л�ò���ɨ���λ", Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								txtSalesDelPDOrder.requestFocus();
								break;
							}
							
							if(lstSaveBody == null || lstSaveBody.size() < 1)
					    	{
					    		
					    	}
					    	else
					    	{
					    		String OldPosName = tmpposCode;
			    				Toast.makeText(SalesDelivery.this, "�������Ѿ���ɨ��,�޷��޸Ļ�λ����Ҫ�޸Ļ�λ�������ɨ����ϸ", Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								txtSalesDelPos.setText(OldPosName);
								txtSalesDelPDOrder.requestFocus();
								break;
					    	}
							
							
							try 
							{
								//txtSalesDelRdcl.requestFocus();
								FindPositionByCode(txtSalesDelPos.getText().toString());
								return true;
							} catch (ParseException e) {
								txtSalesDelPos.setText("");
								tmpposCode = "";
								tmpposName = "";
								tmpposID = "";
								txtSalesDelPos.requestFocus();
								Toast.makeText(SalesDelivery.this, 
										e.getMessage(), Toast.LENGTH_LONG).show();
								e.printStackTrace();

								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						break;
						//wuqiong
					case id.txtSalesDelRdcl:
						if(arg1 == arg2.KEYCODE_ENTER && arg2.getAction() == KeyEvent.ACTION_UP)//&& arg2.getAction() == KeyEvent.ACTION_DOWN
						{
							
							String ScanCode = txtSalesDelRdcl.getText().toString();	
							txtSalesDelRdcl.setText("");
							if(jsonSaveHead == null || jsonSaveHead.length() < 1)							
							{
								txtSalesDelRdcl.setText("");
								Toast.makeText(SalesDelivery.this, 
										R.string.DanJuXinXiMeiYouBuNengXuanZeShouFaLeiBie, Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								txtSalesDelPDOrder.requestFocus();
								return false;
							}
							
							try {
								btnSalesDelRdclClick(ScanCode);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								Toast.makeText(SalesDelivery.this, e.getMessage(), Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
								e.printStackTrace();
							}
							
							return true;
						}
						break;
						//wuqiong
				}
				return false;
			}	    	
	    };
	    
	    
    private void GetTaskCount()throws JSONException
    {
    	    this.TaskCount = 0;
    	    tmpWHStatus = "";
    	    if ((this.jsonBillBodyTask == null) || (this.jsonBillBodyTask.equals("")))
    	    {
    	    	return;
    	    }
    	    
    	      

    	    JSONArray JsonArrays = (JSONArray)this.jsonBillBodyTask.get("dbBody");
    	      for (int i = 0; i < JsonArrays.length(); i++)
    	      {
    	    	  String Batch = ((JSONObject)JsonArrays.get(i)).getString("batchcode");
    	    	  if(Batch.equals("null"))
    	    	  {
    	    		  
    	    	  }
    	    	    String nnum = ((JSONObject)JsonArrays.get(i)).getString("number");
    	            String ntranoutnum = ((JSONObject)JsonArrays.get(i)).getString("outnumber");
    	            String snnum = "0";
					if(!ntranoutnum.equals("null"))
					{
						snnum = ntranoutnum.replaceAll("\\.0", "");
					}
					int shouldinnum = Integer.valueOf(nnum).intValue() - Integer.valueOf(snnum).intValue();
		            TaskCount = (TaskCount + shouldinnum);
    	      }
    	    	  
    	 }
	
	
	//��������
    private void SaveTransData() throws JSONException, ParseException, IOException
    {
        JSONObject sendJsonSave = new JSONObject();
        JSONArray sendJsonArrBody = new JSONArray();
        JSONArray sendJsonArrBodyLocation = new JSONArray();
        HashMap<String, Object> sendMapHead = new HashMap<String, Object>();
        HashMap<String, Object> sendMapBody = new HashMap<String, Object>();
        if ((this.jsonSaveHead == null) || (this.jsonSaveHead.length() < 1))
        {
          Toast.makeText(this, R.string.WuKeBaoCunShuJu, 1).show();
          MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
          return;
        }
        if ((this.lstSaveBody == null) || (this.lstSaveBody.size() < 1))
        {
          Toast.makeText(this, R.string.WuKeBaoCunShuJu, 1).show();
          MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
          return;
        }
        
	
		for(int j=0;j<lstSaveBody.size();j++)
		{
			sendMapBody = (HashMap<String,Object>)lstSaveBody.get(j);
			
			if(!sendMapBody.get("spacenum").toString().equals("1"))
			{
				Toast.makeText(this, R.string.YouWeiSaoWanDeFenBao, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			    			
			//String sHBillCode = sendMapHead.get("No").toString();
			String sBBillCode = sendMapBody.get("BillCode").toString();
			
			if(tvSalesDelBillCode.getText().equals(sBBillCode))
			{
				JSONObject jsonSaveBody = new JSONObject();
  		
    				jsonSaveBody = Common.MapTOJSONOBject(sendMapBody);
    				sendJsonArrBody.put(jsonSaveBody);
    				
        			if(tmpWHStatus.equals("Y"))
        			{
        				JSONObject saveJsonBodyLocation = new JSONObject();
        				saveJsonBodyLocation.put("csourcebillbid", sendMapBody.get("billbid").toString());
        	            saveJsonBodyLocation.put("cspaceidf", tmpposID);
        	            saveJsonBodyLocation.put("spacenum", sendMapBody.get("spacenum").toString());
        				sendJsonArrBodyLocation.put(saveJsonBodyLocation);
        			}
    	            sendJsonSave.put("ScanDetail", sendJsonArrBody);
    	            sendJsonSave.put("ScanDetailLocation", sendJsonArrBodyLocation);
			}
		}
    	 	
        if((sendJsonSave == null) || (sendJsonSave.length() < 0x1)) {
            Toast.makeText(this, R.string.WuKeBaoCunShuJu, 1).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
        }
        
        
        String ErrMsg = "";
               

        if(tvSaleOutSelect.getText().toString().startsWith("�˻�"))
        {
        	jsonBillBodyTask2 = null;
            GetBillBodyDetailInfo2(SaleFlg);
            
    	    if ((this.jsonBillBodyTask2 == null) || (this.jsonBillBodyTask2.equals("")))
    	    {
                Toast.makeText(this, "����ɨ���������ٴμ�鵥�ݺ��ٽ��б���", 1).show();
                MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
                return;
    	    }
    	    else
    	    {	
    	    	JSONArray jsarray= jsonBillBodyTask2.getJSONArray("dbBody");
    	    	for(int i = 0;i<jsarray.length();i++)
    			{
    				JSONObject tempJso = jsarray.getJSONObject(i);
    				String outnumber = tempJso.getString("outnumber");
    				String number = tempJso.getString("number");
    				
    				if(outnumber == null || outnumber.equals("")|| outnumber.equals("null"))
					{
    					outnumber = "0";
					}
    				
    				String invcode = tempJso.getString("invcode");
    				String batchcode = tempJso.getString("batchcode");
    				
    				int shouldoutnum = Integer.valueOf(number).intValue() - Integer.valueOf(outnumber).intValue();
    				
    				for(int j=0;j<sendJsonArrBody.length();j++)
    				{
    					String AccID = (String) ((JSONObject) sendJsonArrBody.get(j)).get("AccID");
    					String Scinvcode = (String) ((JSONObject) sendJsonArrBody.get(j)).get("InvCode");
    					String Scbatchcode = (String) ((JSONObject) sendJsonArrBody.get(j)).get("batchcode");
    					if(Scinvcode.equals(invcode)& batchcode.equals(Scbatchcode))
    					{	
    						shouldoutnum--;
    						if(shouldoutnum<0)
    						{
    							ErrMsg = ErrMsg+Scinvcode+","+Scbatchcode+" " + "\r\n";
    						}
    					}					
    				}				
    			}	    	
    	    }
        }
	    
	    if(!ErrMsg.equals(""))
	    {
            Toast.makeText(this, ErrMsg+"����ɨ���������ٴμ�鵥�ݺ��ٽ��б���", 1).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return;
	    }
	    
    	
		if(uploadGuid == null) {
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
        
        if(!MainLogin.getwifiinfo()) {
            Toast.makeText(this, R.string.WiFiXinHaoCha,Toast.LENGTH_LONG).show();
            MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
            return ;
        }
        /////////////////////////////////
        JSONObject jas = Common.DoHttpQuery(sendJsonSave, "SaveSaleOutBill", tmpAccID);
        
        if(jas==null)
		{
			Toast.makeText(SalesDelivery.this, R.string.DanJuZaiBaoCunGuoChengZhongChuXianWenTi, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
		if(!jas.has("Status"))
		{
			Toast.makeText(SalesDelivery.this, "���ݱ�������г���������," +
					"�볢���ٴ��ύ�򵽵���ϵͳ��ȷ�Ϻ��پ����Ƿ��������!", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}


		boolean loginStatus= jas.getBoolean("Status");
		
		if(loginStatus==true)
		{
//			String lsResultBillCode = jas.getString("BillCode");
			String lsResultBillCode = "";
			
			if(jas.has("BillCode"))
			{
				lsResultBillCode = jas.getString("BillCode");
			}
			else
			{
				Toast.makeText(this, "���ݱ�������г���������," +
						"�볢���ٴ��ύ�򵽵���ϵͳ��ȷ�Ϻ��پ����Ƿ��������!", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;
			}
			
			Map<String,Object> mapResultBillCode = new HashMap<String,Object>();
			mapResultBillCode.put("BillCode", lsResultBillCode);
			ArrayList<Map<String,Object>> lstResultBillCode = new ArrayList<Map<String,Object>>();
			lstResultBillCode.add(mapResultBillCode);
			//Toast.makeText(StockTransContent.this, "���ݱ���ɹ�", Toast.LENGTH_LONG).show();
			//IniActivyMemor();
			//return;
			
			uploadGuid=null;
			//ADD BY WUQIONG START
			//tmpmanualNo=null;
			//ADD BY WUQIONG END
			SimpleAdapter listItemAdapter = new SimpleAdapter(SalesDelivery.this,lstResultBillCode,//����Դ   
	                android.R.layout.simple_list_item_1,       
	                new String[] {"BillCode"},
	                new int[] {android.R.id.text1}  
	            ); 
			new AlertDialog.Builder(SalesDelivery.this).setTitle(R.string.DanJuBaoCunChengGong)
							.setAdapter(listItemAdapter, null)
							.setPositiveButton(R.string.QueRen,null).show();
			
			//����ɹ����ʼ��������ڴ�����
			
			//д��log�ļ�
	   		writeTxt = new writeTxt();
	   		
	   		Date day=new Date();    
	   		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
	   		
	   		SimpleDateFormat dfd= new SimpleDateFormat("yyyy-MM-dd");
	   		
	   		String BillCode = lsResultBillCode;
	   		String BillType = "4C";
	   		String UserID = MainLogin.objLog.UserID;
	   		
	   		String LogName = BillType + UserID + dfd.format(day)+".txt";
	   		String LogMsg = df.format(day) + " " + tmpAccID + " " + BillCode; 
	   		
	   		writeTxt.writeTxtToFile(LogName,LogMsg);
	   		//д��log�ļ�
			
			InitActiveMemor();
			this.tvSaleOutSelect.setText("δѡ��");
			return;
		}
		else
		{
			String ErrMsg1=jas.getString("ErrMsg");
			Toast.makeText(SalesDelivery.this, ErrMsg1, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}
		
		/////////////////////////////////
        
        
    }
    
    private void InitActiveMemor()
    {
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
      this.txtSalesDelPos.setText("");
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
      this.tvSalesDelBillCodeName.setText("");
      this.tvSalesDelAccIDName.setText("");
      this.tvSalesDelCorpName.setText("");
      this.tvSalesDelBillCode.setText("");
      this.tvSalesDelAccID.setText("");
      this.tvSalesDelCorp.setText("");
      this.ScanedBarcode = new ArrayList<String>();
      
      this.txtSalesDelPDOrder.requestFocus();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sales_delivery, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Changeline();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	private static AlertDialog SelectLine = null;
	private buttonOnClickC buttonOnClickC = new buttonOnClickC(0);
	static String[] LNameList = new String[2];
	
	private void Changeline() {

		int lsindex = 0;
		if (Common.lsUrl.equals(MainLogin.objLog.LoginString2)) {
			lsindex = 1;
		}

		LNameList[0] = getString(R.string.ZhuWebDiZhi);
		LNameList[1] = getString(R.string.FuWebDiZhi);

		SelectLine = new AlertDialog.Builder(this).setTitle(R.string.QieHuanDiZhi)
				.setSingleChoiceItems(LNameList, lsindex, buttonOnClickC)
				.setPositiveButton(R.string.QueRen, buttonOnClickC)
				.setNegativeButton(R.string.QuXiao, buttonOnClickC).show();
	}

	private void ShowLineChange(String WebName) {

		String CommonUrl = Common.lsUrl;
		CommonUrl = CommonUrl.replace("/service/nihao", "");

		AlertDialog.Builder bulider = new AlertDialog.Builder(this).setTitle(
				R.string.QieHuanChengGong).setMessage(R.string.YiJingQieHuanZhi + WebName + "\r\n" + CommonUrl);

		bulider.setPositiveButton(R.string.QueRen, null).setCancelable(false).create()
				.show();
		return;
	}

	private class buttonOnClickC implements DialogInterface.OnClickListener {
		public int index;

		public buttonOnClickC(int index) {
			this.index = index;
		}

		@Override
		public void onClick(DialogInterface dialog, int whichButton) {
			if (whichButton >= 0) {
				index = whichButton;
			} else {

				if (dialog.equals(SelectLine)) {
					if (whichButton == DialogInterface.BUTTON_POSITIVE) {
						if (index == 0) {

							Common.lsUrl = MainLogin.objLog.LoginString;
							ShowLineChange(LNameList[0]);
							System.gc();
						} else if (index == 1) {
							Common.lsUrl = MainLogin.objLog.LoginString2;
							ShowLineChange(LNameList[1]);
							System.gc();
						}
						return;
					} else if (whichButton == DialogInterface.BUTTON_NEGATIVE) {
						return;
					}
				}
			}
		}
	}
}
