package testcase;

class InterruptThreadDemo3 {
	public static void main(String[] args) throws InterruptedException {
		MyThread m1 = new MyThread();
		System.out.println("Starting thread...");
		m1.start();
		Thread.sleep(3000);
		System.out.println("Interrupt thread...: " + m1.getName());
		m1.stop = true; // ���ù������Ϊtrue
		m1.interrupt(); // ����ʱ�˳�����״̬
		Thread.sleep(3000); // ���߳�����3���Ա�۲��߳�m1���ж����
		System.out.println("Stopping application...");
	}
}