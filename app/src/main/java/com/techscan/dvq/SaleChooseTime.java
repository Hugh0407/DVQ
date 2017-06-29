package com.techscan.dvq;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;


import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * Created by Hugh on 2017/6/28.
 */

public class SaleChooseTime extends Activity {
    @InjectView(R.id.txtChooseTimeBegin)
    EditText txtChooseTimeBegin;
    @InjectView(R.id.txtChooseTimeEnd)
    EditText txtChooseTimeEnd;
    @InjectView(R.id.bt_Confirm)
    Button bt_Confirm;

    int year;
    int month;
    int day;
    Calendar mycalendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_time);
        ButterKnife.inject(this);
        mycalendar = Calendar.getInstance();//��ʼ��Calendar��������
        year = mycalendar.get(Calendar.YEAR); //��ȡCalendar�����е���
        month = mycalendar.get(Calendar.MONTH);//��ȡCalendar�����е���
        day = mycalendar.get(Calendar.DAY_OF_MONTH);//��ȡ����µĵڼ���

        txtChooseTimeBegin.setOnFocusChangeListener(myFocusListener);
    }
    @OnClick({R.id.txtChooseTimeBegin,R.id.txtChooseTimeEnd,R.id.bt_Confirm})
    public void onViewClicked(View view) {
        switch(view.getId()){
            case  R.id.txtChooseTimeBegin:
                year = mycalendar.get(Calendar.YEAR); //��ȡCalendar�����е���
                month = mycalendar.get(Calendar.MONTH);//��ȡCalendar�����е���
                day = mycalendar.get(Calendar.DAY_OF_MONTH);//��ȡ����µĵڼ���
                DatePickerDialog dpd = new DatePickerDialog(SaleChooseTime.this, Datelistener, year, month, day);
                dpd.show();//��ʾDatePickerDialog���
                break;
            case R.id.txtChooseTimeEnd:
                year = mycalendar.get(Calendar.YEAR); //��ȡCalendar�����е���
                month = mycalendar.get(Calendar.MONTH);//��ȡCalendar�����е���
                day = mycalendar.get(Calendar.DAY_OF_MONTH);//��ȡ����µĵڼ���
                DatePickerDialog dpds = new DatePickerDialog(SaleChooseTime.this, Datelistener, year, month, day);
                dpds.show();//��ʾDatePickerDialog���
                break;

            default:
                break;

        }
    }
    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
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
            txtChooseTimeBegin.setText(year + "-" + (month + 1) + "-" + day);
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

}
