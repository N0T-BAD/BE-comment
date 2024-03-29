package com.blockpage.commentservice.adaptor.infrastructure.persistence;

import com.blockpage.commentservice.adaptor.infrastructure.entity.CommentEntity;
import com.blockpage.commentservice.adaptor.infrastructure.repository.CommentRepository;
import com.blockpage.commentservice.application.port.out.CommentPort;
import com.blockpage.commentservice.domain.CommentDomain;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentAdaptor implements CommentPort {

    private final CommentRepository commentRepository;

    private final EntityManager entityManager;

    @Override
    @Transactional
    public void saveComment(CommentDomain commentDomain) {

        CommentEntity commentEntity = CommentEntity.toEntityFromDomain(commentDomain);
        commentRepository.save(commentEntity);
        if (commentEntity.getParentsCommentId() != null) {
            Optional<CommentEntity> changeEntity = commentRepository.findById(commentEntity.getParentsCommentId());
            Integer replyCount = changeEntity.get().getReplyCount() + 1;
            changeEntity.get().updateReplyCount(replyCount);
        }

    }

    @Override
    @Transactional
    public void pinComment(CommentDomain commentDomain) {

        Optional<CommentEntity> commentEntity = commentRepository.findById(commentDomain.getCommentId());
        commentEntity.get().update(true);

    }

    @Override
    @Transactional
    public void deleteComment(CommentDomain commentDomain) {

        Optional<CommentEntity> commentEntity = commentRepository.findById(commentDomain.getCommentId());
        // 댓글의 대댓글이 없거나 대댓글일때
        if (commentEntity.get().getReplyCount() == 0) {

            Long parents = commentEntity.get().getParentsCommentId();
            if (parents != null) {
                log.info("parents : " + parents);
                Optional<CommentEntity> changeEntity = commentRepository.findById(parents);
                Integer replyCount = changeEntity.get().getReplyCount() - 1;
                log.info("replyCount : " + replyCount);
                changeEntity.get().updateReplyCount(replyCount);
            }

            commentRepository.deleteById(commentEntity.get().getId());
        } else {
            commentEntity.get().delete(true, "삭제된 댓글 입니다.");

        }
    }

    @Override
    @Transactional
    public void reportComment(CommentDomain commentDomain) {
        Optional<CommentEntity> commentEntity = commentRepository.findById(commentDomain.getCommentId());
        commentEntity.get().report(true, "신고되어 삭제된 댓글 입니다.");
    }

    @Override
    @Transactional
    public void updateComment(CommentDomain commentDomain) {

        Optional<CommentEntity> commentEntity = commentRepository.findById(commentDomain.getCommentId());
        commentEntity.get().updateReaction(commentDomain.getLikesCount(), commentDomain.getDisLikesCount());
//        commentRepository.save(commentEntity.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDomain> getComment(CommentDomain commentDomain) {
        List<CommentEntity> commentEntityList = commentRepository.findByEpisodeIdOrderByPinDesc(commentDomain.getEpisodeId());
        List<CommentEntity> commentEntityList2 = commentEntityList.stream()
            .filter(c -> c.getChildId() == null && c.getParentsId() != null)
            .toList();
        return commentEntityList2.stream().map(CommentDomain::toDomainFromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDomain> getReply(CommentDomain commentDomain) {
        List<CommentEntity> commentEntityList = commentRepository.findAllByParentsCommentId(commentDomain.getCommentId());
        List<CommentEntity> commentEntity = commentEntityList.stream()
            .filter(c -> c.getChildId() != null)
            .toList();

        return commentEntity.stream().map(CommentDomain::toDomainFromEntity).toList();
    }


    @Override
    @Transactional(readOnly = true)
    public Integer getCommentCount(Long episodeId) {
        return commentRepository.findAllByEpisodeIdAndEraseIsFalse(episodeId).size();
    }
}
