package com.techscan.dvq;

public class SplitBarcode 
{
	public String AccID = "";
	public String cInvCode = "";//���Ϻ�
	public String cBatch = "";//����
	public String cInvName = "";
	public String cSerino = "";//���к�
	public String cBatchStatus = "";
	public String currentBox = "";
	public String TotalBox = "";
	public String CheckNo = "";
	public String FinishBarCode = "";
	public String CheckBarCode = "";

	public String BarcodeType;//�������ͣ�Y��Һ̬ԭ���ϣ�C����̬����ԭ���ϣ�TC����̬����ԭ���ϣ�P��������Ʒ��TP���̳�Ʒ��
	//private String m_sInventoryCode;//���Ϻ�
	//private String m_sLotCode;//����
	public String TaxFlag;//��˰����˰��־
	public Float fQuantity;//����
	public Integer iNumber;//����
	public String Outsourcing;//ί��
	//public String SeriNo;//���к�
	public String CWFlag;//��Ʒ�����־
	public String OnlyFlag;//��ƷΨһ��ʶ


	public boolean creatorOk=false;

	public SplitBarcode(String sBarcode) throws Exception
	{
		if (sBarcode.equals(""))
			throw new Exception("�����벻���Ϲ���");

		if(!sBarcode.contains("|"))
			throw new Exception("�����벻���Ϲ���");

		String[] lsSplitArray = sBarcode.replace("\n", "").split("|");
		if (lsSplitArray.length < 2)
			throw new Exception("�����벻���Ϲ���");

		BarcodeType = lsSplitArray[0];
		cInvCode = lsSplitArray[1];

		switch (eBarcodeType.getBarcodeType(BarcodeType)) {
			case Y:
				if(lsSplitArray.length != 2)
					throw new Exception("�����벻���Ϲ���");
				break;
			case C:
				if(lsSplitArray.length != 6)
					throw new Exception("�����벻���Ϲ���");
				cBatch = lsSplitArray[2];
				TaxFlag = lsSplitArray[3];
				fQuantity = Float.parseFloat(lsSplitArray[4]);
				cSerino = lsSplitArray[5];
				break;
			case TC:
				if(lsSplitArray.length != 7)
					throw new Exception("�����벻���Ϲ���");
				cBatch = lsSplitArray[2];
				TaxFlag = lsSplitArray[3];
				fQuantity = Float.parseFloat(lsSplitArray[4]);
				iNumber = Integer.parseInt(lsSplitArray[5]);
				cSerino = lsSplitArray[6];
				break;
			case P:
				if(lsSplitArray.length != 9)
					throw new Exception("�����벻���Ϲ���");
				cBatch = lsSplitArray[2];
				Outsourcing = lsSplitArray[3];
				TaxFlag = lsSplitArray[4];
				fQuantity = Float.parseFloat(lsSplitArray[5]);
				CWFlag = lsSplitArray[6];
				OnlyFlag = lsSplitArray[7];
				cSerino = lsSplitArray[8];
				break;
			case TP:
				if(lsSplitArray.length != 10)
					throw new Exception("�����벻���Ϲ���");
				cBatch = lsSplitArray[2];
				Outsourcing = lsSplitArray[3];
				TaxFlag = lsSplitArray[4];
				fQuantity = Float.parseFloat(lsSplitArray[5]);
				iNumber = Integer.parseInt(lsSplitArray[6]);
				CWFlag = lsSplitArray[7];
				OnlyFlag = lsSplitArray[8];
				cSerino = lsSplitArray[9];
				break;
			default:
				throw new Exception("�����벻���Ϲ���");
		}

	}
	private enum eBarcodeType {
		Y,C,TC,P,TP;

		private static eBarcodeType getBarcodeType(String sType){
			return valueOf(sType.toUpperCase());
		}
	}

}
