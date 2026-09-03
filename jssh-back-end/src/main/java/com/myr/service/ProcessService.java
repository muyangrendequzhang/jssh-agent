package com.myr.service;

import com.myr.entity.ProcessInfo;
import com.myr.entity.Result;

import java.util.List;

public interface ProcessService {

    Result<List<ProcessInfo>> getProcessInfo();
}
