package com.factory.repository;

import com.factory.entity.BookEntity;
import org.springframework.data.repository.CrudRepository;

public interface BookRepository extends CrudRepository<BookEntity, Long> {

    BookEntity findByIsbn(String isbn);
    BookEntity findById(long id);
}
