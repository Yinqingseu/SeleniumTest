package testcase;

import java.util.*;
import java.util.concurrent.TimeUnit;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class WebdriverTest {
	public static void main(String[] args) {
		
		test();
	}
	
	static void test() {
		System.setProperty("webdriver.chrome.driver", "D:\\selenium\\chromedriver_win32\\chromedriver.exe");
		
		WebDriver driver = new ChromeDriver();
		
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		
		String url = "http://www.baidu.com/";
	
		driver.get(url);
			
			//��ȡ��ǰҳ��ȫ��iframe������iframe����Ԫ��
			try {
				List<WebElement> iframes = driver.findElements(By.tagName("iframe")); //��ȡȫ��iframe��ǩ
				if(iframes.size()!=0) {
					for(WebElement iframe : iframes) {
						if(iframe.getSize() != null) {
							  System.out.println(iframe.getAttribute("outerHtml"));
						}
					}
				}else{
					System.out.println("��ҳ�治����iframe");
				}				
			}catch(Exception e) {
				System.out.println(e);
			}
		
	}

}
