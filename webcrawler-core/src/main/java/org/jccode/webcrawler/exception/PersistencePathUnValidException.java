package org.jccode.webcrawler.exception;

/**
 * PersistencePathUnValidException
 *
 * @Description TODO
 * @Author jc-henry
 * @Date 2019/12/5 20:54
 * @Version 1.0
 **/
public class PersistencePathUnValidException extends RuntimeException {

    private String message = "Persistence path cannot be null or empty.";

    public PersistencePathUnValidException() {
        super();
    }

    public PersistencePathUnValidException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
