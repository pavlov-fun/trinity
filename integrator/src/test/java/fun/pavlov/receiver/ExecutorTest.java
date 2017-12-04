package fun.pavlov.receiver;

import fun.pavlov.pojo.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static configuration.JmsConfiguration.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExecutorTest {

    @Autowired
    Executor executor;

    @MockBean
    private JmsTemplate jmsTemplate;

    @MockBean
    private RestTemplate restTemplate;

    @Value("${calling.execute-path}")
    private String executePath;

    @Value("${calling.repeats-max}")
    private int repeatsMax;

    @Test
    public void successfulReceivingTheTaskAndGetExecutedTask() {
        Task task = new Task(9000L, "Over 9000!");

        ResponseEntity<Task> executed = new ResponseEntity<Task>(new Task(9000L, "Over 9000!").execute(), HttpStatus.OK);

        given(this.restTemplate.postForEntity(executePath, task, Task.class)).willReturn(executed);
        Task receivedTask = executor.receiveTask(task);

        assertThat(receivedTask.getStatus()).isEqualTo(Task.TaskStatus.EXECUTED);
    }

    @Test
    public void unsuccessfulReceivingTheTaskAndGetCorruptedTask() {
        Task task = new Task(9000L, "Over 9000!");

        given(this.restTemplate.postForEntity(executePath, task, Task.class)).willThrow(ResourceAccessException.class);
        Task receivedTask = executor.receiveTask(task);

        assertThat(receivedTask.getStatus()).isEqualTo(Task.TaskStatus.CORRUPTED);
    }

    @Test
    public void unsuccessfulReceivingTheTaskAndCheckWhetherItStartedFewTimes() {
        Task task = new Task(9000L, "Over 9000!");
        given(this.restTemplate.postForEntity(executePath, task, Task.class)).willThrow(ResourceAccessException.class);
        Task receivedTask = executor.receiveTask(task);

        verify(restTemplate, times(Integer.valueOf(repeatsMax + 1))).postForEntity(executePath, task, Task.class);
    }

    @Test
    public void unsuccessfulReceivingTheTaskAndCheckItPutedInQueue() {
        Task task = new Task(9000L, "Over 9000!");
        given(this.restTemplate.postForEntity(executePath, task, Task.class)).willThrow(ResourceAccessException.class);
        Task receivedTask = executor.receiveTask(task);

        verify(jmsTemplate, times(Integer.valueOf(1))).convertAndSend(TASK_QUEUE, task.corrupt());
    }

}