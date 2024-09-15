package com.common.utils.dutil.download;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * 文件信息实体
 *
 * @author workerinchina@163.com
 */
public class FileInfo implements Serializable {
    public String fileId;                        // 文件id
    public String fileName;                      // 文件名称
    public String filePath;                      // 文件路径
    public long   fileSize;                      // 文件大小
    public long   progress;                      // 进度
    public String type;                          // 文件类型
    public String md5;                           // 文件MD5
    public long   createTime;                    // 文件创建时间
    public long   expires;                       // 文件有效期
    public int    permission;                    // 文件可操作权限
    public String url;                           // 网络链接


    private FileInfo() {}

    public static class Builder {
        private String url;
        private String filePath;
        private String fileName;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder filePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public Builder fileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public FileInfo build() {
            if (TextUtils.isEmpty(url) || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(filePath)) {
                throw new IllegalArgumentException("The parameter url/fileName/filePath cannot be empty");
            }

            FileInfo fileInfo = new FileInfo();
            fileInfo.url = url;
            fileInfo.filePath = filePath;
            fileInfo.fileName = fileName;

            return fileInfo;
        }
    }
}
