import java.util.*;

public class Tier1Test {

    public static void runTest(int quantum) {

        System.out.println("\nTesting Quantum: " + quantum);

        Tier1Scheduler tier1 = new Tier1Scheduler();
        Tier2Scheduler tier2 = new Tier2Scheduler();

        List<ACSProcess> processes = Arrays.asList(

            new ACSProcess("P1", 0, 8, 3),
            new ACSProcess("P2", 1, 5, 1),
            new ACSProcess("P3", 4, 2, 2),
            new ACSProcess("P4", 10, 6, 4)

        );

        int finishTime = tier1.executeTier1(
            processes,
            quantum,
            0,
            tier2
        );

        System.out.println(
            "\nTier 1 finished at: " + finishTime
        );

        tier2.displayQueue();
    }

    public static void main(String[] args) {

        runTest(2);

        runTest(3);

        runTest(4);
    }
}
