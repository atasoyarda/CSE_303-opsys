import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class c20180808045 {
    public static void main(String[] args) {
        List<Integer> processIds = new ArrayList<>();
        LinkedHashMap<Integer, int[]> processReturns = new LinkedHashMap<>();
        Map<Integer, List<Integer>> cpu_map = new HashMap<>();
        Map<Integer, List<Integer>> io_map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(args[0]))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                int process_number = Integer.parseInt(parts[0]);
                processIds.add(process_number);
                processReturns.put(process_number, new int[]{0, 0, 0});
                String[] pairs = parts[1].split(";");
                List<Integer> cpu_bursts = new ArrayList<>();
                List<Integer> io_bursts = new ArrayList<>();
                for (String pair : pairs) {
                    String[] bursts = pair.replaceAll("[()]", "").split(",");
                    int cpu_burst = Integer.parseInt(bursts[0]);
                    int io_burst = Integer.parseInt(bursts[1]);
                    cpu_bursts.add(cpu_burst);
                    io_bursts.add(io_burst);
                }
                cpu_map.put(process_number, cpu_bursts);
                io_map.put(process_number, io_bursts);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int currentId = 0;
        int beginningTime = 0;
        int processAmount=processIds.size();
        int iter = 0;
        int idle = 0;
        int totalTurnRoundTimes=0;
        int totalWaitTimes=0;
        //Collections.sort(processIds);
        while (!processIds.isEmpty()) {
            currentId = currentId % processIds.size();
            int processId = processIds.get(currentId);
            int cpuTime = cpu_map.get(processId).get(processReturns.get(processId)[0]);
            int ioTime = io_map.get(processId).get(processReturns.get(processId)[0]);
            if (processReturns.get(processId)[1] <= beginningTime) {
                if (ioTime != -1) {
                    int processTime = cpuTime + ioTime;
                    int returnTime = beginningTime + processTime;
                    processReturns.get(processId)[1] = returnTime;
                    beginningTime += cpuTime;
                    processReturns.get(processId)[0]++;//refers next index
                    processReturns.get(processId)[2]+=cpuTime;
                    processIds.remove(0);
                    processIds.add(processId);
                } else {
                    processReturns.get(processId)[1] = beginningTime + cpuTime;
                    totalTurnRoundTimes+=processReturns.get(processId)[1];
                    //System.out.println("Turnround time for PID-"+processId+": --> "+processReturns.get(processId)[1]);
                    processReturns.get(processId)[2]+=cpuTime;
                    beginningTime += cpuTime;
                    totalWaitTimes+=(beginningTime-processReturns.get(processId)[2]);
                    processIds.remove(currentId);
                    //processReturns.remove(processId);
                    cpu_map.remove(processId);
                    io_map.remove(processId);
                }
                currentId--;
                iter = 0;
            } else {
                iter++;
                if (iter == processIds.size()) {
                    int s = processIds.get(0);
                    processIds.remove(0);
                    processIds.add(s);
                    int tempProcessId = processIds.get(0);
                    int min = processReturns.get(tempProcessId)[1];
                    int minIndex = 0;
                    for (int k = 1; k < processIds.size(); k++) {
                        tempProcessId = processIds.get(k);
                        if (min > processReturns.get(tempProcessId)[1]) {
                            min = processReturns.get(tempProcessId)[1];
                            minIndex = k;
                        }
                    }
                    beginningTime += (min - beginningTime);
                    idle++;
                    iter = 0;
                    currentId = minIndex - 1;
                }
            }
            currentId++;
        }
        System.out.println("Average Turnround time: "+(double)totalTurnRoundTimes/processAmount);
        System.out.println("Average Waiting time: "+(double)totalWaitTimes/processAmount);
        System.out.println("IDLE process amount: " + idle);
        System.out.println("HALT");
    }
}
