package com.toyproject.instagram.service;

import com.toyproject.instagram.dto.UploadFeedReqDto;
import com.toyproject.instagram.entity.Feed;
import com.toyproject.instagram.entity.FeedImg;
import com.toyproject.instagram.repository.FeedMapper;
import com.toyproject.instagram.security.PrincipalUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service // IoC 등록
@RequiredArgsConstructor
public class FeedService {

    @Value("${file.path}") // IoC 에서 주입이 될때 yml 에서 참조해서 파일을 들고옴
    private String filePath;

    private final FeedMapper feedMapper;

    @Transactional( rollbackFor = Exception.class) //메소드 위에 붙이는 어노테이션 , 모든 예외가 발생하면 롤백
    public void upload(UploadFeedReqDto uploadFeedReqDto) {
        String content = uploadFeedReqDto.getContent();
        List<FeedImg> feedImgList = new ArrayList<>();
        PrincipalUser principalUser =
                (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principalUser.getUsername();

        Feed feed = Feed.builder()
                .content(content)
                .username(username)
                .build();

        feedMapper.saveFeed(feed);

        uploadFeedReqDto.getFiles().forEach(file -> {
            String originName = file.getOriginalFilename();
            String extensionName = originName.substring(originName.lastIndexOf("."));
            String saveName = UUID.randomUUID().toString().replaceAll("-", "").concat(extensionName);  // 파일 이름이 겹쳐지면 덮어씌워질 수 있으니 UUID로 이미지명을 변경한다.

            Path uploadPath = Paths.get(filePath + "/feed/" + saveName);

            File f = new File(filePath + "/feed");
            if(!f.exists()) { // 경로가 존재하지 않으면
                f.mkdirs();  // feed 폴더를 만들어라
            }

            try {
                Files.write(uploadPath, file.getBytes()); //uploadPath 경로에 실제데이터를 복사해라
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            feedImgList.add(FeedImg.builder()
                    .feedId(feed.getFeedId())
                    .originFileName(originName)
                    .saveFileName(saveName)
                    .build());

        });

        feedMapper.saveFeedImgList(feedImgList);
    }
}
