package app.gateway.rest.entity;

import java.util.List;

public class ErrorResponseBody {
    private List<StandardServerError> errors;

    public List<StandardServerError> getErrors() {
        return errors;
    }

    public void setErrors(List<StandardServerError> errors) {
        this.errors = errors;
    }
}
