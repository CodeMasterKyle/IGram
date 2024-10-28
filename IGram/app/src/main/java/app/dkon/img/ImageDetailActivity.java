package app.dkon.img;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
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

public class ImageDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView likesTextView;
    private TextView commentsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.imageView);
        likesTextView = findViewById(R.id.likesTextView);
        commentsTextView = findViewById(R.id.commentsTextView);
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String itemId = getIntent().getStringExtra("itemId");
        String accessToken = sharedPreferences.getString("ACCESS_TOKEN", null);
        String accountId = sharedPreferences.getString("ACCOUNT_ID", null);

        if (itemId != null && accessToken != null && accountId != null) {
            getImageDetails(itemId, accessToken, accountId);
        } else {
            Toast.makeText(this, "Необходимые данные отсутствуют", Toast.LENGTH_SHORT).show();
        }
    }

    private void getImageDetails(String itemId, String accessToken, String accountId) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String bodyString = "clientId=1302&accountId=" + accountId + "&accessToken=" + accessToken + "&itemId=" + itemId;
        RequestBody body = RequestBody.create(mediaType, bodyString);

        Request request = new Request.Builder()
                .url("https://api.dkon.app/api/v3/method/gallery.info")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ImageDetailActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            // Извлекаем данные изображения
                            JSONArray items = jsonObject.getJSONArray("items");
                            JSONObject item = items.getJSONObject(0); // Предполагаем, что мы получаем только одно изображение
                            String imgUrl = item.getString("imgUrl");
                            int likesCount = item.getInt("likesCount");
                            int commentsCount = item.getInt("commentsCount");

                            // Извлекаем комментарии
                            JSONObject commentsObject = jsonObject.getJSONObject("comments");
                            JSONArray commentsArray = commentsObject.getJSONArray("comments");
                            StringBuilder commentsBuilder = new StringBuilder();
                            for (int i = 0; i < commentsArray.length(); i++) {
                                JSONObject comment = commentsArray.getJSONObject(i);
                                String commentText = comment.getString("comment");
                                commentsBuilder.append(commentText).append("\n");
                            }

                            runOnUiThread(() -> {
                                Glide.with(ImageDetailActivity.this)
                                        .load(imgUrl)
                                        .into(imageView);
                                likesTextView.setText("Likes: " + likesCount);
                                commentsTextView.setText("Comments: " + commentsCount + "\n" + commentsBuilder.toString());
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(ImageDetailActivity.this, "Ошибка получения данных изображения", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(ImageDetailActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(ImageDetailActivity.this, "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}


/*
package app.dkon.img;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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

public class ImageDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView likesTextView;
    private TextView commentsCountTextView;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.imageView);
        likesTextView = findViewById(R.id.likesTextView);
        commentsCountTextView = findViewById(R.id.commentsCountTextView);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(comments);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);

        String itemId = getIntent().getStringExtra("itemId");
        String accessToken = getIntent().getStringExtra("accessToken");
        String accountId = getIntent().getStringExtra("accountId");

        if (itemId != null && accessToken != null && accountId != null) {
            getImageDetails(itemId, accessToken, accountId);
        } else {
            Toast.makeText(this, "Необходимые данные отсутствуют", Toast.LENGTH_SHORT).show();
        }
    }

    private void getImageDetails(String itemId, String accessToken, String accountId) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String bodyString = "clientId=1302&accountId=" + accountId + "&accessToken=" + accessToken + "&itemId=" + itemId;
        RequestBody body = RequestBody.create(mediaType, bodyString);

        Request request = new Request.Builder()
                .url("https://api.dkon.app/api/v3/method/gallery.info")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ImageDetailActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            // Извлекаем данные изображения
                            JSONArray items = jsonObject.getJSONArray("items");
                            JSONObject item = items.getJSONObject(0); // Предполагаем, что мы получаем только одно изображение
                            String imgUrl = item.getString("imgUrl");
                            int likesCount = item.getInt("likesCount");
                            int commentsCount = item.getInt("commentsCount");

                            // Обновляем UI
                            runOnUiThread(() -> {
                                Glide.with(ImageDetailActivity.this)
                                        .load(imgUrl)
                                        .into(imageView);
                                likesTextView.setText("Likes: " + likesCount);
                                commentsCountTextView.setText("Comments: " + commentsCount);
                            });

                            // Извлекаем комментарии
                            JSONObject commentsObject = jsonObject.getJSONObject("comments");
                            JSONArray commentsArray = commentsObject.getJSONArray("comments");
                            comments.clear(); // Очищаем список перед добавлением новых комментариев
                            for (int i = 0; i < commentsArray.length(); i++) {
                                JSONObject commentJson = commentsArray.getJSONObject(i);
                                String commentText = commentJson.getString("comment");
                                String commenterName = commentJson.getJSONObject("owner").getString("fullname");
                                String commenterImageUrl = commentJson.getJSONObject("owner").optString("normalPhotoUrl", null);

                                // Добавляем комментарий в список
                                comments.add(new Comment(commentText, commenterName, commenterImageUrl));
                            }
                            // Обновляем адаптер комментариев на UI потоке
                            runOnUiThread(() -> {
                                commentAdapter.notifyDataSetChanged();
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(ImageDetailActivity.this, "Ошибка получения данных изображения", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(ImageDetailActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(ImageDetailActivity.this, "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            };


        }
    }
}


*/
/*
package app.dkon.img;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
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

public class ImageDetailActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView likesTextView;
    private TextView commentsCountTextView;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.imageView);
        likesTextView = findViewById(R.id.likesTextView);
        commentsCountTextView = findViewById(R.id.commentsCountTextView);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);

        comments = new ArrayList<>();
        commentAdapter = new CommentAdapter(comments);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentAdapter);

        String itemId = getIntent().getStringExtra("itemId");
        String accessToken = getIntent().getStringExtra("accessToken");
        String accountId = getIntent().getStringExtra("accountId");

        if (itemId != null && accessToken != null && accountId != null) {
            getImageDetails(itemId, accessToken, accountId);
        } else {
            Toast.makeText(this, "Необходимые данные отсутствуют", Toast.LENGTH_SHORT).show();
        }
    }

    private void getImageDetails(String itemId, String accessToken, String accountId) {
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String bodyString = "clientId=1302&accountId=" + accountId + "&accessToken=" + accessToken + "&itemId=" + itemId;
        RequestBody body = RequestBody.create(mediaType, bodyString);

        Request request = new Request.Builder()
                .url("https://api.dkon.app/api/v3/method/gallery.info")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ImageDetailActivity.this, "Ошибка сети", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        boolean error = jsonObject.getBoolean("error");

                        if (!error) {
                            // Извлекаем данные изображения
                            JSONArray items = jsonObject.getJSONArray("items");
                            JSONObject item = items.getJSONObject(0); // Предполагаем, что мы получаем только одно изображение
                            String imgUrl = item.getString("imgUrl");
                            int likesCount = item.getInt("likesCount");
                            int commentsCount = item.getInt("commentsCount");

                            // Обновляем UI
                            runOnUiThread(() -> {
                                Glide.with(ImageDetailActivity.this)
                                        .load(imgUrl)
                                        .into(imageView);
                                likesTextView.setText("Likes: " + likesCount);
                                commentsCountTextView.setText("Comments: " + commentsCount);
                            });

                            // Извлекаем комментарии
                            JSONObject commentsObject = jsonObject.getJSONObject("comments");
                            JSONArray commentsArray = commentsObject.getJSONArray("comments");
                            comments.clear(); // Очищаем список перед добавлением новых комментариев
                            for (int i = 0; i < commentsArray.length(); i++) {
                                JSONObject commentJson = commentsArray.getJSONObject(i);
                                String commentText = commentJson.getString("comment");
                                String commenterName = commentJson.getJSONObject("owner").getString("fullname");
                                String commenterImageUrl = commentJson.getJSONObject("owner").optString("normalPhotoUrl", null);

                                // Добавляем комментарий в список
                                comments.add(new Comment(commentText, commenterName, commenterImageUrl));
                            }
                            // Обновляем адаптер комментариев на UI потоке
                            runOnUiThread(() -> {
                                commentAdapter.notifyDataSetChanged();
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(ImageDetailActivity.this, "Ошибка получения данных изображения", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(ImageDetailActivity.this, "Ошибка обработки данных", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(ImageDetailActivity.this, "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}

*/
