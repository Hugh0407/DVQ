package com.techscan.dvq.module.saleout.scan;

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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.techscan.dvq.R;
import com.techscan.dvq.bean.PurSaleOutGoods;
import com.techscan.dvq.bean.SaleOutGoods;
import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;
import static com.techscan.dvq.common.Utils.formatDecimal;
import static com.techscan.dvq.common.Utils.isNumber;
import static com.techscan.dvq.common.Utils.showToast;


/**
 * ��̬ת��ģ���µ� ɨ�����
 */

public class Sale extends Activity {

   @InjectView(R.id.txtBarcode)
    EditText txtBarcode;
    @InjectView(R.id.txtSaleInvCode)
    EditText txtSaleInvCode;
    @InjectView(R.id.txtSaleInvName)
    EditText txtSaleInvName;
    @InjectView(R.id.txtSaleType)
    EditText txtSaleType;
    @InjectView(R.id.txtSaleSpec)
    EditText txtSaleSpec;
    @InjectView(R.id.txtSaleBatch)
    EditText txtSaleBatch;
    @InjectView(R.id.txtSaleNumber)
    EditText txtSaleNumber;
    @InjectView(R.id.txtSaleWeight)
    EditText txtSaleWeight;
    @InjectView(R.id.txtSaleTotal)
    EditText txtSaleTotal;
    @InjectView(R.id.txtSaleUnit)
    EditText txtSaleUnit;
    @InjectView(R.id.btnTask)
    Button btnTask;
    @InjectView(R.id.btnDetail)
    Button btnDetail;
    @InjectView(R.id.btnReturn)
    Button btnReturn;
    @InjectView(R.id.tvSalecount)
    TextView tvSalecount;

    String PK_CORP;
    String BillCode;
    String CSALEID;
    ProgressDialog progressDialog;
    Activity activity;
    JSONObject jsonBody;
    public static List<PurSaleOutGoods> taskList = new ArrayList<PurSaleOutGoods>();
    public static List<SaleOutGoods> detailList = new ArrayList<SaleOutGoods>();
    public static List<SaleOutGoods> ovList = new ArrayList<SaleOutGoods>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sale_out_scan_detail_new);
        ButterKnife.inject(this);
        activity = this;
        init();
        initTaskData();
    }

    private void init() {
        ActionBar actionBar = this.getActionBar();
        actionBar.setTitle("���۳���ɨ��");
        txtBarcode.setOnKeyListener(mOnKeyListener);
        txtSaleNumber.setOnKeyListener(mOnKeyListener);
        txtSaleNumber.addTextChangedListener(new CustomTextWatcher(txtSaleNumber));
        txtBarcode.addTextChangedListener(new CustomTextWatcher(txtBarcode));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity = null;
    }

    private void initTaskData() {
        PK_CORP = this.getIntent().getStringExtra("PK_CORP");
        BillCode = this.getIntent().getStringExtra("BillCode");
        CSALEID = this.getIntent().getStringExtra("CSALEID");
        if (taskList.size() == 0) {
            showProgressDialog();
            getSaleOutBody();
        }
    }
    @OnClick({R.id.btnTask, R.id.btnDetail, R.id.btnReturn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnTask:
                SaleOutTaskAdapter scAdapter = new SaleOutTaskAdapter(taskList);
                showDialog(taskList, scAdapter, "������Ϣ");
                break;
            case R.id.btnDetail:
                SalesOutDetailAdapter myBaseAdapter = new SalesOutDetailAdapter(detailList);
                showDialog(detailList, myBaseAdapter, "ɨ����ϸ");
                break;
            case R.id.btnReturn:
//               if (detailList.size() > 0) {
//                    setDataToBack();
//                } else {
//                    Utils.showToast(activity, "û��ɨ�赥��");
//                    finish();
//                }
//                break;
                if (ovList.size() > 0) {
                    Intent in = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("overViewList", (ArrayList<? extends Parcelable>) ovList);
                    in.putExtras(bundle);
                    Sale.this.setResult(5, in);
                } else {
                    showToast( Sale.this,"û��ɨ�赥��");
                }
                finish();
        }
    }


    /**
     * �����������߳�ͨ��
     * msg.obj �Ǵ����̴߳��ݹ���������
     */
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //�����������
                    jsonBody = new JSONObject();
                   jsonBody = (JSONObject) msg.obj;
                    try {
                        if (jsonBody != null && jsonBody.getBoolean("Status")) {
                            Log.d("TAG", "jsonBody: " + jsonBody);
                            JSONArray jsonArray = jsonBody.getJSONArray("dbBody");
                            PurSaleOutGoods purSaleOutGoods;
                            JSONObject object;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                object = jsonArray.getJSONObject(i);
                                purSaleOutGoods = new PurSaleOutGoods();
                                purSaleOutGoods.setInvCode(object.getString("invcode"));
                                Log.d(TAG, "handleMessage: "+object.getString("invcode"));
                                purSaleOutGoods.setNumber(object.getString("nnumber"));
                                purSaleOutGoods.setInvName(object.getString("invname"));
                                purSaleOutGoods.setSpec(object.getString("invspec"));
                                purSaleOutGoods.setType(object.getString("invtype"));
                                taskList.add(purSaleOutGoods);
                            }

//                            tvSalecount.setText("����" + jsonBody.getString("nnumber") + " | " + "��ɨ" + jsonBody.getString("nottaloutinvnum")
//                                    + " | " + "δɨ" + (Float.valueOf( jsonBody.getString("nnumber"))  -Float.valueOf(jsonBody.getString("nottaloutinvnum"))));
                        } else {
                            Log.d("TAG", "jsonBody = null ");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialogDismiss();
                    break;
                case 3:
                    //��������������������Ұ��������õ�UI��
                    JSONObject json = (JSONObject) msg.obj;
                    if (null == json) {
                        return;
                    }
                    try {
                        setInvBaseToUI(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    /**
     * ͨ����ȡ����json �����õ�������Ϣ,�����õ�UI��
     *
     * @param json
     * @throws JSONException
     */
    String pk_invbasdoc = "";
    String pk_invmandoc = "";

    private void setInvBaseToUI(JSONObject json) throws JSONException {
        Log.d("TAG", "setInvBaseToUI: " + json);
        if (json.getBoolean("Status")) {
            JSONArray val = json.getJSONArray("baseInfo");
            HashMap<String, Object> map = null;
            for (int i = 0; i < val.length(); i++) {
                JSONObject tempJso = val.getJSONObject(i);
                map = new HashMap<String, Object>();
                map.put("invname", tempJso.getString("invname"));   //�������
                map.put("invcode", tempJso.getString("invcode"));   //00179
                map.put("measname", tempJso.getString("measname"));   //ǧ��
                map.put("pk_invbasdoc", tempJso.getString("pk_invbasdoc"));
                pk_invbasdoc = tempJso.getString("pk_invbasdoc");
                map.put("pk_invmandoc", tempJso.getString("pk_invmandoc"));
                pk_invmandoc = tempJso.getString("pk_invmandoc");
                map.put("invtype", tempJso.getString("invtype"));   //�ͺ�
                map.put("invspec", tempJso.getString("invspec"));   //���
                map.put("oppdimen", tempJso.getString("oppdimen"));   //����
            }
            if (map != null) {
                txtSaleInvName.setText(map.get("invname").toString());
                txtSaleUnit.setText(map.get("measname").toString());
                txtSaleType.setText(map.get("invtype").toString());
                txtSaleSpec.setText(map.get("invspec").toString());
            }

        }
    }


    private void showDialog(final List list, final BaseAdapter adapter, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Sale.this);
        builder.setTitle(title);
        if (list.size() > 0) {
            View view = LayoutInflater.from(Sale.this).inflate(R.layout.dialog_scan_details, null);
            ListView lv = (ListView) view.findViewById(R.id.lv);
            if (title.equals("ɨ����ϸ")) { //ֻ����ϸ��ҳ���ǿɵ���ģ�����ҳ���ǲ��ɵ����
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        AlertDialog.Builder delDialog = new AlertDialog.Builder(Sale.this);
                        delDialog.setTitle("�Ƿ�ɾ����������");
                        delDialog.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);

                                adapter.notifyDataSetChanged();
//                                addDataToDetailList();
                            }
                        });
                        delDialog.setNegativeButton("ȡ��", null);
                        delDialog.show();
                    }
                });
            }
            lv.setAdapter(adapter);
            builder.setView(view);
        } else {
            builder.setMessage("û��ɨ������");
        }
        builder.setPositiveButton("ȷ��", null);
        builder.show();
    }

    /**
     * ��ȡ����
     */
    private void getSaleOutBody() {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetSaleOutBodyNew");
        parameter.put("BillCode", BillCode);
        parameter.put("CSALEID", CSALEID);
        parameter.put("CorpPK", "4100");
        parameter.put("TableName", "dbBody");
        Log.d(TAG, "GetBillBodyDetailInfo: " + BillCode);
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread td = new Thread(requestThread);
        td.start();
    }


    /**
     * ��ȡ���������Ϣ
     *
     * @param
     */
    private void getInvBaseInfo(String invcode) {
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", PK_CORP);
        parameter.put("InvCode", invcode);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 3);
        Thread td = new Thread(requestThread);
        td.start();
    }

    private void setDataToBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("��ʾ");
        builder.setMessage("����ɨ������,�Ƿ��˳�");
        builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent in = new Intent();
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

    /**
     * ��ȡ���ݵĵȴ�dialog
     */
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(Sale.this);
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
                        // cancel��dismiss�������ʶ���һ���ģ����Ǵ���Ļ��ɾ��Dialog,Ψһ��������
                        // ����cancel������ص�DialogInterface.OnCancelListener���ע��Ļ�,dismiss��������ص�
                        // progressDialog.dismiss();
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

    /**
     * �������
     */
    private boolean barAnalysis() {
        String Bar = txtBarcode.getText().toString().trim();
        if (Bar.contains("\n")) {
            Bar = Bar.replace("\n", "");
        }
        txtBarcode.setText(Bar);
        txtBarcode.setSelection(txtBarcode.length());   //������ƶ�������λ��
        txtBarcode.selectAll();
        String[] barCode = Bar.split("\\|");
        if (barCode.length == 8 && barCode[0].equals("P")) {

            /*********************************************************************/
            //�жϸ������ڡ����� �б����Ƿ����
            for (PurSaleOutGoods pur : taskList) {
                Log.d(TAG, "barAnalysis: "+taskList.size());

                Log.d(TAG, "barAnalysis: "+pur.getInvCode());
                Log.d(TAG, "barAnalysis: "+barCode[1].toString());
//                if (!pur.getInvCode().equals(barCode[1].trim())) {
//                    Utils.showToast(activity, "�����벻�ڱ���ɨ��������!");
//                    return false;
//                }
            }
            /*********************************************************************/

            String invcode = barCode[1];
            txtSaleInvCode.setText(invcode);
            txtSaleBatch.setText(barCode[2]);
            txtSaleWeight.setText(barCode[5]);
            txtSaleTotal.setText("");
            txtSaleNumber.setEnabled(true);
            txtSaleNumber.setText("1");
            txtSaleNumber.requestFocus();  //����ɨ�����������������,��������,��ӵ��б�
            txtSaleNumber.setSelection(txtSaleNumber.length());   //������ƶ�������λ��
            getInvBaseInfo(invcode);
            return true;
        } else if (barCode.length == 9 && barCode[0].equals("TP")) {//����TP|SKU|LOT|WW|TAX|QTY|NUM|CW|ONLY|SN
            /*********************************************************************/
            //�жϸ������ڡ����� �б����Ƿ����
            for (PurSaleOutGoods pur : taskList) {
                if (!pur.getInvCode().equals(barCode[1])) {
                    Utils.showToast(activity, "��������!");
                    return false;
                }
            }
            /*********************************************************************/
            for (int i = 0; i < detailList.size(); i++) {
                if (detailList.get(i).getBarcode().equals(Bar)) {
                    Utils.showToast(activity, "��������ɨ��!");
                    return false;
                }
            }
            //��������룬ȫ������Ϊ���ɱ༭��Ĭ���������ǿɱ༭��
//            mEdLot.setEnabled(false);
//            mEdQty.setEnabled(false);
//            mEdNum.setEnabled(false);
            String invcode = barCode[1];
            txtSaleInvCode.setText(invcode);
            txtSaleBatch.setText(barCode[2]);
            txtSaleWeight.setText(barCode[5]);
            txtSaleNumber.setText(barCode[6]);
            double weight = Double.valueOf(barCode[5]);
            double mEdNum = Double.valueOf(barCode[6]);
            txtSaleTotal.setText(String.valueOf(weight * mEdNum));
            getInvBaseInfo(invcode);
            return true;
        } else {
            Utils.showToast(activity, "�������Ͳ�ƥ��");
            return false;
        }
    }

    /**
     * �ж����е�edtext�Ƿ�Ϊ��
     *
     * @return true---->���е�ed����Ϊ��,false---->���е�ed��Ϊ��
     */
    private boolean isAllEdNotNull() {
        return (!TextUtils.isEmpty(txtBarcode.getText())
                && !TextUtils.isEmpty(txtSaleNumber.getText())
                && !TextUtils.isEmpty(txtSaleInvCode.getText())
                && !TextUtils.isEmpty(txtSaleBatch.getText())
                && !TextUtils.isEmpty(txtSaleInvName.getText())
                && !TextUtils.isEmpty(txtSaleSpec.getText())
                && !TextUtils.isEmpty(txtSaleType.getText())
                && !TextUtils.isEmpty(txtSaleWeight.getText())
                && !TextUtils.isEmpty(txtSaleUnit.getText())
                && !TextUtils.isEmpty(txtSaleTotal.getText()));
    }

    /**
     * �����Ϣ�� ������
     *
     * @return
     */
    private void addDataToDetailList() {
        SaleOutGoods goods = new SaleOutGoods();
        try {
            goods.setBarcode(txtBarcode.getText().toString());
            goods.setInvCode(txtSaleInvCode.getText().toString());
            goods.setInvName(txtSaleInvName.getText().toString());
            goods.setType(txtSaleType.getText().toString());
            goods.setSpec(txtSaleSpec.getText().toString());
            goods.setUnit(txtSaleUnit.getText().toString());
            goods.setBatch(txtSaleBatch.getText().toString());
            goods.setQty(Float.valueOf(txtSaleTotal.getText().toString()));
            goods.setPk_invbasdoc(pk_invbasdoc);
            goods.setPk_invmandoc(pk_invmandoc);
            goods.setNumber(jsonBody.getString("nnumber").toString());
            goods.setCadvisecalbodyid(jsonBody.getString("cadvisecalbodyid").toString());
            goods.setCinvbasdocid(jsonBody.getString("cinvbasdocid").toString());
            goods.setCinventoryid(jsonBody.getString("cinventoryid").toString());
            goods.setCorder_bid(jsonBody.getString("corder_bid").toString());
            goods.setCrowno(jsonBody.getString("crowno").toString());
            goods.setCsaleid(jsonBody.getString("csaleid").toString());
            goods.setPk_corp(jsonBody.getString("pk_corp").toString());
            goods.setVreceiveaddress(jsonBody.getString("vreceiveaddress").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        for (PurSaleOutGoods pur : taskList) {
            if (pur.getInvCode().equals(goods.getInvCode())) {
                float nowNum = goods.getQty() + Float.valueOf(pur.getNum_task());
                if (nowNum > Float.valueOf(pur.getNumber())) {
                    Utils.showToast(activity, "��������,������ɨ��");
                    return ;
                }
                pur.setNum_task(String.valueOf(nowNum));
            }
        }
        detailList.add(goods);
        addDataToOvList();

    }

    /**
     * ������ݵ������б��У���д��Goods��equals��hashcode
     */
    private void addDataToOvList() {
        ovList.clear();
        int detailSize = detailList.size();
        for (int i = 0; i < detailSize; i++) {
            SaleOutGoods dtGood = detailList.get(i);
            if (ovList.contains(dtGood)) {
                int j = ovList.indexOf(dtGood);
                SaleOutGoods ovGood = ovList.get(j);
                ovGood.setQty(ovGood.getQty() + dtGood.getQty());
            } else {
                try {
                    SaleOutGoods goods = new SaleOutGoods();
                    goods.setBarcode(txtBarcode.getText().toString());
                    goods.setInvCode(txtSaleInvCode.getText().toString());
                    goods.setInvName(txtSaleInvName.getText().toString());
                    goods.setType(txtSaleType.getText().toString());
                    goods.setSpec(txtSaleSpec.getText().toString());
                    goods.setUnit(txtSaleUnit.getText().toString());
                    goods.setBatch(txtSaleBatch.getText().toString());
                    goods.setQty(Float.valueOf(txtSaleTotal.getText().toString()));
                    goods.setPk_invbasdoc(pk_invbasdoc);
                    goods.setPk_invmandoc(pk_invmandoc);
                    goods.setNumber(jsonBody.getString("nnumber").toString());
                    goods.setCadvisecalbodyid(jsonBody.getString("cadvisecalbodyid").toString());
                    goods.setCinvbasdocid(jsonBody.getString("cinvbasdocid").toString());
                    goods.setCinventoryid(jsonBody.getString("cinventoryid").toString());
                    goods.setCorder_bid(jsonBody.getString("corder_bid").toString());
                    goods.setCrowno(jsonBody.getString("crowno").toString());
                    goods.setCsaleid(jsonBody.getString("csaleid").toString());
                    goods.setPk_corp(jsonBody.getString("pk_corp").toString());
                    goods.setVreceiveaddress(jsonBody.getString("vreceiveaddress").toString());
                    ovList.add(goods);
                    Log.d(TAG, "addDataToOvList: "+ovList.size());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    }


    /**
     * ������е�Edtext
     */
    private void changeAllEdTextToEmpty() {
        txtSaleNumber.setText("");
        txtBarcode.setText("");
        txtSaleInvCode.setText("");
        txtSaleInvName.setText("");
        txtSaleType.setText("");
        txtSaleUnit.setText("");
        txtSaleBatch.setText("");
        txtSaleTotal.setText("");
        txtSaleWeight.setText("");
        txtSaleSpec.setText("");
        txtSaleNumber.setEnabled(false);
        txtSaleWeight.setEnabled(false);
        txtSaleTotal.setEnabled(false);
    }



    /**
     * mEdBarCode�����룩�ļ���
     */
    private class CustomTextWatcher implements TextWatcher {
        EditText ed;

        public CustomTextWatcher(EditText ed) {
            this.ed = ed;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (ed.getId()) {
                case R.id.ed_bar_code:
                    if (TextUtils.isEmpty(txtBarcode.getText().toString())) {
                        txtSaleNumber.setText("");
                        txtSaleInvCode.setText("");
                        txtSaleInvName.setText("");
                        txtSaleType.setText("");
                        txtSaleUnit.setText("");
                        txtSaleBatch.setText("");
                        txtSaleTotal.setText("");
                        txtSaleWeight.setText("");
                        txtSaleSpec.setText("");
                    }
                    break;
                case R.id.txtSaleNumber:
                    if (TextUtils.isEmpty(txtSaleNumber.getText())) {
                        txtSaleTotal.setText("");
                        return;
                    }
                    if (!isNumber(txtSaleNumber.getText().toString())) {
                        Utils.showToast(activity, "��������ȷ");
                        txtSaleNumber.setText("");
                        return;
                    }
                    if (Float.valueOf(txtSaleNumber.getText().toString()) < 0) {
                        Utils.showToast(activity, "��������ȷ");
                        return;
                    }
                    float num = Float.valueOf(txtSaleNumber.getText().toString());
                    float weight = Float.valueOf(txtSaleWeight.getText().toString());
                    txtSaleTotal.setText(formatDecimal(num * weight));
                    break;
            }
        }
    }

    /**
     * �س����ĵ���¼�
     */
    View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.txtBarcode:
                        if (!TextUtils.isEmpty(txtBarcode.getText().toString())) {
                            if (isAllEdNotNull()) {
                                txtBarcode.requestFocus();  //�����ӳɹ����ܱ����������롱��
                                changeAllEdTextToEmpty();
                            } else {
                                barAnalysis();
                            }
                        } else {
                            Utils.showToast(activity, "����������");
                        }
                        return true;
                    case R.id.txtSaleNumber:
                        if (TextUtils.isEmpty(txtSaleNumber.getText().toString())) {
                            Utils.showToast(activity, "����������");
                            return true;
                        }
                        if (!isNumber(txtSaleNumber.getText().toString())) {
                            Utils.showToast(activity, "��������ȷ");
                            return true;
                        }
                        //������Ҫ���� �ж��ٰ����������������
                        float num = Float.valueOf(txtSaleNumber.getText().toString());
                        if (num < 0) {
                            Utils.showToast(activity, "��������ȷ");
                            return true;
                        }

                        float weight = Float.valueOf(txtSaleWeight.getText().toString());
                        txtSaleTotal.setText(formatDecimal(num * weight));
                            addDataToDetailList();
                            txtBarcode.requestFocus();  //�����ӳɹ����ܱ����������롱��
                            changeAllEdTextToEmpty();

                        return true;
                }
            }
            return false;
        }
    };
}
