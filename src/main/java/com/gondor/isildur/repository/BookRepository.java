package com.gondor.isildur.repository;

import java.util.List;

import com.gondor.isildur.entity.Book;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface BookRepository extends PagingAndSortingRepository<Book, Long> {
  List<Book> findByBookTitleContaining(String bookTitle);
  // 添加通过ISBN查询书籍的方法
  Book findByBookIsbn(String bookIsbn);

}