
public class ACSProcess {

    String processId;

    int arrivalTime;
    int burstTime;
    int remainingTime;

    int initialPriority;
    int effectivePriority;

    int tier2EntryTime = -1;

    int startTime = -1;
    int finishTime = -1;

    boolean completed = false;

    public ACSProcess(String processId,
                      int arrivalTime,
                      int burstTime,
                      int priority) {

        this.processId = processId;
        this.arrivalTime = arrivalTime;

        this.burstTime = burstTime;
        this.remainingTime = burstTime;

        this.initialPriority = priority;
        this.effectivePriority = priority;
    }
}
