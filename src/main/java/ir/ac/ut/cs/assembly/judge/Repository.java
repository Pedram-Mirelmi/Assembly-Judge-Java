package ir.ac.ut.cs.assembly.judge;
;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.checkerframework.checker.units.qual.A;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Repository implements IRepository {
    private List<String> problemNames;
    private Map<String, String> students;

    public Repository() throws IOException {
        Gson gson = new Gson();

        problemNames = Arrays.asList(gson.fromJson(new String(Files.readAllBytes(Paths.get("./data/problemNames.json"))), String[].class));
        students = gson.fromJson(new String(Files.readAllBytes(Paths.get("./data/students.json"))), new TypeToken<Map<String, String>>(){}.getType());
    }


    @Override
    public boolean authStudent(String studentId, String password) {
        return students.containsKey(studentId) && students.get(studentId).equals(password);
    }

    @Override
    public boolean isValidProblem(String problemName) {
        return problemNames.contains(problemName);
    }

    @Override
    public List<String> getProblemNames() {
        return problemNames;
    }
}
