/**
 * @(#)ForgetPasswordController.java 2021/09/09.
 * <p>
 * Copyright(C) 2021 by PHOENIX TEAM.
 * <p>
 * Last_Update 2021/09/09.
 * Version 1.00.
 */
package poly.store.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import poly.store.common.Constants;
import poly.store.entity.User;
import poly.store.model.UserRegister;
import poly.store.service.UserService;
import poly.store.service.impl.MailerServiceImpl;

/**
 * Class de lay lai mat khau
 *
 * @author khoa-ph
 * @version 1.00
 */
@Controller
public class ForgetPasswordController {

    @Autowired
    UserService userService;

    @Autowired
    MailerServiceImpl mailerService;

    @Autowired
    PasswordEncoder pe;

    /**
     * Hien thi man hinh forget-password
     *
     * @param model
     * @return man hinh forget-password
     */
    @GetMapping("/forget-password")
    public String displayFormForgetPassword(Model model) {
        UserRegister userForm = new UserRegister();
        model.addAttribute("userForm", userForm);
        return Constants.USER_DISPLAY_FORGET_PASSWORD;
    }

    @PostMapping("/forget-password")
    public String handlerFormForgetPassword(Model model, @ModelAttribute("userForm") @Validated UserRegister userForm,
            BindingResult result) {
        if (userForm.getEmail().isEmpty()) {
            result.rejectValue("email", "NotBlank.userRegister.email");
        } else {
            User user = userService.findUserByEmail(userForm.getEmail());
            if (user == null) {
                result.rejectValue("email", "NotExist.userLogin.username");
            } else {
                String password = pe.encode(user.getPassword());
                mailerService.queue(userForm.getEmail(), "L??m m???i m???t kh???u!",
                        "Vui l??ng click v??o link n??y: " + "http://localhost:8080/reset-password?code=" + password
                                + "&email=" + user.getEmail() + " ????? reset m???t kh???u.");
            }
        }

        if (result.hasErrors()) {
            return Constants.USER_DISPLAY_FORGET_PASSWORD;
        }

        model.addAttribute("alert", "Th??ng b??o!");
        model.addAttribute("message", "Vui l??ng ki???m tra email ????? thay ?????i m???t kh???u!");
        return Constants.USER_DISPLAY_ALERT_STATUS;
    }
}
