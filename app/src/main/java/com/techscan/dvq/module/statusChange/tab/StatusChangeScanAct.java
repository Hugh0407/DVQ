package com.techscan.dvq.module.statusChange.tab;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.Goods;
import com.techscan.dvq.bean.PurGood;
import com.techscan.dvq.common.Utils;
import com.techscan.dvq.module.materialOut.MyBaseAdapter;
import com.techscan.dvq.module.statusChange.scan.ScAdapter;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.techscan.dvq.common.Utils.doRequest;

public class StatusChangeScanAct extends FragmentActivity implements View.OnKeyListener {

    @InjectView(R.id.ed_bar_code)
    EditText    edBarCode;
    @InjectView(R.id.before)
    RadioButton before;
    @InjectView(R.id.after)
    RadioButton after;
    @InjectView(R.id.viewPager)
    ViewPager   viewPager;
    @InjectView(R.id.btn_task)
    Button      btnTask;
    @InjectView(R.id.btn_detail)
    Button      btnDetail;
    @InjectView(R.id.btn_back)
    Button      btnBack;

    public static List<PurGood> taskList   = new ArrayList<PurGood>();
    public static List<Goods>   detailList = new ArrayList<Goods>();
    Activity activity = this;
    ProgressDialog progressDialog;
    String         BillNo;
    String         OrderID;
    String         OrderType;
    String         AccID;
    String         WarehouseID;
    String         PkCorp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_change_scan);
        ButterKnife.inject(this);
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("��̬ת��ɨ��");
        FragmentManager                  manager = getSupportFragmentManager();
        StatusChangeFragmentPagerAdapter adapter = new StatusChangeFragmentPagerAdapter(manager);
        viewPager.setAdapter(adapter);
        edBarCode.setOnKeyListener(this);
        viewPager.setOnPageChangeListener(pagerListener);
        initTaskData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    @OnClick({R.id.before, R.id.after, R.id.btn_task, R.id.btn_detail, R.id.btn_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.before:
                viewPager.setCurrentItem(0);
                break;
            case R.id.after:
                viewPager.setCurrentItem(1);
                break;
            case R.id.btn_task:
                ScAdapter scAdapter = new ScAdapter(taskList);
                showDialog(taskList, scAdapter, "������Ϣ");
                break;
            case R.id.btn_detail:
                MyBaseAdapter myBaseAdapter = new MyBaseAdapter(detailList);
                showDialog(detailList, myBaseAdapter, "ɨ����ϸ");
                break;
            case R.id.btn_back:
                if (detailList.size() > 0) {
                    setDataToBack();
                } else {
                    Utils.showToast(activity, "û��ɨ�赥��");
                    finish();
                }
                break;
        }
        edBarCode.requestFocus();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //����ͷ������Ϻ󣬿�ʼ�������
                    getOtherInOutBody();
                    break;
                case 2:
                    //�����������
                    setBodyToTaskList((JSONObject) msg.obj);
                    progressDialogDismiss();
                    break;
                default:

                    break;
            }
        }
    };

    private void initTaskData() {
        BillNo = this.getIntent().getStringExtra("BillNo");
        OrderID = this.getIntent().getStringExtra("OrderID");
        OrderType = this.getIntent().getStringExtra("OrderType");
        AccID = this.getIntent().getStringExtra("AccID");
        WarehouseID = this.getIntent().getStringExtra("m_WarehouseID");
        PkCorp = this.getIntent().getStringExtra("pk_corp");
        if (taskList.size() == 0) {
            showProgressDialog();
            getOtherInOutHead();
        }
    }

    private void setDataToBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("��ʾ");
        builder.setMessage("����ɨ������,�Ƿ��˳�");
        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in     = new Intent();
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("taskList", (ArrayList<? extends Parcelable>) taskList);
                in.putExtras(bundle);
                activity.setResult(7, in);
                finish();
            }
        });
        builder.setNegativeButton("ȡ��", null);
        builder.show();
    }

    private void showDialog(List list, BaseAdapter adapter, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        if (list.size() > 0) {
            View     view = LayoutInflater.from(activity).inflate(R.layout.dialog_scan_details, null);
            ListView lv   = (ListView) view.findViewById(R.id.lv);
            lv.setAdapter(adapter);
            builder.setView(view);
        } else {
            builder.setMessage("û��ɨ������");
        }
        builder.setPositiveButton("ȷ��", null);
        builder.show();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            String barcode = edBarCode.getText().toString().trim();
            if (barcode.contains("\n")) {
                barcode = barcode.replace("\n", "");
            }
            edBarCode.setText(barcode);
            edBarCode.setSelection(edBarCode.length());
            edBarCode.selectAll();

            EventBean bean = new EventBean();
            /**
             * ɨ�����������־λ��
             * position�������-1����ʾ�����к��ж��ţ���Ҫ�ֿ����н���
             *          �����0����ʾ���͵�Fragment[0]
             *          �����1�����͵�Fragment[1]
             */
            if (barcode.contains(",")) {
                bean.barcode = barcode;
                bean.position = -1;
                EventBus.getDefault().post(bean);
                return true;
            }

            if (viewPager.getCurrentItem() == 0) {
                bean.barcode = barcode;
                bean.position = 0;
                EventBus.getDefault().post(bean);
                return true;
            }

            if (viewPager.getCurrentItem() == 1) {
                bean.barcode = barcode;
                bean.position = 1;
                EventBus.getDefault().post(bean);
                return true;
            }
            return true;
        }
        return false;
    }

    /**
     * ��ȡ��ͷ
     */
    private void getOtherInOutHead() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetOtherInOutHead");
        parameter.put("BillType", OrderType);
        parameter.put("accId", AccID);
        parameter.put("BillCode", BillNo);
        parameter.put("pk_corp", PkCorp);
        parameter.put("TableName", "PurHead");
        doRequest(parameter, handler, 1);
    }

    /**
     * ��ȡ����
     */
    private void getOtherInOutBody() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetOtherInOutBody");
        parameter.put("BillID", OrderID);
        parameter.put("accId", AccID);
        parameter.put("TableName", "PurBody");
        doRequest(parameter, handler, 2);
    }

    private void setBodyToTaskList(JSONObject jsonBody) {
        try {
            if (jsonBody != null && jsonBody.getBoolean("Status")) {
                Log.d("TAG", "jsonBody: " + jsonBody);
                JSONArray  jsonArray = jsonBody.getJSONArray("PurBody");
                PurGood    purGood;
                JSONObject object;
                for (int i = 0; i < jsonArray.length(); i++) {
                    object = jsonArray.getJSONObject(i);
                    purGood = new PurGood();
                    purGood.setSourceBill(BillNo);
                    purGood.setPk_invbasdoc(object.getString("pk_invbasdoc"));
                    purGood.setNshouldinnum(object.getString("nshouldinnum"));
                    purGood.setInvcode(object.getString("invcode"));
                    purGood.setInvname(object.getString("invname"));
                    purGood.setCinventoryid(object.getString("cinventoryid"));
                    purGood.setVbatchcode(object.getString("vbatchcode"));
                    purGood.setFbillrowflag(object.getString("fbillrowflag"));  //2ת��ǰ��3ת����
                    purGood.setNshouldinnum(object.getString("nshouldinnum"));
                    purGood.setVbatchcode(object.getString("vbatchcode"));
                    purGood.setVsourcerowno(object.getString("crowno"));
                    purGood.setCsourcebillhid(object.getString("csourcebillhid"));
                    purGood.setCsourcebillbid(object.getString("csourcebillbid"));
                    taskList.add(purGood);
                }
            } else {
                Utils.showToast(activity, "�����������");
                Log.d("TAG", "jsonBody = null ");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * ��ȡ���ݵĵȴ�dialog
     */
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// ���ý���������ʽΪԲ��ת���Ľ�����
        progressDialog.setCancelable(false);// �����Ƿ����ͨ�����Back��ȡ��
        progressDialog.setCanceledOnTouchOutside(false);// �����ڵ��Dialog���Ƿ�ȡ��Dialog������
        progressDialog.setTitle("��ȡ����");
        progressDialog.setMessage("���ڻ�ȡ����,��ȴ�...");
        progressDialog.show();
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    if (progressDialog.isShowing()) {
                        Thread.sleep(30 * 1000);
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    /**
     * progressDialog ��ʧ
     */
    private void progressDialogDismiss() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    ViewPager.OnPageChangeListener pagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    before.setChecked(true);
                    break;
                case 1:
                    after.setChecked(true);
                    break;
            }
            edBarCode.requestFocus();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
