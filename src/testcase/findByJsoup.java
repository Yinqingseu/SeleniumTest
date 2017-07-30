package testcase;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import cn.wanghaomiao.xpath.model.JXDocument;


/**
 * 多线程应该调用Thread.start()方法，这个方法将创建一个执行run方法的新线程。
 * 直接调用Thread类或Runnable对象的run方法，只会执行同一个线程中的任务，不会启动新线程
 */


public class findByJsoup {
	public volatile static boolean exit = false;  //线程终止共享变量 
	public static HashMap<WebElement,Thread> threadContainer = null; //存储线程,以iframe标记线程
	
	private static String url = "http://www.sina.com.cn/";
	private static String targetXapth = "//*[@id='sinaads-script']"; //要查找的目标元素xpath
	private static WebDriver driver = null;
	
	public  static void main(String[] args)
	{
//		findsInIframByJsoup();
		findsInIframRaw();
	}
	
	//多线程，获取页面iframe内容,JsoupXpath查找元素
	protected static void findsInIframRaw() {
		System.setProperty("webdriver.chrome.driver", "D:\\selenium\\chromedriver_win32\\chromedriver.exe");
		driver = new ChromeDriver();		
		
		//打开目标页面
		driver.get(url);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); //应该放在get的下面
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		
		long beginTime = System.currentTimeMillis(); //查找元素开始时间
		long timeOut = beginTime + Long.valueOf(5)*1000;
		
		try {
			List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
			System.out.println(iframes.size()); 
			int i = 1; //标记iframe个数
			int flag = 0; //标记是否找到元素
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
//						System.out.println("iframe"+i+":"+iframeContent); //打印iframe内的html
						for(int j=0;j<3;j++)
						{
							try
							{
								driver.findElement(By.xpath(targetXapth));							
								System.out.println("找到元素"); //268ms
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
			System.out.println("查找元素时间："+(endTime - beginTime) +"ms");
		}catch(Exception s) {s.printStackTrace();}
	}
	
	//多线程，获取页面iframe内容,JsoupXpath查找元素
	protected static void findsInIframByJsoup() {
		System.setProperty("webdriver.chrome.driver", "D:\\selenium\\chromedriver_win32\\chromedriver.exe");
		driver = new ChromeDriver();		
		//打开目标页面
		driver.get(url);
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); //应该放在get的下面
		driver.manage().timeouts().pageLoadTimeout(5, TimeUnit.SECONDS);
		
		long beginTime = System.currentTimeMillis(); //查找元素开始时间
		long timeOut = beginTime + Long.valueOf(5)*1000;
		
		try {
			List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
			JsoupXpathFind(iframes,targetXapth);
			}catch(Exception e) {
				e.printStackTrace();
			}
	
			long endTime = System.currentTimeMillis();
			System.out.println("查找元素时间："+(endTime - beginTime) +"ms");
	}
	
	/*遍历iframe,使用多线程查找元素
	 */
	private static void JsoupXpathFind(List<WebElement> iframes,String Xapth)
	{		
		System.out.println(iframes.size()); 
		for(WebElement iframe : iframes)
		{	
//			while (!exit) { //当线程未终止时
				Runnable r = () -> {
					try {
						System.out.println(LockFind(iframe,Xapth)); 
						Thread.sleep(3000);
					}catch(Exception e) {}
				};					
				//创建线程
				Thread t = new Thread(r);
//				threadContainer.put(iframe,t);
				System.out.println(t.getName() + " is running");
				t.start();			
//			}
		}
			
	}
	
	/**
	 * 使用JsoupXpath解析网页，查找目标元素的存在性
	 * @param htmlContent 要解析的网页html
	 * @param locator 要查找元素的xpath
	 * @return 找到返回true，找不到返回false
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
					System.out.println("找到元素");
					return true;
				}
			}catch(Exception es) {es.printStackTrace();}
		}
		return false;
	}
	
	//加入锁，防止代码块受并发访问干扰
	public static WebElement LockFind(WebElement iframe, String Xpath)
	{
		Lock findLock;
		WebElement targetIframe = null; //元素所在的iframe
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
//					System.out.println("iframe"+i+":"+iframeContent); //打印iframe内的html
					//用多线程 JsoupXpath解析网页内容
					if(JsoupXpathParser(iframeContent,Xpath)) {
						targetIframe = iframe;
						Thread currentThread = Thread.currentThread(); //获取当前线程
						System.out.println("找到的线程名："+currentThread.getName());
						//根据value获取key
//						WebElement currentIframe = GetKeyByValue(threadContainer,currentThread);
//						findByJsoup.exit = true;  //若找到，标记线程退出
//						stopAllThreads(threadContainer); //停止其他线程						
					}
				}catch(Exception s) {s.printStackTrace();}
			}
		}finally {
			findLock.unlock();
		}
		return targetIframe;		
	}
	
	/** HashMap<WebElement,Thread> 根据value获取key
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
		 System.out.println("未找到对应iframe");
		 return null;
	 }
	
	/***停止链表中存储的全部线程
	 * @param threads  存储开启的线程，key：iframe，value:线程
	 */
	private static void stopAllThreads(HashMap<WebElement,Thread> threads)
	{
		Iterator It = threads.keySet().iterator();
		while(It.hasNext()) 
		{
			WebElement iframeKy = (WebElement)It.next(); //key值：iframe
			Thread thread = (Thread)threads.get(iframeKy);//通过key取出value:thread
			System.out.println("Interrupt thread...: " + thread.getName());
			thread.interrupt(); //阻塞时退出阻塞状态
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
