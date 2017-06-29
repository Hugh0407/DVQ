package com.techscan.dvq;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.content.ContentValues.TAG;


/**
 * Created by Hugh on 2017/6/28.
 */

public class SaleChooseTime extends Activity {
    @InjectView(R.id.et_BeginDate)
    EditText txtBeginDate;
    @InjectView(R.id.et_EndDate)
    EditText txtEndDate;
    @InjectView(R.id.bt_Search)
    Button btSearch;
    @InjectView(R.id.et_BillCode)
    EditText txtBillCode;

    int year;
    int month;
    int day;
    Calendar mycalendar;
    String sEndDate = "";
    String sBillCodes = "";
    String sBeginDate = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_time);
        this.setTitle("���ݲ�ѯ");
        ButterKnife.inject(this);
        initView();

    }
    @OnClick({R.id.et_BillCode, R.id.et_BeginDate, R.id.et_EndDate,R.id.bt_Search})
    public void onViewClicked(View view) {
        switch(view.getId()){
            case R.id.et_BeginDate:
                year = mycalendar.get(Calendar.YEAR); //��ȡCalendar�����е���
                month = mycalendar.get(Calendar.MONTH);//��ȡCalendar�����е���
                day = mycalendar.get(Calendar.DAY_OF_MONTH);//��ȡ����µĵڼ���
                DatePickerDialog dpd = new DatePickerDialog(SaleChooseTime.this, Datelister_s, year, month, day);
                dpd.show();//��ʾDatePickerDialog���
                break;
            case R.id.et_EndDate:
                year = mycalendar.get(Calendar.YEAR); //��ȡCalendar�����е���
                month = mycalendar.get(Calendar.MONTH);//��ȡCalendar�����е���
                day = mycalendar.get(Calendar.DAY_OF_MONTH);//��ȡ����µĵڼ���
                DatePickerDialog dpds = new DatePickerDialog(SaleChooseTime.this, Datelistener, year, month, day);
                dpds.show();//��ʾDatePickerDialog���
                break;
            case R.id.bt_Search:
                Intent in = this.getIntent();
                sBillCodes  = txtBillCode.getText().toString();
                sBeginDate =  txtBeginDate.getText().toString();
                sEndDate = txtEndDate.getText().toString();
                Log.d(TAG, "onActivityResult: "+sBillCodes);
                Log.d(TAG, "onActivityResult: "+sBeginDate);
                Log.d(TAG, "onActivityResult: "+sEndDate);
                in.putExtra("sBillCodes",sBillCodes);
                in.putExtra("sBeginDate",sBeginDate);
                in.putExtra("sEndDate",sEndDate);
                SaleChooseTime.this.setResult(4,in);
                finish();
                break;
            default:
                break;

        }
    }

    /**
     * ���ݿ�ʼ
     */

    private  DatePickerDialog.OnDateSetListener Datelister_s = new DatePickerDialog.OnDateSetListener() {
        String mo="";
        /**params��view�����¼����������
         * params��myyear����ǰѡ�����
         * params��monthOfYear����ǰѡ�����
         * params��dayOfMonth����ǰѡ�����
         */
        @Override
        public void onDateSet(DatePicker view, int myear, int monthOfYear, int dayOfMonth) {
            //�޸�year��month��day�ı���ֵ���Ա��Ժ󵥻���ťʱ��DatePickerDialog����ʾ��һ���޸ĺ��ֵ
            year = myear;
            month = monthOfYear;

            if (month<10){
                mo = "0"+(month+1);
            }else{
                mo = (month+1)+"";
            }
            day = dayOfMonth;
            updateDates();
            txtEndDate.requestFocus();

        }

        //��DatePickerDialog�ر�ʱ������������ʾ
        private void updateDates() {
            //��TextView����ʾ����
            txtBeginDate.setText(year + "-" + mo+ "-" + day);
        }
    };

    /**
     * �������ڽ���
     */

    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
        String mo;
        /**params��view�����¼����������
         * params��myyear����ǰѡ�����
         * params��monthOfYear����ǰѡ�����
         * params��dayOfMonth����ǰѡ�����
         */
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {
            //�޸�year��month��day�ı���ֵ���Ա��Ժ󵥻���ťʱ��DatePickerDialog����ʾ��һ���޸ĺ��ֵ
            if (month<10){
                mo = "0"+(month+1);
            }else{
                mo = (month+1)+"";
            }
            year = myyear;
            month = monthOfYear;
            day = dayOfMonth;
            updateDate();

        }

        //��DatePickerDialog�ر�ʱ������������ʾ
        private void updateDate() {
            //��TextView����ʾ����
            txtEndDate.setText(year + "-" + mo + "-" + day);
        }

    };


    private View.OnFocusChangeListener myFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpd = new DatePickerDialog(SaleChooseTime.this, Datelistener, year, month, day);
                dpd.show();//��ʾDatePickerDialog���
            }
        }
    };

    private View.OnFocusChangeListener myFocusListeners = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpds = new DatePickerDialog(SaleChooseTime.this, Datelister_s, year, month, day);
                dpds.show();//��ʾDatePickerDialog���
            }
        }
    };

/**
 * �س��¼�
 */

    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
             if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()){
                    case  R.id.et_BillCode:
                        Log.d(TAG, "onKey: "+"kkk");
                        txtBeginDate.requestFocus();
                        return true;
//                    case  R.id.et_BeginDate:
//                        txtEndDate.requestFocus();
//                        return true;
                }
             }
            return false;
        }
    };


    /**
     * ��ʼ������
     * ��ʼ������
     */
    private void initView(){
          mycalendar = Calendar.getInstance();//��ʼ��Calendar��������
          year = mycalendar.get(Calendar.YEAR); //��ȡCalendar�����е���
          month = mycalendar.get(Calendar.MONTH);//��ȡCalendar�����е���
          day = mycalendar.get(Calendar.DAY_OF_MONTH);//��ȡ����µĵڼ���
          txtBeginDate.setOnFocusChangeListener(myFocusListeners);
          txtBeginDate.setOnKeyListener(mOnKeyListener);
          txtEndDate.setOnFocusChangeListener(myFocusListener);
          txtEndDate.setOnKeyListener(mOnKeyListener);

           }


}
