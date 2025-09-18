package it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.service;

import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.dao.UserReactionsDao;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.EmbeddedReaction;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.ReactionRequest;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.UserReaction;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.model.UserReactionsResponse;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.apps.reaction.repository.UserReactionsRepository;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.authentication.user.UserToken;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.model.EntityTypeEnum;
import it.unipi.adrien.koumgang.smartnewsagreggatortomcat.lib.service.BaseService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class ReactionService extends BaseService {

    @Contract(" -> new")
    public static @NotNull ReactionService getInstance() {
        return new ReactionService(UserReactionsRepository.getInstance());
    }


    private final UserReactionsDao userReactionsDao;

    public ReactionService(UserReactionsDao userReactionsDao) {
        this.userReactionsDao   = userReactionsDao;
    }



    private static final int MAX_EMBEDDED_REACTIONS = 100;



    /**
     * Toggle reaction with user reaction tracking
     */
    public UserReaction toggleReaction(UserToken userToken, EntityTypeEnum entityType, String entityId, ReactionRequest reactionRequest) {


        // Check if user already has a reaction
        Optional<EmbeddedReaction> existingReaction = userReactionsDao.findByArticleId(entityType, entityId, reactionRequest.getAuthorId());

        if(existingReaction.isPresent()) {
            EmbeddedReaction embeddedReaction = existingReaction.get();

            if(Objects.equals(embeddedReaction.getReactionType(), reactionRequest.getReactionType())) {
                // Same reaction - remove it
                return removeReaction(userToken, entityType, entityId, reactionRequest);
            } else {
                // Different reaction - update it
                return updateReaction(userToken, entityType, entityId, reactionRequest);
            }
        } else {
            // New reaction
            return addReaction(userToken, entityType, entityId, reactionRequest);
        }
    }

    /**
     * Add new reaction
     */
    private UserReaction addReaction(UserToken userToken, EntityTypeEnum entityType, String entityId, ReactionRequest reactionRequest) {
        return null;
    }

    /**
     * Update existing reaction
     */
    private UserReaction updateReaction(UserToken userToken, EntityTypeEnum entityType, String entityId, ReactionRequest reactionRequest) {
        return null;
    }

    /**
     * Remove reaction
     */
    private UserReaction removeReaction(UserToken userToken, EntityTypeEnum entityType, String entityId, ReactionRequest reactionRequest) {
        return null;
    }


    /**
     * Add reaction to embedded array
     */
    private void addReactionToEmbedded(UserToken userToken, EntityTypeEnum entityType, String entityId, UserReaction userReaction) {

    }

    /**
     * Add reaction to overflow collection
     */
    private void addReactionToOverflow(UserToken userToken, EntityTypeEnum entityType, String entityId, UserReaction userReaction) {

    }

    /**
     * Update user reaction in embedded or overflow
     */
    private boolean updateUserReaction(UserToken userToken, EntityTypeEnum entityType, String entityId, ReactionRequest reactionRequest) {
        return false;
    }

    /**
     * Remove user reaction from embedded or overflow
     */
    private boolean removeUserReaction(UserToken userToken, EntityTypeEnum entityType, String entityId, ReactionRequest reactionRequest) {
        return false;
    }

    /**
     * Check if reactions should be migrated between embedded and overflow
     */
    private void checkAndMigrateReactions(UserToken userToken, EntityTypeEnum entityType, String entityId, ReactionRequest reactionRequest) {

    }

    /**
     * Migrate reactions from overflow to embedded
     */
    private void migrateOverflowToEmbedded(UserToken userToken, EntityTypeEnum entityType, String entityId, ReactionRequest reactionRequest) {

    }

    public UserReactionsResponse getUserReactions(UserToken userToken, EntityTypeEnum entityType, String entityId, Integer page, Integer pageSize) {

        Integer embeddedCount = 0; // getEmbeddedReactionCount

        return null;
    }

}
