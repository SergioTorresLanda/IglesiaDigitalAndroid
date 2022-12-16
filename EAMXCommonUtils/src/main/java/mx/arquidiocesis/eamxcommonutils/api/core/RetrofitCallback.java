package mx.arquidiocesis.eamxcommonutils.api.core;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RetrofitCallback <T> implements Callback<T> {

    private BaseCallback<T> callback;

    public RetrofitCallback(BaseCallback<T> baseCallback) {
        this.callback = baseCallback;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        String messageError="Error en el servidor remoto, por favor intente de nuevo";
        if (response.isSuccessful()) {
            callback.onSuccess(response.body());
        } else {
            try{
//                Status status = new Status();
                if (response.code() == 404 || response.code() == 503 || response.code() == 409) {

                    if (response.errorBody() != null && response.errorBody().string().contains("message")) {
                        JsonElement mJson = new JsonParser().parse(response.errorBody().string());
//                        status = new Gson().fromJson(mJson, Status.class);

//                        messageError=status.getMessage();

                    }

                }
                callback.onFailure(messageError);
            }catch (Exception e) {
                e.printStackTrace();
                messageError=e.getMessage();
                callback.onFailure(messageError);
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        String messageError="Error en el servidor remoto, por favor intente de nuevo";
        if(t instanceof UnknownHostException || t instanceof SSLException || t instanceof TimeoutException
                || t instanceof SocketTimeoutException || t instanceof InterruptedIOException){
            messageError="Tu conexión a la red ha fallado, por favor intenta nuevamente";
        }

        callback.onFailure(messageError);
    }
}
