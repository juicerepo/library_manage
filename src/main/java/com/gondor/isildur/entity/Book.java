package com.gondor.isildur.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.gondor.isildur.DTO.BookDTO;
import com.gondor.isildur.util.ToDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "book")
@ToDTO(DTOClass = BookDTO.class)
public class Book extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long bookId;

  @NotBlank(message = "ISBN不能为空")
  @Pattern(regexp = "\\d{13}", message = "ISBN必须是13位数字")
  private String bookIsbn;

  @NotBlank(message = "书名不能为空")
  @Size(max = 255, message = "书名长度不能超过255个字符")
  private String bookTitle;

  @Size(max = 511, message = "图片URL长度不能超过511个字符")
  private String bookImage;

  @NotBlank(message = "作者不能为空")
  @Size(max = 255, message = "作者名称长度不能超过255个字符")
  private String bookAuthor;

  @Size(max = 255, message = "译者名称长度不能超过255个字符")
  private String bookTranslator;

  @NotBlank(message = "分类不能为空")
  @Size(max = 255, message = "分类长度不能超过255个字符")
  private String bookCategory;

  @NotBlank(message = "出版社不能为空")
  @Size(max = 255, message = "出版社名称长度不能超过255个字符")
  private String bookPublisher;

  @NotBlank(message = "简介不能为空")
  private String bookSummary;

  private java.sql.Timestamp createTime;
  private java.sql.Timestamp updateTime;
}