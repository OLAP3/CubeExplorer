package com.olap3.cubeexplorer.evaluate;

import com.olap3.cubeexplorer.model.ECube;

import java.util.List;

/**
 * Handle actual query eval (jdbc, mondrian ...) and running ML libs here
 */
interface Evaluator {
    boolean setup(ExecutionPlan p, boolean reoptEnable);
    List<ECube> evaluate();
    EXPRunStats getStats();

}
