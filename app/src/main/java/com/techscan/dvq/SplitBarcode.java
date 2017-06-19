package com.techscan.dvq;

public class SplitBarcode 
{
	public String AccID = "";
	public String cInvCode = "";
	public String cBatch = "";
	public String cInvName = "";
	public String cSerino = "";
	public String cBatchStatus = "";
	public String currentBox = "";
	public String TotalBox = "";
	public String CheckNo = "";
	public String FinishBarCode = "";
	public String CheckBarCode = "";
	
	public boolean creatorOk=false;
	
	public SplitBarcode(String barcode)
	{
		String[] val;
		CheckNo = "0";
		if(barcode.contains("|"))
		{
			val = barcode.split("\\|");
			if( val.length != 8 && val.length != 7)
			{
				creatorOk=false;
				return;
			}
			
	
			AccID = val[0];
			cInvCode = val[1];
			cBatch = val[2];
			//cSerino = val[3];
			if(val.length==8)
			{
				cInvName= val[3];
				cSerino = val[4];
				if(val.length > 6)
				{
					currentBox = val[6];
					TotalBox = val[7].replace("\n", "");
				}
			}
			else if(val.length==7)
			{				
				cInvName= "";
				cSerino = val[3];
				if(val.length > 5)
				{
					currentBox = val[5];
					TotalBox = val[6].replace("\n", "");
				}
			}
		}
		else if(barcode.contains("\r\n"))
		{
			barcode = barcode.replace("\r\n", "|");
			val = barcode.split("\\|");
			
			if( val.length != 11)
			{
				creatorOk=false;
				return;
			}
			AccID = val[0].replace("���׺�:", "");
			cInvCode = val[1].replace("�������:", "");
			cBatch = val[3].replace("����:", "");
			cInvName= val[6].replace("Ʒ��:", "");
			cSerino = val[7].replace("��ˮ��:", "");
			if(val.length > 9)
			{
				currentBox = val[9].replace("�ְ���:", "");
				TotalBox = val[10].replace("\r", "").replace("\n", "").replace("�ܰ�װ��:", "");
			}
		}
		else if(barcode.contains("\r"))
		{
			barcode = barcode.replace("\r", "|");
			val = barcode.split("\\|");
			
			if( val.length != 11)
			{
				creatorOk=false;
				return;
			}
			AccID = val[0].replace("���׺�:", "");
			cInvCode = val[1].replace("�������:", "");
			cBatch = val[3].replace("����:", "");
			cInvName= val[6].replace("Ʒ��:", "");
			cSerino = val[7].replace("��ˮ��:", "");
			if(val.length > 9)
			{
				currentBox = val[9].replace("�ְ���:", "");
				TotalBox = val[10].replace("\r", "").replace("\n", "").replace("�ܰ�װ��:", "");
			}
		}
		else
		{
			creatorOk=false;
			return;
		}
		creatorOk=true;
		FinishBarCode = AccID + "|" + cInvCode + "|" + 
				cBatch + "|" + cSerino + "|" + CheckNo + "|" + 
				currentBox + "|" + TotalBox;
		CheckBarCode = AccID + "|" + cInvCode + "|" + 
				cBatch + "|" + cSerino + "|" + CheckNo;

	}

}
