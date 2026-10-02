package com.antony.openjobs.modules.posts.usecase.feed;

import com.antony.openjobs.modules.comments.model.CommentEntity;
import com.antony.openjobs.modules.comments.repository.ICommentRepository;
import com.antony.openjobs.modules.comments.usecase.findall.FindAllPostCommentsUseCase;
import com.antony.openjobs.modules.comments.usecase.create.CreateCommentUseCase;
import com.antony.openjobs.modules.comments.usecase.create.CreateCommentRequest;
import com.antony.openjobs.modules.likes.usecase.set.SetPostLikeUseCase;
import com.antony.openjobs.modules.likes.usecase.set.SetPostLikeRequest;
import com.antony.openjobs.modules.likes.model.LikeEntity;
import com.antony.openjobs.modules.likes.repository.ILikeRepository;
import com.antony.openjobs.modules.posts.model.PostEntity;
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
        assertThat(findComments.execute(newer.getId(), 0).items().get(0).author().name()).isEqualTo("Maria");

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
