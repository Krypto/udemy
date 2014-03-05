package com.udemy.runalienrun;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import com.udemy.runalienrun.SceneManager.SceneType;

public class MainMenu extends BaseScene implements IOnMenuItemClickListener {
	
	private MenuScene menuChildScene;
	
	private final int PLAY = 0;

	@Override
	public void createScene() {
		createMenuScene();
		
		attachChild(new Sprite(0, 0, resourceManager.mainMenuBackground, vbom) {

			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) {
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}

		});
		
		resourceManager.themeMusic.play();
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub

	}
	
	private void createMenuScene() {
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);
		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(
				new SpriteMenuItem(PLAY, resourceManager.playButton, vbom), 
				1.2f, 
				1);
		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);
		menuChildScene.setOnMenuItemClickListener(this);
		
		setChildScene(menuChildScene, false, true, true);
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case PLAY:
				SceneManager.getInstance().createGameScene();
				return true;
			default:
				return false;
		}
	}

}
