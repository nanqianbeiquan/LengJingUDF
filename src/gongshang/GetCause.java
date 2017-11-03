package gongshang;

import org.apache.hadoop.hive.ql.exec.UDF;


public class GetCause extends UDF {
	
	
	
	public String evaluate(String causeInfo)
	{
		int idx=causeInfo.lastIndexOf("[");
		if(idx==-1)
		{
			return causeInfo;
		}
		else
		{
			return causeInfo.substring(1,idx);
		}
	}
	
	public static void main(String[] args)
	{
		GetCause udf=new GetCause();
		System.out.println(udf.evaluate("民事案由->知识产权与竞争纠纷->知识产权合同纠纷->著作权合同纠纷->邻接权转让合同纠纷[0.6636851434758431]"));
	}

}