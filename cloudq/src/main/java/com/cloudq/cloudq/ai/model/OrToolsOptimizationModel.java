package com.cloudq.cloudq.ai.model;

import com.google.ortools.constraintsolver.*;

public class OrToolsOptimizationModel {

    static {
        // Initialize native libraries for OR-Tools.
        System.loadLibrary("jniortools");  // Ensure the OR-Tools JNI library is loaded
    }

    private final Solver solver;

    public OrToolsOptimizationModel() {
        this.solver = new Solver("SchedulingProblem");
    }

    public void solveSchedulingProblem() {
        // Define the variables and constraints for a scheduling problem

        // Example: Schedule for 3 tasks
        int numTasks = 3;
        int maxTime = 10; // Maximum time (for example, hours)

        // Define variables for task start times
        IntVar[] startTimes = new IntVar[numTasks];
        for (int i = 0; i < numTasks; i++) {
            startTimes[i] = solver.makeIntVar(0, maxTime, "start_time_" + i);
        }

        // Define constraints: For example, Task 0 should start before Task 1
        solver.addConstraint(solver.makeLessOrEqual(startTimes[0], startTimes[1]));

        // Example constraint: Task durations and gaps between them
        solver.addConstraint(solver.makeGreaterOrEqual(startTimes[1], startTimes[0].var()));  // Task 1 starts 2 hours after Task 0

        // Define objective function, e.g., minimize the latest task start time
        IntVar endTime = (IntVar) solver.makeMax(startTimes);
        OptimizeVar objective = solver.makeMinimize(endTime, 1);

        // Solve the problem
        DecisionBuilder db = solver.makePhase(startTimes, Solver.CHOOSE_FIRST_UNBOUND, Solver.ASSIGN_MIN_VALUE);
        SolutionCollector solutionCollector = solver.makeLastSolutionCollector();
        solutionCollector.addObjective(endTime);
        for (IntVar var : startTimes) {
            solutionCollector.add(var);
        }

        if (solver.solve(db, solutionCollector, objective)) {
            System.out.println("Solution found!");
            for (int i = 0; i < numTasks; i++) {
                System.out.printf("Task %d start time: %d\n", i, solutionCollector.value(0, startTimes[i]));
            }
        } else {
            System.out.println("No solution found.");
        }
    }

    public static void main(String[] args) {
        OrToolsOptimizationModel optimizationModel = new OrToolsOptimizationModel();
        optimizationModel.solveSchedulingProblem();
    }
}