package com.techscan.dvq;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.common.Common;
import com.techscan.dvq.login.MainLogin;

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

public class InvOrderList extends Activity {
	// ADD CAIXY TEST START
//	private SoundPool sp;// ����һ��SoundPool
//	private int MainLogin.music;// ����һ��int������suondID
	// ADD CAIXY TEST END
	@Nullable
	Button btinv_order_listReturn = null;

	private List<Map<String, Object>> getData(@NonNull JSONObject jas, String AccID)
			throws JSONException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;

		JSONObject tempJso = null;
		if (!jas.has("Status")) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			return null;
		}
		if (!jas.getBoolean("Status")) {

			String errMsg = "";

			if (!jas.has("ErrMsg"))
				errMsg = getString(R.string.WangLuoChuXianWenTi);
			else
				errMsg = jas.getString("ErrMsg");

			Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			return null;
		}
		JSONArray jsarray = jas.getJSONArray("dbHead");

		for (int i = 0; i < jsarray.length(); i++) {
			tempJso = jsarray.getJSONObject(i);
			map = new HashMap<String, Object>();
			map.put("No", tempJso.getString("vbillcode"));
			map.put("From", tempJso.getString("storname"));
			map.put("BillId", tempJso.getString("cspecialhid"));
			map.put("AccID", AccID);
			map.put("companyID", tempJso.getString("pk_corp"));
			map.put("warehouseID", tempJso.getString("coutwarehouseid"));
			map.put("warehouseCode", tempJso.getString("storcode"));

			list.add(map);
		}
		return list;
	}

	@Nullable
	public List<Map<String, Object>> mData;
	@Nullable
	private Handler handler = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inv_order_list);

		this.setTitle("�̵㵥��ϸ");
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
				.penaltyLog().penaltyDeath().build());

		// ADD CAIXY START
//		sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);// ��һ������Ϊͬʱ���������������������ڶ����������ͣ�����Ϊ��������
//		MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); // ����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
//		// ADD CAIXY END
		btinv_order_listReturn = (Button) findViewById(R.id.btinv_order_listReturn);
		btinv_order_listReturn.setOnClickListener(ButtonOnClickListener);

		JSONObject para = new JSONObject();
		Intent intent = this.getIntent();

		String AccID = intent.getStringExtra("AccID");
		String FunctionName = "";
		String Bill = "";
		if (intent.hasExtra("BillCode")) {
			// ���ݵ��Ż�ȡ�̵㵥//��Ҫ�޸�
			String BillCode = intent.getStringExtra("BillCode");
			String acc = BillCode.substring(0, 1);
			Bill = BillCode.substring(1);

			FunctionName = "GetSpecialBillHead";// ;
			try {
				if (intent.hasExtra("AccID")) {
					AccID = acc;
				}

				para.put("BillCode", Bill);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
			}

		} else {

			FunctionName = "GetSpecialBillHead";

			try {
				if (intent.hasExtra("AccID")) {
					AccID = intent.getCharSequenceExtra("AccID").toString();
				}

				para.put("BillCode", "null");

			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				e2.printStackTrace();
			}
		}

		try {
			para.put("TableName", "dbHead");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			if (!Bill.equals("")) {
				this.finish();
			}
			return;
		}

		JSONObject jas;
		try {
//			if (!MainLogin.getwifiinfo()) {
//				Toast.makeText(this, R.string.WiFiXinHaoCha, Toast.LENGTH_LONG)
//						.show();
//				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
//				return;
//			}
			jas = Common.DoHttpQuery(para, FunctionName, AccID);
		} catch (Exception ex) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END
			if (!Bill.equals("")) {
				this.finish();
			}
			return;
		}
		try {
			if (jas == null) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END
				if (!Bill.equals("")) {
					this.finish();
				}
				return;
			}
			if (!jas.has("Status")) {
				Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG)
						.show();
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);

				if (!Bill.equals("")) {
					this.finish();
				}
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
				// ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				// ADD CAIXY TEST END

				if (!Bill.equals("")) {
					this.finish();
				}
				return;
			}

			mData = getData(jas, AccID);
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END

			if (!Bill.equals("")) {
				this.finish();
			}
			return;
		} catch (Exception ex) {
			Toast.makeText(this, R.string.WangLuoChuXianWenTi, Toast.LENGTH_LONG).show();
			// ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			// ADD CAIXY TEST END

			if (!Bill.equals("")) {
				this.finish();
			}
			return;
		}

		ListView list = (ListView) findViewById(R.id.invorderlist);

		SimpleAdapter listItemAdapter = new SimpleAdapter(this, mData,// ����Դ
				R.layout.vlistinvs,// ListItem��XMLʵ��
				// ��̬������ImageItem��Ӧ������
				new String[] { "No", "From", "AccID" },
				// ImageItem��XML�ļ������һ��ImageView,����TextView ID
				new int[] { R.id.listinvorder, R.id.listinvfromware,
						R.id.listinvaccid });

		// list.addHeaderView()

		list.setOnItemClickListener(itemListener);
		list.setAdapter(listItemAdapter);

		if (!Bill.equals("")) {
			Map<String, Object> map = mData.get(0);
			String orderNo = map.get("No").toString();
			String billId = map.get("BillId").toString();
			String AccId = map.get("AccID").toString();

			// String OrgId=map.get("OrgId").toString();
			String companyID = map.get("companyID").toString();
			String vCode = map.get("No").toString();
			String warehouseID = map.get("warehouseID").toString();
			String warehouseCode = map.get("warehouseCode").toString();
			String warehouseName = map.get("From").toString();

			if (!Common.CheckUserRole(AccId, companyID, "40081016")) {
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				Toast.makeText(InvOrderList.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian,
						Toast.LENGTH_LONG).show();
				this.finish();
				return;
			}

			if (!Common.CheckUserWHRole(AccId, companyID, warehouseID)) {
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				Toast.makeText(InvOrderList.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian,
						Toast.LENGTH_LONG).show();
				this.finish();
				return;
			}

			Intent intentx = new Intent();
			intentx.putExtra("result", orderNo);// �ѷ������ݴ���Intent
			intentx.putExtra("BillId", billId);
			intentx.putExtra("AccID", AccId);
			// intent.putExtra("OrgId", OrgId);
			intentx.putExtra("companyID", companyID);
			intentx.putExtra("vcode", vCode);
			intentx.putExtra("warehouseID", warehouseID);
			intentx.putExtra("warehouseCode", warehouseCode);
			intentx.putExtra("warehouseName", warehouseName);

			InvOrderList.this.setResult(1, intentx);// ���ûش����ݡ�resultCodeֵ��1�����ֵ�������ڽ��������ֻش����ݵ���Դ��������ͬ�Ĵ���
			InvOrderList.this.finish();// �ر��Ӵ���ChildActivity
			this.finish();
		}

	}

	@NonNull
	private OnClickListener ButtonOnClickListener = new OnClickListener() {

		@Override
		public void onClick(@NonNull View v) {
			switch (v.getId()) { // btnSDScanReturn
			case id.btinv_order_listReturn:
				finish();
				break;
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.pd_order_list, menu);
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

	// private List.. itemListener = new
	// DialogInterface.OnClickListener()

	@NonNull
	private ListView.OnItemClickListener itemListener = new ListView.OnItemClickListener() {
		@Override
		public void onItemClick(@NonNull AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
			// TODO Auto-generated method stub
			Adapter adapter = arg0.getAdapter();
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) adapter
					.getItem(arg2);
			String orderNo = map.get("No").toString();
			String billId = map.get("BillId").toString();
			String AccId = map.get("AccID").toString();

			// String OrgId=map.get("OrgId").toString();
			String companyID = map.get("companyID").toString();
			String vCode = map.get("No").toString();
			String warehouseID = map.get("warehouseID").toString();
			String warehouseCode = map.get("warehouseCode").toString();
			String warehouseName = map.get("From").toString();

			if (!Common.CheckUserRole(AccId, companyID, "40081016")) {
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				Toast.makeText(InvOrderList.this, R.string.MeiYouShiYongGaiDanJuDeQuanXian,
						Toast.LENGTH_LONG).show();
				return;
			}

			if (!Common.CheckUserWHRole(AccId, companyID, warehouseID)) {
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				Toast.makeText(InvOrderList.this, R.string.MeiYouShiYongGaiCangKuDeQuanXian,
						Toast.LENGTH_LONG).show();
				return;
			}

			Intent intent = new Intent();
			intent.putExtra("result", orderNo);// �ѷ������ݴ���Intent
			intent.putExtra("BillId", billId);
			intent.putExtra("AccID", AccId);
			// intent.putExtra("OrgId", OrgId);
			intent.putExtra("companyID", companyID);
			intent.putExtra("vcode", vCode);
			intent.putExtra("warehouseID", warehouseID);
			intent.putExtra("warehouseCode", warehouseCode);
			intent.putExtra("warehouseName", warehouseName);

			InvOrderList.this.setResult(1, intent);// ���ûش����ݡ�resultCodeֵ��1�����ֵ�������ڽ��������ֻش����ݵ���Դ��������ͬ�Ĵ���
			InvOrderList.this.finish();// �ر��Ӵ���ChildActivity

		}
	};
}
