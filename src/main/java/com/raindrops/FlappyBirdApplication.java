package com.raindrops;

import com.almasb.fxgl.animation.Interpolators;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.raindrops.collision.BirdCollisionHandler;
import com.raindrops.component.BirdComponent;
import com.raindrops.data.RankData;
import com.raindrops.enums.EntityTypeEnum;
import com.raindrops.factory.BackgroundFactory;
import com.raindrops.factory.BirdFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FlappyBirdApplication extends GameApplication {

    private final BirdComponent birdComponent = new BirdComponent();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Flappy Bird");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setVersion("1.0");
    }

    @Override
    protected void initGame() {
        FXGL.getGameWorld().addEntity(BackgroundFactory.createEntity());
        FXGL.loopBGM("bgm.mp3");

        Entity bird = BirdFactory.createBird(birdComponent);
        FXGL.getGameScene().getViewport().setBounds(0, 0, Integer.MAX_VALUE, FXGL.getAppHeight());
        FXGL.getGameScene().getViewport().bindToEntity(bird, FXGL.getAppWidth() / 3d, FXGL.getAppHeight() / 2d);
        // 动画出现效果
        FXGL.spawnWithScale(bird, Duration.seconds(0.86), Interpolators.BOUNCE.EASE_OUT());
    }


    @Override
    protected void initUI() {
        Text text = new Text();
        text.setFont(Font.font(50));
        text.setTranslateX(FXGL.getAppWidth() - 200);
        text.setTranslateY(100);
        text.textProperty().bind(FXGL.getip("score").asString());
        FXGL.addUINode(text);
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("score", 0);
        vars.put("wallColor", Color.GREY);
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                this.birdComponent.jump();
            }
        });
    }

    @Override
    protected void initPhysics() {
        FXGL.getPhysicsWorld().addCollisionHandler(new BirdCollisionHandler(EntityTypeEnum.BIRD, EntityTypeEnum.WALL));
    }

    public void gameOver() {
        this.recordScore("raindrops");
        FXGL.getDialogService().showConfirmationBox("游戏结束，是否重新开始", restart -> {
            if (restart) {
                FXGL.getGameController().startNewGame();
            } else {
                FXGL.getGameController().exit();
            }
        });
    }

    private void recordScore(String name) {
        if (name == null || name.isEmpty()) {
            name = "未命名";
        }
        StringBuilder rank = new StringBuilder();
        rank.append(FXGL.getip("score").getValue())
                .append(",").append(name).append(",")
                .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\r\n");
        Path path = Paths.get("./rank/RankList.txt");
        List<RankData> rankList = this.getRankList();
        if (rankList.size() < 10) {
            try {
                Files.writeString(path, rank.toString(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            return;
        }
        int minScore = rankList.stream().mapToInt(RankData::getScore).min().getAsInt();
        if (minScore >= FXGL.getip("score").getValue()) {
            return;
        }
        rankList.add(new RankData(FXGL.getip("score").getValue(), name, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
        try {
            Files.writeString(path, rankList.stream().map(RankData::toString).collect(Collectors.joining("\r\n")), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private List<RankData> getRankList() {
        try {
            List<String> rankList = Files.readAllLines(Paths.get("./rank/RankList.txt"), StandardCharsets.UTF_8);
            return rankList.stream().map(item -> {
                String[] arr = item.split(",");
                return new RankData(Integer.parseInt(arr[0]), arr[1], arr[2]);
            }).collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return Collections.emptyList();
    }
}