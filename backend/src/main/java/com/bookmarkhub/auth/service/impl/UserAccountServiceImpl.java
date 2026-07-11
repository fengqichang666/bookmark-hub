package com.bookmarkhub.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookmarkhub.auth.entity.UserAccount;
import com.bookmarkhub.auth.mapper.UserAccountMapper;
import com.bookmarkhub.auth.service.UserAccountService;
import org.springframework.stereotype.Service;

@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {
}
