package com.gondor.isildur.controller;

import java.io.IOException;
import java.sql.Timestamp;


import com.alibaba.fastjson.JSONObject;
import com.gondor.isildur.DTO.BaseDTO;
import com.gondor.isildur.entity.Book;
import com.gondor.isildur.service.BookService;

import com.gondor.isildur.util.ReturnObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

@RestController
@RequestMapping("/book")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/{bookId}")
    public JSONObject get(@PathVariable long bookId) {
        return bookService.get(bookId);
    }

    @DeleteMapping("/{bookId}")
    public JSONObject delete(@PathVariable long bookId) {
        return bookService.delete(bookId);
    }

    @PutMapping("/{bookId}")
    public JSONObject put(@PathVariable long bookId, @Valid @RequestBody Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        return bookService.put(bookId, book);
    }

    @PostMapping()
    public JSONObject create(@Valid @RequestBody Book book, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return handleValidationErrors(bindingResult);
        }
        book.setCreateTime(new Timestamp(System.currentTimeMillis()));
        book.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        return bookService.create(book);
    }

    // 处理验证错误的辅助方法
    private JSONObject handleValidationErrors(BindingResult bindingResult) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        Map<String, String> errors = fieldErrors.stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ?
                                fieldError.getDefaultMessage() : "参数错误"
                ));

        return new ReturnObject(400, "数据验证失败", errors);
    }


    @GetMapping()
    public JSONObject getByPage(@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return bookService.getByPage(page, size);
    }

    @GetMapping("/count")
    public JSONObject getCount() {
        return bookService.getCount();
    }

//    @GetMapping("/search")
//    public JSONObject search(@RequestParam(value = "bookTitle", defaultValue = "") String bookTitle) {
//        System.out.println("bookTitle: " + bookTitle);
//        return bookService.searchByBookTitle(bookTitle);
//    }

    @GetMapping("/search")
    public JSONObject search(@RequestParam(value = "bookTitle", defaultValue = "") String bookTitle) {
        System.out.println("接收到的搜索参数: bookTitle=" + bookTitle);
        try {
            JSONObject result = bookService.searchByBookTitle(bookTitle);
            System.out.println("搜索结果: " + result.toJSONString());
            if (result.containsKey("code") && result.containsKey("data")) {
                return result;
            }
            return new ReturnObject(0, "success", result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnObject(500, "搜索失败: " + e.getMessage());
        }
    }

    //添加测试方法
    @GetMapping("/testException")
    public JSONObject testException() {
        // 模拟空指针异常
        String str = null;
        int length = str.length(); // 这里会抛出NullPointerException
        return new BaseDTO().build("正常返回");
    }

    @GetMapping("/list")
    public JSONObject listBooks(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return bookService.getByPage(page, size);
    }

    @GetMapping("/proxy")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) throws IOException {
        try {
            URL imageUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
            connection.setRequestMethod("GET");

            // 设置请求头，模拟浏览器请求
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestProperty("Referer", "http://www.douban.com/");

            InputStream inputStream = connection.getInputStream();
            byte[] imageBytes = IOUtils.toByteArray(inputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG); // 根据实际图片类型调整

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_GATEWAY);
        }
    }

}
