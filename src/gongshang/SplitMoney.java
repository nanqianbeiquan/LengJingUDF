package gongshang;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.hadoop.hive.ql.exec.UDF;

public class SplitMoney extends UDF {
	
	static HashMap<String,String> currencyMap=new HashMap<String,String>();
	static
	{
		currencyMap.put("万元","万元");
		currencyMap.put("万元港币","万港币");
		currencyMap.put("万阿富汗尼","万阿富汗尼");
		currencyMap.put("万元欧元","万欧元");
		currencyMap.put("万元澳大利亚元","万澳大利亚元");
		currencyMap.put("万法国法郎","万法国法郎");
		currencyMap.put("万香港元","万港币");
		currencyMap.put("万马来西亚林吉特","万马来西亚林吉特");
		currencyMap.put("万加拿大元","万加拿大元");
		currencyMap.put("万元新加坡元","万新加坡元");
		currencyMap.put("万元人民币","万元");
		currencyMap.put("万德拉克马","万德拉克马");
		currencyMap.put("万人民币","万元");
		currencyMap.put("万新西兰元","万新西兰元");
		currencyMap.put("万人民币元","万元");
		currencyMap.put("万瑞典克朗","万瑞典克朗");
		currencyMap.put("万美元","万美元");
		currencyMap.put("万加元","万加拿大元");
		currencyMap.put("万元加拿大元","万加拿大元");
		currencyMap.put("万元英镑","万英镑");
		currencyMap.put("万澳大利亚元","万澳大利亚元");
		currencyMap.put("万港元","万港币");
		currencyMap.put("万瑞士法郎","万瑞士法郎");
		currencyMap.put("万韩元","万韩元");
		currencyMap.put("万新台币","万新台币");
		currencyMap.put("万英镑","万英镑");
		currencyMap.put("万元美元","万美元");
		currencyMap.put("万港币","万港币");
		currencyMap.put("万欧元","万欧元");
		currencyMap.put("万元日元","万日元");
		currencyMap.put("万新加坡元","万新加坡元");
		currencyMap.put("万德国马克","万德国马克");
		currencyMap.put("万元港元（港币）","万港币");
		currencyMap.put("万元港元","万港币");
		currencyMap.put("万日元","万日元");
		currencyMap.put("万元欧元（原欧洲货币单位）","万欧元");
		currencyMap.put("万瑞典克郎","万瑞典克朗");
		currencyMap.put("万","万元");
		currencyMap.put("人民币元","元");
		currencyMap.put("人民币","元");
		currencyMap.put("元人民币","元");
		currencyMap.put("元","元");
		currencyMap.put("亿元","亿元");
		currencyMap.put("亿","亿元");
	}
	
	static Set<Character> ignoreSet=new HashSet<Character>();
	static
	{
		ignoreSet.add(' ');
		ignoreSet.add('\t');
		ignoreSet.add('\n');
		ignoreSet.add(',');
		ignoreSet.add(';');
		ignoreSet.add('&');
		ignoreSet.add('n');
		ignoreSet.add('b');
		ignoreSet.add('s');
		ignoreSet.add('p');
	}
	
	public String evaluate(String money)
	{
		StringBuilder amountBuilder=new StringBuilder();
		StringBuilder currencyBuilder=new StringBuilder();	
		for(int i=0;i<money.length();i++)
		{
			char c=money.charAt(i);
			if(!ignoreSet.contains(c))
			{
				if((c>=48 && c<=57) || c==46)
				{
					
					if(currencyBuilder.length()==0)
					{
						amountBuilder.append(c);
					}
					else
					{
						break;
					}
				}
				else
				{
					currencyBuilder.append(c);
				}
			}
			
		}
		String currency=String.valueOf(currencyMap.get(currencyBuilder.toString()));
		String amount=amountBuilder.toString();
		if(!amount.equals("") && (currency.equals("亿元") || currency.equals("亿")))
		{
			amount=String.format("%.0f",Double.valueOf(amount)*10000*10000);
			currency=currency.replace("亿", "");
		}
		else if(!amount.equals("") && !currency.equals("元"))
		{
			amount=String.format("%.0f",Double.valueOf(amount)*10000);
			currency=currency.replace("万", "");
		}
		
		return String.format("%s,%s", amount,currency);
	}
	
	public static void main(String[] args)
	{
		System.out.println(Arrays.asList(new SplitMoney().evaluate("1223元")));
		HashSet<String> set=new HashSet<String>();
		for(String s:currencyMap.values())
			set.add(s.replace("万", ""));
		System.out.println(set);
	}
	
	
}
