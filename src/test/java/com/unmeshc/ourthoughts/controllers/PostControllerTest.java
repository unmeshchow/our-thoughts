package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.controllers.pagination.SearchPostPageTracker;
import com.unmeshc.ourthoughts.domain.Post;
import com.unmeshc.ourthoughts.domain.User;
import com.unmeshc.ourthoughts.dtos.PostDetailsDto;
import com.unmeshc.ourthoughts.dtos.PostSearchDto;
import com.unmeshc.ourthoughts.services.PostService;
import com.unmeshc.ourthoughts.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    @Mock
    private ControllerUtils controllerUtils;

    @Mock
    private SearchPostPageTracker searchPostPageTracker;

    @InjectMocks
    private PostController controller;

    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void searchWithDefaultValues() throws Exception {
        List<PostSearchDto> postAdminDtos = new ArrayList<>();
        Page<Post> postPage = Mockito.mock(Page.class);
        when(searchPostPageTracker.getSearchValue()).thenReturn("Uttam Kumar");
        when(searchPostPageTracker.getCurrentPage()).thenReturn(1);
        when(postService.getPostsLikeTitle(anyString(), any(Pageable.class)))
                .thenReturn(postPage);
        when(controllerUtils.convertToPostSearchDtoList(postPage.getContent()))
                .thenReturn(postAdminDtos);

        mockMvc.perform(get("/visitor/post/search"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postSearchDtos"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("pageNumbers"))
                .andExpect(view().name(PostController.INDEX));

        ArgumentCaptor<Pageable> pageableArgumentCaptor =
                ArgumentCaptor.forClass(Pageable.class);
        verify(postService).getPostsLikeTitle(eq("Uttam Kumar"),
                pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(0);
        assertThat(pageable.getPageSize()).isEqualTo(2);
        assertThat(pageable.getSort()).isNotNull();
        assertThat(pageable.getSort().getOrderFor("creationDateTime")).isNotNull();

        verify(searchPostPageTracker, times(2)).getSearchValue();
        verify(searchPostPageTracker).setCurrentPage(anyInt());
        verify(searchPostPageTracker).setSearchValue("Uttam Kumar");
        verify(controllerUtils).convertToPostSearchDtoList(postPage.getContent());
        verify(searchPostPageTracker, times(2)).getCurrentPage();
        verify(searchPostPageTracker).getPageNumbersForPagination(postPage);
    }

    @Test
    public void searchWithValues() throws Exception {
        List<PostSearchDto> postAdminDtos = new ArrayList<>();
        Page<Post> postPage = Mockito.mock(Page.class);
        when(searchPostPageTracker.getSearchValue()).thenReturn("");
        when(searchPostPageTracker.getCurrentPage()).thenReturn(1);
        when(postService.getPostsLikeTitle(anyString(), any(Pageable.class)))
                .thenReturn(postPage);
        when(controllerUtils.convertToPostSearchDtoList(postPage.getContent()))
                .thenReturn(postAdminDtos);

        mockMvc.perform(get("/visitor/post/search")
                    .param("page", "3")
                    .param("size", "5")
                    .param("search", "Madonna"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postSearchDtos"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("pageNumbers"))
                .andExpect(view().name(PostController.INDEX));

        ArgumentCaptor<Pageable> pageableArgumentCaptor =
                ArgumentCaptor.forClass(Pageable.class);
        verify(postService).getPostsLikeTitle(eq("Madonna"),
                pageableArgumentCaptor.capture());
        Pageable pageable = pageableArgumentCaptor.getValue();
        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort()).isNotNull();
        assertThat(pageable.getSort().getOrderFor("creationDateTime")).isNotNull();

        verify(searchPostPageTracker).reset();
        verify(searchPostPageTracker, times(1)).getSearchValue();
        verify(searchPostPageTracker).setCurrentPage(anyInt());
        verify(searchPostPageTracker).setSearchValue("Madonna");
        verify(controllerUtils).convertToPostSearchDtoList(postPage.getContent());
        verify(searchPostPageTracker, times(1)).getCurrentPage();
        verify(searchPostPageTracker).getPageNumbersForPagination(postPage);
    }

    @Test
    public void viewPostDetails() throws Exception {
        when(postService.getPostDetailsById(anyLong())).thenReturn(
                PostDetailsDto.builder().build());

        mockMvc.perform(get("/visitor/post/" + 1 + "/details"))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("postDetails"))
               .andExpect(view().name(PostController.POST_DETAILS));

        verify(postService).getPostDetailsById(1);
    }

    @Test
    public void obtainPostPhotoPostNotFound() throws Exception {
        when(postService.getById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/visitor/post/" + 1 + "/photo"))
                .andExpect(status().isOk());

        verify(postService).getById(1);
        verifyZeroInteractions(controllerUtils);
    }

    @Test
    public void obtainPostPhoto() throws Exception {
        Byte[] bytes = new Byte[100];
        when(postService.getById(anyLong())).thenReturn(
                Post.builder().id(1L).photo(bytes).build());

        mockMvc.perform(get("/visitor/post/" + 1 + "/photo"))
                .andExpect(status().isOk());

        verify(postService).getById(1);
        verify(controllerUtils).convertIntoByteArray(bytes);
        verify(controllerUtils).copyBytesToResponse(any(HttpServletResponse.class), any());
    }

    @Test
    public void obtainUserImageUserNotFound() throws Exception {
        when(userService.getById(anyLong())).thenReturn(null);

        mockMvc.perform(get("/visitor/user/" + 1 + "/image"))
                .andExpect(status().isOk());

        verify(userService).getById(1);
        verifyZeroInteractions(controllerUtils);
    }

    @Test
    public void obtainUserImage() throws Exception {
        Byte[] bytes = new Byte[100];
        when(userService.getById(anyLong())).thenReturn(
                User.builder().id(1L).image(bytes).build());

        mockMvc.perform(get("/visitor/user/" + 1 + "/image"))
                .andExpect(status().isOk());

        verify(userService).getById(1);
        verify(controllerUtils).convertIntoByteArray(bytes);
        verify(controllerUtils).copyBytesToResponse(any(HttpServletResponse.class), any());
    }
}