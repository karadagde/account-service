package account.model.record;

import account.model.enums.LockUnlockOperation;

public record LockUnlockUserRequest(String user,
                                    LockUnlockOperation operation) {
    @Override
    public String user() {
        return user.toLowerCase();
    }
}
