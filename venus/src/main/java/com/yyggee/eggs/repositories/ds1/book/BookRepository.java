package com.yyggee.eggs.repositories.ds1.book;

import com.yyggee.eggs.model.ds1.book.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface BookRepository extends JpaRepository<Book, Integer>{
    @EntityGraph(attributePaths = "bookCategory")
    List<Book> findFirst10ByOrderByNameAsc();

    @Modifying
    @Transactional
    @Query("DELETE FROM Book b WHERE b.bookCategory.id = ?1")
    void deleteInBulkByCategoryId(int categoryId);

    @Transactional
    void deleteByBookCategoryId(int categoryId);
}
