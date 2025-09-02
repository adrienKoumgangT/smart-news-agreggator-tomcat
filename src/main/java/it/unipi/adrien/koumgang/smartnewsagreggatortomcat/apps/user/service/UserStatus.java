package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.service;

public enum UserStatus {

    // Core lifecycle
    ACTIVE(1, "ACTIVE", "User can log in and use the system normally", "green"),
    INACTIVE(2, "INACTIVE", "User account exists but is disabled (cannot log in)", "gray"),
    PENDING(3, "PENDING", "Account created but not yet verified (e.g., email confirmation)", "orange"),
    DELETED(4, "DELETED", "User requested account deletion or admin removed account (soft-delete)", "red"),

    UNVERIFIED(5, "UNVERIFIED", "Email/phone not verified.", "orange"),
    VERIFICATION_PENDING(6, "VERIFICATION_PENDING", "Waiting for action (email code, SMS OTP, KYC docs)", "yellow"),
    VERIFIED(7, "VERIFIED", "Verified identity/contacts", "blue"),

    BLOCKED(8, "BLOCKED", "Temporarily suspended by system (e.g., too many login attempts)", "red"),
    SUSPENDED(9, "SUSPENDED", "Disabled by admin due to violation", "red"),
    LOCKED(10, "LOCKED", "Security lockout (e.g., policy or regulatory reason)", "darkred"),
    BANNED(11, "BANNED", "Permanently prohibited from using the system", "black"),

    ARCHIVED(12, "ARCHIVED", "Old/inactive account kept for records", "lightgray"),
    EXPIRED(13, "EXPIRED", "Subscription or trial expired", "brown"),
    PENDING_APPROVAL(14, "PENDING_APPROVAL", "Awaiting admin/moderator approval", "yellow"),
    GUEST(15, "GUEST", "Temporary/anonymous account", "cyan");

    private final int idUserStatus;
    private final String code;
    private final String label;
    private final String color;

    UserStatus(int idUserStatus, String code, String label, String color) {
        this.idUserStatus = idUserStatus;
        this.code = code;
        this.label = label;
        this.color = color;
    }

    public int getIdUserStatus() { return idUserStatus; }
    public String getCode() { return code; }
    public String getLabel() { return label; }
    public String getColor() { return color; }

    public static UserStatus fromCode(String code) {
        if (code == null) return null;

        for (UserStatus s : values()) {
            if (s.code.equalsIgnoreCase(code)) {
                return s;
            }
        }
        return null;
    }

    public static UserStatus fromID(Long idUserStatus) {
        if (idUserStatus == null) return null;

        for (UserStatus s : values()) {
            if (s.idUserStatus == idUserStatus) {
                return s;
            }
        }
        return null;
    }
}
