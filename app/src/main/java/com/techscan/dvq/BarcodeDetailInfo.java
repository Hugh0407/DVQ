package com.techscan.dvq;

/**
 * Created by walter on 2017/6/28.
 */

public class BarcodeDetailInfo {
    private String m_sBarcodeType;//�������ͣ�Y��Һ̬ԭ���ϣ�C����̬����ԭ���ϣ�TC����̬����ԭ���ϣ�P��������Ʒ��TP���̳�Ʒ��
    private String m_sInventoryCode;//���Ϻ�
    private String m_sLotCode;//����
    private String m_sTaxFlag;//��˰����˰��־
    private Float m_fQuantity;//����
    private Integer m_iNumber;//����
    private String m_sOutsourcing;//ί��
    private String m_sSeriNo;//���к�
    private String m_sCWFlag;//��Ʒ�����־
    private String m_sOnlyFlag;//��ƷΨһ��ʶ

    public String ReBarcodeType(){return m_sBarcodeType;}
    public void SetBarcodeType(String Value){m_sBarcodeType = Value;}

    public String ReInventoryCode(){return m_sInventoryCode;}
    public void SetInventoryCode(String Value){m_sInventoryCode = Value;}

    public String ReLotCode(){return m_sLotCode;}
    public void SetLotCode(String Value){m_sLotCode = Value;}

    public String ReTaxFlag(){return m_sTaxFlag;}
    public void SetTaxFlag(String Value){m_sTaxFlag = Value;}

    public Float ReQuantity(){return m_fQuantity;}
    public void SetQuantity(Float Value){m_fQuantity = Value;}

    public Integer ReNumber(){return m_iNumber;}
    public void SetNumber(Integer Value){m_iNumber = Value;}

    public String ReOutsourcing(){return m_sOutsourcing;}
    public void SetOutsourcing(String Value){m_sOutsourcing = Value;}

    public String ReSeriNo(){return m_sSeriNo;}
    public void SetSeriNo(String Value){m_sSeriNo = Value;}

    public String ReCWFlag(){return m_sCWFlag;}
    public void SetCWFlag(String Value){m_sCWFlag = Value;}

    public String ReOnlyFlag(){return m_sOnlyFlag;}
    public void SetOnlyFlag(String Value){m_sOnlyFlag = Value;}
}
