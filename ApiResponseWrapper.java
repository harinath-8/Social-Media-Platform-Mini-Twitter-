package mini_twitter.follow_service.dto;

public class ApiResponseWrapper {
    private UserResponseDto data;
    private Object errors;

    public UserResponseDto getData() {
        return data;
    }

    public void setData(UserResponseDto data) {
        this.data = data;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }
}
