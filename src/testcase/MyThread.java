package testcase;

public class MyThread extends Thread {
	volatile boolean stop = false;
	 
	public void run() {
		while (!stop) {
			System.out.println(getName() + " is running");
			try {
			sleep(1000);
			} catch (InterruptedException e) {
			System.out.println("week up from blcok...");
			stop = true; // 在异常处理代码中修改共享变量的状态
			}
		}
		System.out.println(getName() + " is exiting...");
	}
}
