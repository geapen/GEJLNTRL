

public class DirectClock {
    public int[] clock;
    int myId;
    public DirectClock(int numProc, int id) {
        myId = id;
        clock = new int[numProc];
        for (int i = 0; i < numProc; i++) {
        	clock[i] = 0;
        }
        clock[myId] = 1;
    }
    public int getValue(int i) {
        return clock[i];
    }
    public void tick() {
        clock[myId]++;
    }
    public void sendAction() {
        // sentValue = clock[myId];
        tick();
    }
    public void receiveAction(int sender, int sentValue) {
        clock[sender] = max(clock[sender], sentValue);
        clock[myId] = max(clock[myId], sentValue) + 1;
    }
    
	public String toString() {
		return "TS-" + this.clock + ";" + "ServerID-" + this.myId;
	}
    
    static int max (int a, int b) {
    	if (a > b) return a;
    	return b;
    }
}