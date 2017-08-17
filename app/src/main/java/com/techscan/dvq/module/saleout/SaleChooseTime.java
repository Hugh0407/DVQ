package com.techscan.dvq.module.saleout;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.techscan.dvq.DateCompare;
import com.techscan.dvq.R;
import com.techscan.dvq.login.MainLogin;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by Hugh on 2017/6/28.
 */

public class SaleChooseTime extends Activity {
    @Nullable
    @InjectView(R.id.et_BeginDate)
    EditText txtBeginDate;
    @Nullable
    @InjectView(R.id.et_EndDate)
    EditText txtEndDate;
    @Nullable
    @InjectView(R.id.bt_Search)
    Button btSearch;
    @Nullable
    @InjectView(R.id.et_BillCode)
    EditText txtBillCode;

    int year;
    int month;
    int day;
    int year_c;
    int month_c;
    int day_c;

    Calendar mycalendar;
    @NonNull
    String sEndDate   = "";
    @NonNull
    String sBillCodes = "";
    @NonNull
    String sBeginDate = "";

    String months;
    String dayys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_time);
        this.setTitle("���ݲ�ѯ");
        ButterKnife.inject(this);
        initView();

    }
    @OnClick({R.id.et_BillCode, R.id.et_BeginDate, R.id.et_EndDate,R.id.bt_Search})
    public void onViewClicked(@NonNull View view) {
        switch(view.getId()){
            case R.id.et_BeginDate:
                year_c = mycalendar.get(Calendar.YEAR); //��ȡCalendar�����е���
                month_c = mycalendar.get(Calendar.MONTH);//��ȡCalendar�����е���
                day_c = mycalendar.get(Calendar.DAY_OF_MONTH);//��ȡ����µĵڼ���
                DatePickerDialog dpd = new DatePickerDialog(SaleChooseTime.this, Datelister_s, year_c, month_c, day_c);
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
                String beginDate = txtBeginDate.getText().toString();
                String endDate  = txtEndDate.getText().toString();
                DateCompare dateCompare = new DateCompare();
                if ((beginDate.equals("")||beginDate==null)&&(endDate.equals("")||endDate==null)){
                    Intent in = this.getIntent();
                    sBillCodes  = txtBillCode.getText().toString();
                    sBeginDate =  txtBeginDate.getText().toString();
                    sEndDate = txtEndDate.getText().toString();
                    in.putExtra("sBillCodes",sBillCodes);
                    in.putExtra("sBeginDate",sBeginDate);
                    in.putExtra("sEndDate",sEndDate);
                    SaleChooseTime.this.setResult(4,in);
                    finish();
                }
                else
                {
                if(dateCompare.timeCompare(beginDate,endDate)){
                    Intent in = this.getIntent();
                    sBillCodes  = txtBillCode.getText().toString();
                    sBeginDate =  txtBeginDate.getText().toString();
                    sEndDate = txtEndDate.getText().toString();
                    in.putExtra("sBillCodes",sBillCodes);
                    in.putExtra("sBeginDate",sBeginDate);
                    in.putExtra("sEndDate",sEndDate);
                    SaleChooseTime.this.setResult(4,in);
                    finish();
                }else{
                    Toast.makeText(this, R.string.dateCompare,Toast.LENGTH_SHORT).show();
                }
                }
                break;
            default:
                break;

        }
    }



    /**
     * ���ݿ�ʼ
     */

    @NonNull
    private DatePickerDialog.OnDateSetListener Datelister_s = new DatePickerDialog.OnDateSetListener() {
        @NonNull
        String mo="";
        @NonNull
        String days="";
        /**params��view�����¼����������
         * params��myyear����ǰѡ�����
         * params��monthOfYear����ǰѡ�����
         * params��dayOfMonth����ǰѡ�����
         */
        @Override
        public void onDateSet(DatePicker view, int myear, int monthOfYear, int dayOfMonth) {
            //�޸�year��month��day�ı���ֵ���Ա��Ժ󵥻���ťʱ��DatePickerDialog����ʾ��һ���޸ĺ��ֵ
            year_c = myear;
            month_c = monthOfYear;
            if (month_c<9){
                mo = "0"+(month_c+1);
            }else{
                mo = (month_c+1)+"";
            }
            day_c = dayOfMonth;
            if (day_c<10){
                days = "0"+(day_c);
            }else{
                days = day_c +"";
            }

            updateDates();


        }

        //��DatePickerDialog�ر�ʱ������������ʾ
        private void updateDates() {
            //��TextView����ʾ����
            txtBeginDate.setText(year_c + "-" + mo+ "-" + days);
        }
    };

    /**
     * �������ڽ���
     */

    @NonNull
    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
        @NonNull
        String mo="";
        @NonNull
        String days="";
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
            if (month<9){
                mo = "0"+(month+1);
            }else{
                mo = (month+1)+"";
            }
            day = dayOfMonth;
            if (day<10){
                days = "0"+(day);
            }else{
                days = day+"";
            }
            updateDate();

        }

        //��DatePickerDialog�ر�ʱ������������ʾ
        private void updateDate() {
            //��TextView����ʾ����
            txtEndDate.setText(year + "-" + mo + "-" + days);
        }

    };


    @NonNull
    private View.OnFocusChangeListener myFocusListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpd = new DatePickerDialog(SaleChooseTime.this, Datelistener, year, month, day);
                dpd.show();//��ʾDatePickerDialog���
            }
        }
    };

    @NonNull
    private View.OnFocusChangeListener myFocusListeners = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                DatePickerDialog dpds = new DatePickerDialog(SaleChooseTime.this, Datelister_s, year_c, month_c, day_c);
                dpds.show();//��ʾDatePickerDialog���
            }
        }
    };

/**
 * �س��¼�
 */

@NonNull
private View.OnKeyListener mOnKeyListener = new View.OnKeyListener(){
        @Override
        public boolean onKey(@NonNull View v, int keyCode, @NonNull KeyEvent event) {
             if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                switch (v.getId()){
                    case  R.id.et_BillCode:
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
          year_c = mycalendar.get(Calendar.YEAR); //��ȡCalendar�����е���
          month_c = mycalendar.get(Calendar.MONTH);//��ȡCalendar�����е���
          day_c = mycalendar.get(Calendar.DAY_OF_MONTH);//��ȡ����µĵڼ���
          txtBeginDate.setOnFocusChangeListener(myFocusListeners);
          txtEndDate.setOnFocusChangeListener(myFocusListener);
          txtBillCode.setOnKeyListener(mOnKeyListener);
          txtBeginDate.setOnKeyListener(mOnKeyListener);
//        if (month<9){
//            months = "0"+(month+1);
//        }else{
//            months = (month+1)+"";
//        }
//        if (day<10){
//            dayys = "0"+(day);
//        }else{
//            dayys = day+"";
//        }
//          txtBeginDate.setText((year+"")+"-"+months+"-"+dayys);
//          txtEndDate.setText((year+"")+"-"+months+"-"+dayys);
          txtBeginDate.setText(MainLogin.appTime);
          txtEndDate.setText(MainLogin.appTime);

    }


}
