package app.dkon.img; // Убедитесь, что пакет соответствует вашему проекту

public class GalleryItem {
    private String id;
    private String imgUrl;

    public GalleryItem(String id, String imgUrl) {
        this.id = id;
        this.imgUrl = imgUrl;
    }

    public String getId() {
        return id;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
