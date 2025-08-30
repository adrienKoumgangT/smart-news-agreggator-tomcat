package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.user.service;

public enum UserStatus {

    // Core lifecycle

    /**User can log in and use the system normally*/
    ACTIVE("ACTIVE"),
    /**User account exists but is disabled (cannot log in*/
    INACTIVE("INACTIVE"),
    /**Account created but not yet verified (e.g., email confirmation)*/
    PENDING("PENDING"),
    /**User requested account deletion or admin removed account (soft-delete)*/
    DELETED("DELETED"),

    /**Email/phone not verified.*/
    UNVERIFIED("UNVERIFIED"),
    /**Waiting for action (email code, SMS OTP, KYC docs)*/
    VERIFICATION_PENDING("VERIFICATION_PENDING"),
    /**Verified identity/contacts*/
    VERIFIED("VERIFIED"),

    /**Temporarily suspended by system (e.g., too many login attempts)*/
    BLOCKED("BLOCKED"),
    /**Disabled by admin due to violation*/
    SUSPENDED("SUSPENDED"),
    /**Security lockout (e.g., policy or regulatory reason)*/
    LOCKED("LOCKED"),
    /**Permanently prohibited from using the system*/
    BANNED("BANNED"),

    /**Old/inactive account kept for records*/
    ARCHIVED("ARCHIVED"),
    /**Subscription or trial expired*/
    EXPIRED("EXPIRED"),
    /**Awaiting admin/moderator approval*/
    PENDING_APPROVAL("PENDING_APPROVAL"),
    /**Temporary/anonymous account*/
    GUEST("GUEST"),


    ;

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }

    public static UserStatus getUserStatus(String status) {
        if(status == null) return null;

        for (UserStatus userStatus : UserStatus.values()) {
            if (userStatus.status.equals(status)) {
                return userStatus;
            }
        }
        return null;
    }

    public String getStatus() {
        return status;
    }
}
