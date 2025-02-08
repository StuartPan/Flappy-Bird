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
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class FlappyBirdApplication extends GameApplication {

    private final BirdComponent birdComponent = new BirdComponent();

    private List<RankData> rankList = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Flappy Bird");
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setVersion("1.1");
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
        rankList = this.getRankList();
        if (rankList.size() < 10) {
            this.recordScore();
            return;
        } else {
            int minScore = rankList.stream().mapToInt(RankData::getScore).min().getAsInt();
            if (minScore < FXGL.getip("score").getValue()) {
                this.recordScore();
                return;
            }
        }
        this.showRankUI();
    }

    private void recordScore() {
        FXGL.getDialogService().showInputBox("请输入您的名字", name -> {
            if (name == null || name.isEmpty()) {
                name = "未命名";
            }
            rankList.add(new RankData(FXGL.getip("score").getValue(), name, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())));
            rankList.sort((o1, o2) -> o2.getScore() - o1.getScore());
            try {
                Files.writeString(Paths.get("./rank/RankList.txt"), rankList.stream().map(RankData::toString).collect(Collectors.joining("\r\n")), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            this.showRankUI();
        });
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

    private void showRankUI() {
        VBox vbox = new VBox(5);
        List<RankData> rankList = this.getRankList();
        for (RankData rankData : rankList) {
            Text rankText = new Text(rankData.getScore() + " - " + rankData.getName() + " - " + rankData.getDate());
            rankText.setFont(Font.font(20));
            vbox.getChildren().add(rankText);
        }
        ScrollPane scrollPane = new ScrollPane(vbox);
        scrollPane.setFitToWidth(true);

        Button restartButton = new Button("重新开始");
        restartButton.setOnAction(event -> FXGL.getGameController().startNewGame());
        Button exitButton = new Button("退出游戏");
        exitButton.setOnAction(event -> FXGL.getGameController().exit());
        FXGL.getDialogService().showBox("排行榜", scrollPane, restartButton, exitButton);
    }
}