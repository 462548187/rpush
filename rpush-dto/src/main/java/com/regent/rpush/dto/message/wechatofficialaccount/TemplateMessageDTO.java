package com.regent.rpush.dto.message.wechatofficialaccount;

import com.regent.rpush.dto.enumration.SchemeValueType;
import com.regent.rpush.dto.message.base.BaseMessage;
import com.regent.rpush.dto.route.sheme.SchemeValue;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 微信公众号模板消息
 *
 * @author 钟宝林
 * @since 2021/4/10/010 15:30
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class TemplateMessageDTO extends BaseMessage {
    private static final long serialVersionUID = 2978534939532888543L;

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

    @SchemeValue("公众号模板id")
    private String wechatTemplateId;

    @SchemeValue("点击跳转链接")
    private String url;

    @SchemeValue("小程序appId")
    private String miniAppId;

    @SchemeValue("小程序页面路径")
    private String miniPagePath;

    @SchemeValue(type = SchemeValueType.MULTI_OBJ_INPUT, value = "模板变量")
    private List<WechatTemplateData> templateDataList;



}
