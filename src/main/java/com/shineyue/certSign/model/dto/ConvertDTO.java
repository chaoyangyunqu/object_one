package com.shineyue.certSign.model.dto;


public interface ConvertDTO<S,T> {
    T convert(S s);
}
