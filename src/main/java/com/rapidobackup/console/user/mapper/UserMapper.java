package com.rapidobackup.console.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.rapidobackup.console.contact.mapper.ContactMapper;
import com.rapidobackup.console.user.dto.UserDto;
import com.rapidobackup.console.user.entity.User;
import com.rapidobackup.console.user.entity.UserStatus;

@Mapper(componentModel = "spring", uses = {ContactMapper.class})
public interface UserMapper {

    /**
     * Convert User entity to UserDto
     * Maps status to activated boolean and contact relation
     */
    @Mapping(target = "activated", source = "status", qualifiedByName = "statusToActivated")
    @Mapping(target = "imageUrl", ignore = true) // Not stored in User entity
    @Mapping(target = "contact", source = "contact")
    UserDto toDto(User user);

    /**
     * Convert UserDto to User entity
     * Ignores computed and managed fields
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "contact", ignore = true)
    @Mapping(target = "status", source = "activated", qualifiedByName = "activatedToStatus")
    @Mapping(target = "externalId", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "profileType", ignore = true)
    @Mapping(target = "isSystemAdmin", ignore = true)
    @Mapping(target = "requiresMfa", ignore = true)
    @Mapping(target = "passwordExpiresAt", ignore = true)
    @Mapping(target = "activationToken", ignore = true)
    @Mapping(target = "activatedAt", ignore = true)
    @Mapping(target = "lastActivityAt", ignore = true)
    @Mapping(target = "suspensionReason", ignore = true)
    @Mapping(target = "resetKey", ignore = true)
    @Mapping(target = "resetDate", ignore = true)
    @Mapping(target = "userMetadata", ignore = true)
    @Mapping(target = "appMetadata", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    @Mapping(target = "failedLoginAttempts", ignore = true)
    @Mapping(target = "accountLockedUntil", ignore = true)
    User toEntity(UserDto dto);

    /**
     * Convert UserStatus enum to activated boolean
     */
    @Named("statusToActivated")
    default boolean statusToActivated(UserStatus status) {
        return status == UserStatus.ACTIVE;
    }

    /**
     * Convert activated boolean to UserStatus enum
     */
    @Named("activatedToStatus")
    default UserStatus activatedToStatus(boolean activated) {
        return activated ? UserStatus.ACTIVE : UserStatus.PENDING;
    }
}