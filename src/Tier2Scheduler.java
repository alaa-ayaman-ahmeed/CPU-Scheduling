
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Tier2Scheduler {

    // Processes waiting in Tier 2
    private List<ACSProcess> readyQueue = new ArrayList<>();

    // Member 4's priority calculation contract
    public interface PriorityCalculator {
        int calculate(ACSProcess process, int currentTime);
    }

    // Add a process coming from Tier 1
    public void addTier1Process(
        ACSProcess process, int currentTime) {

    if (process == null) {
        throw new IllegalArgumentException(
            "Process cannot be null"
        );
    }

    if (process.remainingTime <= 0) {
        throw new IllegalArgumentException(
            "Process must have remaining time"
        );
    }

    if (readyQueue.contains(process)) {
        throw new IllegalArgumentException(
            "Process is already in Tier 2"
        );
    }

    // Record when the process entered Tier 2
    process.tier2EntryTime = currentTime;

    // Add the same process object to Tier 2
    readyQueue.add(process);

    System.out.println(
        process.processId +
        " entered Tier 2 at time " + currentTime
    );
}

    // Select the process with the highest effective priority
    public ACSProcess selectNext(
            int currentTime,
            PriorityCalculator calculator) {

        if (readyQueue.isEmpty()) {
            return null;
        }

        // Recalculate priorities before every selection
        for (ACSProcess p : readyQueue) {
            p.effectivePriority =
                    calculator.calculate(p, currentTime);
        }

        // Smaller priority number = higher priority
        ACSProcess selected = readyQueue.stream()
                .min(
                        Comparator
                                .comparingInt(
                                        (ACSProcess p) -> p.effectivePriority)
                                .thenComparingInt(
                                        p -> p.tier2EntryTime)
                                .thenComparing(
                                        p -> p.processId)
                )
                .orElse(null);

        // Remove selected process from waiting queue
        readyQueue.remove(selected);

        return selected;
    }

    // Execute the selected process until it finishes
    public int executeSelected(
            ACSProcess process, int startTime) {

        if (process == null || process.completed) {
            throw new IllegalArgumentException(
                    "Invalid process");
        }

        if (process.startTime == -1) {
    process.startTime = startTime;
}

        // Non-preemptive: execute ALL remaining burst time
        int finishTime =
                startTime + process.remainingTime;

        process.remainingTime = 0;
        process.finishTime = finishTime;
        process.completed = true;

        System.out.println(
                process.processId + " executes from "
                        + startTime + " to " + finishTime);

        return finishTime;
    }

    // Display waiting processes and their effective priorities
    public void displayQueue() {

        System.out.println("\nTier 2 Ready Queue:");

        if (readyQueue.isEmpty()) {
            System.out.println("Empty");
            return;
        }

        for (ACSProcess p : readyQueue) {

            System.out.println(
                    p.processId
                            + " | Remaining: " + p.remainingTime
                            + " | Initial Priority: " + p.initialPriority
                            + " | Effective Priority: "
                            + p.effectivePriority
            );
        }
    }

    public boolean isEmpty() {
        return readyQueue.isEmpty();
    }
}
