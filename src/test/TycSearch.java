package test;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by likai on 16/10/28.
 */
public class TycSearch {

    public static void search(String keyword) throws IOException, InterruptedException {
        String kwParam= URLEncoder.encode(keyword,"UTF-8");
        System.out.println(kwParam);
        String baseUrl=String.format("http://www.tianyancha.com/search/%s.json",kwParam);
        Document doc = MaYiDaiLi.get(baseUrl);
        System.out.println(doc);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        MaYiDaiLi.turnOffProxy();

        for(int i=0;i<20;i++)
        {
            try
            {
                search("百度");
            }
            catch (Exception e)
            {

            }
        }

    }
}
