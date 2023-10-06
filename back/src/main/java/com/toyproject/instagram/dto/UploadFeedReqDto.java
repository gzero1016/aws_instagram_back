package com.toyproject.instagram.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UploadFeedReqDto {
    private String content;
    private List<MultipartFile> files; //MultipartFile: file을 받을 때 사용 여러개를 받을때는 List로
}
