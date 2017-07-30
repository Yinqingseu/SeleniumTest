package cn.wanghaomiao.example;

import java.io.IOException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.wanghaomiao.xpath.exception.XpathSyntaxErrorException;
import cn.wanghaomiao.xpath.model.JXDocument;
import cn.wanghaomiao.xpath.model.JXNode;

/**
 * @author github.com/zhegexiaohuozi [seimimaster@gmail.com]
 * @since 14-3-19
 */
public class XpathTest {
    public static void main(String[] args)  {
        String html = "<html><body><script>console.log('aaaaa')</script><div class='test'>搜易贷致力于普惠金融，专注于互联网投资理财与小额贷款，搭建中国最大、用户体验最好的个人及中小企业的互联网信贷平台</div><div class='xiao'>Two</div></body></html>";
        JXDocument underTest = new JXDocument(html);
        
        Document doc;
		try {
			doc = Jsoup.connect("https://book.douban.com/subject_search?start=15&search_text=java&cat=1001").userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0").get();
			JXDocument doubanTest = new JXDocument(doc);
			String xpath = "//a/@href";
	    	try {
				List<JXNode> rs = doubanTest.selN(xpath);
				 for (JXNode n : rs) {
		            if (!n.isText()) {
		                int index = n.getElement().siblingIndex();
		                System.out.println(index);
		            }
		            System.out.println(n.toString());
		         }
				
			} catch (XpathSyntaxErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	
    }
}
