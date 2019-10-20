package com.unmeshc.ourthoughts.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by uc on 10/3/2019
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;
    private LocalDateTime expiryDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    public void calculateExpiryDate() {
        expiryDate = LocalDateTime.now().plusDays(1L);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}