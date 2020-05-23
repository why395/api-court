package sch.xmut.wu.apicourt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sch.xmut.wu.apicourt.entity.FormEntity;

public interface FormRepository extends JpaRepository<FormEntity, Integer> {
}
