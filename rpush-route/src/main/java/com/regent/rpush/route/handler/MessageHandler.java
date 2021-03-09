package com.regent.rpush.route.handler;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lmax.disruptor.EventHandler;
import com.regent.rpush.dto.enumration.MessagePlatformEnum;
import com.regent.rpush.dto.message.base.BaseMessage;
import com.regent.rpush.dto.message.base.MessagePushDTO;
import com.regent.rpush.dto.message.base.PlatformMessageDTO;
import com.regent.rpush.dto.message.config.Config;
import com.regent.rpush.route.model.RpushPlatformConfig;
import com.regent.rpush.route.service.IRpushPlatformConfigService;
import com.regent.rpush.route.utils.MessageHandlerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 消息处理器基类
 *
 * @author 钟宝林
 * @date 2021/2/8 20:25
 **/
public abstract class MessageHandler<T extends BaseMessage<?>> implements EventHandler<MessagePushDTO> {

    private final static Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    @Autowired
    private IRpushPlatformConfigService rpushPlatformConfigService;

    @SuppressWarnings("unchecked")
    @Override
    public void onEvent(MessagePushDTO event, long sequence, boolean endOfBatch) throws Exception {
        Map<MessagePlatformEnum, PlatformMessageDTO> platformParamMap = event.getPlatformParam();
        if (!platformParamMap.containsKey(platform())) {
            // 不处理
            return;
        }
        MessagePlatformEnum platform = platform();
        PlatformMessageDTO platformMessageDTO = platformParamMap.get(platform);
        if (platformMessageDTO == null) {
            return;
        }
        try {
            // 处理参数
            JSONObject param = platformMessageDTO.getParam();
            Type actualTypeArgument = MessageHandlerUtils.getParamType(this);
            BaseMessage<?> baseMessage = param.toBean(actualTypeArgument);
            baseMessage.setContent(event.getContent());
            baseMessage.setRequestNo(event.getRequestNo());

            // 处理配置
            processPlatformConfig(platformMessageDTO, baseMessage);

            // 最后调用实际消息处理的方法
            handle((T) baseMessage);
        } catch (Exception e) {
            LOGGER.error("消息处理异常", e);
        }
    }

    /**
     * 根据参数传入的配置id，转换成具体的配置数据，提供给下游具体的消息处理器使用
     */
    private void processPlatformConfig(PlatformMessageDTO platformMessageDTO, BaseMessage<?> baseMessage) {
        List<Long> configIds = platformMessageDTO.getConfigIds();
        if (configIds == null || configIds.size() <= 0) {
            // 查一个默认配置出来用
            QueryWrapper<RpushPlatformConfig> queryWrapper = new QueryWrapper<>();
            RpushPlatformConfig config = rpushPlatformConfigService.getOne(queryWrapper);
            if (config == null) {
                return;
            }
            configIds = Collections.singletonList(config.getId());
        }
        Map<Long, Map<String, String>> configMap = rpushPlatformConfigService.queryConfig(configIds); // 键为配置id，值为：具体的配置键值
        List<Config> configs = MessageHandlerUtils.convertConfig(this, configMap); // 转成具体的配置实体类
        baseMessage.setConfigs(configs);
    }

    /**
     * 所有消息处理器必须实现这个接口，标识自己处理的是哪个平台的消息
     */
    public abstract MessagePlatformEnum platform();

    /**
     * 实现这个接口来处理消息，再正式调用这个方法之前会处理好需要的参数和需要的配置
     */
    public abstract void handle(T param);

}
