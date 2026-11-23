import java.util.*;

public class SJFScheduler {

    static class Process {
        String id;
        int arrivalTime;
        int burstTime;
        int remainingTime;
        int firstStartTime = -1;
        int finishTime = -1;

        Process(String id, int arrivalTime, int burstTime) {
            if (arrivalTime < 0 || burstTime <= 0) {
                throw new IllegalArgumentException(
                    "Arrival time must be >= 0 and burst time must be > 0."
                );
            }
            this.id = id;
            this.arrivalTime = arrivalTime;
            this.burstTime = burstTime;
            this.remainingTime = burstTime;
        }

        int getTurnaroundTime() {
            return finishTime - arrivalTime;
        }

        int getWaitingTime() {
            return getTurnaroundTime() - burstTime;
        }

        int getResponseTime() {
            return firstStartTime - arrivalTime;
        }
    }

    static class Segment {
        String label;
        int start;
        int end;

        Segment(String label, int start, int end) {
            this.label = label;
            this.start = start;
            this.end = end;
        }
    }

    static class Result {
        List<Process> processes;
        List<Segment> timeline;
        double averageWaitingTime;
        double averageTurnaroundTime;
        double averageResponseTime;

        Result(List<Process> processes, List<Segment> timeline) {
            this.processes = processes;
            this.timeline = timeline;

            double totalWaiting = 0;
            double totalTurnaround = 0;
            double totalResponse = 0;

            for (Process p : processes) {
                totalWaiting += p.getWaitingTime();
                totalTurnaround += p.getTurnaroundTime();
                totalResponse += p.getResponseTime();
            }

            int n = processes.size();
            averageWaitingTime = n == 0 ? 0 : totalWaiting / n;
            averageTurnaroundTime = n == 0 ? 0 : totalTurnaround / n;
            averageResponseTime = n == 0 ? 0 : totalResponse / n;
        }
    }


    public static Result schedule(List<Process> inputProcesses,
                                  int contextSwitchTime) {
        if (contextSwitchTime < 0) {
            throw new IllegalArgumentException(
                "Context-switch time cannot be negative."
            );
        }

    
        List<Process> processes = new ArrayList<>();
        for (Process p : inputProcesses) {
            processes.add(new Process(p.id, p.arrivalTime, p.burstTime));
        }

        List<Segment> timeline = new ArrayList<>();
        int time = 0;
        int completed = 0;
        String lastExecutedProcess = null;

        while (completed < processes.size()) {
            Process selected = selectShortestRemaining(processes, time);
            if (selected == null) {
                int nextArrival = Integer.MAX_VALUE;
                for (Process p : processes) {
                    if (p.remainingTime > 0) {
                        nextArrival = Math.min(nextArrival, p.arrivalTime);
                    }
                }

                addSegment(timeline, "IDLE", time, nextArrival);
                time = nextArrival;
                lastExecutedProcess = null;
                continue;
            }
            if (lastExecutedProcess != null
                    && !lastExecutedProcess.equals(selected.id)
                    && contextSwitchTime > 0) {

                int switchStart = time;
                time += contextSwitchTime;
                addSegment(timeline, "CS", switchStart, time);

                selected = selectShortestRemaining(processes, time);
                if (selected == null) {
                    lastExecutedProcess = null;
                    continue;
                }
            }

            if (selected.firstStartTime == -1) {
                selected.firstStartTime = time;
            }

            int executionStart = time;
            selected.remainingTime--;
            time++;

            addSegment(timeline, selected.id, executionStart, time);

            if (selected.remainingTime == 0) {
                selected.finishTime = time;
                completed++;
            }

            lastExecutedProcess = selected.id;
        }

        return new Result(processes, timeline);
    }


    private static Process selectShortestRemaining(List<Process> processes,
                                                   int currentTime) {
        Process best = null;

        for (Process p : processes) {
            if (p.arrivalTime <= currentTime && p.remainingTime > 0) {
                if (best == null
                        || p.remainingTime < best.remainingTime
                        || (p.remainingTime == best.remainingTime
                            && p.arrivalTime < best.arrivalTime)
                        || (p.remainingTime == best.remainingTime
                            && p.arrivalTime == best.arrivalTime
                            && p.id.compareTo(best.id) < 0)) {
                    best = p;
                }
            }
        }
        return best;
    }

    private static void addSegment(List<Segment> timeline, String label,
                                   int start, int end) {
        if (start == end) return;

        if (!timeline.isEmpty()) {
            Segment previous = timeline.get(timeline.size() - 1);
            if (previous.label.equals(label) && previous.end == start) {
                previous.end = end;
                return;
            }
        }
        timeline.add(new Segment(label, start, end));
    }

    private static void printResult(Result result) {
        System.out.println("\n===== PREEMPTIVE SJF (SRTF) TIMELINE =====");
        for (Segment s : result.timeline) {
            System.out.printf("[%d - %d] %s%n", s.start, s.end, s.label);
        }

        System.out.println("\n===== PROCESS METRICS =====");
        System.out.printf("%-10s %-10s %-10s %-10s %-12s %-10s%n",
                "Process", "Arrival", "Burst", "Finish",
                "Turnaround", "Waiting");
        for (Process p : result.processes) {
            System.out.printf("%-10s %-10d %-10d %-10d %-12d %-10d%n",
                    p.id, p.arrivalTime, p.burstTime, p.finishTime,
                    p.getTurnaroundTime(), p.getWaitingTime());
            System.out.printf("  Response time for %s = %d%n",
                    p.id, p.getResponseTime());
        }

        System.out.printf("%nAverage Waiting Time    = %.2f%n",
                result.averageWaitingTime);
        System.out.printf("Average Turnaround Time = %.2f%n",
                result.averageTurnaroundTime);
        System.out.printf("Average Response Time   = %.2f%n",
                result.averageResponseTime);
    }

    public static void main(String[] args) {
        List<Process> processes = Arrays.asList(
            new Process("P1", 0, 4),
            new Process("P2", 1, 7),
            new Process("P3", 3, 2)
        );

        int contextSwitchTime = 0;

        Result result = schedule(processes, contextSwitchTime);
        printResult(result);
    }
}