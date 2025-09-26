package com.rapidobackup.console.contact.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.rapidobackup.console.contact.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, UUID> {

  List<Contact> findByTenantId(UUID tenantId);

  @Query("SELECT c FROM Contact c WHERE c.email = :email")
  Optional<Contact> findByEmail(@Param("email") String email);

  List<Contact> findByContactTypeAndTenantId(String contactType, UUID tenantId);

  @Query("SELECT c FROM Contact c WHERE c.tenant.id = :tenantId AND c.isPrimary = true")
  List<Contact> findPrimaryContactsByTenantId(@Param("tenantId") UUID tenantId);
}