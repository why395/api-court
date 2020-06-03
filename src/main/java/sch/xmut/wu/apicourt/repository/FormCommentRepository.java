package sch.xmut.wu.apicourt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sch.xmut.wu.apicourt.entity.FormCommentEntity;

import java.util.List;

public interface FormCommentRepository extends JpaRepository<FormCommentEntity, Integer> {
    List<FormCommentEntity> findAllByFormIdOrderByCommentTimeDesc(Integer formId);
}
