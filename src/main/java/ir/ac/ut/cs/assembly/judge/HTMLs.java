package ir.ac.ut.cs.assembly.judge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HTMLs {
    private static String submitPage;
    private static String homePage;
    private static String errorPage;
    private static String resultPage;

    public static String getSubmitPage() {
        return submitPage;
    }

    public static void init() throws IOException {
        submitPage = new String(Files.readAllBytes(Paths.get("./templates/submission.html")));
        homePage = new String(Files.readAllBytes(Paths.get("./templates/index.html")));
        errorPage = new String(Files.readAllBytes(Paths.get("./templates/Error.html")));
        resultPage = new String(Files.readAllBytes(Paths.get("./templates/resultPage.html")));
    }

    public static String getHomePage() {
        return homePage;
    }

    public static String getErrorPage() {
        return errorPage;
    }

    public static String getResultPage() {
        return resultPage;
    }
}
