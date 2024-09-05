package com.wesleybertipaglia.securepass.services.checker;

import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerRequestRecord;
import com.wesleybertipaglia.securepass.records.checker.PasswordCheckerResponseRecord;

public interface PasswordCheckerServiceInterface {
    public PasswordCheckerResponseRecord checkPassword(PasswordCheckerRequestRecord passwordCheckerRequestRecord);
}
