package com.pri.tool.commonreq.filter.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wang.song
 * @date 2020-12-09 18:13
 * @Desc
 */
@Data
public class PublicParam implements Serializable {
    /**
     * 用户token
     */
    private String token;

    /**
     * 登录人ID
     */
    private String userName;

    /**
     * 登录人姓名
     */
    private String updatedUser;

}
