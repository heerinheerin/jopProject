package com.Myproject.insurance.controller;

import com.Myproject.insurance.dto.*;
import com.Myproject.insurance.entity.*;
import com.Myproject.insurance.service.CartService;
import com.Myproject.insurance.service.ItemService;
import com.Myproject.insurance.service.MemberService;
import com.Myproject.insurance.service.OrderService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@AllArgsConstructor
public class CartController {

    private  final CartService cartService;
    private  final OrderService orderService;
    private  final ItemService itemService;
    private  final MemberService memberService;

    @GetMapping(value = "/cart")
    public String main(Principal principal, Model model) {
        if (principal ==null){ //만약에 개인정보가 없으면 로그인으로 이동시킨다.
            model.addAttribute("errorMessage", "로그인 후 이용하시기 바랍니다.");
            return "member/memberLoginForm";
        }
        //개인정보가 있으면 그 뒤에 진행.
        //아이템과 오더, 카트를 조인한 후 거기에 필요한 정보를 빼와서 리스트로 만든다.
        List<CartDetailDto> cartDetailItem = cartService.getCartList(principal.getName());
        model.addAttribute("cartDetailItem", cartDetailItem);

        return "cart/cartList";
    }



    @PostMapping(value = "/cart")
    public @ResponseBody
    ResponseEntity order(@RequestBody @Valid CartItemDto cartItemDto,
                         BindingResult bindingResult, Principal principal){
        if (bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }
        String email = principal.getName(); //로그인된 이메일을 변수 이메일에 대입
        Long cartItemId; //변수 선언
        try {
            cartItemId = cartService.addCart(cartItemDto, email); // cartService로 이동

        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);


        }
        return  new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }


    @DeleteMapping(value = "/cart/{cartItemId}/delete")
    public  @ResponseBody ResponseEntity cartItemDelete(@PathVariable("cartItemId")  Long cartItemId
                                            , Principal principal){
        if (!cartService.validateCartItem(cartItemId, principal.getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cartService.deleteCartItem(cartItemId);
        return new ResponseEntity<Long>(cartItemId, HttpStatus.OK);
    }

    @PostMapping(value = "/cart/{cartId}/order")
    public @ResponseBody
    ResponseEntity order(@PathVariable("cartId") Long cartItemId, Principal principal){


            if (!cartService.validateCartItem(cartItemId, principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        CartOrderDto cartorderDto = new CartOrderDto();
            cartorderDto.setCartItemId(cartItemId);
        Long orderId;
        try{
            orderId = cartService.orderCartItem(cartorderDto, principal.getName());
        }catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return  new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }


}
