package com.regent.rpush.route.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.regent.rpush.route.mapper.RpushTemplateReceiverGroupMapper;
import com.regent.rpush.route.model.RpushTemplateReceiver;
import com.regent.rpush.route.model.RpushTemplateReceiverGroup;
import com.regent.rpush.route.service.IRpushTemplateReceiverGroupService;
import com.regent.rpush.route.service.IRpushTemplateReceiverService;
import com.regent.rpush.route.utils.Qw;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 消息模板-接收人分组表 服务实现类
 * </p>
 *
 * @author 钟宝林
 * @since 2021-03-06
 */
@Service
public class RpushTemplateReceiverGroupServiceImpl extends ServiceImpl<RpushTemplateReceiverGroupMapper, RpushTemplateReceiverGroup> implements IRpushTemplateReceiverGroupService {

    @Autowired
    private IRpushTemplateReceiverService rpushTemplateReceiverService;

    @Override
    public void updateGroup(RpushTemplateReceiverGroup group) {
        String groupName = group.getGroupName();
        Long id = group.getId();
        if (StringUtils.isNotBlank(groupName)) {
            // 名称判重
            QueryWrapper<RpushTemplateReceiverGroup> groupNameQw = Qw.newInstance(RpushTemplateReceiverGroup.class)
                    .eq("platform", group.getPlatform())
                    .eq("group_name", groupName);
            if (id != null) {
                groupNameQw.ne("id", id);
            }
            RpushTemplateReceiverGroup existGroup = getOne(groupNameQw);
            Assert.isTrue(existGroup == null, "分组名称重复");
        }

        // 入库
        if (id == null) {
            save(group);
        } else {
            updateById(group);
        }
    }

    @Override
    public Set<String> listReceiverIds(List<Long> receiverGroupIds) {
        List<RpushTemplateReceiver> receivers = rpushTemplateReceiverService.list(Qw.newInstance(RpushTemplateReceiver.class).in("group_id", receiverGroupIds));
        receivers = receivers == null ? new ArrayList<>() : receivers;
        return receivers.stream().map(RpushTemplateReceiver::getReceiverId).collect(Collectors.toSet());
    }
}
