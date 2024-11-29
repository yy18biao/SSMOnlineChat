package com.chat.user.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "chat_user")
public class UserEs {
    @Id
    @Field(type = FieldType.Text)
    private String userId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_max_word")
    private String nickname;

    @Field(type = FieldType.Text)
    private String photo = "https://biao22.oss-cn-guangzhou.aliyuncs.com/%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F.png";

    @Field(type = FieldType.Text)
    private String phone;

    @Field(type = FieldType.Text)
    private String email;

    @Field(type = FieldType.Text)
    private String introduce;

    @Field(type = FieldType.Integer)
    private Integer status;
}