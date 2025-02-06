package com.raindrops.collision;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.CollisionHandler;
import com.raindrops.FlappyBirdApplication;

/**
 * BirdCollisionHandler
 *
 * @author raindrops
 */
public class BirdCollisionHandler extends CollisionHandler {

    /**
     * The order of types determines the order of entities in callbacks.
     *
     * @param a entity type of the first entity
     * @param b entity type of the second entity
     */
    public BirdCollisionHandler(Object a, Object b) {
        super(a, b);
    }

    @Override
    protected void onCollisionBegin(Entity a, Entity b) {
        FXGL.<FlappyBirdApplication>getAppCast().gameOver();
    }
}