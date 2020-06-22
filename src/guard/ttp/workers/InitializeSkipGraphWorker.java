package guard.ttp.workers;

import guard.ttp.RegisteredNodeInformation;
import guard.ttp.TTP;

import java.util.Map;

public class InitializeSkipGraphWorker implements Runnable {

    private final TTP ttp;
    private final Map<String, RegisteredNodeInformation> registrations;

    public InitializeSkipGraphWorker(TTP ttp, Map<String, RegisteredNodeInformation> registrations) {
        this.ttp = ttp;
        this.registrations = registrations;
    }

    @Override
    public void run() {
        Object[] addresses = registrations.keySet().toArray();

        Thread[] insertThreads = new Thread[addresses.length];
        String globalInitiatorAddress = (String) addresses[0];
        for(int i = 0; i < addresses.length; i++) {
            insertThreads[i] = new Thread(new InsertNodeWorker(ttp, (String) addresses[i], (i == 0) ? null : globalInitiatorAddress));
            insertThreads[i].start();
        }

        for(Thread t : insertThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Thread[] constructThreads = new Thread[addresses.length];
        for(int i = 0; i < addresses.length; i++) {
            constructThreads[i] = new Thread(new ConstructNodeWorker(ttp, (String) addresses[i]));
            constructThreads[i].start();
        }

        for(Thread t : constructThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Thread[] assignThreads = new Thread[addresses.length];
        for(int i = 0; i < addresses.length; i++) {
            assignThreads[i] = new Thread(new AssignNodeWorker(ttp, (String) addresses[i]));
            assignThreads[i].start();
        }

        for(Thread t : assignThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
