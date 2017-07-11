package com.techscan.dvq;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.module.materialOut.DepartmentListAct;
import com.techscan.dvq.module.materialOut.StorgListACt;

import org.apache.http.ParseException;
import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.techscan.dvq.common.Utils.HANDER_DEPARTMENT;
import static com.techscan.dvq.common.Utils.HANDER_STORG;
import static com.techscan.dvq.common.Utils.HANDER_POORDER_HEAD;
import static com.techscan.dvq.common.Utils.HANDER_POORDER_BODY;

public class PurStockIn extends Activity {
	private ButtonOnClick buttonOnClick = new ButtonOnClick(0);
	boolean NoScanSave = false;

	String PurBillCode = "";
	String DBBillCode = "";
	private String[] ExitNameList = null;
	String fileName = null;
	String fileNameScan = null;
	String ScanedFileName = null;
	String UserID = null;
	File file = null;
	File fileScan = null;
	String ReScanHead = "1";
	private AlertDialog SelectButton = null;
	private AlertDialog SelectButtonNoScan = null;

	int year;
	int month;
	int day;
	Calendar mycalendar;

	private ArrayList<String> ScanedBarcode = new ArrayList<String>();
	Button btnSave;
	//Button btnUpdate;
	ImageButton btnBrowOrderNo;
	Button btnExit;
	Button btnScan;
	TextView labVendor;
	TextView labWarehouse;
	//TextView labWHName;
	//TextView tvbillstatus;
	//EditText txtPosition;
	EditText txtPurOrderNo;

	ImageButton btnWarehouse;
	ImageButton btnOrganization;
	ImageButton btnCategory;
	ImageButton btnDepartment;
	EditText txtWareHouse;
	EditText txtOrganization;
	EditText txtCategory;
	EditText txtDepartment;
	EditText txtStartDate;
	EditText txtEndDate;
	EditText txtPurInBillCode;
	EditText txtReMark;

	String CDISPATCHERID = "";//�շ����code
	String CDPTID = "";  //����id
	String CUSER;   //��¼Ա��id
	String CWAREHOUSEID = "";    //�ֿ�id
	String PK_CALBODY = "";      //�����֯
	String PK_CORP;         //��˾
	String VBILLCOD;        //���ݺ�

	Intent scanDetail = null;
	JSONObject jsDBBody;
	JSONObject jsDBHead;
	JSONObject jsHead;
	JSONObject jsBody;
	JSONObject jsBoxTotal;
	JSONObject jsSerino;

	String m_FrePlenishFlag = "N";
	String m_WarehouseID = "";
	String pk_purcorp = "";
	String pk_calbody = "";
	String m_AccID = "A";

	String m_BillID="";
	String m_BillNo="";

	String m_companyCode = "1001";

	String m_PosCode = "";
	String m_PosName = "";
	String m_PosID = "";

	String tmpWHStatus = "";//�ֿ��Ƿ����û�λ
	String tmpBillStatus = "";//�����Ƿ�����ɹ�

	int SaveFlg = 0;

	//ADD CAIXY TEST START
//	private SoundPool sp;//����һ��SoundPool
//	private int MainLogin.music;//����һ��int������suondID
//	private int MainLogin.music2;//����һ��int������suondID
	private writeTxt writeTxt;        //����LOG�ļ�

	//GUID
	UUID uploadGuid = null;

	private Context MyContext = this;

	private boolean SaveDBOrder() throws JSONException, ParseException, IOException {
		jsDBHead = new JSONObject();
		jsDBBody = new JSONObject();
		//����У���Ƿ���û����ɶ�����

//		JSONObject temp = new JSONObject();		
//		JSONArray bodys = jsBody.getJSONArray("PurBody");
//		JSONArray head = new JSONArray();
//		head = jsHead.getJSONArray("PurGood");

		//��д����������ͷ		
		jsDBHead.put("cbiztypeid", "0001ZZ1000000000UFQ0");                //�������ͱ�ʶ
		jsDBHead.put("cincbid", pk_calbody);        //��������֯ID 	pk_calbody
		jsDBHead.put("cincorpid", pk_purcorp);            //���빫˾ID 		pk_corp
		//jsDBHead.put("cinwhid", "1001AA100000000FEGIT");				//����ֿ� (��ʱдһ����			cwarehouseid
		jsDBHead.put("coutcbid", pk_calbody);        //���������֯ID 	pk_calbody
		jsDBHead.put("coutwhid", m_WarehouseID);        //�����ֿ�
		jsDBHead.put("fallocflag", "1");                                //�������ͱ�־
		jsDBHead.put("coperatorid", MainLogin.objLog.UserID);
		jsDBHead.put("BillCode", m_BillNo);

//		int x = 0;	
//		//=============Body		
//		for(int i = 0;i<bodys.length();i++)
//		{
//			if(bodys.getJSONObject(i).getInt("doneqty")
//					< bodys.getJSONObject(i).getInt("nshouldinnum"))
//			{
//				
//				int val = bodys.getJSONObject(i).getInt("nshouldinnum") -
//						bodys.getJSONObject(i).getInt("doneqty");
//				JSONObject obj = new JSONObject();
//				obj.put("cininvid", bodys.getJSONObject(i).getString("cinventoryid"));				//��������ʶ    
//				obj.put("cinvbasid", bodys.getJSONObject(i).getString("cinvbasid"));			//���������ʶ   
//				obj.put("cquoteunitid", bodys.getJSONObject(i).getString("cquoteunitid"));		//���ۼ�����λID    
//				obj.put("nnum", val);															//����
//				obj.put("vbatch", bodys.getJSONObject(i).getString("vbatchcode"));					//����
//				obj.put("vfree1", bodys.getJSONObject(i).getString("vfree1"));
//				
//				jsDBBody.put(x + "", obj);
//				x++;
//			}
//		}

		//=====================
		JSONObject saveHeadJons = new JSONObject();
		//saveHeadJons.put("cgeneralhid", this.m_BillID);					//�ɹ���ⵥID
		saveHeadJons.put("coperatorid", MainLogin.objLog.UserID);            //����Ա
		//saveHeadJons.put("bincode", this.m_PosCode);						//��λ��
		saveHeadJons.put("pk_purcorp", pk_purcorp);
		saveHeadJons.put("pk_corp", pk_purcorp);
		JSONObject saveJons = new JSONObject();
		saveJons.put("Head", jsDBHead);
		saveJons.put("Body", jsDBBody);

		//��ȡguid
		if (uploadGuid == null) {
			uploadGuid = UUID.randomUUID();
		}
		saveJons.put("GUIDS", uploadGuid.toString());
		saveJons.put("tmpWHStatus", tmpWHStatus);
		saveJons.put("tmpBillStatus", tmpBillStatus);

		saveJons.put("cgeneralhid", this.m_BillID);//�ɹ���ⵥID

		if (!MainLogin.getwifiinfo()) {
			Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}

		JSONObject jas = Common.DoHttpQuery(saveJons, "SaveToBill", "A");

		if (jas == null) {
			Toast.makeText(this, "���ݱ�������г���������," +
					"�볢���ٴ��ύ!", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return false;
		}

		if (!jas.has("Status")) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
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
			return false;
		}

		if (jas.getBoolean("Status")) {
			SaveFlg = 1;
			String lsResultBillCode = "";

			if (jas.has("BillCode")) {
				lsResultBillCode = jas.getString("BillCode");
			} else {
				Toast.makeText(this, "���ݱ�������г���������," +
						"�볢���ٴ��ύ!", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
			DBBillCode = lsResultBillCode;


			//д��log�ļ�
		}

		return true;
	}

	private void SaveOk() {

		if (!PurBillCode.equals("")) {
			Map<String, Object> mapResultBillCode = new HashMap<String, Object>();
			mapResultBillCode.put("BillCode", PurBillCode);
			ArrayList<Map<String, Object>> lstResultBillCode = new ArrayList<Map<String, Object>>();
			lstResultBillCode.add(mapResultBillCode);

			SimpleAdapter listItemAdapter = new SimpleAdapter(PurStockIn.this, lstResultBillCode,//����Դ
					android.R.layout.simple_list_item_1,
					new String[]{"BillCode"},
					new int[]{android.R.id.text1}
			);
			new AlertDialog.Builder(PurStockIn.this).setTitle("����ǩ�ֳɹ�")
					.setAdapter(listItemAdapter, null)
					.setPositiveButton(R.string.QueRen, null).show();

			//д��log�ļ�
			writeTxt = new writeTxt();

			Date day = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

			SimpleDateFormat dfd = new SimpleDateFormat("yyyy-MM-dd");

			String BillCode = PurBillCode;
			String BillType = "45";
			String UserID = MainLogin.objLog.UserID;

			String LogName = BillType + UserID + dfd.format(day) + ".txt";
			String LogMsg = df.format(day) + " " + "A" + " " + BillCode;

			writeTxt.writeTxtToFile(LogName, LogMsg);
		}

		if (!DBBillCode.equals("�޲ɹ�����") && !DBBillCode.equals("")) {
			Map<String, Object> mapResultBillCode = new HashMap<String, Object>();
			mapResultBillCode.put("BillCode", "1001 " + DBBillCode);
			ArrayList<Map<String, Object>> lstResultBillCode = new ArrayList<Map<String, Object>>();
			lstResultBillCode.add(mapResultBillCode);

			SimpleAdapter listItemAdapter = new SimpleAdapter(PurStockIn.this, lstResultBillCode,//����Դ
					android.R.layout.simple_list_item_1,
					new String[]{"BillCode"},
					new int[]{android.R.id.text1}
			);
			new AlertDialog.Builder(PurStockIn.this).setTitle(R.string.DanJuBaoCunChengGong)
					.setAdapter(listItemAdapter, null)
					.setPositiveButton(R.string.QueRen, null).show();

			//д��log�ļ�
			writeTxt = new writeTxt();

			Date day = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			SimpleDateFormat dfd = new SimpleDateFormat("yyyy-MM-dd");
			String BillCode = DBBillCode;
			String BillType = "5X";
			String UserID = MainLogin.objLog.UserID;

			String LogName = BillType + UserID + dfd.format(day) + ".txt";
			String LogMsg = df.format(day) + " " + "A" + " " + "1001 " + BillCode;

			writeTxt.writeTxtToFile(LogName, LogMsg);
		}


	}

	private boolean UpdatePurOrder() throws JSONException,
			ParseException, IOException {
		//�ݴ���˲ɹ����

		JSONObject saveHeadJons = new JSONObject();
		saveHeadJons.put("cgeneralhid", this.m_BillID);                    //�ɹ���ⵥID
		saveHeadJons.put("coperatorid", MainLogin.objLog.UserID);        //����Ա
		saveHeadJons.put("bincode", this.m_PosCode);                    //��λ��
		saveHeadJons.put("WarehouseID", this.m_WarehouseID);
		saveHeadJons.put("pk_purcorp", pk_purcorp);

		JSONArray head = new JSONArray();
		head = jsHead.getJSONArray("PurGood");
		saveHeadJons.put("pk_corp", head.getJSONObject(0).getString("pk_corp"));

		JSONArray arrays = jsSerino.getJSONArray("Serino");
		JSONArray lstSerino = new JSONArray();

		for (int i = 0; i < arrays.length(); i++) {
			String OKFlg = "0";
			String sSerial = ((JSONObject) (arrays.get(i))).getString("sno");
			String sBatch = ((JSONObject) (arrays.get(i))).getString("batch");
			String sInvCode = ((JSONObject) (arrays.get(i))).getString("invcode");
			String serino = ((JSONObject) (arrays.get(i))).getString("serino");
			serino = serino.replace("\n", "");
			String totalnum = ((JSONObject) (arrays.get(i))).getString("box");
			totalnum = Integer.valueOf(totalnum).toString();
			String sbarcode = serino;
			String sfree1 = ((JSONObject) (arrays.get(i))).getString("vfree1");

			if (lstSerino.length() < 1) {
				JSONObject map = new JSONObject();
				map.put("invcode", sInvCode);
				map.put("batch", sBatch);
				map.put("sno", sSerial);
				map.put("free1", sfree1);
				map.put("barcode", sbarcode);
				map.put("totalnum", totalnum);
				lstSerino.put(map);
			} else {
				for (int x = 0; x < lstSerino.length(); x++) {
					String invcode = (String) ((JSONObject) (lstSerino).get(x)).get("invcode");
					String batch = (String) ((JSONObject) (lstSerino).get(x)).get("batch");
					String Serial = (String) ((JSONObject) (lstSerino).get(x)).get("sno");

					if (Serial.equals(sSerial)
							&& invcode.equals(sInvCode)
							&& batch.equals(sBatch)) {
						OKFlg = "1";
					}
				}
				if (OKFlg.equals("0")) {
					JSONObject map = new JSONObject();
					map.put("invcode", sInvCode);
					map.put("batch", sBatch);
					map.put("sno", sSerial);
					map.put("free1", sfree1);
					map.put("barcode", sbarcode);
					map.put("totalnum", totalnum);
					lstSerino.put(map);
					OKFlg = "1";
				}
			}
		}

		JSONArray bodys = jsBody.getJSONArray("PurBody");
		jsDBBody = new JSONObject();
		int x = 0;
		for (int i = 0; i < bodys.length(); i++) {
//				int val = bodys.getJSONObject(i).getInt("nshouldinnum") -
//						bodys.getJSONObject(i).getInt("doneqty");

			JSONObject obj = new JSONObject();
			obj.put("cininvid", bodys.getJSONObject(i).getString("cinventoryid"));                //��������ʶ
			obj.put("cinvbasid", bodys.getJSONObject(i).getString("cinvbasid"));            //���������ʶ
			obj.put("cquoteunitid", bodys.getJSONObject(i).getString("cquoteunitid"));        //���ۼ�����λID
			obj.put("nnum", bodys.getJSONObject(i).getInt("doneqty"));                                                            //����
			obj.put("vbatch", bodys.getJSONObject(i).getString("vbatchcode"));                    //����
			obj.put("vfree1", bodys.getJSONObject(i).getString("vfree1"));                    //������1 ����
			jsDBBody.put(x + "", obj);
			x++;
		}


		JSONObject saveJons = new JSONObject();
		saveJons.put("Head", saveHeadJons);
		saveJons.put("Body", jsDBBody);
		saveJons.put("lstSerino", lstSerino);
		//��ȡguid
		if (uploadGuid == null) {
			uploadGuid = UUID.randomUUID();
		}
		saveJons.put("GUIDS", uploadGuid.toString());
		saveJons.put("tmpWHStatus", tmpWHStatus);
		saveJons.put("tmpBillStatus", tmpBillStatus);


		if (!MainLogin.getwifiinfo()) {
			Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}

		JSONObject jas = Common.DoHttpQuery(saveJons, "UpdateInBound", "A");

		if (jas == null) {
			Toast.makeText(this, "���ݱ�������г���������," +
					"�볢���ٴ��ύ!", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return false;
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
			return false;
		}

		if (jas.getBoolean("Status")) {
			SaveFlg = 0;
			String lsResultBillCode = "";

			if (jas.has("BillCode")) {
				lsResultBillCode = jas.getString("BillCode");
			} else {
				Toast.makeText(this, "���ݱ�������г���������," +
						"�볢���ٴ��ύ!", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}

			Map<String, Object> mapResultBillCode = new HashMap<String, Object>();
			mapResultBillCode.put("BillCode", lsResultBillCode);
			ArrayList<Map<String, Object>> lstResultBillCode = new ArrayList<Map<String, Object>>();
			lstResultBillCode.add(mapResultBillCode);

			SimpleAdapter listItemAdapter = new SimpleAdapter(PurStockIn.this, lstResultBillCode,//����Դ
					android.R.layout.simple_list_item_1,
					new String[]{"BillCode"},
					new int[]{android.R.id.text1}
			);
			new AlertDialog.Builder(PurStockIn.this).setTitle("�����ݴ�ɹ�")
					.setAdapter(listItemAdapter, null)
					.setPositiveButton(R.string.QueRen, null).show();

//			//д��log�ļ�
//	   		writeTxt = new writeTxt();
//	   		
//	   		Date day=new Date();    
//	   		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");  
//	   		
//	   		SimpleDateFormat dfd= new SimpleDateFormat("yyyy-MM-dd");
//	   		
//	   		String BillCode = lsResultBillCode;
//	   		String BillType = "45";
//	   		String UserID = MainLogin.objLog.UserID;
//	   		
//	   		String LogName = BillType + UserID + dfd.format(day)+".txt";
//	   		String LogMsg = df.format(day) + " " + "A" + " " + BillCode; 
//	   		
//	   		writeTxt.writeTxtToFile(LogName,LogMsg);

			//д��log�ļ�
			IniActivyMemor();

		}
		return true;
	}

	private boolean SavePurOrder() throws JSONException,
			ParseException, IOException {
		//������˲ɹ����
		jsDBBody = new JSONObject();

		JSONArray heads = jsHead.getJSONArray("PurGood");
		JSONObject saveHeadJons = new JSONObject();
//		saveHeadJons.put("cgeneralhid", this.m_BillID);                    //�ɹ���ⵥID
//		saveHeadJons.put("coperatorid", MainLogin.objLog.UserID);        //����Ա
//		saveHeadJons.put("bincode", this.m_PosCode);                    //��λ��
//		saveHeadJons.put("WarehouseID", this.m_WarehouseID);
//		saveHeadJons.put("pk_purcorp", pk_purcorp);
//		saveHeadJons.put("pk_corp", pk_purcorp);
		saveHeadJons.put("VBILLCODE", txtPurInBillCode.getText().toString()); //�ɹ���ⵥ��(����)
		saveHeadJons.put("CUSER", MainLogin.objLog.UserID);               //����ԱID
		saveHeadJons.put("CUSERNAME", MainLogin.objLog.UserName);        //����ԱName
		saveHeadJons.put("CWAREHOUSEID", CWAREHOUSEID);               //�ֿ�
		saveHeadJons.put("PK_CORP", MainLogin.objLog.CompanyCode);
		saveHeadJons.put("CDISPATCHERID", CDISPATCHERID);             //�շ����code
		saveHeadJons.put("PK_CALBODY", PK_CALBODY);                    //�����֯
		saveHeadJons.put("CDPTID", CDPTID);                    //����
		saveHeadJons.put("CBIZTYPE", heads.getJSONObject(0).getString("cbiztype"));//ҵ������
		saveHeadJons.put("VNOTE", txtReMark.getText().toString());//��ע
		saveHeadJons.put("FREPLENISHFLAG", m_FrePlenishFlag);//�˻���־

		JSONArray lstSerino = new JSONArray();
		//JSONArray head = new JSONArray();

		if (NoScanSave == false) {

			//head = jsHead.getJSONArray("PurGood");

			JSONArray arrays = jsSerino.getJSONArray("Serino");

			for (int i = 0; i < arrays.length(); i++) {
				String OKFlg = "0";
				String sSerial = ((JSONObject) (arrays.get(i))).getString("sno");
				String sBatch = ((JSONObject) (arrays.get(i))).getString("batch");
				String sInvCode = ((JSONObject) (arrays.get(i))).getString("invcode");
				String serino = ((JSONObject) (arrays.get(i))).getString("serino");
				serino = serino.replace("\n", "");
				String totalnum = ((JSONObject) (arrays.get(i))).getString("box");
				totalnum = Double.valueOf(totalnum).toString();
				String sbarcode = serino;
				String sfree1 = ((JSONObject) (arrays.get(i))).getString("vfree1");

				if (lstSerino.length() < 1) {
//					HashMap<String, Object> map = new HashMap<String, Object>();

					JSONObject map = new JSONObject();
					map.put("invcode", sInvCode);
					map.put("batch", sBatch);
					map.put("sno", sSerial);
					map.put("free1", sfree1);
					map.put("barcode", sbarcode);
					map.put("totalnum", totalnum);
					lstSerino.put(map);
				} else {
					for (int x = 0; x < lstSerino.length(); x++) {

						//Object temp = lstSerino.get(x);
						String invcode = (String) ((JSONObject) (lstSerino).get(x)).get("invcode");
						String batch = (String) ((JSONObject) (lstSerino).get(x)).get("batch");
						//String Serial = (String) ((JSONObject) (lstSerino).get(x)).get("sbatchno");

//						String invcode=temp.get("invcode");
//						String batch=(String)temp.get("batch");
//						String Serial=(String)temp.get("sno");
//						
//						if (Serial.equals(sSerial)
//								&& invcode.equals(sInvCode)
//								&& batch.equals(sBatch)) {
//							OKFlg = "1";
//						}
						if (invcode.equals(sInvCode)
								&& batch.equals(sBatch)) {
							OKFlg = "1";
						}
					}
					if (OKFlg.equals("0")) {
						//HashMap<String, Object> map = new HashMap<String, Object>();
						JSONObject map = new JSONObject();
						map.put("invcode", sInvCode);
						map.put("batch", sBatch);
						map.put("sno", sSerial);
						map.put("free1", sfree1);
						map.put("barcode", sbarcode);
						map.put("totalnum", totalnum);
						lstSerino.put(map);
						OKFlg = "1";
					}
				}
			}
		}


		JSONObject saveJons = new JSONObject();

		saveJons.put("lstSerino", lstSerino);
		saveJons.put("Head", saveHeadJons);

		saveJons.put("tmpWHStatus", tmpWHStatus);
		saveJons.put("tmpBillStatus", tmpBillStatus);
		//��ȡguid
		if (uploadGuid == null) {
			uploadGuid = UUID.randomUUID();
		}
		saveJons.put("GUIDS", uploadGuid.toString());

		JSONArray arraySaveBody = new JSONArray();
		if (NoScanSave == false) {
			//JSONArray heads = jsHead.getJSONArray("PurGood");
			JSONArray bodys = jsBody.getJSONArray("PurBody");
			JSONArray arraysSerino = jsSerino.getJSONArray("Serino");

//			if (tmpBillStatus.equals("N")) {
				int y = 0;
				for (int j=0; j<arraysSerino.length(); j++) {

					for (int i = 0; i < bodys.length(); i++) {

						if(arraysSerino.getJSONObject(j).getString("invcode").toLowerCase().equals(
								bodys.getJSONObject(i).getString("invcode")))
						//Double ldDoneQty = 0.0;
						{
							JSONObject obj = new JSONObject();
							obj.put("CINVBASID", bodys.getJSONObject(i).getString("cbaseid"));
							obj.put("CINVENTORYID", bodys.getJSONObject(i).getString("cmangid"));
							obj.put("CINVCODE", bodys.getJSONObject(i).getString("invcode"));
							obj.put("BLOTMGT", "1");        //�Ƿ����ι���
//						if (!bodys.getJSONObject(i).getString("nconfirmnum").toLowerCase().equals(null) &&
//								!bodys.getJSONObject(i).getString("nconfirmnum").isEmpty())
//							ldDoneQty = bodys.getJSONObject(i).getDouble("nconfirmnum");
							obj.put("NINNUM", arraysSerino.getJSONObject(j).getDouble("box"));                    //����
							obj.put("NORDERNUM", bodys.getJSONObject(i).getDouble("nordernum"));
							obj.put("PK_BODYCALBODY", PK_CALBODY);
							obj.put("VBATCHCODE", arraysSerino.getJSONObject(j).getString("batch"));
							obj.put("SOURCCEBILLHID", bodys.getJSONObject(i).getString("corderid"));
							obj.put("SOURCCEBILLBID", bodys.getJSONObject(i).getString("corder_bid"));
							obj.put("VENDORID", heads.getJSONObject(0).getString("cvendormangid"));
							obj.put("VENDORBASID", heads.getJSONObject(0).getString("cvendorbaseid"));
							obj.put("NPRICE", "0.00");
							obj.put("VSOURCEBILLCODE", m_BillNo);
							obj.put("VSOURCEBILLROWNO", bodys.getJSONObject(i).getString("crowno"));
							//jsDBBody.put(y + "", obj);
							arraySaveBody.put(obj);
							y++;
						}
					}
				}
			//}
		}

		jsDBBody.put("ScanDetails", arraySaveBody);
		saveJons.put("Body", jsDBBody);

		if (!MainLogin.getwifiinfo()) {
			Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return false;
		}
		Log.d("TAG", "SavePurOrder: " + saveJons);
		JSONObject jas = Common.DoHttpQuery(saveJons, "SavePurStockIn", "A");

		if (jas == null) {
			Toast.makeText(this, "���ݱ�������г���������," +
					"�볢���ٴ��ύ��!", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return false;
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
			return false;
		}

		if (jas.getBoolean("Status")) {
			SaveFlg = 0;
			String lsResultBillCode = "";

			if (jas.has("BillCode")) {
				lsResultBillCode = jas.getString("BillCode");
			} else {
				Toast.makeText(this, "���ݱ�������г���������," +
						"�볢���ٴ��ύ�򵽵���ϵͳ��ȷ�Ϻ��پ����Ƿ��������!", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}

			PurBillCode = lsResultBillCode;


			//д��log�ļ�


		}
		return true;
	}


	private boolean CheckBox() throws JSONException {
		if (jsBoxTotal == null || jsBoxTotal.length() == 0) {
			Toast.makeText(this, R.string.MeiYouXuYaoBaoCunDeSaoMiaoJiLu, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return false;
		}

		JSONArray boxs = jsBoxTotal.getJSONArray("BoxList");

		for (int i = 0; i < boxs.length(); i++) {
			String serino = boxs.getJSONObject(i).getString("serial");
			String invcode = boxs.getJSONObject(i).getString("invcode");
			String batch = boxs.getJSONObject(i).getString("batch");

			int total = boxs.getJSONObject(i).getInt("total");
			int icurrent = boxs.getJSONObject(i).getInt("current");


			if (icurrent == total) {
				continue;
			} else {
				Toast.makeText(this, "���:" + invcode + "���к�" +
						serino + " �ְ�δɨ��ȫ��������ɨ�� ", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return false;
			}
		}
		return true;
	}

	private DialogInterface.OnClickListener listenUpdate = new
			DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
									int whichButton) {
					try {
						UpdatePurOrder();
					} catch (ParseException e) {
						Toast.makeText(PurStockIn.this, getString(R.string.DanJuBaoCunShiBai) + e.getMessage(),
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						e.printStackTrace();
					} catch (JSONException e) {
						Toast.makeText(PurStockIn.this, getString(R.string.DanJuBaoCunShiBai) + e.getMessage(),
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						e.printStackTrace();
					} catch (IOException e) {
						Toast.makeText(PurStockIn.this, getString(R.string.DanJuBaoCunShiBai) + e.getMessage(),
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						e.printStackTrace();
					}
				}
			};


	private DialogInterface.OnClickListener listenSave2 = new
			DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
									int whichButton) {
					NoScanSave = true;
					try {
						Save();

					} catch (ParseException e) {
						Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi,
								Toast.LENGTH_LONG).show();
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (JSONException e) {
						Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi,
								Toast.LENGTH_LONG).show();
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi,
								Toast.LENGTH_LONG).show();
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};


	private DialogInterface.OnClickListener listenSave = new
			DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
									int whichButton) {
					try {
						//SavePurOrder();//������
						if (tmpBillStatus.equals("Y")) {
							if (SavePurOrder() == true) {
								if (SaveDBOrder() == true) {
									SaveOk();
									IniActivyMemor();
								}
							}
						}
						//
						else {
							SavePurOrder();
						}
					} catch (ParseException e) {
						Toast.makeText(PurStockIn.this, getString(R.string.DanJuBaoCunShiBai) + e.getMessage(),
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						e.printStackTrace();
					} catch (JSONException e) {
						Toast.makeText(PurStockIn.this, getString(R.string.DanJuBaoCunShiBai) + e.getMessage(),
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						e.printStackTrace();
					} catch (IOException e) {
						Toast.makeText(PurStockIn.this, getString(R.string.DanJuBaoCunShiBai) + e.getMessage(),
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						e.printStackTrace();
					}
				}
			};

	private DialogInterface.OnClickListener listenExit = new
			DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,
									int whichButton) {
					finish();
					Common.ReScanErr = false;
					System.gc();
				}
			};


	private void Save1() throws JSONException, ParseException, IOException {
//		GetWHPosStatus();
//		if(tmpWHStatus.equals("Y"))
//		{
//			if(m_PosID.equals(""))
//			{
//				Toast.makeText(PurStockIn.this,R.string.QingShuRuHuoWeiHao, Toast.LENGTH_LONG).show();
//
//				//ADD CAIXY TEST START
//				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//				//ADD CAIXY TEST END
//				return;
//			}
//		}
//		Log.d("TAG", "SaveBoxTotal: " + jsBoxTotal);
//		if(jsBoxTotal == null||jsBoxTotal.length() == 0)
//		{
//			 AlertDialog.Builder bulider =
//					 new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("����û��ɨ����ϸ" +
//					 		"��ȷ��Ҫ����ɹ���ⵥ��"+"?");
//			 bulider.setNegativeButton(R.string.QuXiao, null);
//			 bulider.setPositiveButton(R.string.QueRen, listenSave2).create().show();
//		}
//		else
//		{
//			try {
//				Save();
//
//			} catch (ParseException e) {
//				Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
//						Toast.LENGTH_LONG).show();
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (JSONException e) {
//				Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
//						Toast.LENGTH_LONG).show();
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
//						Toast.LENGTH_LONG).show();
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		try {
			Save();

		} catch (ParseException e) {
			Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi,
					Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi,
					Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi,
					Toast.LENGTH_LONG).show();
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void Save() throws JSONException, ParseException, IOException {
		if (m_BillID == null || m_BillID.equals("")) {
			Toast.makeText(this, "����ȷ����Ҫɨ��Ķ�����", Toast.LENGTH_LONG).show();

			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}

		if (SavePurOrder() == true) {
			//SaveOk();
			IniActivyMemor();
		}

//		Log.d("TAG", "NoScanSave: " + NoScanSave);
//
//		if (NoScanSave == true) {
//			if (SavePurOrder() == true) {
////				if(SaveDBOrder()==true)
////				{
//				SaveOk();
//				IniActivyMemor();
////				}
//			}
//
//			return;
//		}


//		if(!CheckBox())
//			return;


//		boolean isHaveDB = false;
//		JSONArray bodys = jsBody.getJSONArray("PurBody");

//		isHaveDB = true;
//		for (int i = 0; i < bodys.length(); i++) {
//			if (bodys.getJSONObject(i).getInt("doneqty")
//					< bodys.getJSONObject(i).getInt("nshouldinnum")) {
//				isHaveDB = true;
//				break;
//			}
//		}
//		if (isHaveDB && SaveFlg == 0 && tmpBillStatus.equals("Y")) {
//			AlertDialog.Builder bulider =
//					new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("��δɨ�赽����Ʒ," +
//							"��ȷ��Ҫ������" + "?");
//			bulider.setNegativeButton(R.string.QuXiao, null);
//			bulider.setPositiveButton(R.string.QueRen, listenSave).create().show();
//		} else {
//			SavePurOrder();
//			SaveOk();
//			IniActivyMemor();
//		}


	}

	private void Update() throws JSONException, ParseException, IOException {
		if (m_BillID == null || m_BillID.equals("")) {
			Toast.makeText(this, "����ȷ����Ҫɨ��Ķ�����", Toast.LENGTH_LONG).show();

			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}


		if (!CheckBox())
			return;

		boolean isHaveDB = false;
		JSONArray bodys = jsBody.getJSONArray("PurBody");


		for (int i = 0; i < bodys.length(); i++) {
			if (bodys.getJSONObject(i).getInt("doneqty")
					< bodys.getJSONObject(i).getInt("nshouldinnum")) {
				isHaveDB = true;
				break;
			}
		}
		if (isHaveDB && SaveFlg == 0) {
			AlertDialog.Builder bulider =
					new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage(
							"��ȷ��Ҫ��ʱ����ɹ���ⵥ��" + "?");
			bulider.setNegativeButton(R.string.QuXiao, null);
			bulider.setPositiveButton(R.string.QueRen, listenUpdate).create().show();
		} else {
			AlertDialog.Builder bulider =
					new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("�ĵ����Ѿ�ȫ��ɨ�����" +
							"�������水ť");
			bulider.setPositiveButton(R.string.QueRen, null).create().show();


		}


	}

	private void FindPositionByCode(String posCode) throws JSONException {

		try {

			if (m_BillID == null || m_BillID.equals("")) {
				Toast.makeText(this, "������Ϣû�л�ò���ɨ���λ",
						Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				//this.txtPosition.setText("");
				return;
			}
			if (m_AccID == null || m_AccID.equals("")) {
				Toast.makeText(this, "�ֿ����׻�û��ȷ��,������ɨ���λ",
						Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				//this.txtPosition.setText("");
				return;
			}
			posCode = posCode.toUpperCase();
			posCode = posCode.replace("\n", "");

			JSONObject para = new JSONObject();
			para.put("FunctionName", "GetBinCodeInfo");
			para.put("CompanyCode", m_companyCode);
			para.put("STOrgCode", MainLogin.objLog.STOrgCode);
			para.put("WareHouse", this.m_WarehouseID);
			para.put("BinCode", posCode);
			para.put("TableName", "position");
			if (!MainLogin.getwifiinfo()) {
				Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}
			JSONObject rev = Common.DoHttpQuery(para,
					"CommonQuery", m_AccID);


			if (rev == null) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}

			if (!rev.has("Status")) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				return;
			}

			if (rev.getBoolean("Status")) {
				JSONArray val = rev.getJSONArray("position");
				if (val.length() < 1) {
					//txtPosition.setText("");
					Toast.makeText(this, "��ȡ��λʧ��", Toast.LENGTH_LONG).show();
					//ADD CAIXY TEST START
					MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
					//ADD CAIXY TEST END
					return;
				}
				String jposName, jposCode, jposID;
				JSONObject temp = val.getJSONObject(0);

				jposName = temp.getString("csname");
				jposCode = temp.getString("cscode");
				jposID = temp.getString("pk_cargdoc");


				//this.txtPosition.setText(jposCode);
				this.m_PosName = jposName;
				this.m_PosCode = jposCode;
				this.m_PosID = jposID;
				SaveScanedHead();

//					if(this.txtPosition.getText().toString().equals(""))
//					{
//						txtPosition.requestFocus();
//						return;
//					}
				return;
			} else {
				//txtPosition.setText("");
				Toast.makeText(this, "��ȡ��λʧ��", Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				//ADD CAIXY TEST END
				return;

			}

		} catch (JSONException e) {

			Toast.makeText(this,
					R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();

			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			e.printStackTrace();
		} catch (Exception e) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();

			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
		}

	}


	private void ShowOrderNoList(String Bar) {

		Intent PurOrder = new Intent(this, PurOrderList.class);

		PurOrder.putExtra("BillCode", Bar);
		PurOrder.putExtra("StartDate", txtStartDate.getText().toString());
		PurOrder.putExtra("EndDate", txtEndDate.getText().toString());
		startActivityForResult(PurOrder, 96);
	}

	private void GetWHPosStatus() throws JSONException {
		JSONObject para = new JSONObject();
		para.put("FunctionName", "GetWHPosStatus");
		para.put("WareHouse", m_WarehouseID);

		if (!MainLogin.getwifiinfo()) {
			Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return;
		}
		JSONObject rev = null;
		try {
			rev = Common.DoHttpQuery(para, "CommonQuery", "A");
		} catch (ParseException e) {

			Toast.makeText(PurStockIn.this, "��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		} catch (IOException e) {

			Toast.makeText(PurStockIn.this, "��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
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
				Toast.makeText(PurStockIn.this, "��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
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
			Toast.makeText(PurStockIn.this, "��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;

		}


	}

	private void ScanDetail() {
		if (m_BillID == null || m_BillID.equals("")) {
			Toast.makeText(this, "����ȷ����Ҫɨ��Ķ�����", Toast.LENGTH_LONG).show();

			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			return;
		}


//		try {
//			GetWHPosStatus();
//		} catch (JSONException e) {
//			Toast.makeText(PurStockIn.this, "��ȡ�ֿ�״̬ʧ��", Toast.LENGTH_LONG).show();
//			//ADD CAIXY TEST START
//			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//			//ADD CAIXY TEST END
//
//		}
//
//		if (tmpWHStatus.equals("")) {
//			return;
//		}
//
//
//		if (tmpWHStatus.equals("Y")) {
//			if (m_PosID.equals("")) {
//				Toast.makeText(PurStockIn.this, R.string.QingShuRuHuoWeiHao, Toast.LENGTH_LONG).show();
//
//				//ADD CAIXY TEST START
//				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//				//ADD CAIXY TEST END
//				return;
//			}
//		}

		scanDetail = new Intent(PurStockIn.this, PurStockInDetail.class);

		scanDetail.putExtra("BillNo", this.m_BillNo);
		scanDetail.putExtra("BillID", this.m_BillID);

		scanDetail.putExtra("tmpWHStatus", tmpWHStatus);
		scanDetail.putExtra("tmpBillStatus", tmpBillStatus);
		scanDetail.putExtra("tmpWarehouseID", m_WarehouseID);

		if (tmpWHStatus.equals("Y")) {
			scanDetail.putExtra("tmpposID", m_PosID);
		}

		if (jsSerino != null) {
			scanDetail.putExtra("Tag", "1");
		} else {
			scanDetail.putExtra("Tag", "0");
		}
		if(jsHead != null){
			scanDetail.putExtra("head", jsHead.toString());
		}
		else{
			Toast.makeText(PurStockIn.this, "û�еõ���ͷ����", Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return;
		}
		Log.d("TAG", "PutScanDetailBody: " + jsBody);
		if (jsBody != null) {
//	       	 SerializableJSONObject sjs = new SerializableJSONObject();
//	       	 sjs.setJs(jsBody);
			scanDetail.putExtra("body", jsBody.toString());
		}
		else{
			Toast.makeText(PurStockIn.this, "û�еõ���������", Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return;
		}
		if (jsSerino != null) {
//	       	 SerializableJSONObject sjs = new SerializableJSONObject();
//	       	 sjs.setJs(jsSerino);
			scanDetail.putExtra("serino", jsSerino.toString());
		}
		if (jsBoxTotal != null) {
//	       	 SerializableJSONObject sjs = new SerializableJSONObject();
//	       	 sjs.setJs(jsBoxTotal);
			scanDetail.putExtra("box", jsBoxTotal.toString());
		}
		scanDetail.putStringArrayListExtra("ScanedBarcode", ScanedBarcode);

		startActivityForResult(scanDetail, 35);

	}

//	private void Exit()
//	{
//		 AlertDialog.Builder bulider = 
//  				 new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("��ȷ��Ҫ�˳���?");
//  		 bulider.setNegativeButton("ȡ��", null);
//  		 bulider.setPositiveButton(R.string.QueRen, listenExit).create().show();
//				
//	}

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

				startActivityForResult(ViewGrid, 99);
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

	private void btnReferSTOrgList() {
		HashMap<String, String> parameter = new HashMap<String, String>();
		parameter.put("FunctionName", "GetSTOrgList");
		parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
		parameter.put("TableName", "STOrg");
		RequestThread requestThread = new RequestThread(parameter, mHandler, HANDER_STORG);
		Thread td = new Thread(requestThread);
		td.start();
	}

	private void btnRdclClick(String Code) throws ParseException, IOException, JSONException {
		Intent ViewGrid = new Intent(this, VlistRdcl.class);
		ViewGrid.putExtra("FunctionName", "GetRdcl");
		// ViewGrid.putExtra("AccID", "A");
		// ViewGrid.putExtra("rdflag", "1");
		// ViewGrid.putExtra("rdcode", "202");
		ViewGrid.putExtra("AccID", "");
		ViewGrid.putExtra("rdflag", "0");//0 ----�����  1----������
		ViewGrid.putExtra("rdcode", "");
		startActivityForResult(ViewGrid, 98);
	}

	private void btnReferDepartment() {
		HashMap<String, String> parameter = new HashMap<String, String>();
		parameter.put("FunctionName", "GetDeptList");
		parameter.put("CompanyCode", MainLogin.objLog.CompanyCode);
		parameter.put("TableName", "department");
		RequestThread requestThread = new RequestThread(parameter, mHandler, HANDER_DEPARTMENT);
		Thread td = new Thread(requestThread);
		td.start();
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case HANDER_DEPARTMENT:
					JSONObject json = (JSONObject) msg.obj;
					try {
						if (json.getBoolean("Status")) {
							JSONArray val = json.getJSONArray("department");
							JSONObject temp = new JSONObject();
							temp.put("department", val);
							Intent ViewGrid = new Intent(PurStockIn.this, DepartmentListAct.class);
							ViewGrid.putExtra("myData", temp.toString());
							startActivityForResult(ViewGrid, 97);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case HANDER_STORG:
					JSONObject storg = (JSONObject) msg.obj;
					try {
						if (storg.getBoolean("Status")) {
							JSONArray val = storg.getJSONArray("STOrg");
							JSONObject temp = new JSONObject();
							temp.put("STOrg", val);
							Intent StorgList = new Intent(PurStockIn.this, StorgListACt.class);
							StorgList.putExtra("STOrg", temp.toString());
							startActivityForResult(StorgList, 94);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case HANDER_POORDER_HEAD:
					try {
						jsHead = (JSONObject) msg.obj;
						JSONArray head = new JSONArray();
						head = jsHead.getJSONArray("PurGood");
						Log.d("TAG", "PurHead: " + jsHead);

						if (jsHead.getBoolean("Status")) {
							labVendor.setText(head.getJSONObject(0).getString("custname"));
						}

						//�õ�����
						HashMap<String, String> parameter = new HashMap<String, String>();
						parameter.put("FunctionName", "GetPOBody");
						parameter.put("CORDERID", m_BillID);
						parameter.put("TableName", "PurBody");
						RequestThread requestThread = new RequestThread(parameter, mHandler, HANDER_POORDER_BODY);
						Thread td = new Thread(requestThread);
						td.start();

					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case HANDER_POORDER_BODY:
					jsBody = (JSONObject) msg.obj;
					//jsBody = jsonBody;
					Log.d("TAG", "PurBody: " + jsBody);
					break;
				default:
					break;
			}
		}
	};
	//�����Ի���İ�ť�¼�����	
    private Button.OnClickListener myListner = new
    		Button.OnClickListener()
    {
		@Override
		public void onClick(View v) 
		{
			switch(v.getId())
			{
				case R.id.btnPurInExit:
				{
					Exit();
					break;
				}
//				case R.id.btnPurinUpdate:
//				{
//					if(tmpBillStatus.equals("Y"))
//					{
//						try {
//							Update();
//						} catch (ParseException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
//									Toast.LENGTH_LONG).show();
//							//ADD CAIXY TEST START
//							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//							//ADD CAIXY TEST END
//						} catch (JSONException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
//									Toast.LENGTH_LONG).show();
//							//ADD CAIXY TEST START
//							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//							//ADD CAIXY TEST END
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//							Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
//									Toast.LENGTH_LONG).show();
//							//ADD CAIXY TEST START
//							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//							//ADD CAIXY TEST END
//						}
//						break;
//					}
//					else
//					{
//						Toast.makeText(PurStockIn.this, "�ɹ��˻�ʱ�޷�ʹ���ݴ湦��" ,
//								Toast.LENGTH_LONG).show();
//						//ADD CAIXY TEST START
//						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//						break;
//					}
//
//				}
				case R.id.btnPurinSave:
				{

					try 
					{
						if(m_BillNo.isEmpty()){
							Toast.makeText(PurStockIn.this, "������ɹ�������" ,Toast.LENGTH_LONG).show();
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							return;
						}

						if(txtPurInBillCode.getText().toString().isEmpty()){
							Toast.makeText(PurStockIn.this, "������ɹ���ⵥ��" ,Toast.LENGTH_LONG).show();
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							txtPurInBillCode.requestFocus();
							return;
						}

						if(CWAREHOUSEID.isEmpty()){
							Toast.makeText(PurStockIn.this, "������ֿ��" ,Toast.LENGTH_LONG).show();
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							txtWareHouse.requestFocus();
							return;
						}
						if(PK_CALBODY.isEmpty()){
							Toast.makeText(PurStockIn.this, "����������֯��" ,Toast.LENGTH_LONG).show();
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							txtOrganization.requestFocus();
							return;
						}
						if(CDISPATCHERID.isEmpty()){
							Toast.makeText(PurStockIn.this, "�������շ����" ,Toast.LENGTH_LONG).show();
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							txtCategory.requestFocus();
							return;
						}
						if(CDPTID.isEmpty()){
							Toast.makeText(PurStockIn.this, "�����벿��" ,Toast.LENGTH_LONG).show();
							MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
							txtDepartment.requestFocus();
							return;
						}

						Save1();
					} 
					catch (JSONException e) 
					{
						e.printStackTrace();
						Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi ,
								Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
					}
					break;
					
				}
				case R.id.btnPurInScan:
				{
					ScanDetail();
					break;
				}
				case R.id.btnPurBrower:
				{
					Common.ShowLoading(MyContext);
					if(jsDBBody == null || jsDBBody.length() < 1)
			    	{
			    		
			    	}
			    	else
			    	{
			    				Toast.makeText(PurStockIn.this, R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG).show();
								//ADD CAIXY TEST START
								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
								//ADD CAIXY TEST END
			    				break;
			    	}
					ShowOrderNoList("");
					//Common.cancelLoading();
					break;
				}
				case R.id.refer_wh:
					try {
						btnWarehouseClick();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case R.id.refer_organization:
					btnReferSTOrgList();
					break;
				case R.id.refer_lei_bie:
					try {
						btnRdclClick("");
					} catch (IOException e) {
						e.printStackTrace();
					} catch (JSONException e) {
						e.printStackTrace();
					}
					break;
				case R.id.refer_department:
					btnReferDepartment();
					break;
			}
		}    		
    };
    
    

    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pur_stock_in);
		
		ActionBar actionBar = this.getActionBar();
		actionBar.setTitle("�ɹ����");
//		Drawable TitleBar = this.getResources().getDrawable(R.drawable.bg_barbackgroup);
//		actionBar.setBackgroundDrawable(TitleBar);
//		actionBar.show();
		
		SaveFlg=0;

		m_FrePlenishFlag = this.getIntent().getStringExtra("freplenishflag");//�˻���־ Y�˻� N���

		btnSave = (Button)findViewById(R.id.btnPurinSave);
//		btnUpdate = (Button)findViewById(R.id.btnPurinUpdate);
		btnExit = (Button)findViewById(R.id.btnPurInExit);
		btnScan = (Button)findViewById(R.id.btnPurInScan);
		btnBrowOrderNo = (ImageButton)findViewById(R.id.btnPurBrower);
		
		labVendor = (TextView)findViewById(R.id.labPurVendor);
		txtPurOrderNo = (EditText)findViewById(R.id.txtPurOrderNo);
//		txtPosition = (EditText)findViewById(R.id.txtPurPosition);
		txtPurInBillCode = (EditText)findViewById(R.id.txtpurinbillcode);
		//labWHName = (TextView)findViewById(R.id.labWHName);
		//tvbillstatus = (TextView)findViewById(R.id.tvbillstatus);
		//this.tvbillstatus.setText("  ");
		
		btnSave.setOnClickListener(myListner);
		//btnUpdate.setOnClickListener(myListner);
		btnBrowOrderNo.setOnClickListener(myListner);
		btnExit.setOnClickListener(myListner);
		btnScan.setOnClickListener(myListner);

		btnWarehouse = (ImageButton)findViewById(R.id.refer_wh);
		btnWarehouse.setOnClickListener(myListner);
		btnOrganization = (ImageButton)findViewById(id.refer_organization);
		btnOrganization.setOnClickListener(myListner);
		btnCategory = (ImageButton)findViewById(id.refer_lei_bie);
		btnCategory.setOnClickListener(myListner);
		btnDepartment = (ImageButton)findViewById(id.refer_department);
		btnDepartment.setOnClickListener(myListner);

		txtWareHouse = (EditText)findViewById(R.id.wh);
		txtOrganization = (EditText)findViewById(R.id.organization);
		txtCategory = (EditText)findViewById(R.id.lei_bie);
		txtDepartment = (EditText)findViewById(R.id.department);

		txtStartDate = (EditText)findViewById(id.txtStartDate);
		txtEndDate = (EditText)findViewById(id.txtEndDate);
		txtStartDate.setOnFocusChangeListener(myFocusListener);
		//txtStartDate.setOnKeyListener(mOnKeyListener);
		txtEndDate.setOnFocusChangeListener(myFocusListener);
		//txtEndDate.setOnKeyListener(mOnKeyListener);
		txtReMark = (EditText)findViewById(id.remark);
		Date SDate = new Date();
		Date EDate = new Date();
		SimpleDateFormat Dateformat=new SimpleDateFormat("yyyy-MM-dd");
		txtEndDate.setText(Dateformat.format(SDate));
		long time =SDate.getTime();
		time = time - 10*24*60*60*1000;//����ʮ��ǰ
		EDate.setTime(time);
		txtStartDate.setText(Dateformat.format(EDate));

    	//ADD CAIXY START
//    	sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//��һ������Ϊͬʱ���������������������ڶ����������ͣ�����Ϊ��������
//    	MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
//    	MainLogin.music2 = MainLogin.sp.load(this, R.raw.yyy, 1); //����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
//    	//ADD CAIXY END
    	//m_companyCode = MainLogin.objLog.CompanyCode;
    	
    	txtPurOrderNo.setAllCaps(true);
    	//txtPosition.setAllCaps(true);
    	
    	btnSave.setFocusable(false);
    	//btnUpdate.setFocusable(false);
    	btnBrowOrderNo.setFocusable(false);
    	btnExit.setFocusable(false);
    	btnScan.setFocusable(false);
		
		txtPurOrderNo.requestFocus();
		//this.txtPosition.addTextChangedListener(watcher);
		//this.txtPosition.setOnKeyListener(myTxtListener);
		this.txtPurOrderNo.setOnKeyListener(myTxtListener);
		txtPurOrderNo.setOnLongClickListener(myTxtLongClick);
		
    	UserID = MainLogin.objLog.UserID;
    	//String LogName = BillType + UserID + dfd.format(day)+".txt";
    	ScanedFileName = "45"+UserID+".txt";
    	fileName = "/sdcard/DVQ/45"+UserID+".txt";
    	fileNameScan = "/sdcard/DVQ/45Scan"+UserID+".txt";
    	
    	
    	file = new File(fileName);
    	fileScan = new File(fileNameScan);
    	ReScanHead();
    	MainMenu.cancelLoading();
	}

	private DatePickerDialog.OnDateSetListener SDatelistener = new DatePickerDialog.OnDateSetListener() {
		/**params��view�����¼����������
		 * params��myyear����ǰѡ�����
		 * params��monthOfYear����ǰѡ�����
		 * params��dayOfMonth����ǰѡ�����
		 */
		@Override
		public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {
			//�޸�year��month��day�ı���ֵ���Ա��Ժ󵥻���ťʱ��DatePickerDialog����ʾ��һ���޸ĺ��ֵ
			year = myyear;
			month = monthOfYear;
			day = dayOfMonth;
			updateDate();
		}

		//��DatePickerDialog�ر�ʱ������������ʾ
		private void updateDate() {
			//��TextView����ʾ����
			String lsStartDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);
			if(Common.CompareDate(lsStartDate, txtEndDate.getText().toString())){
				txtStartDate.setText(lsStartDate);
			}
			else{
				Toast.makeText(PurStockIn.this, "��ʼ�մ��ڽ����ջ����ڸ�ʽ����ȷ", Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

			}
			txtStartDate.selectAll();
		}

	};

	private DatePickerDialog.OnDateSetListener EDatelistener = new DatePickerDialog.OnDateSetListener() {
		/**params��view�����¼����������
		 * params��myyear����ǰѡ�����
		 * params��monthOfYear����ǰѡ�����
		 * params��dayOfMonth����ǰѡ�����
		 */
		@Override
		public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {
			//�޸�year��month��day�ı���ֵ���Ա��Ժ󵥻���ťʱ��DatePickerDialog����ʾ��һ���޸ĺ��ֵ
			year = myyear;
			month = monthOfYear;
			day = dayOfMonth;
			updateDate();
		}

		//��DatePickerDialog�ر�ʱ������������ʾ
		private void updateDate() {
			//��TextView����ʾ����
			String lsEndDate = year + "-" + String.format("%02d", (month + 1)) + "-" + String.format("%02d", day);
			if(Common.CompareDate(txtStartDate.getText().toString(), lsEndDate)){
				txtEndDate.setText(lsEndDate);
			}
			else{
				Toast.makeText(PurStockIn.this, "��ʼ�մ��ڽ����ջ����ڸ�ʽ����ȷ", Toast.LENGTH_LONG).show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

			}
			txtEndDate.selectAll();
		}

	};

	private View.OnFocusChangeListener myFocusListener = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange(View view, boolean hasFocus) {
			if (hasFocus) {
				switch(view.getId()) {
					case R.id.txtStartDate: {
						year = Integer.valueOf(txtStartDate.getText().toString().split("-")[0]);
						month = Integer.valueOf(txtStartDate.getText().toString().split("-")[1]) - 1;
						day = Integer.valueOf(txtStartDate.getText().toString().split("-")[2]);
						DatePickerDialog dpd = new DatePickerDialog(PurStockIn.this, SDatelistener, year, month, day);
						dpd.show();//��ʾDatePickerDialog���
						break;
					}
					case R.id.txtEndDate: {
						year = Integer.valueOf(txtEndDate.getText().toString().split("-")[0]);
						month = Integer.valueOf(txtEndDate.getText().toString().split("-")[1]) - 1;
						day = Integer.valueOf(txtEndDate.getText().toString().split("-")[2]);
						DatePickerDialog dpd = new DatePickerDialog(PurStockIn.this, EDatelistener, year, month, day);
						dpd.show();//��ʾDatePickerDialog���
						break;
					}
				}

			}
		}
	};
	
	//��¼��ͷ����
    private void SaveScanedHead()
    {
    	if(ReScanHead.equals("0"))
    	{
    		return;
    	}

		
    	writeTxt = new writeTxt();
    	
    	//��¼ɨ������
    	String lsBillBarCode = "";
    	String lsm_WarehouseID = "";
    	String lspk_purcorp = "";
    	String lsm_BillID  = "";
    	String lsm_BillNo = "";
    	String lstmpBillStatus = "";
    	String lsPosCode = "";
    	String lsm_PosName = "";
    	String lsm_PosID = "";
    	
    	String lspk_calbody = "";
    	
    	
    	if(this.m_BillNo.equals(""))
    	{
    		lsBillBarCode = "null";
        	lsm_WarehouseID = "null";
        	lspk_purcorp = "null";
        	lsm_BillID  = "null";
        	lsm_BillNo = "null";
        	lstmpBillStatus = "null";
        	lspk_calbody = "null";
    	}
    	else
    	{
    		lsBillBarCode = m_BillNo;
        	lsm_WarehouseID = m_WarehouseID;
        	lspk_purcorp = pk_purcorp;
        	lsm_BillID  = m_BillID;
        	lsm_BillNo = m_BillNo;
        	lstmpBillStatus = tmpBillStatus;
        	lspk_calbody = pk_calbody;
    	}

    	
    	if(this.m_PosCode ==null||m_PosCode.equals(""))
    	{
    		lsPosCode = "null";
    		lsm_PosName = "null";
    		lsm_PosID = "null";
    	}
    	else
    	{
    		lsPosCode = m_PosCode;
    		lsm_PosName = m_PosCode;
    		lsm_PosID = m_PosID;
    	}
    	
		//ScanedHeadInfo.add(ScanedPosBar);
		if(file.exists())
		{
			file.delete();
		}

		//del walter 20170630 ��ʱ��д�뱾���ı�
//		writeTxt.writeTxtToFile(ScanedFileName,
//				lsBillBarCode +"|"
//		    	+lsm_WarehouseID +"|"
//		    	+lspk_purcorp +"|"
//		    	+lsm_BillID  +"|"
//		    	+lsm_BillNo +"|"
//		    	+lstmpBillStatus +"|"
//		    	+lsm_PosName +"|"
//		    	+lsm_PosID +"|"
//				+lsPosCode +"|"
//				+lspk_calbody);
		
    }
    
    private void Exit()
	{
//		ExitNameList =new String[2];
//		ExitNameList[0]="�˳���������������";
//		ExitNameList[1]="�˳���ɾ����������";
//
//		SelectButton=new AlertDialog.Builder(this).setTitle(R.string.QueRenTuiChu).setSingleChoiceItems(
//				ExitNameList, -1, buttonOnClick).setPositiveButton(R.string.QueRen,
//				buttonOnClick).setNegativeButton(R.string.QuXiao, buttonOnClick).show();
			AlertDialog.Builder bulider =
					new AlertDialog.Builder(this).setTitle(R.string.XunWen).setMessage("����δ�����Ƿ��˳�");
			bulider.setNegativeButton(R.string.QuXiao, null);
			bulider.setPositiveButton(R.string.QueRen, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					IniActivyMemor();
					finish();
				}
			}).create().show();
	}

    
    private void IniActivyMemor()
	{

    	jsHead = null;
   		jsBody= null;
   		jsBoxTotal= null;
   		jsSerino= null;
   		scanDetail = null;
   		jsDBBody= null;
   		jsDBHead= null;

		CWAREHOUSEID = "";
		CDISPATCHERID = "";
		PK_CALBODY = "";
		CDPTID = "";
		m_FrePlenishFlag = "";

		txtPurInBillCode.setText("");
		txtWareHouse.setText("");
		txtOrganization.setText("");
		txtCategory.setText("");
		txtDepartment.setText("");
		txtReMark.setText("");

   		m_WarehouseID = "";
   		m_BillID= "";
   		m_BillNo= "";
   		m_PosCode= "";
   		m_PosName= "";
   		tmpBillStatus = "";
   		tmpWHStatus = "";
   		ScanedBarcode = new ArrayList<String>();
   		//txtPosition.setText("");
   		txtPurOrderNo.setText("");
   		labVendor.setText(" ----");
   		//labWHName.setText(" ----");
   		//tvbillstatus.setText(" ");
   		uploadGuid=null;
   		PurBillCode = "";
   		DBBillCode = "";
   		txtPurOrderNo.requestFocus();
		ScanedBarcode = new ArrayList<String>();
		if(file.exists())
		{
			file.delete();
		}
		
 		if(fileScan.exists())
 		{
 			fileScan.delete();
 		}
		
 		this.txtPurOrderNo.requestFocus();
		
		System.gc();
	}
    
    private class ButtonOnClick implements DialogInterface.OnClickListener
	{
		public int index ;

		public ButtonOnClick(int index)
		{
			this.index = index;
		}

		@Override
		public void onClick(DialogInterface dialog, int whichButton)
		{
			if (whichButton >=0)
			{
				index = whichButton+3;
				// dialog.cancel(); 				
			}
			else
			{
				
				if(dialog.equals(SelectButton))
				{
					if (whichButton == DialogInterface.BUTTON_POSITIVE)
					{
						if(index == 3)
						{
							finish();	
				  			System.gc();
						}
						else if (index == 4)
						{
							IniActivyMemor();
							finish();	
				  			System.gc();
						}	
						return;
					}
					else if (whichButton == DialogInterface.BUTTON_NEGATIVE)
					{
						return;
					}
				}
			}
		}
	}
    
    private void ReScanErr(){
	 AlertDialog.Builder bulider = 
				 new AlertDialog.Builder(this).setTitle(R.string.CuoWu).setMessage("���ݼ��س��ִ���"+"\r\n"+"�˳���ģ�鲢���ٴγ��Լ���");

	 bulider.setPositiveButton(R.string.QueRen, listenExit).setCancelable(false).create().show();
	 MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
	 return;
}
	 
	 private void ReScanHead()
	    {

	    	String res = ""; 
	    	
	    	
			if(!file.exists())
			{
				ReScanHead = "1";
				return;
			}
			 
	        try { 
	 
	            FileInputStream fin = new FileInputStream(fileName); 
	 
	            int length = fin.available(); 
	 
	            byte[] buffer = new byte[length]; 
	 
	            fin.read(buffer); 
	 
	            res = EncodingUtils.getString(buffer, "UTF-8"); 
	 
	            fin.close(); 
	            
	            
	            String[] val;
	    		if(res.contains("|"))
	    		{
	    			ReScanHead = "0";
	    			val = res.split("\\|");
	    			
	    			if( val.length != 10)
	    			{
	    				ReScanHead = "1";
	    				return;
	    			}
	    			
	    			String lsBillBarCode = val[0];
	    	    	String lsm_WarehouseID = val[1];
	    	    	String lspk_purcorp = val[2];
	    	    	String lsm_BillID  = val[3];
	    	    	String lsm_BillNo = val[4];
	    	    	String lstmpBillStatus = val[5];
	    	    	String lsPosCode = val[6];
	    	    	String lsm_PosName = val[7];
	    	    	String lsm_PosID = val[8];
	    	    	String lspk_calbody = val[9];
	    	    	
	    	    	lspk_calbody = lspk_calbody.replace("\r\n", "");

	            	if(!lsBillBarCode.equals("null"))
	            	{        
	            		//ShowOrderNoList(lsBillBarCode);
	            		m_BillNo = lsBillBarCode;
	            		m_WarehouseID= lsm_WarehouseID;
	            		pk_purcorp= lspk_purcorp;
	            		m_BillID= lsm_BillID;
	            		m_BillNo= lsm_BillNo;
	            		pk_calbody = lspk_calbody;
	            		tmpBillStatus= lstmpBillStatus;
	            		this.txtPurOrderNo.setText(m_BillNo);
//	        			this.labVendor.setText("  "+vendor);
//	        			this.labWHName.setText("  "+warehouse);
	        			
//	        			if(tmpBillStatus.equals("Y"))
//	        			{
//	        				this.tvbillstatus.setText("  ");
//	        			}
//	        			else
//	        			{
//	        				this.tvbillstatus.setText("�� ��  �� ��");
//	        			}
	            		//txtPosition.requestFocus();
	            	}
	            	
	            	if(!lsPosCode.equals("null"))
	            	{
	            		m_PosCode = lsPosCode;
		    	    	m_PosName = lsm_PosName;
		    	    	m_PosID = lsm_PosID;
		    	    	//this.txtPosition.setText(lsPosCode);
	            	}
	            	
	            	int x=0;
	            	for(int i=0;i<1;i++)
            		{
            			
                		if(x>10)
                		{
//                			AlertDialog alertDialog = null;
//                			alertDialog = new AlertDialog.Builder(this).create(); 
//                	        alertDialog.setTitle("���ݼ��س��ִ���");  
//                	        alertDialog.setMessage("���ڱ������糩ͨ������³����ٴμ��ػ�������");
//                	        alertDialog.setPositiveButton("ȷ��", listenExit).create().show();
//                	        alertDialog.show();
                			
                			Common.ReScanErr = true;
                			ReScanErr();
                			return;
                		}
                		
                		GetWHPosStatus();
						if(tmpWHStatus == null || tmpWHStatus.equals(""))							
						{
							x++;
							i--;
						}
            		}	            	
	            	//tmpWHStatus
	            	
	            	if((!lsPosCode.equals("null")&&!lsBillBarCode.equals("null"))||
	            			(tmpWHStatus.equals("N")&&!lsBillBarCode.equals("null")))
	            	{
	            		ScanDetail();
	            	}
	            	
	            	ReScanHead = "1";

	    		}
	        } 
	 
	        catch (Exception e) { 
	 
	            e.printStackTrace(); 
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

	        } 
	    	
	    
	    }
	private View.OnLongClickListener myTxtLongClick = new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View view) {
			switch(view.getId()){

				case R.id.txtPurOrderNo:
					Common.ShowLoading(MyContext);
					if(jsDBBody == null || jsDBBody.length() < 1)
					{

					}
					else
					{
						Toast.makeText(PurStockIn.this, R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG).show();
						//ADD CAIXY TEST START
						MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
						//ADD CAIXY TEST END
						break;
					}
					ShowOrderNoList("");
					break;

			}
				return false;
		}
	};


	private OnKeyListener myTxtListener = new
    		OnKeyListener()
    {
		@Override
		public boolean onKey(View v, int arg1, KeyEvent arg2) {
			{		
				if(arg1 == 66 && arg2.getAction() 
						== KeyEvent.ACTION_UP)
				{
					switch(v.getId())
					{				
//						case id.txtPurPosition:
//							String val = txtPosition.getText().toString();
//							val = val.toUpperCase();
//							val = val.replace("\n", "");
//
//							if(jsDBBody == null || jsDBBody.length() < 1)
//					    	{
//
//					    	}
//					    	else
//					    	{
//					    		String oldposname = m_PosCode;
//			    				Toast.makeText(PurStockIn.this, "�������Ѿ���ɨ��,�޷��޸Ļ�λ����Ҫ�޸Ķ����������ɨ���λ", Toast.LENGTH_LONG).show();
//								//ADD CAIXY TEST START
//								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//								//ADD CAIXY TEST END
//								txtPosition.setText(oldposname);
//			    				break;
//					    	}
//							try {
//								FindPositionByCode(val);
//							} catch (JSONException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//								Toast.makeText(PurStockIn.this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
//								//ADD CAIXY TEST START
//								MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//								//ADD CAIXY TEST END
//							}
//							break;
						case id.txtPurOrderNo:
							if(jsDBBody == null || jsDBBody.length() < 1)
					    	{
					    		
					    	}
					    	else
					    	{
					    				Toast.makeText(PurStockIn.this, R.string.GaiRenWuYiJingBeiSaoMiao_WuFaXiuGaiDingDan, Toast.LENGTH_LONG).show();
										//ADD CAIXY TEST START
										MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
										//ADD CAIXY TEST END
					    				break;
					    	}
							String valbar = txtPurOrderNo.getText().toString();
							valbar = valbar.replace("\r\n", "");
							valbar = valbar.replace("\n", "");
							txtPurOrderNo.setText("");
							ShowOrderNoList(valbar);
							break;
							
					}
				}
			}
			return false;
		}
    };
//	private  TextWatcher watcher = new TextWatcher()
//	{   
//		  
//        @Override  
//        public void afterTextChanged(Editable s) {   
//            // TODO Auto-generated method stub   
//            m_PosID="";
//        }   
//  
//        @Override  
//        public void beforeTextChanged(CharSequence s, int start, int count,   
//                int after) {   
//            // TODO Auto-generated method stub   
//               
//        }   
//  
//        @Override  
//        public void onTextChanged(CharSequence s, int start, int before,   
//                int count) {   
//    
//               
//        }   
//           
//    };
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.pur_stock_in, menu);
		return true;
	}
	
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
	
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data)  
	{
		
		if(Common.ReScanErr==true)
    	{
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
    		ReScanErr();
    		return;
    	}
		//�ֿ�
		if (requestCode == 99 && resultCode == 13) {
			String warehousePK1 = data.getStringExtra("result1");
			String warehousecode = data.getStringExtra("result2");
			String warehouseName = data.getStringExtra("result3");
			CWAREHOUSEID = warehousePK1;
			txtWareHouse.requestFocus();
			txtWareHouse.setText(warehouseName);
		}

		//�շ����
		if (requestCode == 98 && resultCode == 2) {
			String code = data.getStringExtra("Code");
			String name = data.getStringExtra("Name");
			String AccID = data.getStringExtra("AccID");
			String RdIDA = data.getStringExtra("RdIDA");    //��Ҫ�ش���id
			String RdIDB = data.getStringExtra("RdIDB");
			CDISPATCHERID = RdIDA;
			txtCategory.requestFocus();
			txtCategory.setText(name);
		}

		//������Ϣ�Ļش�����
		if (requestCode == 97 && resultCode == 4) {
			String deptname = data.getStringExtra("deptname");
			String pk_deptdoc = data.getStringExtra("pk_deptdoc");
			String deptcode = data.getStringExtra("deptcode");
			CDPTID = pk_deptdoc;
			txtDepartment.requestFocus();
			txtDepartment.setText(deptname);
		}

		//�����֯
		if (requestCode == 94 && resultCode == 6) {
			String pk_areacl = data.getStringExtra("pk_areacl");
			String bodyname = data.getStringExtra("bodyname");
			String pk_calbody = data.getStringExtra("pk_calbody");
			PK_CALBODY = pk_calbody;
			txtOrganization.requestFocus();
			txtOrganization.setText(bodyname);
		}

		//�ɹ�����
		if(requestCode==96)
		{
			txtPurOrderNo.requestFocus();
			if(resultCode != 1)
			{
//				labVendor.setText(" ----");
//				txtPurOrderNo.setText("");
				return;
			}
			Bundle bundle = data.getExtras();      
			String orderNo = bundle.getString("BillCode");
//			String vendor = bundle.getString("Vendor");
//			String warehouse = bundle.getString("Warehouse");
//
//			m_WarehouseID = bundle.getString("WarehouseID");
//			pk_purcorp = bundle.getString("pk_purcorp");
//			pk_calbody = bundle.getString("pk_calbody");
			m_BillID = bundle.getString("BillId");
			m_BillNo = orderNo;

			//tmpBillStatus = bundle.getString("billstatus");
			//map.put("billstatus", "N");//���ֲɹ�
	   		//tmpWHStatus = "";
			//m_PosCode = "";
			//m_PosName = "";
			//m_PosID = "";
			//txtPosition.setText("");
			this.txtPurOrderNo.setText(orderNo);
			//this.labVendor.setText("  "+vendor);
			//this.labWHName.setText("  "+warehouse);
			
//			if(tmpBillStatus.equals("Y"))
//			{
//				this.tvbillstatus.setText("  ");
//			}
//			else
//			{
//				this.tvbillstatus.setText("�� ��  �� ��");
//			}
			//this.txtPosition.requestFocus();

			//SaveScanedHead();

			//�õ��ɹ�������ͷ�ͱ���
			HashMap<String, String> parameter = new HashMap<String, String>();
			parameter.put("FunctionName", "GetPOHead");
			parameter.put("CORDERID", m_BillID);
			parameter.put("TableName", "PurGood");
			RequestThread requestThread = new RequestThread(parameter, mHandler, HANDER_POORDER_HEAD);
			Thread td = new Thread(requestThread);
			td.start();
		}
		if(requestCode == 35)
		{


			if(resultCode != 1)
			{
				return;
			}
			
			Bundle bundle = data.getExtras();
			
			String boxJS = bundle.getString("box");
			String serJS = bundle.getString("serino");
			String bodyJS = bundle.getString("body");
			String headJS = bundle.getString("head");
			ScanedBarcode = bundle.getStringArrayList("ScanedBarcode");
			
			try 
			{
				
				this.jsBody = new JSONObject(bodyJS);			
				this.jsHead = new JSONObject(headJS);
				this.jsSerino = new JSONObject(serJS);
//				this.jsBoxTotal = new JSONObject(boxJS);
				this.jsBoxTotal = null;
			}
			catch (JSONException e)
			{
			// TODO Auto-generated catch block
			e.printStackTrace();

			Toast.makeText(PurStockIn.this, e.getMessage() ,
					Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			//ADD CAIXY TEST END
			}
		}
	}
}
