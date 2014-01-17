package com.odong.portal.controller;

import com.odong.itpkg.model.SessionItem;
import com.odong.portal.util.Uploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午3:57
 */
@Controller("c.attach")
@SessionAttributes(SessionItem.KEY)
public class AttachController {
    @RequestMapping(value = "/attachments/**", method = RequestMethod.GET)
    @ResponseBody
    FileSystemResource attachments(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try {
            request.setCharacterEncoding("utf-8");
            response.setCharacterEncoding("utf-8");
            return new FileSystemResource(new Uploader(realPath, request).getPhysicalPath(request.getRequestURI().substring("/attachments/".length())));
        } catch (IOException e) {
            logger.error("下载文件出错", e);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        return null;
    }

    @RequestMapping(value = "/editor/fileUp", method = RequestMethod.POST)
    void fileUp(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionItem.KEY) SessionItem si) throws IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        Uploader up = new Uploader(realPath, request);
        up.setSavePath(savePath(si)); //保存路径
        String[] fileType = {".rar", ".doc", ".docx", ".zip", ".pdf", ".txt", ".swf", ".wmv"};  //允许的文件类型
        up.setAllowFiles(fileType);
        up.setMaxSize(10000);        //允许的文件最大尺寸，单位KB
        up.upload();
        response.getWriter().print("{'url':'" + up.getUrl() + "','fileType':'" + up.getType() + "','state':'" + up.getState() + "','original':'" + up.getOriginalName() + "'}");
    }


    @RequestMapping(value = "/editor/scrawlUp", method = RequestMethod.POST)
    void scrawlUp(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionItem.KEY) SessionItem si) throws IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String param = request.getParameter("action");
        Uploader up = new Uploader(realPath, request);

        up.setSavePath(savePath(si));
        String[] fileType = {".gif", ".png", ".jpg", ".jpeg", ".bmp"};
        up.setAllowFiles(fileType);
        up.setMaxSize(10000); //单位KB

        if (param != null && param.equals("tmpImg")) {
            up.upload();
            response.getWriter().print("<script>parent.ue_callback('" + up.getUrl() + "','" + up.getState() + "')</script>");
        } else {
            up.uploadBase64("content");
            response.getWriter().print("{'url':'" + up.getUrl() + "',state:'" + up.getState() + "'}");
        }
    }


    @RequestMapping(value = "/editor/getRemoteImage", method = RequestMethod.POST)
    void getRemoteImage(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionItem.KEY) SessionItem si) throws IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        String url = request.getParameter("upfile");
        String state = "远程图片抓取成功！";

        final String filePath = savePath(si);
        String[] arr = url.split("ue_separate_ue");
        String[] outSrc = new String[arr.length];
        for (int i = 0; i < arr.length; i++) {

            //保存文件路径
            String str = realPath;
            File f = new File(str);
            String savePath = f.getParent() + "/" + filePath;
            //格式验证
            String type = getImageType(arr[i]);
            if (type.equals("")) {
                state = "图片类型不正确！";
                continue;
            }
            String saveName = Long.toString(new Date().getTime()) + type;
            //大小验证
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection conn = (HttpURLConnection) new URL(arr[i]).openConnection();
            if (!conn.getContentType().contains("image")) {
                state = "请求地址头不正确";
                continue;
            }
            if (conn.getResponseCode() != 200) {
                state = "请求地址不存在！";
                continue;
            }
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File savetoFile = new File(savePath + "/" + saveName);
            outSrc[i] = filePath + "/" + saveName;
            try {
                InputStream is = conn.getInputStream();
                OutputStream os = new FileOutputStream(savetoFile);
                int b;
                while ((b = is.read()) != -1) {
                    os.write(b);
                }
                os.close();
                is.close();
                // 这里处理 inputStream
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("页面无法访问");
            }
        }
        String outstr = "";
        for (String s : outSrc) {
            outstr += s + "ue_separate_ue";
        }
        outstr = outstr.substring(0, outstr.lastIndexOf("ue_separate_ue"));
        response.getWriter().print("{'url':'" + outstr + "','tip':'" + state + "','srcUrl':'" + url + "'}");
    }

    @RequestMapping(value = "/editor/imageManager", method = RequestMethod.POST)
    void imageManager(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionItem.KEY) SessionItem si) throws IOException {


        String imgStr = "";
        String phyPath = new Uploader(realPath, request).getPhysicalPath(savePath(si));
        List<File> files = tree4image(phyPath, new ArrayList<File>());
        for (File file : files) {
            imgStr += file.getPath().replace(phyPath, "") + "ue_separate_ue";
        }
        if (!"".equals(imgStr)) {
            imgStr = imgStr.substring(0, imgStr.lastIndexOf("ue_separate_ue")).replace(File.separator, "/").trim();
        }
        response.getWriter().print(imgStr);
    }

    @RequestMapping(value = "/editor/imageUp", method = RequestMethod.POST)
    void imageUp(HttpServletRequest request, HttpServletResponse response, @ModelAttribute(SessionItem.KEY) SessionItem si) throws IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        Uploader up = new Uploader(realPath, request);
        up.setSavePath(savePath(si));
        String[] fileType = {".gif", ".png", ".jpg", ".jpeg", ".bmp"};
        up.setAllowFiles(fileType);
        up.setMaxSize(10000); //单位KB
        up.upload();
        response.getWriter().print("{'original':'" + up.getOriginalName() + "','url':'" + up.getUrl() + "','title':'" + up.getTitle() + "','state':'" + up.getState() + "'}");
    }

    @RequestMapping(value = "/editor/getMovie", method = RequestMethod.POST)
    void getMovie(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        StringBuilder readOneLineBuff = new StringBuilder();
        String content = "";
        String searchkey = request.getParameter("searchKey");
        String videotype = request.getParameter("videoType");
        try {
            searchkey = URLEncoder.encode(searchkey, "utf-8");
            URL url = new URL("http://api.tudou.com/v3/gw?method=item.search&appKey=myKey&format=json&kw=" + searchkey + "&pageNo=1&pageSize=20&channelId=" + videotype + "&inDays=7&media=v&sort=s");
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                readOneLineBuff.append(line);
            }
            content = readOneLineBuff.toString();
            reader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        response.getWriter().print(content);
    }


    private String savePath(SessionItem si) {
        return "upload/u" + si.getSsAccountId();
    }

    private String getImageType(String fileName) {
        for (String t : new String[]{".gif", ".png", ".jpg", ".jpeg", ".bmp"}) {
            if (fileName.endsWith(t)) {
                return t;
            }
        }
        return null;
    }

    private List<File> tree4image(String realPath, List<File> files) {

        File realFile = new File(realPath);
        if (realFile.isDirectory()) {
            File[] subFiles = realFile.listFiles();
            if (subFiles != null) {
                for (File file : subFiles) {
                    if (file.isDirectory()) {
                        tree4image(file.getAbsolutePath(), files);
                    } else {
                        if (getImageType(file.getName()) != null) {
                            files.add(file);
                        }
                    }
                }
            }
        }
        return files;
    }


    @PostConstruct
    void init() {
        realPath += "/attach";
    }

    @Value("${app.store}")
    private String realPath;
    private final static Logger logger = LoggerFactory.getLogger(AttachController.class);

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }
}
