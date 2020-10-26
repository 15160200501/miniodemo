package com.example.utils;

import com.example.config.MinioProperties;
import io.minio.*;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

/**
 * @Description :
 * Created by huming on 2020/09/07 10:58
 */
@Slf4j
@Component
public class MinioUtil {

    @Autowired
    private MinioProperties minioProperties;

    private static MinioClient minioClient;

    /** 初始化minio配置 */
    @PostConstruct
    public void init() {
        try {
            minioClient = MinioClient.builder().endpoint(minioProperties.getEndpoint())
                    .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化minio配置异常: 【{}】", e.fillInStackTrace());
        }
    }

    /** 判断 bucket是否存在 */
    @SneakyThrows(Exception.class)
    public static boolean bucketExists(String bucketName) {

        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /** 创建 bucket */
    @SneakyThrows(Exception.class)
    public static void createBucket(String bucketName) {
        boolean isExist = minioClient.bucketExists(bucketName);
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /** 获取全部bucket */
    @SneakyThrows(Exception.class)
    public static List<Bucket> getAllBuckets() {
        return minioClient.listBuckets();
    }

    /** 文件上传 */
    @SneakyThrows(Exception.class)
    public static String upload(String bucketName, MultipartFile file) {
        //得到文件流
        InputStream inputStream = file.getInputStream();

        //文件名
        String originalFilename = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();

        //存储文件新名称
        String newName = originalFilename + "--" + uuid;

        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName)
                .object(newName).stream(inputStream,-1,10485760).build());

        inputStream.close();

        return minioClient.getObjectUrl(bucketName, newName);
    }

    /** 下载文件 */
    @SneakyThrows(Exception.class)
    public static void download(String bucketName, String fileName, HttpServletResponse response) {
        // 获取对象的元数据
        final ObjectStat stat = minioClient.statObject(bucketName, fileName);
        response.setContentType(stat.contentType());
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        InputStream is = minioClient.getObject(bucketName, fileName);
        IOUtils.copy(is, response.getOutputStream());
        is.close();
    }


    /** 删除文件 */
    @SneakyThrows(Exception.class)
    public static void deleteFile(String bucketName, String fileName) {
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
    }

}
