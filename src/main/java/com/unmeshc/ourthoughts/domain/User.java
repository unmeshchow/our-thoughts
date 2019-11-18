package com.unmeshc.ourthoughts.domain;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by uc on 10/9/2019
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(length = 60)
    private String password;
    private Boolean active = false;
    private LocalDateTime registrationDateTime;

    @Lob
    private Byte[] image;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public boolean hasImage() {
        return image != null;
    }

    public boolean isAdmin() {
        List<Role> adminRoles = roles
                .stream()
                .filter(role -> "ADMIN".equalsIgnoreCase(role.getName()))
                .collect(Collectors.toList());

        return adminRoles.isEmpty() ? false : true;
    }

    @PrePersist
    public void setRegistrationDateAndTime() {
        registrationDateTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
