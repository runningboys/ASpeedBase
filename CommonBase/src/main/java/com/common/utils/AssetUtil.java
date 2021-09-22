package com.common.utils;

import android.content.res.AssetManager;
import android.text.TextUtils;

import com.common.CommonUtil;

import org.jetbrains.annotations.NotNull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * asset资源工具
 *
 * @author LiuFeng
 * @data 2021/9/18 18:27
 */
public class AssetUtil {
    private static final AssetManager mAssetManager = CommonUtil.getContext().getAssets();

    /**
     * 获取资源的文件内容（例如在assets/assets.lst文件中）
     *
     * @param asset
     * @return
     */
    public static String getStringFromAssets(String asset) {
        StringBuilder sb = new StringBuilder();
        try {
            InputStream source = getInputStream(asset);
            BufferedReader br = new BufferedReader(new InputStreamReader(source));

            String line;
            while (null != (line = br.readLine())) {
                sb.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 将asset文件拷贝到指定文件夹
     *
     * @param asset
     * @param dir
     * @return
     */
    public static File copyFile(String asset, File dir) {
        checkFolderExists(dir);
        String fileName = getAssetFileName(asset);
        File destFile = new File(dir, fileName);
        try {
            InputStream source = getInputStream(asset);
            writeFile(source, destFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return destFile;
    }

    /**
     * 复制文件夹
     *
     * @param assetsDir
     * @param dir
     */
    public static void copyDir(String assetsDir, File dir) {
        checkFolderExists(dir);
        assetsDir = getAssetDirName(assetsDir);

        try {
            // 获取资源目录下文件名称列表
            String[] fileNames = mAssetManager.list(assetsDir);

            // 文件
            if (fileNames.length == 0) {
                copyFile(assetsDir, dir);
                return;
            }

            // 文件夹
            for (String name : fileNames) {
                //补全assets资源路径
                String filePath = assetsDir + File.separator + name;
                String[] childNames = mAssetManager.list(filePath);

                if (childNames.length > 0) {
                    copyDir(name, dir);
                } else {
                    copyFile(filePath, dir);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将流写入目标文件
     *
     * @param source
     * @param destFile
     * @return
     */
    private static boolean writeFile(InputStream source, File destFile) {
        boolean bRet = true;
        try {
            int read;
            byte[] buffer = new byte[1024];
            OutputStream dest = new FileOutputStream(destFile);
            while ((read = source.read(buffer)) != -1) {
                dest.write(buffer, 0, read);
            }

            source.close();
            source = null;
            dest.flush();
            dest.close();
            dest = null;
        } catch (Exception e) {
            e.printStackTrace();
            bRet = false;
        }
        return bRet;
    }

    @NotNull
    private static String getAssetFileName(String asset) {
        String fileName = asset;
        if (asset.contains("/")) {
            fileName = asset.substring(asset.lastIndexOf("/") + 1);
        }
        return fileName;
    }

    @NotNull
    private static String getAssetDirName(String asset) {
        if (TextUtils.isEmpty(asset) || asset.equals("/")) {
            asset = "";
        }

        if (asset.endsWith("/")) {
            asset = asset.substring(0, asset.length() - 1);
        }
        return asset;
    }

    @NotNull
    private static InputStream getInputStream(String asset) throws IOException {
        return mAssetManager.open(new File(asset).getPath());
    }

    private static void checkFolderExists(File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }
}
