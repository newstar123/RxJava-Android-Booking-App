package app.delivering.mvp.main.init.model;

public class ResponseErrModel {
    private int code;
    private String metaMessage;
    private String title;
    private String status;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMetaMessage() {
        return metaMessage;
    }

    public void setMetaMessage(String message) {
        this.metaMessage = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
