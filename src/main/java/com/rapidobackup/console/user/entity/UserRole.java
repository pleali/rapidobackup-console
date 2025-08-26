package com.rapidobackup.console.user.entity;

public enum UserRole {
  ADMIN("ROLE_ADMIN", "Admin", 0),
  GROSSISTE("ROLE_GROSSISTE", "Grossiste", 1),
  PARTENAIRE("ROLE_PARTENAIRE", "Partenaire", 2),
  CLIENT("ROLE_CLIENT", "Client", 3);

  private final String authority;
  private final String displayName;
  private final int hierarchyLevel;

  UserRole(String authority, String displayName, int hierarchyLevel) {
    this.authority = authority;
    this.displayName = displayName;
    this.hierarchyLevel = hierarchyLevel;
  }

  public String getAuthority() {
    return authority;
  }

  public String getDisplayName() {
    return displayName;
  }

  public int getHierarchyLevel() {
    return hierarchyLevel;
  }

  public boolean canManage(UserRole otherRole) {
    return this.hierarchyLevel <= otherRole.hierarchyLevel;
  }

  public boolean isHigherThan(UserRole otherRole) {
    return this.hierarchyLevel < otherRole.hierarchyLevel;
  }

  public static UserRole fromAuthority(String authority) {
    for (UserRole role : values()) {
      if (role.authority.equals(authority)) {
        return role;
      }
    }
    throw new IllegalArgumentException("No role found for authority: " + authority);
  }
}