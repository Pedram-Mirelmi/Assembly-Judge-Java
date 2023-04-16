package ir.ac.ut.cs.assembly.judge;

import java.util.List;
import java.util.SplittableRandom;

public interface IRepository {
    boolean authStudent(String studentId, String password);

    boolean isValidProblem(String problemName);

    List<String> getProblemNames();



}
