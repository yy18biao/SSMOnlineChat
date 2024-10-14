package com.chat.admin.domain.user;

import com.chat.core.domain.PageQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserSearchDto extends PageQueryDto {
    private Long userId;
    private String nickname;
}
