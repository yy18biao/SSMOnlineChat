package com.chat.user.es;

import com.chat.user.domain.UserEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends ElasticsearchRepository<UserEs, Long> {
    // 根据账号查找
    UserEs findUserEsByUserId(Long userId);

    // 根据昵称查找
    UserEs findUserEsByNickname(String nickname);

    // 根据手机号码查找
    UserEs findUserEsByPhone(String phone);

    // 根据状态查找
    UserEs findUserEsByStatus(Integer status);
}
