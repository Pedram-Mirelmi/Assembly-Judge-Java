package ir.ac.ut.cs.assembly.judge;

import java.io.IOException;
import java.util.Map;

public interface IRepository {
    boolean authStudent(String studentId, String password) throws IOException;

    boolean isValidProblem(String problemName) throws IOException;

    Map<String, Integer> getProblems() throws IOException;


    long getProblemTimeLimit(String problemName) throws IOException;
}
