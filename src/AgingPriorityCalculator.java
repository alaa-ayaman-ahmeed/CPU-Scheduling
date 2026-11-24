
public class AgingPriorityCalculator
        implements Tier2Scheduler.PriorityCalculator {

    private int agingInterval;
    private int minimumPriority;

    public AgingPriorityCalculator(
            int agingInterval,
            int minimumPriority) {

        if (agingInterval <= 0 || minimumPriority < 1) {
            throw new IllegalArgumentException(
                    "Invalid aging configuration"
            );
        }

        this.agingInterval = agingInterval;
        this.minimumPriority = minimumPriority;
    }

    // Calculate the effective priority using aging
    @Override
    public int calculate(ACSProcess process, int currentTime) {

        if (process == null) {
            throw new IllegalArgumentException(
                    "Process cannot be null"
            );
        }

        if (process.tier2EntryTime < 0
                || currentTime < process.tier2EntryTime) {
            throw new IllegalArgumentException(
                    "Invalid Tier 2 waiting time"
            );
        }

        // Calculate how long the process waited in Tier 2
        int waitingTime =
                currentTime - process.tier2EntryTime;

        // Improve priority every aging interval
        int improvement = waitingTime / agingInterval;

        int effectivePriority =
                process.initialPriority - improvement;

        // Priority cannot go below the minimum
        return Math.max(minimumPriority, effectivePriority);
    }
}
