package pe.edu.pucp.lab7.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitClient {

    // 10.0.2.2 = "localhost de la PC" visto desde el emulador.
    // En un celular físico: poner la IP LAN de la PC (ej. http://192.168.1.10:8002/).
    private static final String BASE_URL = "http://10.0.2.2:8002/";

    private static AvanceApi api;

    private RetrofitClient() { }

    public static AvanceApi api() {
        if (api == null) {
            api = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(AvanceApi.class);
        }
        return api;
    }
}
