package sch.xmut.wu.apicourt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sch.xmut.wu.apicourt.entity.FormEntity;

import java.util.List;

public interface FormRepository extends JpaRepository<FormEntity, Integer> {
}
