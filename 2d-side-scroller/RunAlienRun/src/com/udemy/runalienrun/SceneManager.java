package com.udemy.runalienrun;

import org.andengine.engine.Engine;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

public class SceneManager {
	
	private static final SceneManager instance = new SceneManager();
	
	private BaseScene mainMenu;
	private BaseScene gameScene;
	
	private BaseScene currentScene;
	private SceneType currentSceneType = SceneType.SCENE_MENU;
	private Engine engine = ResourceManager.getInstance().engine;
	
	private SceneManager() {
	}
	
	public void setScene(BaseScene scene) {
		engine.setScene(scene);
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}
	
	public static SceneManager getInstance(){
		return instance;
	}
	
	public SceneType getSceneType() {
		return currentSceneType;
	}
	
	public void createGameScene() {
		ResourceManager.getInstance().loadGameResources();
		
		gameScene = new GameScene();
		currentScene = gameScene;
		
		ResourceManager.getInstance().engine.setScene(gameScene);
	}
	
	public void createMainMenu(OnCreateSceneCallback pOnCreateSceneCallback) {
		ResourceManager.getInstance().loadMenuResources();
		
		mainMenu = new MainMenu();
		currentScene = mainMenu;
		pOnCreateSceneCallback.onCreateSceneFinished(mainMenu);
		
	}
	
	public void setMainMenu() {
		ResourceManager.getInstance().engine.setScene(mainMenu);
	}
	
	public enum SceneType {
		SCENE_MENU,
		SCENE_GAME
	}
}
