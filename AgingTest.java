
public class AgingTest {

    public static void main(String[] args) {

        AgingPriorityCalculator aging =
                new AgingPriorityCalculator(4, 1);

        // =========================
        // TEST 1: Priority Improvement
        // =========================

        System.out.println("\n===== TEST 1 =====");

        ACSProcess p1 =
                new ACSProcess("P1", 0, 5, 5);

        p1.tier2EntryTime = 3;

        int[] times = {3, 6, 7, 11, 15, 19};

        for (int time : times) {

            int priority =
                    aging.calculate(p1, time);

            System.out.println(
                    "Time: " + time
                    + " | Waiting: " + (time - 3)
                    + " | Effective Priority: " + priority
            );
        }

        // Expected priorities: 5, 5, 4, 3, 2, 1


        // =========================
        // TEST 2: Minimum Priority
        // =========================

        System.out.println("\n===== TEST 2 =====");

        int priority =
                aging.calculate(p1, 100);

        System.out.println(
                "Effective Priority: " + priority
        );

        // Expected: 1


        // =========================
        // TEST 3: Tier 2 Integration
        // =========================

        System.out.println("\n===== TEST 3 =====");

        Tier2Scheduler scheduler = new Tier2Scheduler();

        ACSProcess a =
                new ACSProcess("P1", 0, 3, 3);

        ACSProcess b =
                new ACSProcess("P2", 0, 3, 2);

        scheduler.addTier1Process(a, 0);
        scheduler.addTier1Process(b, 8);

        int currentTime = 12;

        ACSProcess selected =
                scheduler.selectNext(currentTime, aging);

        System.out.println(
                "Selected Process: " + selected.processId
        );

        System.out.println(
                "Effective Priority: " + selected.effectivePriority
        );

        // P1 priority = 3 - (12-0)/4 = 0, minimum becomes 1.
        // P2 priority = 2 - (12-8)/4 = 1.
        // Both have priority 1, so P1 wins the tie
        // because it entered Tier 2 earlier.
    }
}
