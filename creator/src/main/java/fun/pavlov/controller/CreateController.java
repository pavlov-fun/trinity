package fun.pavlov.controller;

import fun.pavlov.pojo.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

import static configuration.JmsConfiguration.*;


@RestController
public class CreateController {

    private final AtomicLong counter = new AtomicLong();

    private final JmsTemplate jmsTemplate;

    @Autowired
    public CreateController(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    @PostMapping("/create")
    public Task create(@RequestBody Task task) {
        task.setUid(counter.incrementAndGet());
        jmsTemplate.convertAndSend(TASK_QUEUE, task);
        return task;
    }
}
