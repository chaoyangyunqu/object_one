package com.shineyue.certSign.common;

/**
 * @Description: TODO
 * @author: luofuwei
 * @date: wrote on 2019/9/8
 */
public class Generic<T> {

    private T key;

    public Generic(T key) {
        this.key = key;
    }

    public T getKey(){
        return key;
    }
}
