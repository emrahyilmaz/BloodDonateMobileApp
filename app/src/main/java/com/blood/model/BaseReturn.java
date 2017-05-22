package com.blood.model;

import java.io.Serializable;

public class BaseReturn<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean result;
	private String message;
	private T data;
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public BaseReturn(boolean result, String message, T data) {
		super();
		this.result = result;
		this.message = message;
		this.data = data;
	}
	@Override
	public String toString() {
		return "BaseReturn [result=" + result + ", message=" + message + ", data=" + data + "]";
	}

}