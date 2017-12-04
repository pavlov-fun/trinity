package fun.pavlov;

import fun.pavlov.pojo.Task;
import org.junit.Ignore;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static org.junit.Assert.*;

public class TaskTest {

    @Test
    @Ignore
    public void create() throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://localhost:9001")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        TaskService service = retrofit.create(TaskService.class);
        Call<Task> taskCall = service.create(new Task(null, "body"));
        Response<Task> taskResponse = taskCall.execute();

        assertTrue(taskCall.isExecuted());
        System.out.println(taskCall.isExecuted());

        assertEquals(200, taskResponse.code());
        System.out.println(taskResponse.code());

        assertNotNull(taskResponse.body().getUid());
        System.out.println(taskResponse.body());
    }
}