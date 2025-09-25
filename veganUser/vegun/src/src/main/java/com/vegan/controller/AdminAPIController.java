package com.vegan.controller;


import com.vegan.entity.User;
import com.vegan.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // API 전용 경로
public class AdminAPIController {
    private final UserService userService;

    public AdminAPIController(UserService userService) {
        this.userService = userService;
    }

    //회원관리
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

   // 회원 삭제
   @DeleteMapping("/users/{id}") // /api/users/{id} 와 일치
   public ResponseEntity<?> deleteUser(@PathVariable int id) { //찾기
       System.out.println("삭제 " + id); // 콘솔 확인

       boolean deleted = userService.deleteUserById(id);
       if (deleted) return ResponseEntity.ok().build();
       else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body("삭제 실패");
   }


}
