package com.cloudq.cloudq.ai.model;


import org.optaplanner.core.api.score.ScoreDirector;
import org.optaplanner.core.api.score.ScoreDirectorFactory;
import org.optaplanner.core.api.score.constraint.ConstraintProvider;
import org.optaplanner.core.api.score.constraint.Constraint;
import org.optaplanner.core.api.score.constraint.ConstraintFactory;
import org.optaplanner.core.api.score.constraint.ConstraintWeight;
import org.optaplanner.core.api.score.constraint.ConstraintConfigurationProvider;
import org.optaplanner.core.api.score.constraint.Solution;
import org.optaplanner.core.api.score.constraint.Solver;
import org.optaplanner.core.api.score.constraint.SolverFactory;
import org.optaplanner.core.api.score.constraint.SolverConfig;
import org.optaplanner.core.api.score.constraint.SolverFactoryConfig;
import org.optaplanner.core.api.score.constraint.SolverResult;
import org.optaplanner.core.api.score.constraint.SolverManager;

import java.util.List;

public class OptaPlannerOptimizationModel {

    private final Solver<OptimizationTask> solver;

    public OptaPlannerOptimizationModel(SolverFactory<OptimizationTask> solverFactory) {
        this.solver = solverFactory.buildSolver();
    }

    public OptimizationTask solve(OptimizationTask task) {
        return solver.solve(task);
    }
}