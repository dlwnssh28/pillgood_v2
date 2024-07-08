package com.pillgood.service;

import com.pillgood.dto.NoticeDto;
import com.pillgood.entity.Notice;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeService {
    List<NoticeDto> getAllNotices();
    NoticeDto createNotice(NoticeDto noticeDto);
    Optional<NoticeDto> updateNotice(int noticeNo, NoticeDto updatedNoticeDto);
    boolean deleteNotice(int noticeNo);
    Optional<NoticeDto> getNoticeById(int noticeNo);
    NoticeDto convertToDto(Notice noticeEntity);
    Notice convertToEntity(NoticeDto noticeDto);
    Page<NoticeDto> getNotices(Pageable pageable); // 페이징 메소드 추가
}
