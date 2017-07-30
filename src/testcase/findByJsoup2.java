package testcase;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import cn.wanghaomiao.xpath.model.JXDocument;

//备份文件 2017.7.29 22:04
/**
 * 多线程应该调用Thread.start()方法，这个方法将创建一个执行run方法的新线程。
 * 直接调用Thread类或Runnable对象的run方法，只会执行同一个线程中的任务，不会启动新线程
 */


public class findByJsoup2 {
	
	public  static void main(String[] args)
	{
//		findsInIframByJsoup();
		findsInIframRaw();
	}
	
	//多线程，获取页面iframe内容,JsoupXpath查找元素
	protected static void findsInIframRaw() {
		System.setProperty("webdriver.chrome.driver", "D:\\selenium\\chromedriver_win32\\chromedriver.exe");
		WebDriver driver = new ChromeDriver();		
		//参数设置	
		String url = "http://bbs.yingjiesheng.com/index.php";
		String targetXapth = "//*[@id='followBtn']"; //要查找的目标元素xpath
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
		WebDriver driver = new ChromeDriver();		
		//参数设置	
		String url = "http://bbs.yingjiesheng.com/index.php";
		String targetXapth = "//*[@id='followBtn']"; //要查找的目标元素xpath
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
						WebElement html = driver.findElement(By.tagName("html"));
						String iframeContent = html.getAttribute("outerHTML");
//						System.out.println("iframe"+i+":"+iframeContent); //打印iframe内的html
						//用duox
						JXDocument iframeDoc = new JXDocument(iframeContent);
						List<Object> rs;
						for(int j=0;j<3;j++)
						{
							try
							{
								rs = iframeDoc.sel(targetXapth);
								if(rs.size() != 0) 
								{
									System.out.println("找到元素");
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
			System.out.println("查找元素时间："+(endTime - beginTime) +"ms");
		}catch(Exception s) {s.printStackTrace();}
	}
	
	//加入锁，防止代码块受并发访问干扰
	public void LockFind()
	{
		Lock findLock;
		findLock = new ReentrantLock();
		Condition freeDriver;
		freeDriver = findLock.newCondition(); //设置条件对象
			
		//锁保护代码块：确保在该线程使用driver期间，没有其他线程使用
		findLock.lock();
		try {
			 findsInIframByJsoup();
		}finally {
			findLock.unlock();
		}
		
	}
	
	//多线程查找，应该一个iframe一个线程，暂时模板后期调整
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
