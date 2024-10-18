package com.chat.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chat.message.domain.Message;
import com.chat.message.domain.vo.MessageVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    @Select("SELECT createBy as userId, content, messageNickname as nickname, messagePhoto as photo, createTime" +
            " FROM message where chatSessionId=#{chatId}")
    List<MessageVo> selectMessageByChatId(Long chatId);
}
