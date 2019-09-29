package app.core.advert.entity;


public class AdvertData {
    private String type;
    private String id;
    private AdvertAttributes attributes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public AdvertAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(AdvertAttributes attributes) {
        this.attributes = attributes;
    }
}
