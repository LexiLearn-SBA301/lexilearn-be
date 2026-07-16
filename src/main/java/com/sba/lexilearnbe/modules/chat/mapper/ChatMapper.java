package com.sba.lexilearnbe.modules.chat.mapper;

import com.sba.lexilearnbe.modules.chat.dto.response.ChatMessageResponse;
import com.sba.lexilearnbe.modules.chat.dto.response.ConversationSummaryResponse;
import com.sba.lexilearnbe.modules.chat.entity.ChatMessage;
import com.sba.lexilearnbe.modules.chat.entity.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ChatMapper {

    ConversationSummaryResponse toSummary(Conversation conversation);

    ChatMessageResponse toMessageResponse(ChatMessage message);
}
