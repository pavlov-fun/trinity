package fun.pavlov.controller;

import fun.pavlov.pojo.Task;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExecuteController {

    @PostMapping("/execute")
    public Task execute(@RequestBody Task task) {
        return task.execute();
    }
}
