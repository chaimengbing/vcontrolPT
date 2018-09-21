package com.vcontrol.vcontroliot.model;

import java.io.Serializable;


public class RTUResponse<T> implements Serializable
{

    private static final long serialVersionUID = 5213230387175987834L;

    public int status;
    public String msg;
    public T data;
}