package app.gateway.rest.entity;

public class StandardServerError {
    private int status;
    private String title;
    private String detail;
    private ServerErrorMetaData meta;
    private int code;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public ServerErrorMetaData getMeta() {
        return meta;
    }

    public void setMeta(ServerErrorMetaData meta) {
        this.meta = meta;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
