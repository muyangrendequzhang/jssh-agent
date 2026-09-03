package com.myr.service;

import com.myr.entity.Result;
import com.myr.entity.SystemInfo;

import java.util.List;

public interface SystemService {

    Result<List<SystemInfo>> getSystemInfo();
}
