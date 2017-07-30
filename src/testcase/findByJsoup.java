package testcase;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import cn.wanghaomiao.xpath.model.JXDocument;


/**
 * ���߳�Ӧ�õ���Thread.start()�������������������һ��ִ��run���������̡߳�
 * ֱ�ӵ���Thread���Runnable�����run������ֻ��ִ��ͬһ���߳��е����񣬲����������߳�
 */


public class findByJsoup {
	public volatile static boolean exit = false;  //�߳���ֹ������� 
	public static HashMap<WebElement,Thread> threadContainer = null; //�洢�߳�,��iframe����߳�
	
	private static String url = "http://www.sina.com.cn/";
	private static String targetXapth = "//*[@id='sinaads-script']"; //Ҫ���ҵ�Ŀ��Ԫ��xpath
	private static WebDriver driver = null;
	
	public  static void main(String[] args)
	{
//		findsInIframByJsoup();
		findsInIframRaw();
	}
	
	//���̣߳���ȡҳ��iframe����,JsoupXpath����Ԫ��
	protected static void findsInIframRaw() {
		System.setProperty("webdriver.chrome.driver", "D:\\selenium\\chromedriver_win32\\chromedriver.exe");
		driver = new ChromeDriver();		
		
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
		driver = new ChromeDriver();		
		//��Ŀ��ҳ��
		driver.get(url);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); //Ӧ�÷���get������
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		
		long beginTime = System.currentTimeMillis(); //����Ԫ�ؿ�ʼʱ��
		long timeOut = beginTime + Long.valueOf(5)*1000;
		
		try {
			List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
			JsoupXpathFind(iframes,targetXapth);
			}catch(Exception e) {
				e.printStackTrace();
			}
	
			long endTime = System.currentTimeMillis();
			System.out.println("����Ԫ��ʱ�䣺"+(endTime - beginTime) +"ms");
	}
	
	/*����iframe,ʹ�ö��̲߳���Ԫ��
	 */
	private static void JsoupXpathFind(List<WebElement> iframes,String Xapth)
	{		
		System.out.println(iframes.size()); 
		for(WebElement iframe : iframes)
		{	
//			while (!exit) { //���߳�δ��ֹʱ
				Runnable r = () -> {
					try {
						System.out.println(LockFind(iframe,Xapth)); 
						Thread.sleep(3000);
					}catch(Exception e) {}
				};					
				//�����߳�
				Thread t = new Thread(r);
//				threadContainer.put(iframe,t);
				System.out.println(t.getName() + " is running");
				t.start();			
//			}
		}
			
	}
	
	/**
	 * ʹ��JsoupXpath������ҳ������Ŀ��Ԫ�صĴ�����
	 * @param htmlContent Ҫ��������ҳhtml
	 * @param locator Ҫ����Ԫ�ص�xpath
	 * @return �ҵ�����true���Ҳ�������false
	 */
	private static boolean JsoupXpathParser(String htmlContent,String locator)
	{
		JXDocument iframeDoc = new JXDocument(htmlContent);
		List<Object> rs;
		for(int j=0;j<3;j++)
		{
			try
			{
				rs = iframeDoc.sel(locator);
				if(rs.size() != 0) 
				{
					System.out.println("�ҵ�Ԫ��");
					return true;
				}
			}catch(Exception es) {es.printStackTrace();}
		}
		return false;
	}
	
	//����������ֹ������ܲ������ʸ���
	public static WebElement LockFind(WebElement iframe, String Xpath)
	{
		Lock findLock;
		WebElement targetIframe = null; //Ԫ�����ڵ�iframe
		findLock = new ReentrantLock();
		findLock.lock();
		try {
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
//					System.out.println("iframe"+i+":"+iframeContent); //��ӡiframe�ڵ�html
					//�ö��߳� JsoupXpath������ҳ����
					if(JsoupXpathParser(iframeContent,Xpath)) {
						targetIframe = iframe;
						Thread currentThread = Thread.currentThread(); //��ȡ��ǰ�߳�
						System.out.println("�ҵ����߳�����"+currentThread.getName());
						//����value��ȡkey
//						WebElement currentIframe = GetKeyByValue(threadContainer,currentThread);
//						findByJsoup.exit = true;  //���ҵ�������߳��˳�
//						stopAllThreads(threadContainer); //ֹͣ�����߳�						
					}
				}catch(Exception s) {s.printStackTrace();}
			}
		}finally {
			findLock.unlock();
		}
		return targetIframe;		
	}
	
	/** HashMap<WebElement,Thread> ����value��ȡkey
	 */
	 private static WebElement GetKeyByValue(Map<WebElement,Thread> map,Thread value)
	 {
		 Set set = map.entrySet();
		 WebElement key = null;
		 Iterator it = set.iterator();
		 while(it.hasNext()) 
		 {
			 Map.Entry entry = (Map.Entry)it.next();
			 if(entry.getValue().equals(value)) {
				 return (WebElement) entry.getKey();
			 }
			 
		 }
		 System.out.println("δ�ҵ���Ӧiframe");
		 return null;
	 }
	
	/***ֹͣ�����д洢��ȫ���߳�
	 * @param threads  �洢�������̣߳�key��iframe��value:�߳�
	 */
	private static void stopAllThreads(HashMap<WebElement,Thread> threads)
	{
		Iterator It = threads.keySet().iterator();
		while(It.hasNext()) 
		{
			WebElement iframeKy = (WebElement)It.next(); //keyֵ��iframe
			Thread thread = (Thread)threads.get(iframeKy);//ͨ��keyȡ��value:thread
			System.out.println("Interrupt thread...: " + thread.getName());
			thread.interrupt(); //����ʱ�˳�����״̬
		}
		
	}

	private static void sleep(int i) {
	  try {
			TimeUnit.SECONDS.sleep(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
