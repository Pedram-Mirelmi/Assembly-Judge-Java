package ir.ac.ut.cs.assembly.judge.handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import ir.ac.ut.cs.assembly.judge.HTMLs;
import ir.ac.ut.cs.assembly.judge.IRepository;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import org.jsoup.Jsoup;

import javax.swing.text.html.HTML;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
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
            if(!repository.authStudent(studentId, password)) {
                context.html(HTMLs.getAuthFailedPage());
                return;
            }
            try {
                var pair = testCode(studentId, password, code, problemName);
                var page = Jsoup.parse(HTMLs.getResultPage());
                page.getElementById("passCount").text(pair.getFirst().toString());
                page.getElementById("total").text(pair.getSecond().toString());
                context.html(page.html());
            }
            catch (IllegalArgumentException e) {
                context.html(HTMLs.getAuthFailedPage());
            }
        }
        else {
            context.html("No other request types are supported except POST");
        }
    }


    private Pair<Integer, Integer> testCode(String studentId, String password, String code, String problemName) throws IOException, InterruptedException {
        if(!repository.isValidProblem(problemName)) {
            throw new IllegalArgumentException("Invalid student id");
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

        Runtime.getRuntime().exec(String.format("nasm -f elf64 %s -o %s/code.o", archiveFilePath, runPath)).waitFor();
        Runtime.getRuntime().exec(String.format("ld %s/code.o -e _start -o %s/a.out", runPath, runPath)).waitFor();

        String testCasesPath = String.format("./data/testCases/%s", problemName);

        var inputFileNames = new File(String.format("%s/in", testCasesPath)).list();

        int correctAnswers = 0;

        for (int i = 0; i < Objects.requireNonNull(inputFileNames).length; i++) {
            ProcessBuilder builder = new ProcessBuilder(String.format("%s/a.out", runPath));
            // Redirect input and output streams
            builder.redirectInput(new File(String.format("%s/in/input%s.txt", testCasesPath, i+1)));
            builder.redirectOutput(new File(String.format("%s/output.txt", runPath)));

            Process process = builder.start();
            // Start the process

            // Wait for the process to complete
            boolean exitCode = process.waitFor(1, TimeUnit.SECONDS);
            if(exitCode) {
                String expected = new String(Files.readAllBytes(Paths.get(String.format("%s/out/output%s.txt", testCasesPath, i+1))));
                String actual = new String(Files.readAllBytes(Paths.get(String.format("%s/output.txt", runPath))));
                if(expected.equals(actual)) {
                    correctAnswers++;
                }
            }
        }
        return new Pair<>(correctAnswers, inputFileNames.length);
    }
}
