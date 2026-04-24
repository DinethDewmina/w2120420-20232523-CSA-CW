package org.example.models;

public class ApiResponse<T> {
    private String status;
    private T data;

    // Empty constructor required by Jackson
    public ApiResponse() {}

    public ApiResponse(String status, T data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}