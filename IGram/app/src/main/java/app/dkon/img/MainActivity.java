package app.dkon.img;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;

    private static final String BASE_URL = "https://api.dkon.app/api/v2/method/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login_button);
        registerButton = findViewById(R.id.register_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void login() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String bodyString = "clientId=1302&username=" + username + "&password=" + password + "&appType=1&lang=ru";
        RequestBody body = RequestBody.create(mediaType, bodyString);

        Request request = new Request.Builder()
                .url(BASE_URL + "account.signIn")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка сети: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            String accessToken = jsonObject.getString("accessToken");
                            String accountId = jsonObject.getString("accountId");
                           String fullname = jsonObject.getJSONArray("account").getJSONObject(0).getString("fullname");
                            String username = jsonObject.getJSONArray("account").getJSONObject(0).getString("username");
                            String normalPhotoUrl = jsonObject.getJSONArray("account").getJSONObject(0).getString("normalPhotoUrl");
                            // Сохраните токен и ID аккаунта для дальнейшего использования
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("ACCESS_TOKEN", accessToken);
                            editor.putString("ACCOUNT_ID", accountId);
                            editor.apply();


                            /*
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show());
                        } else {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка авторизации", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка обработки данных: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка сервера: " + response.message(), Toast.LENGTH_SHORT).show());
                }
           }*/
                         //   Intent intent;
                           // startActivity(intent);
                            Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                            intent.putExtra("accountId", accountId);
                            intent.putExtra("accessToken", accessToken);
                            intent.putExtra("fullname", fullname);
                            intent.putExtra("username", username);
                            intent.putExtra("normalPhotoUrl", normalPhotoUrl); //userphoto
                            startActivity(intent);
                            finish();//
                        } else {
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка авторизации", Toast.LENGTH_SHORT).show());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show());
                }
            } else {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка сервера", Toast.LENGTH_SHORT).show());
            }

}
        });
    }

   /* private void register() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String email = "example@example.com"; // Замените на реальный ввод пользователя
        String fullname = "Имя"; // Замените на реальный ввод пользователя
        String hash = "U2F5YSBzdWthIGtldGlrYSBBbGV4YSBtZW5pZHVyaSBzYXlh"; // Замените на реальный хэш

        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || fullname.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String bodyString = "clientId=1302&hash=" + hash + "&appType=1&lang=ru&username=" + username + "&fullname=" + fullname + "&password=" + password + "&email=" + email;
        RequestBody body = RequestBody.create(mediaType, bodyString);

        Request request = new Request.Builder()
                .url(BASE_URL + "account.signUp")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка сети: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            String accessToken = jsonObject.getString("accessToken");
                            String accountId = jsonObject.getString("accountId");
                            String fullname = jsonObject.getJSONArray("account").getJSONObject(0).getString("fullname");
                            String username = jsonObject.getJSONArray("account").getJSONObject(0).getString("username");
                            /*
                            // Сохраните токен и ID аккаунта для дальнейшего использования
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Регистрация успешна!", Toast.LENGTH_SHORT).show());
                        } else {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка обработки данных: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка сервера: " + response.message(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

/*/
            /*                // Переход в GalleryActivity
                            Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                            intent.putExtra("accountId", accountId);
                            intent.putExtra("accessToken", accessToken);
                            intent.putExtra("fullname", fullname);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            finish(); // Закрываем MainActivity
                        } else {
                            runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Ошибка сервера", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

*/
   private void register() {
       String url = "https://dkon.app/signup";
       Intent intent = new Intent(Intent.ACTION_VIEW);
       intent.setData(Uri.parse(url));
       startActivity(intent);
   }


}


