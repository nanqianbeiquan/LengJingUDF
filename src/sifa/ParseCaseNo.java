package sifa;

import org.apache.hadoop.hive.ql.exec.UDF;

public class ParseCaseNo  extends UDF {

	public String evaluate(String caseNo)
	{
		caseNo=caseNo.replace(" ","");
		caseNo=caseNo.replace(']','）');
		caseNo=caseNo.replace('(','（');
		caseNo=caseNo.replace('[','（');
		caseNo=caseNo.replace(')','）');
		caseNo=caseNo.replace("（（","（");
		caseNo=caseNo.replace("））","）");
		caseNo=caseNo.replace("？","");
		caseNo=caseNo.replace("？","");
		caseNo=caseNo.replace("?","");
		if(caseNo.endsWith("）"))
		{
			caseNo=caseNo.substring(0, caseNo.length()-1);
		}
		return caseNo;
	}
	public static void main(String[] args)
	{
		
	}
}
