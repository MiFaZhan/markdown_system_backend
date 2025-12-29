package com.mifazhan.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> {
    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 成功响应（无数据）
     * @return Result对象
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    /**
     * 成功响应（带数据）
     * @param data 响应数据
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }

    /**
     * 成功响应（带消息和数据）
     * @param message 响应消息
     * @param data 响应数据
     * @return Result对象
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }

    /**
     * 失败响应（默认消息）
     * @return Result对象
     */
    public static <T> Result<T> error() {
        return new Result<>(500, "操作失败", null);
    }

    /**
     * 失败响应（自定义消息）
     * @param message 错误消息
     * @return Result对象
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

    /**
     * 失败响应（自定义状态码和消息）
     * @param code 状态码
     * @param message 错误消息
     * @return Result对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
