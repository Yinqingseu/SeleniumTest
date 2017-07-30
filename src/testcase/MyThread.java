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
			stop = true; // ���쳣����������޸Ĺ��������״̬
			}
		}
		System.out.println(getName() + " is exiting...");
	}
}
