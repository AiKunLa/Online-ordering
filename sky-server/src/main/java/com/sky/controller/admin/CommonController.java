package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {


//    @Autowired
//    private AliOssUtil aliOssUtil;
//
//    /**
//     * 文件上传 oss对象存储
//     *
//     * @param file
//     * @return
//     */
//    @PostMapping("/upload")
//    @ApiOperation(value = "文件上传")
//    public Result<String> upload(MultipartFile file) {
//        log.info("文件上传: {}", file);
//        //将文件上传到阿里云
//        String filename = file.getOriginalFilename();
//        String extension = filename.substring(filename.lastIndexOf("."));
//
//        String fileName = UUID.randomUUID().toString() + extension;
//
//        try {
//            String filePath = aliOssUtil.upload(file.getBytes(), fileName);
//            return Result.success(filePath);
//        } catch (IOException e) {
//            log.error("文件上传失败:{}", e.getMessage());
//        }
//
//        return Result.error(MessageConstant.UPLOAD_FAILED);
//    }
//

    /**
     * 文件上传 上传到本地
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传 : {}", file);
        try {
            //获取文件名
            String filename = file.getOriginalFilename();
            //获取文件后缀
            String extension = filename.substring(filename.lastIndexOf("."));
            //生成随机文名 并拼接文件后缀
            String newFileName = UUID.randomUUID().toString() + extension;
            //获取储存路径 并拼接文件名
            //1.设置储存路径
            ApplicationHome applicationHome = new ApplicationHome(this.getClass());
            String pre = applicationHome.getDir().getParentFile().getParentFile().getAbsolutePath() +
                    "\\src\\main\\resources\\static\\images\\";
            //拼接路径
            String path = pre + newFileName;


            file.transferTo(new File(path));
            return Result.success(path);
        } catch (IOException e) {
            log.info("上传失败！！！");
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);


    }


}
