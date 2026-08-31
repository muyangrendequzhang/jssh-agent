package com.myr.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileStructure {

    //子目录下文件名
    public List<FileStructure> childrenFiles;

    //当前文件的路径
    public String path;

    //当前文件名
    public String name;
}
