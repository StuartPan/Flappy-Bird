package com.raindrops.factory;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.views.ScrollingBackgroundView;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.raindrops.enums.EntityTypeEnum;


/**
 * BackgroundFactory
 *
 * @author raindrops
 */
public class BackgroundFactory {

    public static Entity createEntity() {
        Entity entity = FXGL.entityBuilder()
                .view(new ScrollingBackgroundView(FXGL.texture("background.png").getImage(), FXGL.getAppWidth(), FXGL.getAppHeight()))
                .zIndex(-1)
                .with(new IrremovableComponent())
                .type(EntityTypeEnum.BACKGROUND)
                .build();
        entity.xProperty().bind(FXGL.getGameScene().getViewport().xProperty());
        entity.yProperty().bind(FXGL.getGameScene().getViewport().yProperty());
        return entity;
    }
}