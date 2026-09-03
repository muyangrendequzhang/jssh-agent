package com.myr.service;

import com.myr.entity.NetworkInfo;
import com.myr.entity.Result;

import java.util.List;

public interface NetworkService {

    Result<List<NetworkInfo>> getNetworkInfo();
}
