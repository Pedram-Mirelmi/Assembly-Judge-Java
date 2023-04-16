package ir.ac.ut.cs.assembly.judge;

import io.javalin.Javalin;
import ir.ac.ut.cs.assembly.judge.handlers.SubmitHandler;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Repository repository = new Repository();
        HashMap<String, String> students = new HashMap<>();
        HTMLs.init();

        var app = Javalin.create(/*config*/)
                .get("/", ctx -> ctx.html(HTMLs.getHomePage()))
                .get("/submitPage", ctx -> ctx.html(HTMLs.getSubmitPage()))
                .post("/submit", new SubmitHandler(repository))
                .start(10000);
    }
}