package com.gondor.isildur.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.gondor.isildur.DTO.BaseDTO;
import com.gondor.isildur.DTO.BookDTO;
import com.gondor.isildur.entity.Book;
import com.gondor.isildur.repository.BookRepository;
import com.gondor.isildur.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gondor.isildur.util.ReturnObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
public class BookServiceImpl extends BaseImpl<Book, Long> implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Transactional
    public JSONObject create(Book book) {
        //检查必填字段是否完整
        if (book.getBookIsbn() == null || book.getBookTitle() == null ||
                book.getBookAuthor() == null || book.getBookCategory() == null ||
                book.getBookPublisher() == null || book.getBookSummary() == null) {
            return new BaseDTO().build(1, "创建失败: 必填字段不能为空");
        }

        // 1. 检查ISBN格式
        if (!isValidISBN(book.getBookIsbn())) {
            return new ReturnObject(400, "ISBN格式错误: 必须是13位数字");
        }

        // 2. 检查ISBN是否已存在
        Book existingBook = bookRepository.findByBookIsbn(book.getBookIsbn());
        if (existingBook != null) {
            return new BaseDTO().build(1, "创建失败: ISBN已存在");
        }

        // 3. 检查字段长度
        if (!validateFieldLengths(book)) {
            return new ReturnObject(400, "创建失败: 字段长度超过限制");
        }

        //设置创建和更新时间
        book.setCreateTime(new Timestamp(System.currentTimeMillis()));
        book.setUpdateTime(new Timestamp(System.currentTimeMillis()));

        // 4. 保存书籍
        try {
            Book savedBook = bookRepository.save(book);
            return new BaseDTO().build(savedBook.toDTO());
        } catch (Exception e) {
            return new BaseDTO().build(500, "创建失败: " + e.getMessage());
        }
    }

    @Transactional
    public JSONObject put(long bookId, Book book) {
        Book oldBook = bookRepository.findById(bookId).orElse(null);
        if (oldBook == null) {
            return new BaseDTO().build(404, "书籍不存在");
        }

        // 1. 检查ISBN格式
        if (book.getBookIsbn() != null && !isValidISBN(book.getBookIsbn())) {
            return new ReturnObject(400, "ISBN格式错误: 必须是13位数字");
        }

        // 检查ISBN是否重复（如果修改了ISBN）
        if (book.getBookIsbn() != null && !book.getBookIsbn().equals(oldBook.getBookIsbn())) {
            Book existingBook = bookRepository.findByBookIsbn(book.getBookIsbn());
            if (existingBook != null && existingBook.getBookId() != bookId) {
                return new BaseDTO().build(400, "更新失败: ISBN已存在");
            }
        }

        // 更新字段
        if (book.getBookTitle() != null) oldBook.setBookTitle(book.getBookTitle());
        if (book.getBookAuthor() != null) oldBook.setBookAuthor(book.getBookAuthor());
        if (book.getBookIsbn() != null) oldBook.setBookIsbn(book.getBookIsbn());
        if (book.getBookImage() != null) oldBook.setBookImage(book.getBookImage());
        if (book.getBookCategory() != null) oldBook.setBookCategory(book.getBookCategory());
        if (book.getBookPublisher() != null) oldBook.setBookPublisher(book.getBookPublisher());
        if (book.getBookSummary() != null) oldBook.setBookSummary(book.getBookSummary());
        if (book.getBookTranslator() != null) oldBook.setBookTranslator(book.getBookTranslator());

        // 设置更新时间
        oldBook.setUpdateTime(new Timestamp(System.currentTimeMillis()));

        try {
            bookRepository.save(oldBook);
            return new BaseDTO().build( oldBook.toDTO());
        } catch (Exception e) {
            return new BaseDTO().build(500, "更新失败: " + e.getMessage());
        }
    }

    // 验证ISBN格式为13位
    private boolean isValidISBN(String isbn) {
        return isbn != null && isbn.matches("\\d{13}");
    }

    // 验证字段长度
    private boolean validateFieldLengths(Book book) {
        if (StringUtils.length(book.getBookTitle()) > 255) return false;
        if (StringUtils.length(book.getBookImage()) > 511) return false;
        if (StringUtils.length(book.getBookAuthor()) > 255) return false;
        if (StringUtils.length(book.getBookTranslator()) > 255) return false;
        if (StringUtils.length(book.getBookCategory()) > 255) return false;
        if (StringUtils.length(book.getBookPublisher()) > 255) return false;
        return true;
    }

//    public JSONObject searchByBookTitle(String bookTitle) {
//        List<Book> books = bookRepository.findByBookTitleContaining(bookTitle);
//        List<BookDTO> bookDTOs = new ArrayList<>();
//        for (Book book : books) {
//            BookDTO bookDTO = book.toDTO();
//            bookDTOs.add(bookDTO);
//        }
//        return new BaseDTO().build(bookDTOs);
//    }

    public JSONObject searchByBookTitle(String bookTitle) {
        System.out.println("执行搜索: " + bookTitle);
        try {
            if (bookTitle == null || bookTitle.trim().isEmpty()) {
                System.out.println("搜索关键词为空，返回空结果");
                return new BaseDTO().build(new ArrayList<>());
            }

            // 添加日志输出
            List<Book> books = bookRepository.findByBookTitleContaining(bookTitle);
            System.out.println("找到 " + books.size() + " 本书");

            List<BookDTO> bookDTOs = books.stream()
                    .map(book -> (BookDTO) book.toDTO())
                    .collect(Collectors.toList());

            //return new BaseDTO().build(bookDTOs);
            return new ReturnObject(0, "success", bookDTOs);
        } catch (Exception e) {
            System.err.println("搜索异常: " + e.getMessage());
            e.printStackTrace();
            return new ReturnObject(500, "搜索失败: " + e.getMessage());
        }
    }


    // 修复分页查询方法
    @Override
    public JSONObject getByPage(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Book> bookPage = bookRepository.findAll(pageable);

            // 修复类型转换问题
            List<BookDTO> bookDTOs = bookPage.getContent().stream()
                    .map(book -> (BookDTO) book.toDTO()) // 添加显式类型转换
                    .collect(Collectors.toList());

            JSONObject result = new JSONObject();
            result.put("content", bookDTOs);
            result.put("totalElements", bookPage.getTotalElements());

            return new ReturnObject(0, "success", result);
        } catch (Exception e) {
            return new ReturnObject(500, "查询失败: " + e.getMessage());
        }
    }
}