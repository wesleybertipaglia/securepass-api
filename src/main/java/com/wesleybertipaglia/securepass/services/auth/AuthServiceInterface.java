package com.wesleybertipaglia.securepass.services.auth;

import com.wesleybertipaglia.securepass.records.auth.SignInRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignInResponseRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpRequestRecord;
import com.wesleybertipaglia.securepass.records.auth.SignUpResponseRecord;

public interface AuthServiceInterface {
    SignInResponseRecord signIn(SignInRequestRecord signInRequest);

    SignUpResponseRecord signUp(SignUpRequestRecord signUpRequest);

    void deleteAccount(String tokenSubject);
}
