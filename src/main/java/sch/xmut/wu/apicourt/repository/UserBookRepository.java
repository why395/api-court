package sch.xmut.wu.apicourt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sch.xmut.wu.apicourt.entity.UserBookEntity;

import java.util.List;

/**
 * Created by wu on 2020/04/13
 */
public interface UserBookRepository extends JpaRepository<UserBookEntity, Integer> {
    List<UserBookEntity> findAllByUserId(Integer userId);
    Page<UserBookEntity> findAll(Pageable pageable);
}
