package com.antony.openjobs.modules.posts.controller;

import com.antony.openjobs.common.pagination.IPaginationResponse;
import com.antony.openjobs.modules.comments.usecase.replies.FindCommentRepliesUseCase;
import com.antony.openjobs.modules.comments.usecase.update.UpdateCommentUseCase;
import com.antony.openjobs.modules.comments.usecase.update.UpdateCommentResponse;
import com.antony.openjobs.modules.comments.usecase.delete.DeleteCommentUseCase;
import com.antony.openjobs.modules.comments.usecase.delete.DeleteCommentResponse;
import org.springframework.http.MediaType;
import com.antony.openjobs.modules.commentlikes.usecase.set.SetCommentLikeUseCase;
import com.antony.openjobs.modules.commentlikes.usecase.findall.FindCommentLikesUseCase;
import com.antony.openjobs.modules.likes.usecase.findall.FindPostLikesUseCase;
import com.antony.openjobs.config.security.AuthenticatedUser;
import com.antony.openjobs.modules.comments.usecase.create.CreateCommentUseCase;
import com.antony.openjobs.modules.comments.usecase.findall.FindAllPostCommentsUseCase;
import com.antony.openjobs.modules.likes.usecase.set.SetPostLikeUseCase;
import com.antony.openjobs.modules.posts.usecase.create.CreatePostUseCase;
import com.antony.openjobs.modules.posts.usecase.delete.DeletePostResponse;
import com.antony.openjobs.modules.posts.usecase.delete.DeletePostUseCase;
import com.antony.openjobs.modules.posts.usecase.feed.FindCommunityFeedUseCase;
import com.antony.openjobs.modules.posts.usecase.profile.FindProfilePostsUseCase;
import com.antony.openjobs.modules.posts.usecase.update.UpdatePostRequest;
import com.antony.openjobs.modules.posts.usecase.update.UpdatePostResponse;
import com.antony.openjobs.modules.posts.usecase.update.UpdatePostUseCase;
import com.antony.openjobs.utils.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommunityControllerTest {
    final UUID userId = UUID.randomUUID();
    final UpdatePostUseCase updatePost = mock(UpdatePostUseCase.class);
    final DeletePostUseCase deletePost = mock(DeletePostUseCase.class);
    final FindProfilePostsUseCase findProfilePosts = mock(FindProfilePostsUseCase.class);
    final UpdateCommentUseCase updateComment = mock(UpdateCommentUseCase.class);
    final DeleteCommentUseCase deleteComment = mock(DeleteCommentUseCase.class);
    MockMvc mvc;

    @BeforeEach
    void setUp() {
        var controller = new CommunityController(
                mock(FindCommunityFeedUseCase.class), mock(FindAllPostCommentsUseCase.class),
                mock(CreateCommentUseCase.class), mock(SetPostLikeUseCase.class),
                mock(CreatePostUseCase.class), findProfilePosts, updatePost, deletePost,
                mock(FindCommentRepliesUseCase.class), updateComment, mock(SetCommentLikeUseCase.class),
                mock(FindPostLikesUseCase.class), mock(FindCommentLikesUseCase.class), deleteComment
        );
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new HandlerMethodArgumentResolver() {
                    public boolean supportsParameter(MethodParameter parameter) {
                        return parameter.getParameterType() == AuthenticatedUser.class;
                    }
                    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container,
                                                  NativeWebRequest request, WebDataBinderFactory binder) {
                        return new AuthenticatedUser(userId, UUID.randomUUID());
                    }
                }).build();
    }

    @Test
    void bindsMultipartEditAndUsesAuthenticatedAuthor() throws Exception {
        var postId = UUID.randomUUID();
        when(updatePost.execute(eq(postId), eq(userId), any())).thenReturn(new UpdatePostResponse("Atualizada"));
        mvc.perform(multipart("/v1/community/posts/{postId}", postId)
                        .file(new MockMultipartFile("file", "photo.png", "image/png", new byte[]{1}))
                        .param("content", "Texto editado").param("removeFile", "false")
                        .param("userId", UUID.randomUUID().toString())
                        .with(request -> { request.setMethod("PUT"); return request; }))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.message").value("Atualizada"));
        var request = ArgumentCaptor.forClass(UpdatePostRequest.class);
        verify(updatePost).execute(eq(postId), eq(userId), request.capture());
        assertThat(request.getValue().file().getOriginalFilename()).isEqualTo("photo.png");
        assertThat(request.getValue().removeFile()).isFalse();
    }

    @Test
    void rejectsBlankAndOversizedEditsBeforeCallingUseCase() throws Exception {
        for (var content : List.of("  ", "a".repeat(3001))) {
            mvc.perform(multipart("/v1/community/posts/{postId}", UUID.randomUUID())
                            .param("content", content).param("removeFile", "false")
                            .with(request -> { request.setMethod("PUT"); return request; }))
                    .andExpect(status().isBadRequest());
        }
        verifyNoInteractions(updatePost);
    }

    @Test
    void listsAndDeletesUsingAuthenticatedUserInsteadOfSubmittedUserId() throws Exception {
        when(findProfilePosts.execute(userId, 2, 10)).thenReturn(new IPaginationResponse<>(2, 10, 0, List.of()));
        mvc.perform(get("/v1/community/posts/me").param("page", "2").param("userId", UUID.randomUUID().toString()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.items").isEmpty());
        verify(findProfilePosts).execute(userId, 2, 10);
        var postId = UUID.randomUUID();
        when(deletePost.execute(postId, userId)).thenReturn(new DeletePostResponse("Excluída"));
        mvc.perform(delete("/v1/community/posts/{postId}", postId).param("userId", UUID.randomUUID().toString()))
                .andExpect(status().isOk());
        verify(deletePost).execute(postId, userId);
    }
    @Test
    void editsAndDeletesCommentsUsingTheAuthenticatedIdentity() throws Exception {
        var postId = UUID.randomUUID();
        var commentId = UUID.randomUUID();
        when(updateComment.execute(eq(postId), eq(commentId), eq(userId), any())).thenReturn(new UpdateCommentResponse("Atualizado"));
        mvc.perform(put("/v1/community/posts/{postId}/comments/{commentId}", postId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"Texto atualizado\",\"userId\":\"" + UUID.randomUUID() + "\"}"))
                .andExpect(status().isOk());
        verify(updateComment).execute(eq(postId), eq(commentId), eq(userId), any());
        when(deleteComment.execute(postId, commentId, userId)).thenReturn(new DeleteCommentResponse("Excluído"));
        mvc.perform(delete("/v1/community/posts/{postId}/comments/{commentId}", postId, commentId)
                        .param("userId", UUID.randomUUID().toString()))
                .andExpect(status().isOk());
        verify(deleteComment).execute(postId, commentId, userId);
    }

}
