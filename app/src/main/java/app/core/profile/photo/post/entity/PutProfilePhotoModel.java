package app.core.profile.photo.post.entity;

import java.io.File;

public class PutProfilePhotoModel {
    private File file;

    public PutProfilePhotoModel() {}

    public PutProfilePhotoModel(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
