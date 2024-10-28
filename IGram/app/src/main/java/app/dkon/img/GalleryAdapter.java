package app.dkon.img;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide; // Убедитесь, что вы добавили Glide в зависимости
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {
    private List<GalleryItem> galleryItems; // Используем конкретный класс GalleryItem

    public GalleryAdapter(List<GalleryItem> galleryItems) {
        this.galleryItems = galleryItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        return new ViewHolder(view);
    }

   /* @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GalleryItem item = galleryItems.get(position); // Теперь item имеет тип GalleryItem
        // Используем Glide для загрузки изображения
        Glide.with(holder.imageView.getContext())
                .load(item.getImgUrl()) // Теперь метод getImgUrl() доступен
                .into(holder.imageView);
    }
*/
   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       GalleryItem item = galleryItems.get(position);
       Glide.with(holder.imageView.getContext())
               .load(item.getImgUrl())
               .into(holder.imageView);

       holder.imageView.setOnClickListener(v -> {
           // Открываем ImageDetailActivity и передаем необходимые данные
           Intent intent = new Intent(holder.imageView.getContext(), ImageDetailActivity.class);
           intent.putExtra("itemId", item.getId());
           intent.putExtra("accessToken", intent.getStringExtra("accessToken"));
           intent.putExtra("accountId", intent.getStringExtra("accountId"));
           holder.imageView.getContext().startActivity(intent);
       });
   }
    @Override
    public int getItemCount() {
        return galleryItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView); // Убедитесь, что ID соответствует вашему макету
        }
    }
}
