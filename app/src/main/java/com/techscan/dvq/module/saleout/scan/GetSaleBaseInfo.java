package com.techscan.dvq.module.saleout.scan;

import android.os.Handler;
import android.support.annotation.Nullable;

import com.techscan.dvq.common.RequestThread;
import com.techscan.dvq.common.SplitBarcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Xuhu on 2017/7/8.
 */

public class GetSaleBaseInfo {

    @Nullable
    public HashMap<String, Object> mapSaleBaseInfo = null;
    String InvCode = "";
    Double a=0.0;
    Double b=0.0;

    /**
     * ��ȡ���������Ϣ
     */
    public GetSaleBaseInfo(SplitBarcode cSplitBarcode, Handler mHandler, String PK_CORP) {
        //�ж�invcode�Ƿ��ж��š�����ж��žͷָ�ö��ź����invcode���������ԭ����invcode��
        InvCode = cSplitBarcode.cInvCode;
        if (InvCode.contains(",")) {
            String[] incCodeArray = InvCode.split("\\,");
            InvCode = incCodeArray[1];
        } else {
            InvCode = cSplitBarcode.cInvCode;
        }
        mapSaleBaseInfo = new HashMap<String, Object>();
        mapSaleBaseInfo.put("barcodetype", cSplitBarcode.BarcodeType);
        mapSaleBaseInfo.put("batch", cSplitBarcode.cBatch);
        mapSaleBaseInfo.put("serino", cSplitBarcode.cSerino);
        mapSaleBaseInfo.put("quantity", cSplitBarcode.dQuantity);
        mapSaleBaseInfo.put("number", cSplitBarcode.iNumber);
        a = Double.valueOf(cSplitBarcode.dQuantity);
        b = Double.valueOf(cSplitBarcode.iNumber);
        mapSaleBaseInfo.put("barqty", a*b);
        mapSaleBaseInfo.put("cwflag", cSplitBarcode.CWFlag);
        mapSaleBaseInfo.put("onlyflag", cSplitBarcode.OnlyFlag);
        mapSaleBaseInfo.put("taxflag", cSplitBarcode.TaxFlag);
        mapSaleBaseInfo.put("outsourcing", cSplitBarcode.Outsourcing);
        mapSaleBaseInfo.put("barcode", cSplitBarcode.FinishBarCode);
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", PK_CORP);
        parameter.put("BarCode","");
        parameter.put("InvCode", InvCode);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 1);
        Thread        td            = new Thread(requestThread);
        td.start();
    }

    public GetSaleBaseInfo(SplitBarcode cSplitBarcode, Handler mHandler, String PK_CORP,String barcode) {
        //�ж�invcode�Ƿ��ж��š�����ж��žͷָ�ö��ź����invcode���������ԭ����invcode��
        InvCode = cSplitBarcode.cInvCode;
        if (InvCode.contains(",")) {
            String[] incCodeArray = InvCode.split("\\,");
            InvCode = incCodeArray[1];
        } else {
            InvCode = cSplitBarcode.cInvCode;
        }
        mapSaleBaseInfo = new HashMap<String, Object>();
        mapSaleBaseInfo.put("barcodetype", cSplitBarcode.BarcodeType);
        mapSaleBaseInfo.put("batch", cSplitBarcode.cBatch);
        mapSaleBaseInfo.put("serino", cSplitBarcode.cSerino);
        mapSaleBaseInfo.put("quantity", cSplitBarcode.dQuantity);
        mapSaleBaseInfo.put("number", cSplitBarcode.iNumber);
        a = Double.valueOf(cSplitBarcode.dQuantity);
        b = Double.valueOf(cSplitBarcode.iNumber);
        mapSaleBaseInfo.put("barqty", a*b);
        mapSaleBaseInfo.put("cwflag", cSplitBarcode.CWFlag);
        mapSaleBaseInfo.put("onlyflag", cSplitBarcode.OnlyFlag);
        mapSaleBaseInfo.put("taxflag", cSplitBarcode.TaxFlag);
        mapSaleBaseInfo.put("outsourcing", cSplitBarcode.Outsourcing);
        mapSaleBaseInfo.put("barcode", cSplitBarcode.FinishBarCode);
        HashMap<String, String> parameter = new HashMap<String, String>();
        parameter.put("FunctionName", "GetInvBaseInfo");
        parameter.put("CompanyCode", PK_CORP);
        parameter.put("BarCode",barcode);
        parameter.put("InvCode", InvCode);
        parameter.put("TableName", "baseInfo");
        RequestThread requestThread = new RequestThread(parameter, mHandler, 3);
        Thread        td            = new Thread(requestThread);
        td.start();
    }


    /**
     * ͨ����ȡ����json �����õ�������Ϣ,�����õ�UI��
     *
     * @param json
     * @throws JSONException
     */

    public void SetSaleBaseToParam(JSONObject json) throws JSONException {
        if (json.getBoolean("Status")) {
            JSONArray val = json.getJSONArray("baseInfo");
            for (int i = 0; i < val.length(); i++) {
                JSONObject tempJso = val.getJSONObject(i);
                mapSaleBaseInfo.put("invname", tempJso.getString("invname"));   //��������
                mapSaleBaseInfo.put("invcode", tempJso.getString("invcode"));   //���Ϻ�
                mapSaleBaseInfo.put("measname", tempJso.getString("measname"));   //��λ
                mapSaleBaseInfo.put("pk_invbasdoc", tempJso.getString("pk_invbasdoc"));//����bas PK
                mapSaleBaseInfo.put("pk_invmandoc", tempJso.getString("pk_invmandoc"));//����man PK
                mapSaleBaseInfo.put("invtype", tempJso.getString("invtype"));   //�ͺ�
                mapSaleBaseInfo.put("invspec", tempJso.getString("invspec"));   //���
                mapSaleBaseInfo.put("currentweight", tempJso.getString("currentweight"));   //���
//                mapSaleBaseInfo.put("vfree5", tempJso.getString("vfree5"));   //���
            }

        }
    }


}
