package com.techscan.dvq;

import android.util.Log;

import org.apache.http.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Inventory {
    private String m_InvCode;
    private String m_InvName;
    private String m_Batch;
    private String m_Serino;
    private String m_Invbasdoc;//
    private String m_Invmandoc;
    private String invspec;
    private String invtype;
    private String m_vFree1;

    private String m_currentID;

    private String m_totalID;
    private String m_Errmsg;

    private String m_AccID;

    /**
     * ��ȡ���׺�
     */
    public String AccID() {
        return m_AccID;
    }

    /**
     * �������׺�
     */
    public void SetAccID(String value) {
        m_AccID = value;
    }

    public String Invbasdoc() {
        return m_Invbasdoc;
    }

    public String Invtype(){return invtype;}
    public String Invspec(){return invspec;}

    public String vFree1() {
        //m_vFree1 = "�����";
        return m_vFree1;
    }

    /**
     * ���������еĵ�ǰ�ְ�ID
     */
    public String currentID() {
        return m_currentID;
    }

    public void SetcurrentID(String value) {
        m_currentID = value;
    }

    /**
     * �ְ���������
     */
    public String totalID() {
        return m_totalID;
    }

    public void SettotalID(String value) {
        m_totalID = value;
    }

    public void SetvFree1(String value) {
        m_vFree1 = value;
    }

    public String Invmandoc() {
        return m_Invmandoc;
    }

    public void SetBatch(String value) {
        m_Batch = value;
    }

    public String GetBatch() {
        return m_Batch;
    }

    public void SetSerino(String value) {
        m_Serino = value;
    }

    public String GetSerino() {
        return m_Serino;
    }

    public String getInvCode() {
        return m_InvCode;
    }

    public String getInvName() {
        return m_InvName;
    }

    public String getErrMsg() {
        return m_Errmsg;
    }

    /**
     * ��ȡ���ϻ�����Ϣ,
     *
     * @param cInvcode ���Ϻ�
     * @param BizType  ҵ������ BADV �����λ����,��Ϊ��λ����ֻ��pk_copr=1001��˾����,���Ե�����BADVʱ,����д���˹�˾code ��A����Ϊ101 ,B����Ϊ1
     *                 ������ҵ���ٿ���
     */
    public Inventory(String cInvcode, String BizType, String AccID) throws JSONException, ParseException, IOException {
        //�����ݿ����ȡ

//		String lgUser = MainLogin.objLog.LoginUser;
//		String lgPwd = MainLogin.objLog.Password;		
//		String LoginString =  MainLogin.objLog.LoginString;	

        JSONObject para = new JSONObject();
        String CompanyCode = BizType;
//        if (BizType.equals("BADV")) {
//            if (AccID.equals("A")) {
//                CompanyCode = "101";
//            } else if (AccID.equals("B")) {
//                CompanyCode = "1";
//            }
//        }
//
//        if (AccID.equals("B")) {
//            CompanyCode = "1";
//        }

        para.put("FunctionName", "GetInvBaseInfo");
        para.put("CompanyCode", CompanyCode);
        para.put("InvCode", cInvcode);
        para.put("TableName", "inventory");

        try {
            JSONObject rev = Common.DoHttpQuery(para, "CommonQuery", AccID);


            if (rev == null) {
                m_Errmsg = "���������������!���Ժ�����";
                return;
            }
            if (!rev.has("Status"))
                return;
            if (rev.getBoolean("Status")) {
                if (!rev.has("inventory"))
                    return;
                JSONArray val = rev.getJSONArray("inventory");
                for (int i = 0;i<val.length();i++){
                    m_InvCode = val.getJSONObject(i).getString("invcode");
                    m_InvName = val.getJSONObject(i).getString("invname");
                    m_Invbasdoc = val.getJSONObject(i).getString("pk_invbasdoc");
                    m_Invmandoc = val.getJSONObject(i).getString("pk_invmandoc");
                    invspec = val.getJSONObject(i).getString("invspec");
                    invtype = val.getJSONObject(i).getString("invtype");
                }

            } else {
                if (!rev.has("ErrMsg"))
                    return;
                m_Errmsg = rev.getString("ErrMsg");
            }

        } catch (Exception e) {
//            m_Errmsg = "���������������!���Ժ�����";
        }

    }
//	public Inventory(JSONObject obj,int orderType) throws JSONException
//	{
//		switch(orderType)
//		{
//			case 1:	//��������
//				m_InvCode = obj.getString("invcode").toString();
//				m_InvName = obj.getString("invname").toString();
//			break;
//		}
//	}
}
