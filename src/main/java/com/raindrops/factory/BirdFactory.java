package com.raindrops.factory;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.raindrops.component.BirdComponent;
import com.raindrops.component.WallComponent;
import com.raindrops.enums.EntityTypeEnum;
import javafx.util.Duration;

/**
 * BirdFactory
 *
 * @author raindrops
 */
public class BirdFactory {

    public static Entity createBird(BirdComponent birdComponent) {
        return FXGL.entityBuilder().at(100, 100)
                .type(EntityTypeEnum.BIRD)
                .bbox(new HitBox(BoundingShape.box(70, 60)))
                .view(FXGL.texture("bird.png").toAnimatedTexture(2, Duration.seconds(0.5)).loop())
                .collidable()
                .with(birdComponent, new WallComponent())
                .build();
    }
}