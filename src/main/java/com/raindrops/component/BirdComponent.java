package com.raindrops.component;

import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.raindrops.FlappyBirdApplication;
import com.raindrops.enums.EntityTypeEnum;

import java.util.Optional;

/**
 * BirdComponent
 *
 * @author raindrops
 */
public class BirdComponent extends Component {

    private final Vec2 acceleration = new Vec2(6, 0);

    @Override
    public void onUpdate(double tpf) {
        acceleration.x += (float) (tpf * 0.1);
        acceleration.y += (float) (tpf * 10);

        if (acceleration.y < -5)
            acceleration.y = -5;

        if (acceleration.y > 5)
            acceleration.y = 5;

        entity.translate(acceleration.x, acceleration.y);

        if (entity.getBottomY() > FXGL.getAppHeight()) {
            FXGL.<FlappyBirdApplication>getAppCast().gameOver();
        }
        Optional<Entity> closestWall = FXGL.getGameWorld().getClosestEntity(entity, item -> item.getType().equals(EntityTypeEnum.WALL));
        closestWall.ifPresent(item -> {
            // 鸟完全通过了墙
            if (entity.getBoundingBoxComponent().getMinXWorld() > item.getRightX()) {
                FXGL.getWorldProperties().setValue("score", item.getProperties().getInt("wallNo"));
            }
        });
    }

    public void jump() {
        acceleration.addLocal(0, -25);
        FXGL.play("jump.wav");
    }
}