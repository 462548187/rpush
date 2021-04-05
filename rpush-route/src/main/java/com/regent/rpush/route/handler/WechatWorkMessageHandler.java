package com.regent.rpush.route.handler;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.regent.rpush.common.SingletonUtil;
import com.regent.rpush.dto.enumration.MessageType;
import com.regent.rpush.dto.message.WechatWorkMessageDTO;
import com.regent.rpush.dto.message.config.Config;
import com.regent.rpush.dto.message.config.WechatWorkConfig;
import com.regent.rpush.route.model.RpushMessageHisDetail;
import com.regent.rpush.route.service.IRpushMessageHisService;
import com.regent.rpush.route.service.IRpushTemplateReceiverGroupService;
import com.regent.rpush.route.service.IRpushTemplateService;
import me.chanjar.weixin.cp.api.impl.WxCpServiceImpl;
import me.chanjar.weixin.cp.bean.message.WxCpMessage;
import me.chanjar.weixin.cp.config.impl.WxCpDefaultConfigImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * 企业微信handler
 *
 * @author 钟宝林
 * @since 2021/3/16/016 10:45
 **/
@Component
public class WechatWorkMessageHandler extends MessageHandler<WechatWorkMessageDTO> {

    private final static Logger LOGGER = LoggerFactory.getLogger(WechatWorkMessageHandler.class);

    @Autowired
    private IRpushTemplateService rpushTemplateService;
    @Autowired
    private IRpushMessageHisService rpushMessageHisService;
    @Autowired
    private IRpushTemplateReceiverGroupService rpushTemplateReceiverGroupService;

    @Override
    public MessageType messageType() {
        return MessageType.WECHAT_WORK_TEXT;
    }

    @Override
    public void handle(WechatWorkMessageDTO param) {
        List<Config> configs = param.getConfigs();
        String content = param.getContent();
        for (Config conf : configs) {
            WechatWorkConfig config = (WechatWorkConfig) conf;
            Set<String> receiverUsers = rpushTemplateReceiverGroupService.listReceiverIds(param.getReceiverGroupIds()); // 先拿参数里分组的接收人
            if (param.getReceiverIds() != null) {
                receiverUsers.addAll(param.getReceiverIds());
            }

            if (receiverUsers.size() <= 0) {
                LOGGER.warn("请求号：{}，消息配置：{}。没有检测到接收用户", param.getRequestNo(), param.getConfigs());
                return;
            }

            WxCpDefaultConfigImpl cpConfig = SingletonUtil.get(config.getCorpId() + config.getSecret() + config.getAgentId(), () -> {
                WxCpDefaultConfigImpl cpConfig1 = new WxCpDefaultConfigImpl();
                cpConfig1.setCorpId(config.getCorpId());
                cpConfig1.setCorpSecret(config.getSecret());
                cpConfig1.setAgentId(config.getAgentId());
                return cpConfig1;
            });

            WxCpServiceImpl wxCpService = new WxCpServiceImpl();
            wxCpService.setWxCpConfigStorage(cpConfig);

            for (String receiverUser : receiverUsers) {
                WxCpMessage message = WxCpMessage.TEXT().agentId(config.getAgentId()).toUser(receiverUser).content(content).build();
                RpushMessageHisDetail hisDetail = RpushMessageHisDetail.builder()
                        .platform(messageType().getPlatform().name())
                        .messageType(messageType().name())
                        .configName(config.getConfigName())
                        .receiverId(receiverUser)
                        .requestNo(param.getRequestNo())
                        .configId(config.getConfigId())
                        .build();
                try {
                    wxCpService.getMessageService().send(message);
                    hisDetail.setSendStatus(RpushMessageHisDetail.SEND_STATUS_SUCCESS);
                } catch (Exception e) {
                    String eMessage = ExceptionUtil.getMessage(e);
                    eMessage = StringUtils.isBlank(eMessage) ? "未知错误" : eMessage;
                    hisDetail.setSendStatus(RpushMessageHisDetail.SEND_STATUS_FAIL);
                    hisDetail.setErrorMsg(eMessage);
                }
                rpushMessageHisService.logDetail(hisDetail);
            }
        }
    }
}
