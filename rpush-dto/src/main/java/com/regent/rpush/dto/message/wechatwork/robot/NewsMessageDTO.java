package com.regent.rpush.dto.message.wechatwork.robot;

import com.regent.rpush.dto.enumration.SchemeValueType;
import com.regent.rpush.dto.message.base.BaseMessage;
import com.regent.rpush.dto.route.sheme.SchemeValue;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 企业微信图文消息发送DTO
 *
 * @author 钟宝林
 * @since 2021/4/7/007 17:30
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class NewsMessageDTO extends BaseMessage {
    private static final long serialVersionUID = 7034106110120563906L;

    /**
     * 接收人分组列表
     */
    @SchemeValue(type = SchemeValueType.RECEIVER_GROUP)
    private List<Long> receiverGroupIds;

    /**
     * 接收人列表
     */
    @SchemeValue(type = SchemeValueType.RECEIVER)
    private List<String> receiverIds;

    @SchemeValue(type = SchemeValueType.MULTI_OBJ_INPUT, value = "图文消息，一个图文消息支持1到8条图文")
    private List<ArticleDTO> articles;

}
