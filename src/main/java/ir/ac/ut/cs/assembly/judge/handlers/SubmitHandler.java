package ir.ac.ut.cs.assembly.judge.handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import ir.ac.ut.cs.assembly.judge.HTMLs;
import ir.ac.ut.cs.assembly.judge.IRepository;
import org.jetbrains.annotations.NotNull;

import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class SubmitHandler implements Handler {
    IRepository repository;

    public SubmitHandler(IRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        if(context.method().equals(HandlerType.POST)) {
            String studentId = context.formParam("studentId");
            String password = context.formParam("password");
            String problemName = context.formParam("problemName");
            String code = context.formParam("code");
            try {
                if(!repository.authStudent(studentId, password)) {
                    throw new IllegalArgumentException("Invalid Student Id or password");
                }
                List<String> results = testCode(studentId, password, code, problemName);
                var page = Jsoup.parse(HTMLs.getResultPage());
                var resultList = page.getElementById("resultList");
                var listItem = resultList.child(0);
                for(int i = 0; i < results.size(); i++) {
                    var newItem = listItem.clone();
                    newItem.text(String.format("#%s: %s", i+1, results.get(i)));
                    resultList.appendChild(newItem);
                }
                listItem.remove();
//                page.getElementById("passCount").text(pair.getFirst().toString());
//                page.getElementById("total").text(pair.getSecond().toString());
                context.html(page.html());
            }
            catch (IllegalArgumentException e) {
                var page = Jsoup.parse(HTMLs.getErrorPage());
                System.out.println(e.getMessage());
                page.getElementById("message").text(e.getMessage());
                context.html(page.html());
            }
        }
        else {
            context.html("No other request types are supported except POST");
        }
    }


    private List<String> testCode(String studentId, String password, String code, String problemName) throws IOException, InterruptedException {
        if(!repository.isValidProblem(problemName)) {
            throw new IllegalArgumentException("Problem name");
        }
        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));

        String archiveDir = String.format("./data/archive/%s/", studentId);
        if(!Files.exists(Path.of(archiveDir))) {
            new File(archiveDir).mkdir();
        }

        String archiveFilePath = archiveDir + String.format("%s-%s.asm", problemName, timeNow);
        FileWriter archiveFile = new FileWriter(archiveFilePath);
        archiveFile.write(code);
        archiveFile.close();

        String runPath = String.format("./data/runFolder/%s", studentId);
        if(!Files.exists(Path.of(runPath))) {
            new File(runPath).mkdir();
        }

        if((Runtime.getRuntime().exec(String.format("nasm -f elf64 %s -o %s/code.o", archiveFilePath, runPath)).waitFor()) != 0){
            throw new IllegalArgumentException("Comile error! Check for any include!");
        }

        if((Runtime.getRuntime().exec(String.format("ld %s/code.o -e _start -o %s/a.out", runPath, runPath)).waitFor()) != 0) {
            throw new IllegalArgumentException("Link Error!");
        }

        String testCasesPath = String.format("./data/testCases/%s", problemName);

        var inputFileNames = new File(String.format("%s/in", testCasesPath)).list();

//        int correctAnswers = 0;
        List<String> results = new LinkedList<String>();

        for (int i = 0; i < Objects.requireNonNull(inputFileNames).length; i++) {
//            String command = String.format("%s/a.out < %s/in/input%s.txt", runPath, testCasesPath, i+1);

            ProcessBuilder builder = new ProcessBuilder(String.format("%s/a.out", runPath));
            // Redirect input and output streams
            builder.redirectInput(new File(String.format("%s/in/input%s.txt", testCasesPath, i+1)));
            builder.redirectOutput(new File(String.format("%s/output.txt", runPath)));

            Process process = builder.start();
            // Start the process

            // Wait for the process to complete
            boolean exitCode = process.waitFor(repository.getProblemTimeLimit(problemName), TimeUnit.MILLISECONDS);
            if(exitCode) {
                String expected = new String(Files.readAllBytes(Paths.get(String.format("%s/out/output%s.txt", testCasesPath, i+1))));
                String actual = new String(Files.readAllBytes(Paths.get(String.format("%s/output.txt", runPath))));
                if(expected.equals(actual)) {
//                    correctAnswers++;
                    results.add("Correct");
                }
                else {
//                    System.out.println("=============================================");
//                    System.out.println(String.format("%s: `%s`, `%s`", i+1, actual, expected));
//                    System.out.println("=============================================");
                    results.add("Wrong");
                }
            }
            else {
                results.add("Run time error or time limit");
            }

        }
        return results;
    }
}
