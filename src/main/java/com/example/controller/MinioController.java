package com.example.controller;

import com.example.common.R;
import com.example.config.MinioProperties;
import com.example.exceptionhandler.SelfException;
import com.example.utils.MinioUtil;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



/**
 * @Description : 实现文件的上传和删除操作
 * Created by huming on 2020/08/03 10:47
 */
@Api(tags = "MinIO对象存储管理")
@RestController
@RequestMapping("/minio")
@Slf4j
public class MinioController {

    @Autowired
    private MinioProperties minioProperties;

    @ApiOperation(value = "上传文件到Minio")
    @PostMapping("/upload")
    public R uploadFile(@ApiParam(name = "file", value = "文件") @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new SelfException(201,"上传文件不能为空");
        }

        boolean isExist = MinioUtil.bucketExists(minioProperties.getBucketName());
        if (!isExist) {
            MinioUtil.createBucket(minioProperties.getBucketName());
            throw new SelfException(201,"创建桶成功，请重新上传文件");
        } else {
            String fileUrl = MinioUtil.upload(minioProperties.getBucketName(), file);
            return R.ok().data("成功", fileUrl);
        }

    }

    @ApiOperation(value = "删除文件")
    @DeleteMapping("/delete")
    public R delete(@ApiParam(name = "fileName", value = "文件名称")
                               @RequestParam("fileName") String fileName) {
        MinioUtil.deleteFile(minioProperties.getBucketName(), fileName);

        return R.ok().message("删除文件成功");
    }

}
