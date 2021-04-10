package com.regent.rpush.dto.message.wechatofficialaccount;

import com.regent.rpush.dto.enumration.SchemeValueType;
import com.regent.rpush.dto.message.base.BaseMessage;
import com.regent.rpush.dto.route.sheme.SchemeValue;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 企业微信消息发送DTO
 *
 * @author 钟宝林
 * @since 2021/2/28/028 21:28
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class TextMessageDTO extends BaseMessage {
    private static final long serialVersionUID = -3289428483627765265L;

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

    @SchemeValue(type = SchemeValueType.TEXTAREA, description = "请输入内容...")
    private String content;

}
