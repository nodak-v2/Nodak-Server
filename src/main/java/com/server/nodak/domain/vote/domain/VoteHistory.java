package com.server.nodak.domain.vote.domain;

import com.server.nodak.domain.model.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class VoteHistory extends BaseEntity {
    @ManyToOne(cascade = {CascadeType.PERSIST}, optional = false)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private VoteOption voteOption;
    // TODO : User FK 추가 예정
//    @ManyToOne(cascade = {CascadeType.PERSIST}, optional = false)
//    @JoinColumn(nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
//    private User user;
}
