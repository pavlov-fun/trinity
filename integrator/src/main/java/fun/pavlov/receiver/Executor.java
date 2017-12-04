package fun.pavlov.receiver;

import fun.pavlov.pojo.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static configuration.JmsConfiguration.MY_FACTORY;
import static configuration.JmsConfiguration.TASK_QUEUE;

@Component
public class Executor {

    @Value("${calling.execute-path}")
    private String executePath;

    @Value("${calling.repeats-max}")
    private int repeatsMax = 5;

    private final JmsTemplate jmsTemplate;

    private final RestTemplate restTemplate;

    @Autowired
    public Executor(JmsTemplate jmsTemplate, RestTemplate restTemplate) {
        this.jmsTemplate = jmsTemplate;
        this.restTemplate = restTemplate;
    }

    @JmsListener(destination = TASK_QUEUE, containerFactory = MY_FACTORY)
    public Task receiveTask(Task task) {
        return callRest(task);
    }

    Task callRest(Task task) {
        try {
            return callRestOk(task);
        } catch (ResourceAccessException e) {
            return repeat(task);
        }
    }

    Task callRestOk(Task task) throws ResourceAccessException {
        ResponseEntity<Task> taskResponse = restTemplate.postForEntity(executePath, task, Task.class);

        if (HttpStatus.OK.equals(taskResponse.getStatusCode())) {
            System.out.println("Executed <" + taskResponse.getBody() + ">");
            return taskResponse.getBody();
        } else {
            throw new ResourceAccessException("callRestOk unsuccessful");
        }
    }

    Task repeat(Task task) {
        int count = 0;
        while (count < repeatsMax) {
            try {
                return callRestOk(task);
            } catch (ResourceAccessException e) {
                System.out.println("Failed while repeat<" + task + ">");
            }
            count++;
        }
        jmsTemplate.convertAndSend(TASK_QUEUE, task.corrupt());
        return task;
    }

}
