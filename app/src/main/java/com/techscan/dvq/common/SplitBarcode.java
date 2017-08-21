package com.techscan.dvq.common;

public class SplitBarcode {

    public String AccID    = "A";
    public String cInvCode = "";//���Ϻ�
    public String cBatch   = "";//����

    public String cInvName = "";
    public String cSerino  = "";//���к�

    public String cBatchStatus = "";

    public String currentBox = "";

    public String TotalBox = "";

    public String CheckNo       = "";
    public String FinishBarCode = "";

    public String CheckBarCode = "";

    public boolean creatorOk = false;
    public String  Barcode   = ""; //ȥ�� \n�� ����
    public String BarcodeType;//�������ͣ�Y��Һ̬ԭ���ϣ�C����̬����ԭ���ϣ�TC����̬����ԭ���ϣ�P��������Ʒ��TP���̳�Ʒ��
    //private String m_sInventoryCode;//���Ϻ�
    //private String m_sLotCode;//����
    public String TaxFlag;//��˰����˰��־
    public Double  dQuantity = 0.00;//����
    public Integer iNumber   = 0;//����
    public String Outsourcing;//ί��
    //public String SeriNo;//���к�
    public String CWFlag;//��Ʒ�����־
    public String OnlyFlag;//��ƷΨһ��ʶ
    public String purductBatch;//��ƷΨһ��ʶ

    public SplitBarcode(String sBarcode) {
        creatorOk = true;

        if (sBarcode.equals("")) {
            creatorOk = false;
            return;
        }
        if (!sBarcode.contains("|")) {
            creatorOk = false;
            return;
        }
        Barcode = sBarcode.replace("\r", "").replace("\n", "");
        String[] lsSplitArray = Barcode.split("\\|");
        if (lsSplitArray.length < 2) {
            creatorOk = false;
            return;
        }
        FinishBarCode = Barcode;

        BarcodeType = lsSplitArray[0];
        cInvCode = lsSplitArray[1];

        CheckBarCode = BarcodeType + "|" + cInvCode;

        switch (eBarcodeType.getBarcodeType(BarcodeType)) {
            case Y:
                if (lsSplitArray.length != 2) {
                    creatorOk = false;
                    return;
                }
                break;
            case C:
                if (lsSplitArray.length != 7) {
                    creatorOk = false;
                    return;
                }
                cBatch = lsSplitArray[2];
                TaxFlag = lsSplitArray[3];
                dQuantity = Double.parseDouble(lsSplitArray[4]);
                cSerino = lsSplitArray[5];
                purductBatch = lsSplitArray[6];
                iNumber = 1;
                CheckBarCode = CheckBarCode + "|" + cBatch;
                break;
            case TC:
                if (lsSplitArray.length != 8) {
                    creatorOk = false;
                    return;
                }
                cBatch = lsSplitArray[2];
                TaxFlag = lsSplitArray[3];
                dQuantity = Double.parseDouble(lsSplitArray[4]);
                iNumber = Integer.parseInt(lsSplitArray[5]);
                cSerino = lsSplitArray[6];
                purductBatch = lsSplitArray[7];
                CheckBarCode = CheckBarCode + "|" + cBatch;
                break;
            case P:
                if (lsSplitArray.length != 9 && lsSplitArray.length != 8) {
                    creatorOk = false;
                    return;
                }
                cBatch = lsSplitArray[2];
                Outsourcing = lsSplitArray[3];
                TaxFlag = lsSplitArray[4];
                dQuantity = Double.parseDouble(lsSplitArray[5]);
//                CWFlag = lsSplitArray[6];
                OnlyFlag = lsSplitArray[6];
                cSerino = lsSplitArray[7];

                if (lsSplitArray.length == 8) {
                    purductBatch = "";
                }
                if (lsSplitArray.length == 9) {
                    purductBatch = lsSplitArray[8];
                }
                iNumber = 1;
                CheckBarCode = CheckBarCode + "|" + cBatch;
                break;
            case TP:
                if (lsSplitArray.length != 10 && lsSplitArray.length != 9) {
                    creatorOk = false;
                    return;
                }

                cBatch = lsSplitArray[2];
                Outsourcing = lsSplitArray[3];
                TaxFlag = lsSplitArray[4];
                dQuantity = Double.parseDouble(lsSplitArray[5]);
                iNumber = Integer.parseInt(lsSplitArray[6]);
//                CWFlag = lsSplitArray[7];
                OnlyFlag = lsSplitArray[7];
                cSerino = lsSplitArray[8];

                if (lsSplitArray.length == 9) {
                    purductBatch = "";
                }
                if (lsSplitArray.length == 10) {
                    purductBatch = lsSplitArray[9];
                }
                CheckBarCode = CheckBarCode + "|" + cBatch;
                break;
            default:
                creatorOk = false;
                break;
        }

    }

    private enum eBarcodeType {
        Y, C, TC, P, TP;

        private static eBarcodeType getBarcodeType(String sType) {
            return valueOf(sType.toUpperCase());
        }
    }
}
