package org.wire.core.common.exception;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class CustomExceptionHandler{

	private ResponseEntity<ErrorResponse> buildError(CustomException e) {
		e.printStackTrace();
		return new ResponseEntity<>(
			new ErrorResponse(
				e.getErrorCode().getCode(), e.getMessage()),
			e.getErrorCode().getStatus());
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(CustomException e) {
		return buildError(e);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> methodArgumentNotValidException(
		MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();
		String description =
			bindingResult.getFieldErrors().get(0).getField()
				+ " : "
				+ bindingResult.getFieldErrors().get(0).getDefaultMessage();
		return buildError(new CustomException(ErrorCode.INVALID_PARAMETER, description, e));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> methodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e) {
		return buildError(new CustomException(ErrorCode.INVALID_PARAMETER, e.getMessage(), e));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> exceptionHandlerConstraintViolationException(
		ConstraintViolationException e) {
		String description =
			e.getConstraintViolations().iterator().next().getPropertyPath()
				+ " : "
				+ e.getConstraintViolations().iterator().next().getMessage();

		return buildError(new CustomException(ErrorCode.BAD_REQUEST, description, e));
	}

	@ExceptionHandler(DataAccessException.class)
	public ResponseEntity<ErrorResponse> exceptionHandlerDataException(DataAccessException e) {
		String description = e.getMessage();

		return buildError(new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, description, e));
	}

	@ExceptionHandler(BeanInstantiationException.class)
	public ResponseEntity<ErrorResponse> exceptionHandlerBeanInstantiationException(
		BeanInstantiationException e) {
		return buildError((CustomException)e.getCause());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> exceptionHandler(Exception e) {
		return buildError(
			new CustomException(ErrorCode.UNKNOWN_SERVER_ERROR, e.getMessage(), e));
	}
}
