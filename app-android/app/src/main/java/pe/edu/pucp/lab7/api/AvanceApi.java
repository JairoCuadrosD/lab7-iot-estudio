package pe.edu.pucp.lab7.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AvanceApi {

    // GET http://10.0.2.2:8002/avance/{codigo}/{horas}
    @GET("avance/{codigo}/{horas}")
    Call<AvanceResponse> evaluar(@Path("codigo") String codigo, @Path("horas") double horas);
}
