package DeadlockLivelock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Livelock {
	public static void main(String[] args) {
		Livelock livelock = new Livelock();
		new Thread(livelock::action1).start();
		new Thread(livelock::action2).start();
	}

	private Lock lock1 = new ReentrantLock(true);
	private Lock lock2 = new ReentrantLock(true);

	public void action1() {
		try {
			while (true) {
				lock1.tryLock(50, TimeUnit.MILLISECONDS);
				System.out.println("LOCK 1 ACQUIRED");
				TimeUnit.MILLISECONDS.sleep(1000);

				if (lock2.tryLock()) {
					System.out.println("LOCK 2 ACQUIRED");
				} else {
					System.out.println("CANNOT ACQUIRE LOCK 2, RELEASING LOCK 1");
					lock1.unlock();
					continue;
				}

				System.out.println("ACTION 1");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock1.unlock();
			lock2.unlock();
		}
	}

	public void action2() {
		try {
			while (true) {
				lock2.tryLock(50, TimeUnit.MILLISECONDS);
				System.out.println("LOCK 2 ACQUIRED");
				TimeUnit.MILLISECONDS.sleep(50);

				if (lock1.tryLock()) {
					System.out.println("LOCK 1 ACQUIRED");
				} else {
					System.out.println("CANNOT ACQUIRE LOCK 1, RELEASING LOCK 2");
					lock2.unlock();
					continue;
				}

				System.out.println("ACTION 2");
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock1.unlock();
			lock2.unlock();
		}
	}
}
