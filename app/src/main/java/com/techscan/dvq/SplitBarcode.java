package com.techscan.dvq;

public class SplitBarcode {
    public String AccID = "A";
    public String cInvCode = "";//物料号
    public String cBatch = "";//批次
    public String cInvName = "";
    public String cSerino = "";//序列号
    public String cBatchStatus = "";
    public String currentBox = "";
    public String TotalBox = "";
    public String CheckNo = "";
    public String FinishBarCode = "";
    public String CheckBarCode = "";

    public boolean creatorOk = false;
    public String Barcode = ""; //去掉 \n的 条码
    public String BarcodeType;//条码类型（Y：液态原材料；C：固态单包原材料；TC：固态托盘原材料；P：单包成品；TP托盘成品）
    //private String m_sInventoryCode;//物料号
    //private String m_sLotCode;//批次
    public String TaxFlag;//完税、保税标志
    public Double dQuantity = 0.00;//重量
    public Integer iNumber = 0;//件数
    public String Outsourcing;//委外
    //public String SeriNo;//序列号
    public String CWFlag;//成品财务标志
    public String OnlyFlag;//成品唯一标识

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
        CheckBarCode = Barcode;

        BarcodeType = lsSplitArray[0];
        cInvCode = lsSplitArray[1];

        switch (eBarcodeType.getBarcodeType(BarcodeType)) {
            case Y:
                if (lsSplitArray.length != 2) {
                    creatorOk = false;
                    return;
                }
                break;
            case C:
                if (lsSplitArray.length != 6) {
                    creatorOk = false;
                    return;
                }
                cBatch = lsSplitArray[2];
                TaxFlag = lsSplitArray[3];
                dQuantity = Double.parseDouble(lsSplitArray[4]);
                cSerino = lsSplitArray[5];
                iNumber = 1;
                break;
            case TC:
                if (lsSplitArray.length != 7) {
                    creatorOk = false;
                    return;
                }
                cBatch = lsSplitArray[2];
                TaxFlag = lsSplitArray[3];
                dQuantity = Double.parseDouble(lsSplitArray[4]);
                iNumber = Integer.parseInt(lsSplitArray[5]);
                cSerino = lsSplitArray[6];
                break;
            case P:
                if (lsSplitArray.length != 9) {
                    creatorOk = false;
                    return;
                }
                cBatch = lsSplitArray[2];
                Outsourcing = lsSplitArray[3];
                TaxFlag = lsSplitArray[4];
                dQuantity = Double.parseDouble(lsSplitArray[5]);
                CWFlag = lsSplitArray[6];
                OnlyFlag = lsSplitArray[7];
                cSerino = lsSplitArray[8];
                iNumber = 1;
                break;
            case TP:
                if (lsSplitArray.length != 10) {
                    creatorOk = false;
                    return;
                }
                cBatch = lsSplitArray[2];
                Outsourcing = lsSplitArray[3];
                TaxFlag = lsSplitArray[4];
                dQuantity = Double.parseDouble(lsSplitArray[5]);
                iNumber = Integer.parseInt(lsSplitArray[6]);
                CWFlag = lsSplitArray[7];
                OnlyFlag = lsSplitArray[8];
                cSerino = lsSplitArray[9];
                break;
            default:
                creatorOk = false;
                return;
        }

    }

    private enum eBarcodeType {
        Y, C, TC, P, TP;

        private static eBarcodeType getBarcodeType(String sType) {
            return valueOf(sType.toUpperCase());
        }
    }

}
