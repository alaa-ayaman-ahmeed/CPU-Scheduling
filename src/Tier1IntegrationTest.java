import java.util.*;

public class Tier1IntegrationTest {

    public static void main(String[] args) {

        Tier1Scheduler tier1 = new Tier1Scheduler();
        Tier2Scheduler tier2 = new Tier2Scheduler();

        List<ACSProcess> processes = Arrays.asList(
            new ACSProcess("P1", 0, 8, 3),
            new ACSProcess("P2", 0, 5, 1),
            new ACSProcess("P3", 0, 2, 2)
        );

        int currentTime = tier1.executeTier1(
            processes, 3, 0, tier2
        );

        System.out.println("\nStarting Tier 2:");

        Tier2Scheduler.PriorityCalculator aging =
            (p, time) -> p.initialPriority;

        while (!tier2.isEmpty()) {

            ACSProcess selected = tier2.selectNext(
                currentTime, aging
            );

            currentTime = tier2.executeSelected(
                selected, currentTime
            );
        }

        System.out.println(
            "\nAll processes completed at: " + currentTime
        );
    }
}

