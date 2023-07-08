package cloud.zhfsmy.ruijidelivery.controller;

import cloud.zhfsmy.ruijidelivery.common.BusinessException;
import cloud.zhfsmy.ruijidelivery.common.R;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
public class CommonController {

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String uploadFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.indexOf("."));
            String basePath = this.getClass().getResource("/images/").getPath();
            String uploadPath = basePath + uploadFileName;
            file.transferTo(new File(uploadPath));
            return R.success(uploadFileName);
        }
        throw new BusinessException("文件上传失败");
    }

    /**
     * 下载文件
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) {
        String basePath = this.getClass().getResource("/images/").getPath();
        String filePath = basePath + name;
        //获取本地文件流
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            //获取输出流
            ServletOutputStream outputStream = response.getOutputStream();
            //设置相应类型
            response.setContentType("image/jpeg");
            //写入相应流
            int length = 0;
            byte[] bytes = new byte[1024];
            while ((length = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
