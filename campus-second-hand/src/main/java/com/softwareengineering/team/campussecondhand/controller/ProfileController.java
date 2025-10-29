package com.softwareengineering.team.campussecondhand.controller;

import org.springframework.security.crypto.password.PasswordEncoder;
import com.softwareengineering.team.campussecondhand.entity.Order;
import com.softwareengineering.team.campussecondhand.entity.Product;
import com.softwareengineering.team.campussecondhand.entity.User;
import com.softwareengineering.team.campussecondhand.entity.UserPassword;
import com.softwareengineering.team.campussecondhand.entity.CartItem;  // 新增
import com.softwareengineering.team.campussecondhand.repository.OrderRepository;
import com.softwareengineering.team.campussecondhand.repository.UserPasswordRepository;
import com.softwareengineering.team.campussecondhand.service.ProductService;
import com.softwareengineering.team.campussecondhand.service.UserService;
import com.softwareengineering.team.campussecondhand.service.CartService; // 新增
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.HashMap;      // 新增
import java.util.Map;         // 新增

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;
    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final UserPasswordRepository userPasswordRepository;
    private final PasswordEncoder passwordEncoder;
    private final CartService cartService; 
    public ProfileController(UserService userService,
                             ProductService productService,
                             OrderRepository orderRepository,
                             UserPasswordRepository userPasswordRepository,
                             PasswordEncoder passwordEncoder,
                             CartService cartService) { 
        this.userService = userService;
        this.productService = productService;
        this.orderRepository = orderRepository;
        this.userPasswordRepository = userPasswordRepository;
        this.passwordEncoder = passwordEncoder;
        this.cartService = cartService; 
    }

    @GetMapping
    public String profile(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";
        User user = userService.findByPhone(authentication.getName());
        if (user == null) return "redirect:/login";

        List<Product> products = productService.findByUserId(user.getId());
        List<Order> orders = orderRepository.findByUidOrderByCreatedAtDesc(user.getId());
        // 购物车数据（与 /cart 页保持一致）
        List<CartItem> cartItems = cartService.getCartItems(user.getId());
        Map<Long, Product> cartProductMap = new HashMap<>();
        if (cartItems != null) {
            for (CartItem ci : cartItems) {
                Product p = productService.findById(ci.getSid());
                if (p != null) cartProductMap.put(ci.getSid(), p);
            }
        }
        model.addAttribute("user", user);
        model.addAttribute("myProducts", products);
        model.addAttribute("orders", orders);
        model.addAttribute("cartItems", cartItems);              // 新增
        model.addAttribute("cartProductMap", cartProductMap);    // 新增
        return "profile";
    }

    // 编辑页 GET 映射：渲染 profile-edit.html，需要放入 user
    @GetMapping("/edit")
    public String editPage(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";
        User user = userService.findByPhone(authentication.getName());
        if (user == null) return "redirect:/login";
        model.addAttribute("user", user);
        return "profile-edit";
    }

    // 保存编辑 POST 映射：表单提交到 /profile/edit
    @PostMapping("/edit")
    public String editSave(Authentication authentication,
                           @RequestParam String username,
                           @RequestParam(required = false) String sno,
                           @RequestParam(required = false) String dormitory,
                           RedirectAttributes ra) {
        if (authentication == null) return "redirect:/login";
        User user = userService.findByPhone(authentication.getName());
        if (user == null) return "redirect:/login";

        if (username != null && !username.isBlank()) user.setUsername(username);
        user.setSno(sno);
        user.setDormitory(dormitory);
        userService.updateUser(user);

        ra.addFlashAttribute("message", "个人信息更新成功");
        return "redirect:/profile";
    }

    @GetMapping("/password")
    public String passwordPage() {
        return "profile-password";
    }

    @PostMapping("/password")
    public String changePassword(Authentication authentication,
                                 @RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes ra) {
        if (authentication == null) return "redirect:/login";
        User user = userService.findByPhone(authentication.getName());
        if (user == null) return "redirect:/login";

        if (newPassword == null || newPassword.length() < 6) {
            ra.addFlashAttribute("error", "新密码长度至少6位");
            return "redirect:/profile/password";
        }
        if (!newPassword.equals(confirmPassword)) {
            ra.addFlashAttribute("error", "两次输入的新密码不一致");
            return "redirect:/profile/password";
        }

        // 从 user_password 表获取哈希并校验
        var upOpt = userPasswordRepository.findByUid(user.getId());
        if (upOpt.isEmpty()) {
            ra.addFlashAttribute("error", "账户未设置密码，无法校验当前密码");
            return "redirect:/profile/password";
        }
        UserPassword up = upOpt.get();
        if (!passwordEncoder.matches(currentPassword, up.getPasswordHash())) {
            ra.addFlashAttribute("error", "当前密码不正确");
            return "redirect:/profile/password";
        }

        // 更新为新密码
        up.setPasswordHash(passwordEncoder.encode(newPassword));
        userPasswordRepository.save(up);

        ra.addFlashAttribute("message", "密码已更新");
        return "redirect:/profile";
    }
}