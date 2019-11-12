package com.unmeshc.ourthoughts.dtos;


import lombok.*;

import java.util.List;
import java.util.Set;

/**
 * Created by uc on 10/9/2019
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserAdminListDto {

    private List<UserAdminDto> userAdminDtos;
    private int currentPage;
    private Set<Integer> pageNumbers;
}
