package sch.xmut.wu.apicourt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sch.xmut.wu.apicourt.entity.ArenaCommentEntity;
import java.util.List;

public interface ArenaCommentRepository extends JpaRepository<ArenaCommentEntity, Integer> {
    List<ArenaCommentEntity> findAllByArenaId(Integer arenaId);
}
