package com.odong.portal.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase.InvalidContentTypeException;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午4:18
 */
public class Uploader {


    public Uploader(String realPath, HttpServletRequest request) {
        this.realPath = realPath;
        this.request = request;
        errorInfo = new HashMap<>();
        errorInfo.put("SUCCESS", "SUCCESS"); //默认成功
        errorInfo.put("NOFILE", "未包含文件上传域");
        errorInfo.put("TYPE", "不允许的文件格式");
        errorInfo.put("SIZE", "文件大小超出限制");
        errorInfo.put("ENTYPE", "请求类型ENTYPE错误");
        errorInfo.put("REQUEST", "上传请求异常");
        errorInfo.put("IO", "IO异常");
        errorInfo.put("DIR", "目录创建失败");
        errorInfo.put("UNKNOWN", "未知错误");

    }

    public void upload() {
        boolean isMultipart = ServletFileUpload.isMultipartContent(this.request);
        if (!isMultipart) {
            this.state = this.errorInfo.get("NOFILE");
            return;
        }
        DiskFileItemFactory dff = new DiskFileItemFactory();
        String savePath = this.getFolder(this.savePath);
        dff.setRepository(new File(savePath));
        try {
            ServletFileUpload sfu = new ServletFileUpload(dff);
            sfu.setSizeMax(this.maxSize * 1024);
            sfu.setHeaderEncoding("utf-8");
            FileItemIterator fii = sfu.getItemIterator(this.request);
            while (fii.hasNext()) {
                FileItemStream fis = fii.next();
                if (!fis.isFormField()) {
                    this.originalName = fis.getName().substring(fis.getName().lastIndexOf(System.getProperty("file.separator")) + 1);
                    if (!this.checkFileType(this.originalName)) {
                        this.state = this.errorInfo.get("TYPE");
                        continue;
                    }
                    this.fileName = this.getName(this.originalName);
                    this.type = this.getFileExt(this.fileName);
                    this.url = savePath + "/" + this.fileName;
                    BufferedInputStream in = new BufferedInputStream(fis.openStream());
                    FileOutputStream out = new FileOutputStream(new File(this.getPhysicalPath(this.url)));
                    BufferedOutputStream output = new BufferedOutputStream(out);
                    Streams.copy(in, output, true);
                    this.state = this.errorInfo.get("SUCCESS");
                    //UE中只会处理单张上传，完成后即退出
                    break;
                } else {
                    String fname = fis.getFieldName();
                    //只处理title，其余表单请自行处理
                    if (!fname.equals("pictitle")) {
                        continue;
                    }
                    BufferedInputStream in = new BufferedInputStream(fis.openStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder result = new StringBuilder();
                    while (reader.ready()) {
                        result.append((char) reader.read());
                    }
                    this.title = new String(result.toString().getBytes(), "utf-8");
                    reader.close();

                }
            }
        } catch (SizeLimitExceededException e) {
            this.state = this.errorInfo.get("SIZE");
            logger.error("大小超限", e);
        } catch (InvalidContentTypeException e) {
            this.state = this.errorInfo.get("ENTYPE");
            logger.error("类型错误", e);
        } catch (FileUploadException e) {
            this.state = this.errorInfo.get("REQUEST");
            logger.error("文件上传异常", e);
        } catch (Exception e) {
            this.state = this.errorInfo.get("UNKNOWN");
            logger.error("未知上传异常", e);
        }
    }

    /**
     * 接受并保存以base64格式上传的文件
     *
     * @param fieldName  field name
     */
    public void uploadBase64(String fieldName) {
        String savePath = this.getFolder(this.savePath);
        String base64Data = this.request.getParameter(fieldName);
        this.fileName = this.getName("test.png");
        this.url = savePath + "/" + this.fileName;

        try(OutputStream ro = new FileOutputStream(new File(this.getPhysicalPath(this.url)))) {
            ro.write(Base64.decodeBase64(base64Data));
            ro.flush();
            this.state = this.errorInfo.get("SUCCESS");
        } catch (Exception e) {
            this.state = this.errorInfo.get("IO");
            logger.error("接收base64文件出错", e);
        }
    }

    /**
     * 文件类型判断
     *
     * @param fileName 文件名
     * @return true false
     */
    private boolean checkFileType(String fileName) {
        for(String ext : allowFiles){
            if (fileName.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件扩展名
     *
     * @return string
     */
    private String getFileExt(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    /**
     * 依据原始文件名生成新文件名
     *
     * @return 新文件名
     */
    private String getName(String fileName) {
        return UUID.randomUUID().toString()+ getFileExt(fileName);
    }

    /**
     * 根据字符串创建本地目录 并按照日期建立子目录返回
     *
     * @param path 目录
     * @return 子目录
     */
    private String getFolder(String path) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        path += "/" + format.format(new Date());
        File dir = new File(this.getPhysicalPath(path));
        if (!dir.exists()) {
            if(dir.mkdirs()){
                this.state = this.errorInfo.get("DIR");
                return "";
            }
        }
        return path;
    }

    /**
     * 根据传入的虚拟路径获取物理路径
     *
     * @param path 虚拟路径
     * @return 物理路径
     */
    public String getPhysicalPath(String path) {
        return realPath + "/" + path;
    }


    private final String realPath;
    // 输出文件地址
    private String url = "";
    // 上传文件名
    private String fileName = "";
    // 状态
    private String state = "";
    // 文件类型
    private String type = "";
    // 原始文件名
    private String originalName = "";
    // 文件大小
    private String size = "";

    private HttpServletRequest request = null;
    private String title = "";

    // 保存路径
    private String savePath = "upload";
    // 文件允许格式
    private String[] allowFiles = {".rar", ".doc", ".docx", ".zip", ".pdf", ".txt", ".swf", ".wmv", ".gif", ".png", ".jpg", ".jpeg", ".bmp"};
    // 文件大小限制，单位KB
    private int maxSize = 10000;

    private HashMap<String, String> errorInfo;
    private final static Logger logger = LoggerFactory.getLogger(Uploader.class);

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public void setAllowFiles(String[] allowFiles) {
        this.allowFiles = allowFiles;
    }

    public void setMaxSize(int size) {
        this.maxSize = size;
    }

    public String getSize() {
        return this.size;
    }

    public String getUrl() {
        return this.url;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getState() {
        return this.state;
    }

    public String getTitle() {
        return this.title;
    }

    public String getType() {
        return this.type;
    }

    public String getOriginalName() {
        return this.originalName;
    }
}
