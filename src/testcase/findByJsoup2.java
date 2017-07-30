package testcase;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import cn.wanghaomiao.xpath.model.JXDocument;

//�����ļ� 2017.7.29 22:04
/**
 * ���߳�Ӧ�õ���Thread.start()�������������������һ��ִ��run���������̡߳�
 * ֱ�ӵ���Thread���Runnable�����run������ֻ��ִ��ͬһ���߳��е����񣬲����������߳�
 */


public class findByJsoup2 {
	
	public  static void main(String[] args)
	{
//		findsInIframByJsoup();
		findsInIframRaw();
	}
	
	//���̣߳���ȡҳ��iframe����,JsoupXpath����Ԫ��
	protected static void findsInIframRaw() {
		System.setProperty("webdriver.chrome.driver", "D:\\selenium\\chromedriver_win32\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();		
		//��������	
		String url = "http://bbs.yingjiesheng.com/index.php";
		String targetXapth = "//*[@id='followBtn']"; //Ҫ���ҵ�Ŀ��Ԫ��xpath
		//��Ŀ��ҳ��
		driver.get(url);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); //Ӧ�÷���get������
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		
		long beginTime = System.currentTimeMillis(); //����Ԫ�ؿ�ʼʱ��
		long timeOut = beginTime + Long.valueOf(5)*1000;
		
		try {
			List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
			System.out.println(iframes.size()); 
			int i = 1; //���iframe����
			int flag = 0; //����Ƿ��ҵ�Ԫ��
			for(WebElement iframe : iframes)
			{
				driver.switchTo().defaultContent();
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				System.out.println("iframe src:"+ iframe.getAttribute("src")); 
				if(!iframe.getSize().equals(new Dimension(0,0)) && !iframe.getLocation().equals(new Point(0,0)))
				{
					driver.switchTo().frame(iframe);
					driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
					try {
//						System.out.println("iframe"+i+":"+iframeContent); //��ӡiframe�ڵ�html
						for(int j=0;j<3;j++)
						{
							try
							{
								driver.findElement(By.xpath(targetXapth));
								
								System.out.println("�ҵ�Ԫ��"); //268ms
								flag = 1;
								break;
							}catch(Exception es) {es.printStackTrace();}
						}
						if(flag == 1) {
							break;
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			long endTime = System.currentTimeMillis();
			System.out.println("����Ԫ��ʱ�䣺"+(endTime - beginTime) +"ms");
		}catch(Exception s) {s.printStackTrace();}
	}
	
	//���̣߳���ȡҳ��iframe����,JsoupXpath����Ԫ��
	protected static void findsInIframByJsoup() {
		System.setProperty("webdriver.chrome.driver", "D:\\selenium\\chromedriver_win32\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();		
		//��������	
		String url = "http://bbs.yingjiesheng.com/index.php";
		String targetXapth = "//*[@id='followBtn']"; //Ҫ���ҵ�Ŀ��Ԫ��xpath
		//��Ŀ��ҳ��
		driver.get(url);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); //Ӧ�÷���get������
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		
		long beginTime = System.currentTimeMillis(); //����Ԫ�ؿ�ʼʱ��
		long timeOut = beginTime + Long.valueOf(5)*1000;
		
		try {
			List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
			System.out.println(iframes.size()); 
			int i = 1; //���iframe����
			int flag = 0; //����Ƿ��ҵ�Ԫ��
			for(WebElement iframe : iframes)
			{
				driver.switchTo().defaultContent();
				driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
				System.out.println("iframe src:"+ iframe.getAttribute("src")); 
				if(!iframe.getSize().equals(new Dimension(0,0)) && !iframe.getLocation().equals(new Point(0,0)))
				{
					driver.switchTo().frame(iframe);
					driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
					try {
						WebElement html = driver.findElement(By.tagName("html"));
						String iframeContent = html.getAttribute("outerHTML");
//						System.out.println("iframe"+i+":"+iframeContent); //��ӡiframe�ڵ�html
						//��duox
						JXDocument iframeDoc = new JXDocument(iframeContent);
						List<Object> rs;
						for(int j=0;j<3;j++)
						{
							try
							{
								rs = iframeDoc.sel(targetXapth);
								if(rs.size() != 0) 
								{
									System.out.println("�ҵ�Ԫ��");
									flag = 1;
									break;
								}
							}catch(Exception es) {es.printStackTrace();}
						}
						if(flag == 1) {
							break;
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			long endTime = System.currentTimeMillis();
			System.out.println("����Ԫ��ʱ�䣺"+(endTime - beginTime) +"ms");
		}catch(Exception s) {s.printStackTrace();}
	}
	
	//����������ֹ������ܲ������ʸ���
	public void LockFind()
	{
		Lock findLock;
		findLock = new ReentrantLock();
		Condition freeDriver;
		freeDriver = findLock.newCondition(); //������������
			
		//����������飺ȷ���ڸ��߳�ʹ��driver�ڼ䣬û�������߳�ʹ��
		findLock.lock();
		try {
			 findsInIframByJsoup();
		}finally {
			findLock.unlock();
		}
		
	}
	
	//���̲߳��ң�Ӧ��һ��iframeһ���̣߳���ʱģ����ڵ���
	public void multiFind() {
		Runnable r = () -> {
			try {
				LockFind();
				Thread.sleep(3000);
			}catch(Exception e) {}
		};
		Thread t = new Thread(r);
		t.start();
	}
	
	
	private static void sleep(int i) {
		// TODO Auto-generated method stub
		  try {
			TimeUnit.SECONDS.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
