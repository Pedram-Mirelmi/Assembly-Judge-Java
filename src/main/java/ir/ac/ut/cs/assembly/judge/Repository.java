package ir.ac.ut.cs.assembly.judge;
;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Repository implements IRepository {
    private Map<String, Integer> problems;
    private Map<String, String> students;

    public Repository() throws IOException {
        Gson gson = new Gson();
        problems = gson.fromJson(new String(Files.readAllBytes(Paths.get("./data/problems.json"))), new TypeToken<Map<String, Integer>>(){}.getType());
        students = gson.fromJson(new String(Files.readAllBytes(Paths.get("./data/students.json"))), new TypeToken<Map<String, String>>(){}.getType());
    }


    @Override
    public boolean authStudent(String studentId, String password) throws IOException {
        students = new Gson().fromJson(new String(Files.readAllBytes(Paths.get("./data/students.json"))), new TypeToken<Map<String, String>>(){}.getType());
        return students.containsKey(studentId) && students.get(studentId).equals(password);
    }

    @Override
    public boolean isValidProblem(String problemName) throws IOException {
        return (problems = new Gson().fromJson(new String(Files.readAllBytes(Paths.get("./data/problems.json"))), new TypeToken<Map<String, Integer>>(){}.getType()))
                .containsKey(problemName);
    }



    @Override
    public Map<String, Integer> getProblems() throws IOException {
        return problems = new Gson().fromJson(new String(Files.readAllBytes(Paths.get("./data/problems.json"))), new TypeToken<Map<String, Integer>>(){}.getType());
    }

    @Override
    public long getProblemTimeLimit(String problemName) throws IOException {
        return (problems = new Gson().fromJson(new String(Files.readAllBytes(Paths.get("./data/problems.json"))), new TypeToken<Map<String, Integer>>(){}.getType())).get(problemName);
    }
}
