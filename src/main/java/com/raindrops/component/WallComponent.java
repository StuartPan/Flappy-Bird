package com.raindrops.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.raindrops.enums.EntityTypeEnum;
import javafx.scene.shape.Rectangle;

import static com.raindrops.constant.CommonConstant.*;

/**
 * WallComponent
 *
 * @author raindrops
 */
public class WallComponent extends Component {

    private double lastWallX = 500d;

    private int wallNo = 1;

    @Override
    public void onUpdate(double tpf) {
        if (lastWallX - entity.getX() < FXGL.getAppWidth()) {
            this.buildWalls();
        }
    }

    private void buildWalls() {
        for (int i = 1; i <= 10; i++) {
            double topHeight = Math.random() * (FXGL.getAppHeight() - WALL_DISTANCE_Y);
            // 生成上半截墙
            Entity topWall = FXGL.entityBuilder()
                    .at(lastWallX + i * WALL_DISTANCE_X, -25)
                    .type(EntityTypeEnum.WALL)
                    .viewWithBBox(this.buildWallView(topHeight))
                    .with(new CollidableComponent(true))
                    .build();
            topWall.getProperties().setValue("wallNo", wallNo);
            FXGL.getGameWorld().addEntity(topWall);

            // 生成下半截墙
            Entity bottomWall = FXGL.entityBuilder()
                    .at(lastWallX + i * WALL_DISTANCE_X, topHeight + WALL_DISTANCE_Y)
                    .type(EntityTypeEnum.WALL)
                    .viewWithBBox(this.buildWallView(FXGL.getAppHeight() - topHeight - WALL_DISTANCE_Y))
                    .with(new CollidableComponent(true))
                    .build();
            bottomWall.getProperties().setValue("wallNo", wallNo++);
            FXGL.getGameWorld().addEntity(bottomWall);
        }
        lastWallX += WALL_DISTANCE_X * 10;
    }

    private Rectangle buildWallView(double height) {
        Rectangle wall = new Rectangle(WALL_WIDTH, height);
        wall.setArcWidth(10);
        wall.setArcHeight(10);
        wall.fillProperty().bind(FXGL.getWorldProperties().objectProperty("wallColor"));
        return wall;
    }
}