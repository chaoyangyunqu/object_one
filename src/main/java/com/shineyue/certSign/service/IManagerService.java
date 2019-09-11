package com.shineyue.certSign.service;

import com.shineyue.certSign.model.DataResult;
import com.shineyue.certSign.model.vo.InManagerVO;


public interface IManagerService {

    DataResult managerQuery(InManagerVO inManagerVO);
}
