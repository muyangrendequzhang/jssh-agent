package com.myr.service;

import com.myr.entity.FileStructure;
import com.myr.entity.Result;

public interface FileService {

    Result<FileStructure> getFileNames(FileStructure structure);

    void downloadFile(FileStructure fileStructure);
}
