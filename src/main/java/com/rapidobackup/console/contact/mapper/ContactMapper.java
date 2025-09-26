package com.rapidobackup.console.contact.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.rapidobackup.console.contact.dto.ContactDto;
import com.rapidobackup.console.contact.entity.Contact;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    /**
     * Convert Contact entity to ContactDto
     */
    ContactDto toDto(Contact contact);

    /**
     * Convert ContactDto to Contact entity
     * Ignores generated/managed fields for entity creation
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenant", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Contact toEntity(ContactDto dto);
}