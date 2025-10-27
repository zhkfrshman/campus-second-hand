package com.softwareengineering.team.campussecondhand.controller;

import com.softwareengineering.team.campussecondhand.entity.*;
import com.softwareengineering.team.campussecondhand.repository.OrderItemRepository;
import com.softwareengineering.team.campussecondhand.repository.OrderRepository;
import com.softwareengineering.team.campussecondhand.service.CartService;
import com.softwareengineering.team.campussecondhand.service.ProductService;
import com.softwareengineering.team.campussecondhand.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/order")
public class OrderController {
    private final CartService cartService;
    private final ProductService productService;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderController(CartService cartService,
                           ProductService productService,
                           UserService userService,
                           OrderRepository orderRepository,
                           OrderItemRepository orderItemRepository) {
        this.cartService = cartService;
        this.productService = productService;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @GetMapping("/checkout")
    public String checkout(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";
        User user = userService.findByPhone(authentication.getName());
        if (user == null) return "redirect:/login";

        List<CartItem> cartItems = cartService.getCartItems(user.getId());
        if (cartItems.isEmpty()) return "redirect:/cart";

        BigDecimal total = BigDecimal.ZERO;
        Map<Long, Product> productMap = new HashMap<>();
        for (CartItem item : cartItems) {
            Product p = productService.findById(item.getSid());
            if (p != null) {
                productMap.put(item.getSid(), p);
                total = total.add(BigDecimal.valueOf(p.getPrice()).multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("productMap", productMap);
        model.addAttribute("total", total);
        model.addAttribute("user", user);
        return "order-checkout";
    }

    @PostMapping("/submit")
    @Transactional
    public String submitOrder(Authentication authentication,
                              @RequestParam String contactName,
                              @RequestParam String address,
                              @RequestParam String phone,
                              RedirectAttributes ra,
                              Model model) {
        if (authentication == null) return "redirect:/login";
        User user = userService.findByPhone(authentication.getName());
        if (user == null) return "redirect:/login";

        // 服务器端严格校验
        if (!StringUtils.hasText(contactName) || !StringUtils.hasText(address) || !StringUtils.hasText(phone)) {
            ra.addFlashAttribute("error", "请完整填写收件人、电话和收货地址");
            return "redirect:/order/checkout";
        }

        List<CartItem> cartItems = cartService.getCartItems(user.getId());
        if (cartItems.isEmpty()) return "redirect:/cart";

        // 计算总价 + 库存校验
        BigDecimal total = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cartItems) {
            Product p = productService.findById(ci.getSid());
            if (p == null) {
                ra.addFlashAttribute("error", "有商品不存在，无法下单");
                return "redirect:/cart";
            }
            if (p.getCount() < ci.getQuantity()) {
                ra.addFlashAttribute("error", "商品【" + p.getName() + "】库存不足，当前库存：" + p.getCount());
                return "redirect:/cart";
            }
            total = total.add(BigDecimal.valueOf(p.getPrice()).multiply(BigDecimal.valueOf(ci.getQuantity())));

            OrderItem oi = new OrderItem();
            oi.setSid(p.getId());
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(p.getPrice());
            oi.setProductName(p.getName());
            oi.setProductImage(p.getImage());
            orderItems.add(oi);
        }

        // 创建订单（状态：0 待付款）
        Order order = new Order();
        order.setUid(user.getId());
        order.setTotalAmount(total.doubleValue());
        order.setAddress(address);
        order.setPhone(phone);
        order.setContactName(contactName);
        order.setStatus(0);
        Order saved = orderRepository.save(order);

        for (OrderItem oi : orderItems) {
            oi.setOrderId(saved.getId());
            orderItemRepository.save(oi);

            Product p = productService.findById(oi.getSid());
            int newCount = p.getCount() - oi.getQuantity();
            p.setCount(newCount);
            // 如果库存为0或小于等于0，标记为下架（display=0）
            if (newCount <= 0) {
                p.setStatus(0);   // 库存为0自动下架（新逻辑）
                p.setDisplay(0);  // 兼容你现有的 display 逻辑（可选）
            }
            productService.saveProduct(p);
        }

        // 清空购物车
        cartService.clearCart(user.getId());

        // 跳转到支付页
        return "redirect:/order/pay/" + saved.getId();
    }

    // 新增：订单详情（供 order-list 使用）
    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Long id, Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";
        User user = userService.findByPhone(authentication.getName());
        if (user == null) return "redirect:/login";

        Optional<Order> opt = orderRepository.findById(id);
        if (opt.isEmpty() || !Objects.equals(opt.get().getUid(), user.getId())) {
            return "redirect:/order/list";
        }
        Order order = opt.get();
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        model.addAttribute("order", order);
        model.addAttribute("items", items);
        return "order-detail"; // 或者你可以返回一个单独的 order-detail 模板
    }


    @GetMapping("/pay/{id}")
    public String payPage(@PathVariable Long id, Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";
        User user = userService.findByPhone(authentication.getName());
        if (user == null) return "redirect:/login";

        Optional<Order> opt = orderRepository.findById(id);
        if (opt.isEmpty() || !Objects.equals(opt.get().getUid(), user.getId())) {
            return "redirect:/order/list";
        }
        Order order = opt.get();
        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        model.addAttribute("order", order);
        model.addAttribute("items", items);
        return "order-pay";
    }

    // 模拟支付：更新状态为1（已付款/待发货），跳转成功页
    @PostMapping("/pay/{id}")
    @Transactional
    public String doPay(@PathVariable Long id, Authentication authentication, RedirectAttributes ra) {
        if (authentication == null) return "redirect:/login";
        User user = userService.findByPhone(authentication.getName());
        if (user == null) return "redirect:/login";

        Optional<Order> opt = orderRepository.findById(id);
        if (opt.isEmpty() || !Objects.equals(opt.get().getUid(), user.getId())) {
            return "redirect:/order/list";
        }
        Order order = opt.get();
        if (order.getStatus() == 0) {
            order.setStatus(1); // 已付款/待发货
            orderRepository.save(order);
        }
        ra.addFlashAttribute("message", "支付成功");
        return "redirect:/order/success/" + id;
    }

    @GetMapping("/success/{id}")
    public String success(@PathVariable Long id, Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";
        User user = userService.findByPhone(authentication.getName());
        if (user == null) return "redirect:/login";

        Optional<Order> opt = orderRepository.findById(id);
        if (opt.isEmpty() || !Objects.equals(opt.get().getUid(), user.getId())) {
            return "redirect:/order/list";
        }
        Order order = opt.get();
        List<OrderItem> items = orderItemRepository.findByOrderId(id);
        model.addAttribute("order", order);
        model.addAttribute("items", items);
        return "order-success";
    }

    @GetMapping("/list")
    public String orderList(Authentication authentication, Model model) {
        if (authentication == null) return "redirect:/login";
        User user = userService.findByPhone(authentication.getName());
        if (user == null) return "redirect:/login";

        List<Order> orders = orderRepository.findByUidOrderByCreatedAtDesc(user.getId());
        model.addAttribute("orders", orders);
        return "order-list";
    }
}