package com.techscan.dvq;

import android.os.Handler;

import com.techscan.dvq.common.RequestThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Xuhu on 2017/7/8.
 */

public class GetSaleBaseInfo {

    public HashMap<String,Object> mapSaleBaseInfo = null;
    String InvCode = "";
    String Batch = "";

    /**
     * ��ȡ���������Ϣ
     *
     *
     */
    public GetSaleBaseInfo(SplitBarcode cSplitBarcode, Handler mHandler,String PK_CORP) {
        //�ж�invcode�Ƿ��ж��š�����ж��žͷָ�ö��ź����invcode���������ԭ����invcode��
        InvCode = cSplitBarcode.cInvCode;
        if (InvCode.contains(",")){
            String[] invCodeArray = InvCode.split("\\,");
            InvCode = invCodeArray[1];
        }else{
            InvCode = cSplitBarcode.cInvCode;
        }
        //�ж�batch�Ƿ��ж��š�����ж��žͷָ�ö��ź����batch���������ԭ����batch
        Batch = cSplitBarcode.cBatch;
        if (Batch.contains(",")){
            String[] batchArray = Batch.split("\\,");
            Batch = batchArray[1];
        }else{
            Batch = cSplitBarcode.cBatch;
        }
        mapSaleBaseInfo = new HashMap<String, Object>();
        mapSaleBaseInfo.put("barcodetype",cSplitBarcode.BarcodeType);
        mapSaleBaseInfo.put("batch",Batch);
        mapSaleBaseInfo.put("serino",cSplitBarcode.cSerino);
        mapSaleBaseInfo.put("quantity",cSplitBarcode.dQuantity);
        mapSaleBaseInfo.put("number",cSplitBarcode.iNumber);
        mapSaleBaseInfo.put("cwflag",cSplitBarcode.CWFlag);
        mapSaleBaseInfo.put("onlyflag",cSplitBarcode.OnlyFlag);
        mapSaleBaseInfo.put("taxflag",cSplitBarcode.TaxFlag);
        mapSaleBaseInfo.put("outsourcing",cSplitBarcode.Outsourcing);
        mapSaleBaseInfo.put("barcode",cSplitBarcode.FinishBarCode);
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", PK_CORP);
        parameter.put("InvCode", InvCode);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread td = new Thread(requestThread);
        td.start();
    }

    /**
     * ͨ����ȡ����json �����õ�������Ϣ,�����õ�UI��
     *
     * @param json
     * @throws JSONException
     */
    String pk_invbasdoc = "";
    String pk_invmandoc = "";

    public void SetSaleBaseToParam(JSONObject json) throws JSONException {
        //Log.d(TAG, "SetInvBaseToUI: " + json);
        if (json.getBoolean("Status")) {
            JSONArray val = json.getJSONArray("baseInfo");
            //mapInvBaseInfo = null;
            for (int i = 0; i < val.length(); i++) {
                JSONObject tempJso = val.getJSONObject(i);
                //mapInvBaseInfo = new HashMap<String, Object>();
                mapSaleBaseInfo.put("invname", tempJso.getString("invname"));   //��������
                mapSaleBaseInfo.put("invcode", tempJso.getString("invcode"));   //���Ϻ�
                mapSaleBaseInfo.put("measname", tempJso.getString("measname"));   //��λ
                mapSaleBaseInfo.put("pk_invbasdoc", tempJso.getString("pk_invbasdoc"));//����bas PK
                //pk_invbasdoc = tempJso.getString("pk_invbasdoc");
                mapSaleBaseInfo.put("pk_invmandoc", tempJso.getString("pk_invmandoc"));//����man PK
                //pk_invmandoc = tempJso.getString("pk_invmandoc");
                mapSaleBaseInfo.put("invtype", tempJso.getString("invtype"));   //�ͺ�
                mapSaleBaseInfo.put("invspec", tempJso.getString("invspec"));   //���
            }

        }
    }

}
