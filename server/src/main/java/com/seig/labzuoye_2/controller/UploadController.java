package com.seig.labzuoye_2.controller;

import com.seig.labzuoye_2.utils.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    @Value("${upload.path}")
    private String uploadPath;

    @Value("${upload.prefix}")
    private String prefix;

    // 上传岗位图片接口
    @PostMapping("/postImg")
    public Result<String> uploadPostImg(@RequestParam("file") MultipartFile file) {
        try {
            // 1. 按日期创建文件夹
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String dateDir = sdf.format(new Date());
            File dir = new File(uploadPath + dateDir);
            if (!dir.exists()) dir.mkdirs();

            // 2. 时间戳+UUID重命名文件，避免重名
            String original = file.getOriginalFilename();
            String suffix = original.substring(original.lastIndexOf("."));
            String newName = System.currentTimeMillis() + "_" + UUID.randomUUID() + suffix;

            // 3. 保存文件
            File target = new File(dir, newName);
            file.transferTo(target);

            // 4. 返回可访问的图片url
            String url = prefix + dateDir + "/" + newName;
            return Result.success(url);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }
}