package com.myr.service;

import com.myr.entity.MemoryInfo;
import com.myr.entity.Result;

public interface MemoryService {

    Result<MemoryInfo> getMemory();
}
