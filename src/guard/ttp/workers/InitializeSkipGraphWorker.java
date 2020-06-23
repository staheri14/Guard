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

    private void runPhase(PhaseWorker[] workers) {
        Thread[] threads = new Thread[workers.length];
        for(int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(workers[i]);
            threads[i].start();
        }

        for(Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(PhaseWorker w : workers) {
            if(w.response.isError()) {
                System.err.println("Error during initialization: " + w.response.errorMessage);
            }
        }
    }

    @Override
    public void run() {
        System.out.println("Automatic initialization through TTP started...");

        Object[] addresses = registrations.keySet().toArray();

        // Create the workers.
        String initiatorAddress = (String) addresses[0];
        InsertNodeWorker[] insertWorkers = new InsertNodeWorker[addresses.length];
        for(int i = 0; i < addresses.length; i++) {
            insertWorkers[i] = new InsertNodeWorker(ttp, (String) addresses[i], (i == 0) ? null : initiatorAddress);
        }
        ConstructNodeWorker[] constructWorkers = new ConstructNodeWorker[addresses.length];
        for(int i = 0; i < addresses.length; i++) {
            constructWorkers[i] = new ConstructNodeWorker(ttp, (String) addresses[i]);
        }
        AssignNodeWorker[] assignWorkers = new AssignNodeWorker[addresses.length];
        for(int i = 0; i < addresses.length; i++) {
            assignWorkers[i] = new AssignNodeWorker(ttp, (String) addresses[i]);
        }
        // Run the workers in the correct order.
        System.out.println("Insertions started...");
        runPhase(insertWorkers);
        System.out.println("Construction started...");
        runPhase(constructWorkers);
        System.out.println("Assignment started...");
        runPhase(assignWorkers);
    }
}
