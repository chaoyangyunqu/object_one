package com.shineyue.certSign.service;

import com.shineyue.certSign.model.DataResult;

public interface IXinjiangCAService {
    void getCertSN();
    DataResult sealPicOfPDF();
}
