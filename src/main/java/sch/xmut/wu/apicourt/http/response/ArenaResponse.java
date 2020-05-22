package sch.xmut.wu.apicourt.http.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import sch.xmut.wu.apicourt.http.vo.Arena;
import sch.xmut.wu.apicourt.http.vo.ArenaComment;
import sch.xmut.wu.apicourt.http.vo.Court;
import java.util.List;

/**
 * Created by wu on 2020/04/13
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArenaResponse extends BaseResponse {
    @JsonProperty("arena_list")
    private List<Arena> arenaList;
    @JsonProperty("arena")
    private Arena arena;
    @JsonProperty("court_list")
    private List<Court> courtList;
    @JsonProperty("comment_list")
    private List<ArenaComment> arenaCommentList;

    public List<Arena> getArenaList() {
        return arenaList;
    }

    public void setArenaList(List<Arena> arenaList) {
        this.arenaList = arenaList;
    }

    public Arena getArena() {
        return arena;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public List<Court> getCourtList() {
        return courtList;
    }

    public void setCourtList(List<Court> courtList) {
        this.courtList = courtList;
    }

    public List<ArenaComment> getArenaCommentList() {
        return arenaCommentList;
    }

    public void setArenaCommentList(List<ArenaComment> arenaCommentList) {
        this.arenaCommentList = arenaCommentList;
    }
}
