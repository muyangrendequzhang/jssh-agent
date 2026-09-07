package com.myr.entity;

import lombok.Data;

import java.util.List;

/**
 * 文件结构 DTO，字段与 jssh-back-end 的 com.myr.entity.FileStructure 保持一致，
 * 用于解析后端 /file 接口返回的目录结构。
 */
@Data
public class FileStructure {

    /** 子目录下的文件/子目录 */
    private List<FileStructure> childrenFiles;

    /** 当前文件/目录的路径 */
    private String path;

    /** 当前文件名 */
    private String name;
}