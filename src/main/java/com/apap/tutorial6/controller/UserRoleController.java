package com.apap.tutorial6.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apap.tutorial6.model.PasswordModel;
import com.apap.tutorial6.model.UserRoleModel;
import com.apap.tutorial6.service.UserRoleService;

@Controller
@RequestMapping("/user")
public class UserRoleController {
	@Autowired
	private UserRoleService userService;

	@RequestMapping(value = "/addUser", method = RequestMethod.POST) 
	public String addUserSubmit(@ModelAttribute UserRoleModel user) {
		userService.addUser(user);
		return "home";
	}


	@RequestMapping(value="/updatePassword", method = RequestMethod.POST)
	public ModelAndView updatePasswordSubmit(@ModelAttribute PasswordModel pass, Model model, RedirectAttributes redir) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserRoleModel user = userService.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		String message = "";
		if (pass.getConPassword().equals(pass.getNewPassword())) {
			if (passwordEncoder.matches(pass.getOldPassword(), user.getPassword())) {
				userService.changePassword(user, pass.getNewPassword());
				message = "Password berhasil diubah";
			}
			else {
				message = "Password lama Anda salah";
			}
		}
		else {
			message = "password baru tidak sesuai";
		}
		
		ModelAndView modelAndView = new ModelAndView("redirect:/");
		redir.addFlashAttribute("msg", message);
		return modelAndView;
	}
}

