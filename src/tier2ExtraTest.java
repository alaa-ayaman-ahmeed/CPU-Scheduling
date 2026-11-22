public class tier2ExtraTest {

    public static void main(String[] args) {

        // Aging formula: every 4 waiting units improves priority by 1
        Tier2Scheduler.PriorityCalculator aging =
                (p, time) -> Math.max(
                        1,
                        p.initialPriority
                                - (time - p.tier2EntryTime) / 4
                );

        // =========================
        // TEST 1: Different Priorities
        // =========================

        System.out.println("\n========== TEST 1 ==========");

        Tier2Scheduler s1 = new Tier2Scheduler();

        ACSProcess p1 = new ACSProcess("P1", 0, 3, 3);
        ACSProcess p2 = new ACSProcess("P2", 0, 3, 1);
        ACSProcess p3 = new ACSProcess("P3", 0, 3, 2);

        s1.addTier1Process(p1, 0);
        s1.addTier1Process(p2, 0);
        s1.addTier1Process(p3, 0);

        while (!s1.isEmpty()) {
            ACSProcess selected = s1.selectNext(0, aging);
            System.out.println("Selected: " + selected.processId);
        }

        // Expected: P2 -> P3 -> P1


        // =========================
        // TEST 2: Aging and Tie-Breaking
        // =========================

        System.out.println("\n========== TEST 2 ==========");

        Tier2Scheduler s2 = new Tier2Scheduler();

        ACSProcess a = new ACSProcess("P1", 0, 3, 3);
        ACSProcess b = new ACSProcess("P2", 0, 3, 2);
        ACSProcess c = new ACSProcess("P3", 0, 3, 1);

        s2.addTier1Process(a, 3);
        s2.addTier1Process(b, 5);
        s2.addTier1Process(c, 7);

        int currentTime = 7;

        while (!s2.isEmpty()) {

            ACSProcess selected = s2.selectNext(currentTime, aging);

            System.out.println(
                    "Selected: " + selected.processId
                            + " | Effective Priority: "
                            + selected.effectivePriority
            );
        }

        // At time 7:
        // P1 = 3 - floor((7-3)/4) = 2
        // P2 = 2 - floor((7-5)/4) = 2
        // P3 = 1 - floor((7-7)/4) = 1
        //
        // Expected: P3 -> P1 -> P2
        // P1 wins the tie with P2 because it entered Tier 2 earlier.


        // =========================
        // TEST 3: Empty Queue
        // =========================

        System.out.println("\n========== TEST 3 ==========");

        Tier2Scheduler s3 = new Tier2Scheduler();

        ACSProcess selected = s3.selectNext(0, aging);

        System.out.println("Selected: " + selected);

        // Expected: null
    }
}