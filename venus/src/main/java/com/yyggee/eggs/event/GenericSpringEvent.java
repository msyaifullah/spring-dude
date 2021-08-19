package com.yyggee.eggs.event;

import org.springframework.context.ApplicationEvent;

public class GenericSpringEvent<T> extends ApplicationEvent {

    private T source;
    protected boolean success;

    public GenericSpringEvent(T source, boolean success) {
        super(source);
        this.source = source;
        this.success = success;
    }

    public T getSource() {
        return this.source;
    }

    public T setSource(T source) {
        this.source = source;
        return source;
    }

}
