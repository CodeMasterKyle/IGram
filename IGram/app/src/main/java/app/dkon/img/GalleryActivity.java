/* package app.dkon.img;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private List<GalleryItem> galleryItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        recyclerView = findViewById(R.id.recycler_view);

        // Устанавливаем GridLayoutManager с 3 колонками
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 колонки

        galleryItems = new ArrayList<>();
        galleryAdapter = new GalleryAdapter(galleryItems);
        recyclerView.setAdapter(galleryAdapter);

        // Получаем данные пользователя из Intent
        String accountId = getIntent().getStringExtra("accountId");
        String accessToken = getIntent().getStringExtra("accessToken");

        // Проверка на null
        if (accountId == null || accessToken == null) {
            Toast.makeText(this, "Необходимые данные отсутствуют", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получаем фотографии
        getGallery(accountId, accessToken);
    }


    private void getGallery(String accountId, String accessToken) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String bodyString = "clientId=1302&accountId=" + accountId + "&accessToken=" + accessToken + "&profileId=" + accountId;
        RequestBody body = RequestBody.create(mediaType, bodyString);

        Request request = new Request.Builder()
                .url("https://api.dkon.app/api/v2/method/gallery.get")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GalleryActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            JSONArray items = jsonObject.getJSONArray("items");
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                String imgUrl = item.getString("imgUrl");
                                String id = item.getString("id");
                                // Создаем объект GalleryItem и добавляем его в список
                                // Создаем объект GalleryItem и добавляем его в список
                                galleryItems.add(new GalleryItem(id, imgUrl));
                            }
                            runOnUiThread(() -> galleryAdapter.notifyDataSetChanged());
                        } else {
                            runOnUiThread(() -> Toast.makeText(GalleryActivity.this, "Ошибка получения галереи", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(GalleryActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(GalleryActivity.this, "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}



 */
package app.dkon.img;

import android.content.Intent;
import android.hardware.camera2.CameraDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.bumptech.glide.Glide; // Убедитесь, что вы добавили Glide в зависимости

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GalleryAdapter galleryAdapter;
    private List<GalleryItem> galleryItems;
    private ImageView profileImageView;
    private TextView fullnameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ///
        Button buttonGallery = findViewById(R.id.button_gallery);
        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Запускаем CameraActivity
                Intent intent = new Intent(GalleryActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        ///

        // Инициализация элементов интерфейса
        profileImageView = findViewById(R.id.profileImageView);
        fullnameTextView = findViewById(R.id.fullnameTextView);
        recyclerView = findViewById(R.id.recycler_view);

        // Устанавливаем GridLayoutManager с 3 колонками
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 колонки

        galleryItems = new ArrayList<>();
        galleryAdapter = new GalleryAdapter(galleryItems);
        recyclerView.setAdapter(galleryAdapter);

        // Получаем данные пользователя из Intent
        String accountId = getIntent().getStringExtra("accountId");
        String accessToken = getIntent().getStringExtra("accessToken");
        String fullname = getIntent().getStringExtra("fullname");
        String normalPhotoUrl = getIntent().getStringExtra("normalPhotoUrl");

        // Проверка на null
        if (accountId == null || accessToken == null) {
            Toast.makeText(this, "Необходимые данные отсутствуют", Toast.LENGTH_SHORT).show();
            return;
        }

        // Устанавливаем имя пользователя
        fullnameTextView.setText(fullname);

        // Устанавливаем аватарку
        if (normalPhotoUrl != null && !normalPhotoUrl.isEmpty()) {
            Glide.with(this)
                    .load(normalPhotoUrl)
                    .placeholder(R.drawable.placeholder) // Замените на ваш ресурс-заполнитель, если нужно
                    .into(profileImageView);
        } else {
            // Если фото профиля нет, используем стандартное изображение
            Glide.with(this)
                    .load("https://res.dkon.app/img/profile_default_photo.png")
                    .into(profileImageView);
        }

        // Получаем фотографии
        getGallery(accountId, accessToken);
    }

    private void getGallery(String accountId, String accessToken) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String bodyString = "clientId=1302&accountId=" + accountId + "&accessToken=" + accessToken + "&profileId=" + accountId;
        RequestBody body = RequestBody.create(mediaType, bodyString);

        Request request = new Request.Builder()
                .url("https://api.dkon.app/api/v2/method/gallery.get")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(GalleryActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            JSONArray items = jsonObject.getJSONArray("items");
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject item = items.getJSONObject(i);
                                String imgUrl = item.getString("imgUrl");
                                String id = item.getString("id");
                                // Создаем объект GalleryItem и добавляем его в список
                                galleryItems.add(new GalleryItem(id, imgUrl));
                            }
                            runOnUiThread(() -> galleryAdapter.notifyDataSetChanged());
                        } else {
                            runOnUiThread(() -> Toast.makeText(GalleryActivity.this, "Ошибка получения галереи", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(GalleryActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(GalleryActivity.this, "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}

