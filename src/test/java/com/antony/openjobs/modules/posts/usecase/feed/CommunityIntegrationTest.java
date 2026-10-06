package com.antony.openjobs.modules.posts.usecase.feed;

import com.antony.openjobs.modules.comments.model.CommentEntity;
import com.antony.openjobs.modules.users.usecase.publicprofile.FindPublicProfileUseCase;
import com.antony.openjobs.modules.posts.usecase.publicprofile.FindPublicProfilePostsUseCase;
import com.antony.openjobs.modules.users.usecase.updateprofile.UpdateProfileUseCase;
import com.antony.openjobs.modules.users.usecase.updateprofile.UpdateProfileRequest;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import com.antony.openjobs.modules.comments.usecase.CommentResponse;
import com.antony.openjobs.modules.comments.usecase.delete.DeleteCommentUseCase;
import com.antony.openjobs.modules.comments.usecase.replies.FindCommentRepliesUseCase;
import com.antony.openjobs.modules.comments.usecase.update.UpdateCommentUseCase;
import com.antony.openjobs.modules.comments.usecase.update.UpdateCommentRequest;
import com.antony.openjobs.modules.commentlikes.repository.ICommentLikeRepository;
import com.antony.openjobs.modules.commentlikes.usecase.set.SetCommentLikeUseCase;
import com.antony.openjobs.modules.commentlikes.usecase.set.SetCommentLikeRequest;
import com.antony.openjobs.modules.commentlikes.usecase.findall.FindCommentLikesUseCase;
import com.antony.openjobs.modules.likes.usecase.findall.FindPostLikesUseCase;
import com.antony.openjobs.modules.users.usecase.UserSummaryResponse;
import java.util.ArrayList;
import java.util.List;
import com.antony.openjobs.modules.comments.repository.ICommentRepository;
import com.antony.openjobs.modules.comments.usecase.findall.FindAllPostCommentsUseCase;
import com.antony.openjobs.modules.comments.usecase.create.CreateCommentUseCase;
import com.antony.openjobs.modules.comments.usecase.create.CreateCommentRequest;
import com.antony.openjobs.modules.likes.usecase.set.SetPostLikeUseCase;
import com.antony.openjobs.modules.likes.usecase.set.SetPostLikeRequest;
import com.antony.openjobs.modules.likes.model.LikeEntity;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;
import com.antony.openjobs.modules.posts.model.PostEntity;
import com.antony.openjobs.modules.posts.usecase.profile.FindProfilePostsUseCase;
import com.antony.openjobs.modules.posts.usecase.update.UpdatePostUseCase;
import com.antony.openjobs.modules.posts.usecase.update.UpdatePostRequest;
import com.antony.openjobs.modules.posts.usecase.delete.DeletePostUseCase;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.antony.openjobs.modules.posts.repository.IPostRepository;
import com.antony.openjobs.modules.posts.usecase.create.CreatePostUseCase;
import com.antony.openjobs.modules.posts.usecase.create.CreatePostRequest;
import com.antony.openjobs.modules.roles.repository.IRoleRepository;
import com.antony.openjobs.modules.users.model.UserEntity;
import com.antony.openjobs.modules.users.repository.IUserRepository;
import com.antony.openjobs.utils.RoleCodeUtils;
import jakarta.persistence.EntityManager;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "R2_ENABLED=false")
@Transactional
class CommunityIntegrationTest {
    @Autowired FindCommunityFeedUseCase findFeed;
    @Autowired FindAllPostCommentsUseCase findComments;
    @Autowired CreateCommentUseCase createComment;
    @Autowired SetPostLikeUseCase setLike;
    @Autowired IUserRepository users;
    @Autowired IRoleRepository roles;
    @Autowired IPostRepository posts;
    @Autowired ILikeRepository likes;
    @Autowired ICommentRepository comments;
    @Autowired EntityManager entityManager;
    @Autowired CreatePostUseCase createPost;
    @Autowired Validator validator;
    @Autowired FindProfilePostsUseCase findProfilePosts;
    @Autowired UpdatePostUseCase updatePost;
    @Autowired DeletePostUseCase deletePost;
    @Autowired FindCommentRepliesUseCase findReplies;
    @Autowired UpdateCommentUseCase updateComment;
    @Autowired SetCommentLikeUseCase setCommentLike;
    @Autowired ICommentLikeRepository commentLikes;
    @Autowired FindCommentLikesUseCase findCommentLikes;
    @Autowired FindPostLikesUseCase findPostLikes;
    @Autowired DeleteCommentUseCase deleteComment;
    @Autowired FindPublicProfileUseCase findPublicProfile;
    @Autowired FindPublicProfilePostsUseCase findPublicPosts;
    @Autowired UpdateProfileUseCase updateProfile;
    @Autowired WebApplicationContext webApplicationContext;
    @Autowired FilterChainProxy springSecurityFilterChain;

    @Test
    void persistsThreeThousandCharactersAndRejectsContentAboveTheLimit() {
        var author = user("Autor do post longo");
        var content = "a".repeat(3000);
        var request = new CreatePostRequest(content, null);
        assertThat(validator.validate(request)).isEmpty();

        createPost.execute(request, author.getId());
        entityManager.flush();
        entityManager.clear();

        var saved = posts.findAllByUser_IdAndDeletedAtIsNull(author.getId(), PageRequest.of(0, 1));
        assertThat(saved.getContent()).hasSize(1);
        assertThat(saved.getContent().get(0).getContent()).isEqualTo(content);
        assertThat(validator.validate(new CreatePostRequest("a".repeat(3001), null)))
                .anySatisfy(violation -> assertThat(violation.getPropertyPath().toString()).isEqualTo("content"));
    }

    @Test void paginatesAllAuthorsAndIncludesPersistedCommentsAndLikes() {
        var maria = user("Maria"); var joao = user("João");
        var older = post(maria, LocalDateTime.now().plusDays(1));
        var newer = post(joao, LocalDateTime.now().plusDays(2));
        var like = new LikeEntity(); like.setPost(newer); like.setUser(maria); likes.save(like);
        var comment = new CommentEntity(); comment.setPost(newer); comment.setUser(maria); comment.setContent("Parabéns!"); comments.save(comment);
        entityManager.flush(); entityManager.clear();

        var first = findFeed.execute(maria.getId(), 0, 1);
        assertThat(first.items()).hasSize(1);
        assertThat(first.items().get(0).id()).isEqualTo(newer.getId());
        assertThat(first.items().get(0).author().name()).isEqualTo("João");
        assertThat(first.items().get(0).liked()).isTrue();
        assertThat(first.items().get(0).likesCount()).isEqualTo(1);
        assertThat(first.items().get(0).commentsCount()).isEqualTo(1);
        assertThat(findFeed.execute(maria.getId(), 1, 1).items().get(0).id()).isEqualTo(older.getId());
        assertThat(findComments.execute(newer.getId(), maria.getId(), 0).items().get(0).author().name()).isEqualTo("Maria");

        setLike.execute(newer.getId(), maria.getId(), new SetPostLikeRequest(false));
        assertThat(findFeed.execute(maria.getId(), 0, 1).items().get(0).likesCount()).isZero();
        setLike.execute(newer.getId(), maria.getId(), new SetPostLikeRequest(true));
        setLike.execute(newer.getId(), maria.getId(), new SetPostLikeRequest(true));
        assertThat(findFeed.execute(maria.getId(), 0, 1).items().get(0).likesCount()).isEqualTo(1);

        var created = createComment.execute(newer.getId(), joao.getId(), new CreateCommentRequest("  Obrigado!  "));
        assertThat(created.content()).isEqualTo("Obrigado!");
        assertThat(created.author().id()).isEqualTo(joao.getId());
        assertThat(findFeed.execute(maria.getId(), 0, 1).items().get(0).commentsCount()).isEqualTo(2);
    }

    @Test
    void listsOnlyOwnPostsAndPersistsEditsAndSoftDeletion() {
        var author = user("Autor do perfil");
        var other = user("Outro autor");
        var older = post(author, LocalDateTime.now().plusDays(1));
        var newer = post(author, LocalDateTime.now().plusDays(2));
        post(other, LocalDateTime.now().plusDays(3));
        newer.setFileUrl("https://example.invalid/original.png");
        posts.saveAndFlush(newer);
        entityManager.clear();

        var first = findProfilePosts.execute(author.getId(), 0, 1);
        assertThat(first.total()).isEqualTo(2);
        assertThat(first.items()).extracting(FeedResponse::id).containsExactly(newer.getId());
        assertThat(findProfilePosts.execute(author.getId(), 1, 1).items())
                .extracting(FeedResponse::id).containsExactly(older.getId());

        var originalCreatedAt = posts.findById(newer.getId()).orElseThrow().getCreatedAt();
        var editedContent = "Texto editado ".repeat(200).trim();
        updatePost.execute(newer.getId(), author.getId(), new UpdatePostRequest("  " + editedContent + "  ", null, false));
        entityManager.flush();
        entityManager.clear();
        var edited = posts.findById(newer.getId()).orElseThrow();
        assertThat(edited.getContent()).isEqualTo(editedContent);
        assertThat(edited.getFileUrl()).isEqualTo("https://example.invalid/original.png");
        assertThat(edited.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(validator.validate(new UpdatePostRequest("a".repeat(3001), null, false))).isNotEmpty();
        assertThat(validator.validate(new UpdatePostRequest("  ", null, false))).isNotEmpty();

        updatePost.execute(newer.getId(), author.getId(), new UpdatePostRequest(editedContent, null, true));
        entityManager.flush();
        entityManager.clear();
        assertThat(posts.findById(newer.getId()).orElseThrow().getFileUrl()).isNull();
        assertThat(findProfilePosts.execute(author.getId(), 0, 10).items().get(0).content()).isEqualTo(editedContent);

        var totalBefore = findFeed.execute(author.getId(), 0, 1).total();
        deletePost.execute(newer.getId(), author.getId());
        entityManager.flush();
        entityManager.clear();
        assertThat(posts.findById(newer.getId()).orElseThrow().getDeletedAt()).isNotNull();
        assertThat(posts.findByIdAndDeletedAtIsNull(newer.getId())).isEmpty();
        assertThat(findProfilePosts.execute(author.getId(), 0, 10).items())
                .extracting(FeedResponse::id).containsExactly(older.getId());
        assertThat(findFeed.execute(author.getId(), 0, 1).total()).isEqualTo(totalBefore - 1);
        assertThatThrownBy(() -> findComments.execute(newer.getId(), author.getId(), 0))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        assertThatThrownBy(() -> setLike.execute(newer.getId(), author.getId(), new SetPostLikeRequest(true)))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void paginatesRepliesKeepsRootsSeparateAndRestrictsCommentEditingToTheAuthor() {
        var author = user("Autor da conversa");
        var replier = user("Autor das respostas");
        var publication = post(author, LocalDateTime.now().plusDays(1));
        var root = createComment.execute(publication.getId(), author.getId(), new CreateCommentRequest("Comentário original"));
        var replies = new ArrayList<CommentResponse>();
        for (int i = 0; i < 11; i++) {
            replies.add(createComment.execute(publication.getId(), replier.getId(), new CreateCommentRequest("Resposta " + i, root.id())));
        }
        var nested = createComment.execute(publication.getId(), author.getId(), new CreateCommentRequest("Resposta à resposta", replies.get(0).id()));
        entityManager.flush();
        entityManager.clear();
        var roots = findComments.execute(publication.getId(), author.getId(), 0);
        assertThat(roots.total()).isEqualTo(1);
        assertThat(roots.items().get(0).parentCommentId()).isNull();
        assertThat(roots.items().get(0).repliesCount()).isEqualTo(11);
        var first = findReplies.execute(publication.getId(), root.id(), author.getId(), 0);
        assertThat(first.total()).isEqualTo(11);
        assertThat(first.items()).extracting(CommentResponse::id).containsExactlyElementsOf(replies.subList(0, 10).stream().map(CommentResponse::id).toList());
        assertThat(findReplies.execute(publication.getId(), root.id(), author.getId(), 1).items()).extracting(CommentResponse::id).containsExactly(replies.get(10).id());
        assertThat(first.items().get(0).repliesCount()).isEqualTo(1);
        assertThat(findReplies.execute(publication.getId(), replies.get(0).id(), author.getId(), 0).items()).extracting(CommentResponse::id).containsExactly(nested.id());
        assertThat(findProfilePosts.execute(author.getId(), 0, 10).items().get(0).commentsCount()).isEqualTo(13);

        var editedId = replies.get(0).id();
        assertThatThrownBy(() -> updateComment.execute(publication.getId(), editedId, author.getId(), new UpdateCommentRequest("Não autorizado")))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
        updateComment.execute(publication.getId(), editedId, replier.getId(), new UpdateCommentRequest("  Resposta editada  "));
        entityManager.flush();
        entityManager.clear();
        var edited = comments.findById(editedId).orElseThrow();
        assertThat(edited.getContent()).isEqualTo("Resposta editada");
        assertThat(edited.getUser().getId()).isEqualTo(replier.getId());
        assertThat(edited.getParentComment().getId()).isEqualTo(root.id());
        assertThat(edited.getCreatedAt()).isEqualTo(replies.get(0).createdAt().toLocalDateTime());
        assertThat(validator.validate(new UpdateCommentRequest("a".repeat(1001)))).isNotEmpty();
        assertThat(validator.validate(new CreateCommentRequest("  ", root.id()))).isNotEmpty();

        var anotherPost = post(author, LocalDateTime.now().plusDays(2));
        assertThatThrownBy(() -> createComment.execute(anotherPost.getId(), replier.getId(), new CreateCommentRequest("Outro post", root.id())))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        var parent = comments.findById(root.id()).orElseThrow();
        parent.setDeletedAt(LocalDateTime.now());
        comments.saveAndFlush(parent);
        assertThatThrownBy(() -> createComment.execute(publication.getId(), replier.getId(), new CreateCommentRequest("Comentário excluído", root.id())))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void commentLikesAreIdempotentRestorableAndExposeOnlyActivePublicProfiles() {
        var author = user("Autor das curtidas");
        var reader = user("Leitor das curtidas");
        var publication = post(author, LocalDateTime.now().plusDays(1));
        var root = createComment.execute(publication.getId(), author.getId(), new CreateCommentRequest("Vamos conversar"));
        var reply = createComment.execute(publication.getId(), reader.getId(), new CreateCommentRequest("Gostei!", root.id()));
        var on = new SetCommentLikeRequest(true);
        var off = new SetCommentLikeRequest(false);
        setCommentLike.execute(publication.getId(), root.id(), reader.getId(), off);
        assertThat(commentLikes.findByUser_IdAndComment_Id(reader.getId(), root.id())).isEmpty();
        setCommentLike.execute(publication.getId(), root.id(), reader.getId(), on);
        setCommentLike.execute(publication.getId(), root.id(), reader.getId(), on);
        var likeId = commentLikes.findByUser_IdAndComment_Id(reader.getId(), root.id()).orElseThrow().getId();
        var view = findComments.execute(publication.getId(), reader.getId(), 0).items().get(0);
        assertThat(view.liked()).isTrue();
        assertThat(view.likesCount()).isEqualTo(1);
        assertThat(findComments.execute(publication.getId(), author.getId(), 0).items().get(0).liked()).isFalse();
        assertThat(findCommentLikes.execute(publication.getId(), root.id(), 0).items()).extracting(UserSummaryResponse::id).containsExactly(reader.getId());
        setCommentLike.execute(publication.getId(), root.id(), reader.getId(), off);
        assertThat(findCommentLikes.execute(publication.getId(), root.id(), 0).total()).isZero();
        setCommentLike.execute(publication.getId(), root.id(), reader.getId(), on);
        assertThat(commentLikes.findByUser_IdAndComment_Id(reader.getId(), root.id()).orElseThrow().getId()).isEqualTo(likeId);
        setCommentLike.execute(publication.getId(), reply.id(), author.getId(), on);
        assertThat(findReplies.execute(publication.getId(), root.id(), author.getId(), 0).items().get(0).liked()).isTrue();
        assertThat(findCommentLikes.execute(publication.getId(), reply.id(), 0).total()).isEqualTo(1);
        setLike.execute(publication.getId(), author.getId(), new SetPostLikeRequest(true));
        setLike.execute(publication.getId(), reader.getId(), new SetPostLikeRequest(true));
        assertThat(findPostLikes.execute(publication.getId(), 0).items()).extracting(UserSummaryResponse::id).containsExactlyInAnyOrder(author.getId(), reader.getId());
        reader.setDeletedAt(LocalDateTime.now());
        users.saveAndFlush(reader);
        assertThat(findCommentLikes.execute(publication.getId(), root.id(), 0).total()).isZero();
        assertThat(findComments.execute(publication.getId(), author.getId(), 0).items().get(0).likesCount()).isZero();
        assertThat(findPostLikes.execute(publication.getId(), 0).items()).extracting(UserSummaryResponse::id).containsExactly(author.getId());
        deletePost.execute(publication.getId(), author.getId());
        for (Runnable action : List.<Runnable>of(
                () -> findReplies.execute(publication.getId(), root.id(), author.getId(), 0),
                () -> findPostLikes.execute(publication.getId(), 0),
                () -> findCommentLikes.execute(publication.getId(), root.id(), 0),
                () -> setCommentLike.execute(publication.getId(), root.id(), author.getId(), on),
                () -> updateComment.execute(publication.getId(), root.id(), author.getId(), new UpdateCommentRequest("Excluído")))) {
            assertThatThrownBy(action::run).isInstanceOfSatisfying(ResponseStatusException.class,
                    error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        }
    }

    @Test
    void onlyAuthorCanSoftDeleteACommentAndItsDescendants() {
        var author = user("Autor do comentário excluído");
        var other = user("Autor da resposta excluída");
        var publication = post(author, LocalDateTime.now().plusDays(1));
        var root = createComment.execute(publication.getId(), author.getId(), new CreateCommentRequest("Comentário"));
        var reply = createComment.execute(publication.getId(), other.getId(), new CreateCommentRequest("Resposta", root.id()));
        var nested = createComment.execute(publication.getId(), author.getId(), new CreateCommentRequest("Resposta à resposta", reply.id()));
        var remaining = createComment.execute(publication.getId(), other.getId(), new CreateCommentRequest("Conversa independente"));
        assertThatThrownBy(() -> deleteComment.execute(publication.getId(), root.id(), other.getId()))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN));
        assertThat(comments.findById(root.id()).orElseThrow().getDeletedAt()).isNull();
        deleteComment.execute(publication.getId(), root.id(), author.getId());
        entityManager.flush();
        entityManager.clear();
        for (var id : List.of(root.id(), reply.id(), nested.id())) {
            assertThat(comments.findById(id).orElseThrow().getDeletedAt()).isNotNull();
        }
        assertThat(comments.findById(remaining.id()).orElseThrow().getDeletedAt()).isNull();
        assertThat(findComments.execute(publication.getId(), author.getId(), 0).items()).extracting(CommentResponse::id).containsExactly(remaining.id());
        assertThat(findProfilePosts.execute(author.getId(), 0, 10).items().get(0).commentsCount()).isEqualTo(1);
        assertThatThrownBy(() -> setCommentLike.execute(publication.getId(), reply.id(), other.getId(), new SetCommentLikeRequest(true)))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        assertThatThrownBy(() -> findCommentLikes.execute(publication.getId(), root.id(), 0))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void publicProfilesExposeProfessionalDetailsAndPostsButKeepPrivateDataAndWritesProtected() throws Exception {
        var author = user("Perfil público");
        var other = user("Outro perfil público");
        var oldPost = post(author, LocalDateTime.now().plusDays(1));
        var newPost = post(author, LocalDateTime.now().plusDays(2));
        post(other, LocalDateTime.now().plusDays(3));
        updateProfile.execute(author.getId(), new UpdateProfileRequest(
                author.getName(), author.getEmail(), author.getUsername(), null, false, null,
                "  Desenvolvedor Java  ", "  Minha trajetória profissional  ", "São Paulo, SP",
                "https://example.com/portfolio", "https://linkedin.com/in/perfil"
        ));
        entityManager.flush();
        entityManager.clear();
        var profile = findPublicProfile.execute(author.getUsername().toUpperCase());
        assertThat(profile.headline()).isEqualTo("Desenvolvedor Java");
        assertThat(profile.bio()).isEqualTo("Minha trajetória profissional");
        assertThat(profile.postsCount()).isEqualTo(2);
        assertThat(findPublicPosts.execute(author.getUsername(), null, 0).items())
                .extracting(FeedResponse::id).containsExactly(newPost.getId(), oldPost.getId());
        assertThat(findPublicPosts.execute(author.getUsername(), null, 0).items())
                .allSatisfy(item -> assertThat(item.liked()).isFalse());

        var mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).addFilters(springSecurityFilterChain).build();
        mvc.perform(get("/v1/profiles/{username}", author.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.headline").value("Desenvolvedor Java"))
                .andExpect(jsonPath("$.data.email").doesNotExist())
                .andExpect(jsonPath("$.data.password").doesNotExist())
                .andExpect(jsonPath("$.data.roleId").doesNotExist());
        mvc.perform(get("/v1/profiles/{username}/posts", author.getUsername()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.items.length()").value(2));
        mvc.perform(get("/v1/community/posts/{postId}/likes", newPost.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.items").isEmpty());
        mvc.perform(get("/v1/community/posts/{postId}/comments", newPost.getId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.items").isEmpty());
        mvc.perform(delete("/v1/community/posts/{postId}", newPost.getId())).andExpect(status().isUnauthorized());
        mvc.perform(get("/v1/users/me")).andExpect(status().isUnauthorized());
        deletePost.execute(newPost.getId(), author.getId());
        assertThat(findPublicProfile.execute(author.getUsername()).postsCount()).isEqualTo(1);
        author = users.findById(author.getId()).orElseThrow();
        author.setDeletedAt(LocalDateTime.now());
        users.saveAndFlush(author);
        var username = author.getUsername();
        assertThatThrownBy(() -> findPublicProfile.execute(username))
                .isInstanceOfSatisfying(ResponseStatusException.class, error -> assertThat(error.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void exposesReadinessWithoutCredentialsOrDependencyDetails() throws Exception {
        var mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilters(springSecurityFilterChain).build();
        mvc.perform(get("/actuator/health/readiness"))
                .andExpect(result -> assertThat(result.getResponse().getStatus()).isIn(200, 503))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.components").doesNotExist())
                .andExpect(jsonPath("$.details").doesNotExist());
    }

    private UserEntity user(String name) {
        var user = new UserEntity(); var suffix = UUID.randomUUID().toString().substring(0, 8);
        user.setName(name); user.setUsername("test" + suffix); user.setEmail("test-" + suffix + "@example.invalid");
        user.setPassword("test-encoded-password"); user.setRole(roles.findByCodeAndDeletedAtIsNull(RoleCodeUtils.CANDIDATE).orElseThrow());
        return users.saveAndFlush(user);
    }

    private PostEntity post(UserEntity author, LocalDateTime date) {
        var post = new PostEntity(); post.setUser(author); post.setContent("Publicação para teste com rollback");
        post = posts.saveAndFlush(post);
        entityManager.createQuery("update PostEntity p set p.createdAt = :date where p.id = :id").setParameter("date", date).setParameter("id", post.getId()).executeUpdate();
        return post;
    }
}
