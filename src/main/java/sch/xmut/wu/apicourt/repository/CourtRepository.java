package sch.xmut.wu.apicourt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import sch.xmut.wu.apicourt.entity.CourtEntity;
import java.util.List;

/**
 * Created by wu on 2020/04/13
 */
public interface CourtRepository extends JpaRepository<CourtEntity, Integer> {
    List<CourtEntity> findAllByArenaId(Integer arenaId);
    Page<CourtEntity> findAll(Pageable pageable);
    Page<CourtEntity> findAllByStatus(Pageable pageable, Integer status);
}
