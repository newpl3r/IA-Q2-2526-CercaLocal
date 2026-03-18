package src;

import aima.search.framework.GoalTest;

public class ProbGoalTest implements GoalTest {

    public boolean isGoalState(Object state) {
        return ((ProbBoard) state).is_goal();
    }
}
