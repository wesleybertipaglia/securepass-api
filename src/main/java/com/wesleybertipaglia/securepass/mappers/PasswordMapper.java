package com.wesleybertipaglia.securepass.mappers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.wesleybertipaglia.securepass.controllers.PasswordController;
import com.wesleybertipaglia.securepass.entities.Password;
import com.wesleybertipaglia.securepass.entities.User;
import com.wesleybertipaglia.securepass.records.password.PasswordResponseRecord;
import com.wesleybertipaglia.securepass.records.password.PasswordRequestRecord;

public class PasswordMapper {
    public static PasswordResponseRecord entityToResponseRecord(Password password) {
        password.add(linkTo(methodOn(PasswordController.class).getPassword(password.getId())).withSelfRel());

        return new PasswordResponseRecord(password.getId(), password.getLabel(), password.getPassword(),
                password.getLinks());
    }

    public static Password requestRecordToEntity(PasswordRequestRecord passwordRequest, User owner) {
        return new Password(passwordRequest.label(), passwordRequest.password(), owner);
    }
}
