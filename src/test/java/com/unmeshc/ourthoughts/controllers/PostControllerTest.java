package com.unmeshc.ourthoughts.controllers;

import com.unmeshc.ourthoughts.dtos.PostDetailsDto;
import com.unmeshc.ourthoughts.dtos.PostSearchListDto;
import com.unmeshc.ourthoughts.services.PostService;
import com.unmeshc.ourthoughts.services.exceptions.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PostControllerTest {

    @Mock
    private PostService postService;

    @Mock
    private ControllerUtils controllerUtils;

    @InjectMocks
    private PostController postController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                    .setControllerAdvice(new ControllerExceptionHandler()).build();
    }

    @Test
    public void searchWithDefaultValues() throws Exception {
        when(postService.getPostsByTitleLike(anyInt(), anyInt(), anyString()))
                .thenReturn(PostSearchListDto.builder().build());

        mockMvc.perform(get("/visitor/post/search"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postSearchListDto"))
                .andExpect(view().name(PostController.INDEX));

        verify(postService).getPostsByTitleLike(0, 0, "");
    }

    @Test
    public void searchWithValues() throws Exception {
        when(postService.getPostsByTitleLike(anyInt(), anyInt(), anyString()))
                .thenReturn(PostSearchListDto.builder().build());

        mockMvc.perform(get("/visitor/post/search")
                    .param("page", "3")
                    .param("size", "5")
                    .param("title", "Bangladesh"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("postSearchListDto"))
                .andExpect(view().name(PostController.INDEX));

        verify(postService).getPostsByTitleLike(3, 5, "Bangladesh");
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
    public void viewPostDetailsNotFound() throws Exception {
        when(postService.getPostDetailsById(anyLong())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/visitor/post/" + 1 + "/details"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void obtainPostPhotoPostNotFound() throws Exception {
        when(postService.getPostPhotoById(anyLong())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/visitor/post/" + 1 + "/photo"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void obtainPostPhoto() throws Exception {
        byte[] bytes = new byte[100];
        when(postService.getPostPhotoById(anyLong())).thenReturn(bytes);

        mockMvc.perform(get("/visitor/post/" + 1 + "/photo"))
                .andExpect(status().isOk());

        verify(controllerUtils).copyBytesToResponse(any(HttpServletResponse.class), eq(bytes));
    }

    @Test
    public void obtainUserImageUserNotFound() throws Exception {
        when(postService.getUserImageById(anyLong())).thenThrow(NotFoundException.class);

        mockMvc.perform(get("/visitor/user/" + 1 + "/image"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void obtainUserImage() throws Exception {
        byte[] bytes = new byte[100];
        when(postService.getUserImageById(anyLong())).thenReturn(bytes);

        mockMvc.perform(get("/visitor/user/" + 1 + "/image"))
                .andExpect(status().isOk());

        verify(controllerUtils).copyBytesToResponse(any(HttpServletResponse.class), eq(bytes));
    }
}