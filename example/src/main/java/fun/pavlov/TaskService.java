package fun.pavlov;

import fun.pavlov.pojo.Task;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TaskService {

    @POST("create")
    Call<Task> create(@Body Task task);
}
