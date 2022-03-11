package algo_questions;

import java.util.Arrays;

public class Solutions {
    /**
     * Method computing the maximal amount of tasks out of n tasks that can be completed with m time slots.
     * @param tasks describing the time it takes to complete the tasks (task[i] = time for task i)
     * @param timeSlots describing the time in hours for every timeslot
     * @return maximal amount of tasks that can be completed
     */
    public static int alotStudyTime(int[] tasks, int[] timeSlots){
        Arrays.sort(tasks);
        Arrays.sort(timeSlots);
        int allocatedTasks = 0;
        int tasksIndex = 0;
        int timeIndex = 0;
        while(tasksIndex < tasks.length && timeIndex < timeSlots.length){
            if(tasks[tasksIndex] <= timeSlots[timeIndex]){
                allocatedTasks++;
                tasksIndex++;
            }
            timeIndex++;
        }
        return allocatedTasks;
    }
    
    /**
     *
     * Method computing the number of ways to fill the water trough
     * @param n number of liters in the water trough
     * @return number of possible ways
     */
    public static int bucketWalk(int n){
        if(n == 0 || n == 1){
            return 1;
        }
        int[] possibleWays = new int[n + 1];
        possibleWays[0] = possibleWays[1] = 1;
        for(int i = 2; i < n + 1; i++){
            possibleWays[i] = possibleWays[i - 2] + possibleWays[i - 1];
        }
        return possibleWays[n];
    }
    
    /**
     * Method computing the nim amount of leaps a frog needs to jump across n waterlily leaves, from leaf 1
     * to leaf n.
     * @param leapNum array containing the distances possible to jump from every leaf
     * @return number of minimal jumps
     */
    public static int minLeap(int[] leapNum){
        if(leapNum.length == 1){
            return 0;
        }
        int maxDistance = 0;
        int placeValue = 0;
        int numberOfHops = 0;
        
        for(int i = 0; i < leapNum.length - 1; i++){
            maxDistance = Math.max(maxDistance, i + leapNum[i]);
            if(leapNum.length + 1 <= maxDistance){
                if(numberOfHops == 0){
                    numberOfHops = 1;
                }
                return numberOfHops;
            }
            
            if(placeValue == i){
                placeValue = maxDistance;
                maxDistance = 0;
                numberOfHops++;
            }
        }
        return numberOfHops;
    }
    
    /**
     * Method computing the number of unique BST with nodes from 1 to n
     * @param n max value for a node in the tree
     * @return number of unique BSTs
     */
    public static int numTrees(int n){
        if(n == 0){
            return 0;
        }
        if(n == 1){
            return 1;
        }
        int[] numTreesUpToN = new int[n + 1];
        numTreesUpToN[0] = 1;
        numTreesUpToN[1] = 1;
        for(int i = 2; i <= n; i++){
            for(int j = 1; j <= i; j++){
                numTreesUpToN[i] += numTreesUpToN[j - 1] * numTreesUpToN[i - j];
            }
        }
        return numTreesUpToN[n];
    }
}
