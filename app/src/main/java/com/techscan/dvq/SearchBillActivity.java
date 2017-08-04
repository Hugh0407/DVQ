package com.techscan.dvq;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.techscan.dvq.R.id;
import com.techscan.dvq.login.MainLogin;

import org.apache.http.util.EncodingUtils;

import java.io.FileInputStream;
import java.util.Calendar;

public class SearchBillActivity extends Activity {
    @NonNull
    private              String      sDate               = "";
    @Nullable
    private              TextView    tvSearchBillDate1   = null;
    @Nullable
    private              ImageButton btSearchBillDate    = null;
    @Nullable
    private              TextView    tvSearchBillType1   = null;
    @Nullable
    private              ImageButton btSearchBillType    = null;
    @Nullable
    private              Button      btSearchSavedBill   = null;
    @Nullable
                         Button      btnSearchBillReturn = null;
    private static final int         DATE_DIALOG_ID      = 1;
    private static final int         SHOW_DATAPICK       = 0;
    private int mYear;  
    private int mMonth;  
    private int mDay;
    @Nullable
    private AlertDialog   SelectButton     =null;
    @Nullable
    private String[]      BillTypeNameList = null;
    @Nullable
    private String[]      BillTypeCodeList = null;
    @NonNull
    private ButtonOnClick buttonOnClick    = new ButtonOnClick(0);
    @Nullable
    private TextView      txtSavedInfo     = null;
    int BillTypeIndex = -1;
    @NonNull
    String     BillTypeCode = "";
    @Nullable
    ScrollView svSavedBill  =null;
    
//	private SoundPool sp;//����һ��SoundPool
//	private int MainLogin.music;//����һ��int������suondID
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_bill);
		
		this.setTitle("���ݲ�ѯ");
	  
		   tvSearchBillDate1 = (TextView) findViewById(R.id.tvBillDateTXT); 
		   btSearchBillDate = (ImageButton) findViewById(R.id.btBillDate);  
		   tvSearchBillType1 = (TextView) findViewById(R.id.tvBillTypeTXT); 
		   btSearchBillType = (ImageButton) findViewById(R.id.btBillType); 
		   btSearchSavedBill = (Button) findViewById(R.id.SearchBillSaved); 
		   txtSavedInfo = (TextView) findViewById(R.id.txtSavedInfo); 
		   svSavedBill = (ScrollView) findViewById(R.id.svSavedBill); 
		   btnSearchBillReturn = (Button)findViewById(R.id.SearchBillReturn);
		   btnSearchBillReturn .setOnClickListener(new OnClickListener());
	       btSearchBillDate.setOnClickListener(new OnClickListener());  
	       btSearchSavedBill.setOnClickListener(new OnClickListener());
	       btSearchBillType.setOnClickListener(new OnClickListener()); 
	       
			//ADD CAIXY START
//			sp= new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);//��һ������Ϊͬʱ���������������������ڶ����������ͣ�����Ϊ��������
//			MainLogin.music = MainLogin.sp.load(this, R.raw.xxx, 1); //����������زķŵ�res/raw���2��������Ϊ��Դ�ļ�����3��Ϊ���ֵ����ȼ�
			//ADD CAIXY END
			
			txtSavedInfo.setText("");

	       final Calendar c = Calendar.getInstance();  
	  
	       mYear = c.get(Calendar.YEAR);  
	  
	       mMonth = c.get(Calendar.MONTH);  
	  
	       mDay = c.get(Calendar.DAY_OF_MONTH);  
	  
	       setDateTime();  
	       SetBillType();//���õ��������б�
	       
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search_bill, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


  
    private void setDateTime() {  
  
       final Calendar c = Calendar.getInstance();  
       mYear = c.get(Calendar.YEAR);  
       mMonth = c.get(Calendar.MONTH);  
       mDay = c.get(Calendar.DAY_OF_MONTH); 
       updateDisplay();  
  
    }  
    private class ButtonOnClick implements DialogInterface.OnClickListener
	{
		public int index;

		public ButtonOnClick(int index)
		{
			this.index = index;
		}

		@Override
		public void onClick(@NonNull DialogInterface dialog, int whichButton)
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
			if(dialog.equals(SelectButton))
			{
				tvSearchBillType1.setText(BillTypeNameList[index].toString());
				BillTypeIndex = index;
			}
		}
	}
   
  
  
    private void updateDisplay() {  
    	
    	final Calendar c = Calendar.getInstance();  
  
       	StringBuilder YYYY = new StringBuilder().append(mYear);
       	
       	StringBuilder MM = new StringBuilder().append(  
                (mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1));
       	
       	StringBuilder DD = new StringBuilder().append(
                (mDay < 10) ? "0" + mDay : mDay);
    	
       	sDate = YYYY.toString() + "-" + MM.toString() + "-" + DD.toString();
       	
       	tvSearchBillDate1.setText(sDate);
    }



    @NonNull
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
  
       public void onDateSet(DatePicker view, int year, int monthOfYear,  
              int dayOfMonth) {  
           mYear = year;  
           mMonth = monthOfYear;  
           mDay = dayOfMonth;  
           updateDisplay();  
       }  
    };  
    
	private void SearchSavedBill()
	{   

		if (BillTypeIndex<0)
		{
			Toast.makeText(this, R.string.QingXuanZeDanJuLeiXing, Toast.LENGTH_LONG).show();
			//ADD CAIXY TEST START
			MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
			txtSavedInfo.setText("");
			//ADD CAIXY TEST END
		}
		else
		{
	    	String UserID = MainLogin.objLog.UserID;
			BillTypeCode = BillTypeCodeList[BillTypeIndex].toString();
	    	//4Y0001AQ1000000002OLMK2015-05-19
	    	//sDate
			String LogName  = BillTypeCode + UserID + sDate;
			
			String fileName = "/sdcard/DVQ/"+LogName+".txt";
			
			//4Y0001AQ1000000002OLMK2015-05-19.txt
			
			String res = ""; 
			 
	        try { 
	 
	            FileInputStream fin = new FileInputStream(fileName); 
	 
	            int length = fin.available(); 
	 
	            byte[] buffer = new byte[length]; 
	 
	            fin.read(buffer); 
	 
	            res = EncodingUtils.getString(buffer, "UTF-8"); 
	 
	            fin.close(); 
	            
	            txtSavedInfo.setText(res);
	 
	        } 
	 
	        catch (Exception e) { 
	 
	            e.printStackTrace(); 
				Toast.makeText(this, R.string.WeiChaXunDaoBaoCunJiLu, Toast.LENGTH_LONG).show();
				//ADD CAIXY TEST START
				MainLogin.sp.play(MainLogin.music, 1, 1, 0, 0, 1);
				txtSavedInfo.setText("");
				//ADD CAIXY TEST END
	        } 
			
		}
		
	}
    
	private void showSingleChoiceDialog()
	{   

		SelectButton=new AlertDialog.Builder(this).setTitle(R.string.QingXuanZeDanJuLeiXing).setSingleChoiceItems(
				BillTypeNameList, -1, buttonOnClick).setNegativeButton(R.string.QuXiao, buttonOnClick).show();
	}
	
	private void SetBillType()
	{
		BillTypeNameList =new String[11];//���õ�����������
		//��ʼ���õ�����������
		BillTypeNameList[0]="��λ������";
		BillTypeNameList[1]="�������ⵥ";
		BillTypeNameList[2]="������ⵥ";
		BillTypeNameList[3]="�ɹ���ⵥ";
		BillTypeNameList[4]="�ɹ����쵥";
		BillTypeNameList[5]="���۳��ⵥ";
		BillTypeNameList[6]="չƷ�زֵ�";
		BillTypeNameList[7]="�����̵㵥";
		
		BillTypeNameList[8]="�顡װ����";
		BillTypeNameList[9]="��ж����";
		BillTypeNameList[10]="��̬ת����";
		
		BillTypeCodeList =new String[11];//���õ�����������
		//��ʼ���õ������ͱ���
		BillTypeCodeList[0]="4Q";
		BillTypeCodeList[1]="4Y"; 
		BillTypeCodeList[2]="4E";
		BillTypeCodeList[3]="45";
		BillTypeCodeList[4]="5X";
		BillTypeCodeList[5]="4C";
		BillTypeCodeList[6]="TB";
		BillTypeCodeList[7]="ST";
		BillTypeCodeList[8]="4L";
		BillTypeCodeList[9]="4M";
		BillTypeCodeList[10]="4N";

	}
  
  
    class OnClickListener implements  
  
           android.view.View.OnClickListener {  
  
       @Override  
  
       public void onClick(@NonNull View v) {
    	   
   			switch(v.getId())
   			{
   				case id.btBillDate:
   					Message msg = new Message();  
   					msg.what = SearchBillActivity.SHOW_DATAPICK;  
   					SearchBillActivity.this.saleHandler.sendMessage(msg); 
   					break;
   				case id.btBillType:
   					showSingleChoiceDialog();
   					break;
   					
   				case id.SearchBillSaved:
   					SearchSavedBill();
   					break;
   				case id.SearchBillReturn:
					finish();					
					break;
   			}
   		}
    }  
  
   
  
    @Nullable
    @Override
  
    protected Dialog onCreateDialog(int id) {  
  
       switch (id) {  
  
       case DATE_DIALOG_ID:  
           return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,mDay); 
       }  
  
       return null;  
  
    }  
  
   
  
    @Override  
  
    protected void onPrepareDialog(int id, @NonNull Dialog dialog) {
  
       switch (id) {  
  
       case DATE_DIALOG_ID:  
  
           ((DatePickerDialog) dialog).updateDate(mYear, mMonth, mDay);  
  
           break;  
  
       }  
  
    }


    @NonNull
    Handler saleHandler = new Handler() {
  
       public void handleMessage(@NonNull Message msg) {
             switch (msg.what) {    
             case SearchBillActivity.SHOW_DATAPICK:    
            	 showDialog(DATE_DIALOG_ID);    
            	 break; 
             }    
       }    
	};  
}
