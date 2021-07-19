package me.toy.atdd.subwayservice.favorite.domain;

import me.toy.atdd.subwayservice.BaseEntity;
import me.toy.atdd.subwayservice.member.domain.Member;
import me.toy.atdd.subwayservice.station.domain.Station;

import javax.persistence.*;

@Entity
public class Favorite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "source_id", nullable = false)
    private Station source;

    @ManyToOne
    @JoinColumn(name = "target_id", nullable = false)
    private Station target;

    protected Favorite() {
    }

    public Favorite(Member member, Station source, Station target) {
        this.member = member;
        this.source = source;
        this.target = target;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Station getSource() {
        return source;
    }

    public Station getTarget() {
        return target;
    }
}
