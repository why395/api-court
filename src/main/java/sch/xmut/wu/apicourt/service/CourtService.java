package sch.xmut.wu.apicourt.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sch.xmut.wu.apicourt.entity.ArenaEntity;
import sch.xmut.wu.apicourt.entity.CourtEntity;
import sch.xmut.wu.apicourt.http.request.CourtRequest;
import sch.xmut.wu.apicourt.http.response.BaseResponse;
import sch.xmut.wu.apicourt.http.response.LayerResponse;
import sch.xmut.wu.apicourt.http.vo.Court;
import sch.xmut.wu.apicourt.repository.ArenaRepository;
import sch.xmut.wu.apicourt.repository.CourtRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourtService {
    @Autowired
    private CourtRepository courtRepository;
    @Autowired
    private ArenaRepository arenaRepository;

    public LayerResponse getCourtList(Pageable pageable) {
        LayerResponse response = new LayerResponse();
        List<Court> courtList = new ArrayList<>();
        Page<CourtEntity> courtEntityPage = courtRepository.findAllByStatus(pageable, CourtEntity.STATUS_BOOK);
        for (CourtEntity courtEntity : courtEntityPage) {
            Court court = new Court();
            BeanUtils.copyProperties(courtEntity, court);
            Optional<ArenaEntity> optional = arenaRepository.findById(courtEntity.getArenaId());    //通过arena_id找球馆
            if (optional.isPresent()) {
                court.setArenaName(optional.get().getName());
            }
            courtList.add(court);
        }
        response.setData(courtList);
        response.setCount((int) courtRepository.count());
        return response;
    }

    public BaseResponse courtDelete(Integer courtId) {
        CourtEntity courtEntity = courtRepository.findById(courtId).get();
        courtEntity.setStatus(CourtEntity.STATUS_DELETE);
        courtRepository.save(courtEntity);
        return new BaseResponse();
    }

    public BaseResponse courtAdd(CourtRequest courtRequest) {
        CourtEntity courtEntity = new CourtEntity();
        BeanUtils.copyProperties(courtRequest, courtEntity);
        courtEntity.setStatus(CourtEntity.STATUS_BOOK);
        courtEntity.setArenaId(courtRequest.getArenaId());
        courtEntity.setScore(4.5);
        courtRepository.save(courtEntity);
        return new BaseResponse();
    }
}

