package ir.ac.ut.cs.assembly.judge.handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import ir.ac.ut.cs.assembly.judge.HTMLs;
import ir.ac.ut.cs.assembly.judge.IRepository;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;

public class SubmitPageHandler implements Handler {
    IRepository repository;

    public SubmitPageHandler(IRepository repository) {
        this.repository = repository;
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {
        if(context.method().equals(HandlerType.GET)) {
            var page = Jsoup.parse(HTMLs.getSubmitPage());
            var selectElement = page.getElementById("problemName");
            var choice = selectElement.child(1);
            for (var pair : repository.getProblems().entrySet()) {
                var newChoice = choice.clone();
                newChoice.attr("value", pair.getKey());
                newChoice.text(pair.getKey());
                selectElement.appendChild(newChoice);
            }
            context.html(page.html());
        }
        else {
            context.html("No other request types are supported except POST");
        }
    }
}
