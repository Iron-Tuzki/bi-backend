package com.yupi.springbootinit.model.dto.user;

import lombok.Data;

import java.util.Date;

/**
 * @author lanshu
 * @date 2023-08-03
 */
@Data
public class UserTokenDTO {

    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户角色: user/admin
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
