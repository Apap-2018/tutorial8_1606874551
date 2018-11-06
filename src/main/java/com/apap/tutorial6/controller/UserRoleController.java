package com.apap.tutorial6.controller;

import java.util.regex.Pattern;

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

	public boolean validatePassword(String pass) {
		if (pass.length()>=8 && Pattern.compile("[a-zA-Z]").matcher(pass).find() && Pattern.compile("[0-9]").matcher(pass).find()) {
			return true;
		}
		return false;
	}
	@RequestMapping(value = "/addUser", method = RequestMethod.POST) 
	public String addUserSubmit(@ModelAttribute UserRoleModel user, Model model) {
		String message = "";
		if(this.validatePassword(user.getPassword())) {
			userService.addUser(user);
			message = null;
				
		}
		else {
			message = "password minimal harus 8 kata yang terdiri dari karakter huruf dan minimal 1 angka";
		}
		model.addAttribute("message", message);
		return "home";
		
	}

	@RequestMapping(value="/updatePassword", method = RequestMethod.POST)
	public ModelAndView updatePasswordSubmit(@ModelAttribute PasswordModel pass, Model model, RedirectAttributes redir) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserRoleModel user = userService.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		String message = "";
		if (this.validatePassword(pass.getOldPassword()) && this.validatePassword(pass.getNewPassword()) && this.validatePassword(pass.getConPassword())) {
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
				message =	 "password baru tidak sesuai";
			}
		}
		else {
			message = "password minimal harus 8 kata yang terdiri dari karakter huruf dan minimal 1 angka";
		}
		
		ModelAndView modelAndView = new ModelAndView("redirect:/");
		redir.addFlashAttribute("message", message);
		return modelAndView;
	}
}

