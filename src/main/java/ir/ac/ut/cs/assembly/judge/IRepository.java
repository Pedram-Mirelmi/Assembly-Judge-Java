package ir.ac.ut.cs.assembly.judge;

import java.util.Map;

public interface IRepository {
    boolean authStudent(String studentId, String password);

    boolean isValidProblem(String problemName);

    Map<String, Integer> getProblems();


    long getProblemTimeLimit(String problemName);
}
