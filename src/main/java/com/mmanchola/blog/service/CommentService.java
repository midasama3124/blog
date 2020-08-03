package com.mmanchola.blog.service;

import com.mmanchola.blog.dao.CommentDataAccessService;
import com.mmanchola.blog.exception.ApiRequestException;
import com.mmanchola.blog.model.Comment;
import org.springframework.stereotype.Service;

import static com.mmanchola.blog.exception.ExceptionMessage.NOT_FOUND;
import static com.mmanchola.blog.model.TableFields.COMMENT_ID;

@Service
public class CommentService {
    private final CommentDataAccessService commentDas;

    public CommentService(CommentDataAccessService commentDas) {
        this.commentDas = commentDas;
    }

    // Get comment given its ID
    public Comment get(long commentId) {
        return commentDas.find(commentId)
                .orElseThrow(() -> new ApiRequestException(NOT_FOUND.getMsg(COMMENT_ID.toString())));
    }

    // Delete comment given its ID
    public void delete(long commentId) {
        if (commentDas.exists(commentId))
            commentDas.delete(commentId);
    }
}
