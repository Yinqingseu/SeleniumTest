package testcase;

class InterruptThreadDemo3 {
	public static void main(String[] args) throws InterruptedException {
		MyThread m1 = new MyThread();
		System.out.println("Starting thread...");
		m1.start();
		Thread.sleep(3000);
		System.out.println("Interrupt thread...: " + m1.getName());
		m1.stop = true; // 设置共享变量为true
		m1.interrupt(); // 阻塞时退出阻塞状态
		Thread.sleep(3000); // 主线程休眠3秒以便观察线程m1的中断情况
		System.out.println("Stopping application...");
	}
}