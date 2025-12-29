package com.mifazhan.domain.exception;

import com.mifazhan.domain.vo.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * @param e 业务异常对象
     * @param request HTTP请求对象
     * @return Result对象
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.error("业务异常：{} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常(@RequestBody @Valid)
     * @param e 参数校验异常
     * @param request HTTP请求对象
     * @return Result对象
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder sb = new StringBuilder();
        for (FieldError error : fieldErrors) {
            sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }
        log.error("参数校验异常：{} - {}", request.getRequestURI(), sb.toString());
        return Result.error(400, "参数校验失败: " + sb.toString());
    }

    /**
     * 处理参数校验异常(@ModelAttribute @Valid)
     * @param e 参数校验异常
     * @param request HTTP请求对象
     * @return Result对象
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e, HttpServletRequest request) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        StringBuilder sb = new StringBuilder();
        for (FieldError error : fieldErrors) {
            sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
        }
        log.error("参数绑定异常：{} - {}", request.getRequestURI(), sb.toString());
        return Result.error(400, "参数绑定失败: " + sb.toString());
    }

    /**
     * 处理参数类型转换异常
     * @param e 参数类型转换异常
     * @param request HTTP请求对象
     * @return Result对象
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        log.error("参数类型转换异常：{} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(400, "参数类型错误: " + e.getName());
    }

    /**
     * 处理请求方法不支持异常
     * @param e 请求方法不支持异常
     * @param request HTTP请求对象
     * @return Result对象
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public Result<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.error("请求方法不支持异常：{} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(405, "请求方法不支持: " + e.getMethod());
    }

    /**
     * 处理404异常
     * @param e 404异常
     * @param request HTTP请求对象
     * @return Result对象
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<?> handleNoHandlerFoundException(NoHandlerFoundException e, HttpServletRequest request) {
        log.error("404异常：{} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(404, "请求的资源不存在");
    }

    /**
     * 处理数据库唯一键冲突异常
     * @param e 数据库唯一键冲突异常
     * @param request HTTP请求对象
     * @return Result对象
     */
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Result<?> handleDuplicateKeyException(DuplicateKeyException e, HttpServletRequest request) {
        log.error("数据库唯一键冲突异常：{} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(409, "数据已存在，请勿重复提交");
    }

    /**
     * 处理请求体缺失异常
     * @param e 请求体缺失异常
     * @param request HTTP请求对象
     * @return Result对象
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.error("请求体缺失或格式错误异常：{} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(400, "请求体缺失或格式错误，请检查请求数据格式");
    }

    /**
     * 处理文件上传大小超限异常
     * @param e 文件上传大小超限异常
     * @param request HTTP请求对象
     * @return Result对象
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e, HttpServletRequest request) {
        log.error("文件上传大小超限异常：{} - {}", request.getRequestURI(), e.getMessage());
        return Result.error(400, "上传文件大小超过限制");
    }

    /**
     * 处理其他未知异常
     * @param e 异常对象
     * @param request HTTP请求对象
     * @return Result对象
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常：{} - {}", request.getRequestURI(), e.getMessage(), e);
        return Result.error(500, "系统内部错误，请联系管理员");
    }
}