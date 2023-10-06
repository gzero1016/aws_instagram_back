package com.toyproject.instagram.service;

import com.toyproject.instagram.dto.UploadFeedReqDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service // IoC 등록
public class FeedService {

    @Value("${file.path}") // IoC 에서 주입이 될때 yml 에서 참조해서 파일을 들고옴
    private String filePath;

    public void upload(UploadFeedReqDto uploadFeedReqDto) {
        uploadFeedReqDto.getFiles().forEach(file -> {
            String originName = file.getOriginalFilename();
            String extensionName = originName.substring(originName.lastIndexOf("."));
            String newName = UUID.randomUUID().toString().replaceAll("-", "").concat(extensionName);  // 파일 이름이 겹쳐지면 덮어씌워질 수 있으니 UUID로 이미지명을 변경한다.

            Path uploadPath = Paths.get(filePath + "/feed/" + newName);

            File f = new File(filePath + "/feed");
            if(!f.exists()) { // 경로가 존재하지 않으면
                f.mkdirs();  // feed 폴더를 만들어라
            }

            try {
                Files.write(uploadPath, file.getBytes()); //uploadPath 경로에 실제데이터를 복사해라
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
