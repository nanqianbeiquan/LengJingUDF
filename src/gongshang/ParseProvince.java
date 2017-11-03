package gongshang;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by likai on 16/10/21.
 */
public class ParseProvince   extends UDF {

    static HashSet<String> minZuSet = new HashSet<String>(Arrays.asList(new String[]{
            "汉族","壮族","满族","回族","苗族","维吾尔族","土家族","彝族","蒙古族","藏族",
            "布依族","侗族","瑶族","朝鲜族","白族","哈尼族","哈萨克族","黎族","傣族","畲族",
            "傈僳族","仡佬族","东乡族","高山族","拉祜族","水族","佤族","纳西族","羌族","土族",
            "仫佬族","锡伯族","柯尔克孜族","达斡尔族","景颇族","毛南族","撒拉族","塔吉克族","阿昌族",
            "普米族","鄂温克族","怒族","京族","基诺族","德昂族","保安族","俄罗斯族","裕固族","乌兹别克族",
            "门巴族","鄂伦春族","独龙族","塔塔尔族","赫哲族","珞巴族","布朗族",}));

    static int maxMinZuLength=0;
    static int maxAreaNameLength=0;
    static int minAreaNameLength=10000;
//    static String rootPath=
    static String rootPath = ParseProvince.class.getClass().getResource("/").getPath()+"../";
    static String area2ProvConfPath=rootPath+"conf/area2ProvConf.txt";
    static HashMap<String,String> area2Prov=new HashMap<String,String>();

    static
    {
        for(String mz:minZuSet)
        {
            if(mz.length()>maxMinZuLength)
            {
                maxMinZuLength=mz.length();
            }
        }
        loadArea2Prov();
    }


    public static void loadArea2Prov()
    {
//        File src=new File(area2ProvConfPath);
//        InputStream x = ParseProvince.class.getResourceAsStream("/conf/cls.txt");
        InputStreamReader read;
        try {
            read = new InputStreamReader(ParseProvince.class.getResourceAsStream("/area2ProvConf.txt"));
            BufferedReader bufferedReader = new BufferedReader(read);
            String lineText = null;
            while((lineText = bufferedReader.readLine()) != null)
            {
                String[] valArr=lineText.split(",");
                area2Prov.put(valArr[0],valArr[1]);
                if(valArr[0].length()>maxAreaNameLength)
                {
                    maxAreaNameLength=valArr[0].length();
                }
                if(valArr[0].length()<minAreaNameLength)
                {
                    minAreaNameLength=valArr[0].length();
                }
            }
            bufferedReader.close();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    public static void updateProvConf() throws IOException {

        HashSet<String> blackList=new HashSet<>();
        String  url="http://www.stats.gov.cn/tjsj/tjbz/xzqhdm/201608/t20160809_1386477.html";

        Document doc = Jsoup.connect(url).get();

        Elements areaList = doc.select("p.MsoNormal > span:nth-child(2) ");
        String province=null;
        for(Element areaEle:areaList)
        {
            String areaName=areaEle.text();
            String subArea=null;
            if(areaName.startsWith("　　　") || areaName.startsWith("　　"))
            {
                subArea=areaName.replace("　","");
            }
            else if(areaName.startsWith("　")) {
                province = areaName.replace("　","");
                subArea = province.replace("　","");
            }
            if(subArea.equals("市辖区") || subArea.equals("县") || subArea.equals("省直辖县级行政区划") || subArea.equals("自治区直辖县级行政区划"))
            {
                continue;
            }
//            System.out.println(province+","+subArea);

            String shortSubArea=getShortName(subArea);
            String shortProvince=getShortName(province);

            String[] areaArr=new String[]{subArea,province,shortSubArea,shortProvince};
            for (String area:areaArr)
            {
                if(area.length()>1)
                {
                    if(!area2Prov.containsKey(area))
                    {
                        area2Prov.put(area,province);
                    }
                    else if (!area2Prov.get(area).equals(province))
                    {
                        if(province.startsWith(area))
                        {
                            area2Prov.put(area,province);
                            blackList.remove(area);
                        }
                        if(!area2Prov.get(area).startsWith(area))
                        {
                            blackList.add(area);
                        }
                    }
                }
            }
        }
        FileWriter fw=new FileWriter(area2ProvConfPath);
        for(String area:area2Prov.keySet())
        {
            if(!blackList.contains(area))
            {
                fw.write(area+","+area2Prov.get(area)+"\n");
            }
        }
        fw.close();
    }

    public String evaluate(String companyName)
    {
        companyName=companyName.replace('(','（').replace(')','）');
        String[] tmpArr=new String[]{"公司","集团","厂","研究院"};
        for(String tmpName:tmpArr)
        {
            int idx1=companyName.indexOf(tmpName);
            if(idx1!=-1 && idx1+2<companyName.length())
            {
                String subName=companyName.substring(idx1+2);
                for(int i=Math.min(subName.length(),minAreaNameLength), j=Math.min(subName.length(),maxAreaNameLength);i<=j;i++)
                {
                    String area=subName.substring(0,i);
                    if(area2Prov.containsKey(area))
                    {
                        if(!subName.matches(area + "(.?路)(.*)"))
                        {
                            return area2Prov.get(area);
                        }

                    }
                }
            }
        }



        int idx2=companyName.indexOf('（');
        int idx3=companyName.indexOf('）');
        if(idx2>-1 && idx3>-1 && idx3>idx2)
        {
            String area=companyName.substring(idx2+1,idx3);

            if(area2Prov.containsKey(area))
            {
                return area2Prov.get(area);

            }
        }
        for(int i=Math.min(companyName.length(),minAreaNameLength), j=Math.min(companyName.length(),maxAreaNameLength);i<=j;i++)
        {
            String area=companyName.substring(0,i);

            if(area2Prov.containsKey(area))
            {
                return area2Prov.get(area);
            }
        }

        return null;

    }

    public static String getShortName(String name)
    {
        if(name.contains("新疆维吾尔"))
        {
            name=name.replace("新疆维吾尔","新疆");
        }
        else if(name.contains("蒙古自治州"))
        {
            name=name.replace("蒙古自治州","");
        }
        else if(name.contains("河南蒙古族"))
        {
            name=name.replace("河南蒙古族","");
        }
        if(name.endsWith("地区"))
        {
            name=name.substring(0,name.length()-2);
        }
        else
        {
            name=name.substring(0,name.length()-1);
        }
        if (name.contains("自治")) {
            String minZu=getMinZu(name);
            name=name.substring(0,name.length()-2);
            name=name.replace(minZu,"");
        }
        return name;
    }

    public static String getMinZu(String name)
    {
        String res="";
        if(name.contains("自治"))
        {
            int stopIdx=name.indexOf("族")+1;

            while(stopIdx>0)
            {
                boolean match=false;
                for(int i=maxMinZuLength;i>1;i--) {
                    int startIdx = Math.max(0, stopIdx - i);
                    String tmpMinZu = name.substring(startIdx, stopIdx);
                    if (minZuSet.contains(tmpMinZu)) {
                        match = true;
                        name = name.replace(tmpMinZu, "");
                        res += tmpMinZu;
                        break;
                    }
                }
                if(match==false) break;
                stopIdx=name.indexOf("族")+1;
            }
        }
        return res;
    }

    public static void main(String[] args) throws SQLException, IOException {
//        updateProvConf();
        System.out.println(new ParseProvince().evaluate("甘肃恒信假期旅行社有限责任公司南关营业部"));

//        System.out.println("abc123".matches("abc(.*)"));
    }

}
