import java.util.*;

public class Tier1Scheduler {

    private Queue<ACSProcess> readyQueue = new LinkedList<>();

    // Adds a process to the Tier 1 queue
    public void addProcess(ACSProcess process) {

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

        readyQueue.add(process);
    }

    // Executes Tier 1 using Round Robin
    public int executeTier1(
            List<ACSProcess> processes,
            int quantum,
            int currentTime,
            Tier2Scheduler tier2) {

        if (quantum <= 0) {
            throw new IllegalArgumentException(
                "Quantum must be greater than zero"
            );
        }

        if (tier2 == null || processes == null) {
            throw new IllegalArgumentException(
                "Invalid scheduler or process list"
            );
        }

        // Sort processes by arrival time
        List<ACSProcess> incoming = new ArrayList<>(processes);

        incoming.sort(
            Comparator.comparingInt(p -> p.arrivalTime)
        );

        int nextProcess = 0;

        while (nextProcess < incoming.size()
                || !readyQueue.isEmpty()) {

            // Add all processes that have arrived
            while (nextProcess < incoming.size()
                    && incoming.get(nextProcess).arrivalTime
                    <= currentTime) {

                addProcess(incoming.get(nextProcess));
                nextProcess++;
            }

            // If no process is ready, move to next arrival
            if (readyQueue.isEmpty()) {

                currentTime =
                    incoming.get(nextProcess).arrivalTime;

                continue;
            }

            // Get the first process in the queue
            ACSProcess process = readyQueue.poll();

            // Calculate execution time
            int executionTime = Math.min(
                quantum,
                process.remainingTime
            );

            int start = currentTime;
            int end = currentTime + executionTime;

            // Record first CPU start
            if (process.startTime == -1) {
                process.startTime = start;
            }

            System.out.println(
                process.processId + " executes from "
                + start + " to " + end
            );

            // Update remaining time
            process.remainingTime -= executionTime;

            currentTime = end;

            // Check whether the process finished
            if (process.remainingTime == 0) {

                process.finishTime = currentTime;
                process.completed = true;

                System.out.println(
                    process.processId + " completed in Tier 1"
                );

            } else {

                // Move unfinished process to Tier 2
                tier2.addTier1Process(process, currentTime);

                System.out.println(
                    process.processId + " moved to Tier 2"
                );
            }
        }

        return currentTime;
    }

    public boolean isEmpty() {
        return readyQueue.isEmpty();
    }
}
