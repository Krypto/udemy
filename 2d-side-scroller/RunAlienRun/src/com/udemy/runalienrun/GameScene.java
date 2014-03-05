package com.udemy.runalienrun;

import java.util.LinkedList;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.debug.Debug;

import android.hardware.SensorManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.udemy.runalienrun.SceneManager.SceneType;

public class GameScene extends BaseScene implements IOnSceneTouchListener, IOnMenuItemClickListener {
	
	private final int RESTART = 0;
	private final int QUIT = 1;
	
	private TMXTiledMap mTMXTiledMap;
	private TMXLayer tmxLayer;
	
	private PhysicsWorld mPhysicsWorld;
	
	private LinkedList<Sprite> coins;
	
	private Text scoreText;
	private Player player;
	private int score = 0;
	
	private MenuScene levelFailed;
	private MenuScene levelCleared;

	@Override
	public void createScene() {
		createPhysicsWorld();
		createLevel();
		createLevelFailedScene();
		createLevelClearedScene();
		setOnSceneTouchListener(this);
	}

	@Override
	public void onBackKeyPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub

	}
	
	private void createLevel() {
		
		scoreText = new Text(10, 10, resourceManager.font, "Coins: 0", vbom);
		
		coins = new LinkedList<Sprite>();
		
		try {
			final TMXLoader tmxLoader = new TMXLoader(
					activity.getAssets(),
					activity.getTextureManager(),
					TextureOptions.BILINEAR_PREMULTIPLYALPHA,
					vbom);
			
			mTMXTiledMap = tmxLoader.loadFromAsset("tmx/level1.tmx");
		}
		catch (final TMXLoadException x) {
			Debug.e(x);
		}
		
		for (final TMXObjectGroup group: mTMXTiledMap.getTMXObjectGroups()) {
			for (final TMXObject object: group.getTMXObjects()) {
				if (object.getName().equals("ground")) {
					Rectangle rect = new Rectangle(
							object.getX(),
							object.getY(),
							object.getWidth(),
							object.getHeight(),
							vbom);
					
					FixtureDef groundFixtureDef = PhysicsFactory.createFixtureDef(0, 0, .2f);
					
					PhysicsFactory.createBoxBody(
							mPhysicsWorld, 
							rect,
							BodyType.StaticBody, 
							groundFixtureDef)
							.setUserData("ground");
					
					rect.setVisible(false);
					
					attachChild(rect);
				}
				else if (object.getName().equals("coin")) {
					Sprite coin = new Sprite(
							object.getX(), 
							object.getY(), 
							object.getWidth(), 
							object.getHeight(),
							resourceManager.coinTexture,
							vbom);
					
					FixtureDef coinFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
					
					Body body = PhysicsFactory.createBoxBody(
							mPhysicsWorld, 
							coin, 
							BodyType.StaticBody, 
							coinFixtureDef);
					
					body.setUserData("coin");
					
					coins.add(coin);
					
					coin.setUserData(body);
				}
				else if (object.getName().equals("spike")) {
					
					Rectangle rect = new Rectangle(
							object.getX(),
							object.getY(),
							object.getWidth(),
							object.getHeight(),
							vbom);
					
					FixtureDef spikeFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
					
					PhysicsFactory.createBoxBody(
							mPhysicsWorld, 
							rect, 
							BodyType.StaticBody, 
							spikeFixtureDef)
							.setUserData("spike");
					
				}
				else if (object.getName().equals("finish_line")) {
					Rectangle rect = new Rectangle(
							object.getX(),
							object.getY(),
							object.getWidth(),
							object.getHeight(),
							vbom);
					
					FixtureDef finishFixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0);
					
					PhysicsFactory.createBoxBody(
							mPhysicsWorld, 
							rect, 
							BodyType.StaticBody, 
							finishFixtureDef)
							.setUserData("finish_line");
				}
			}
		}
		
		tmxLayer = mTMXTiledMap.getTMXLayers().get(0);
		attachChild(tmxLayer);
		
		tmxLayer = mTMXTiledMap.getTMXLayers().get(1);
		attachChild(tmxLayer);
		
		for (int i = 0; i < coins.size(); i++) {
			attachChild(coins.get(i));
		}
		
		player = new Player(100, 300, 80, 100, vbom, camera, mPhysicsWorld){
			@Override
			public void onDie() {
				showLevelFailed();
			}
		};
		player.setRunning();
		
		attachChild(player);
		
		HUD hud = new HUD();
		hud.attachChild(scoreText);
		camera.setHUD(hud);
	}
	
	private ContactListener createContactListener()
	{
		ContactListener contactListener = new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
			
			@Override
			public void endContact(Contact contact) {
			}
			
			@Override
			public void beginContact(Contact contact) {
				Body a = contact.getFixtureA().getBody();
				Body b = contact.getFixtureB().getBody();
				
				if (a.getUserData() != null && b.getUserData() != null) {
					if (a.getUserData().equals("player") && b.getUserData().equals("ground")
							|| b.getUserData().equals("player") && a.getUserData().equals("ground")) {
						player.canJump = true;
						player.setRunning();
					}
					if (a.getUserData().equals("player") && b.getUserData().equals("coin")
							|| b.getUserData().equals("player") && a.getUserData().equals("coin")) {
						
						resourceManager.coinCollect.play();
						
						score++;
						scoreText.setText("Coins: " + score);
						
						removeBody(a.getUserData().equals("coin") ? a : b);
					}
					if (a.getUserData().equals("player") && b.getUserData().equals("spike")
							|| b.getUserData().equals("player") && a.getUserData().equals("spike")) {
						showLevelFailed();
					}
					if (a.getUserData().equals("player") && b.getUserData().equals("finish_line")
							|| b.getUserData().equals("player") && a.getUserData().equals("finish_line")) {
						showLevelCleared();
					}
				}
			}
		};
		return contactListener;
	}
	
	private void showLevelFailed() {
		setChildScene(levelFailed, false, true, true);
	}
	
	private void showLevelCleared() {
		setChildScene(levelCleared, false, true, true);
	}
	
	private void removeBody(final Body body) {
		for (int i = 0; i < coins.size(); i++) {
			if (coins.get(i).getUserData() == body) {
				detachChild(coins.get(i));
			}
		}
		
		activity.runOnUpdateThread(new Runnable(){

			@Override
			public void run() {
				mPhysicsWorld.destroyBody(body);
			}
			
		});
	}
	
	private void createPhysicsWorld() {
		mPhysicsWorld = new PhysicsWorld(
				new Vector2(0, SensorManager.GRAVITY_EARTH), 
				false);
		
		registerUpdateHandler(mPhysicsWorld);
		
		mPhysicsWorld.setContactListener(createContactListener());
	}
	
	private void createLevelFailedScene() {
		levelFailed = new MenuScene(camera);
		levelFailed.setPosition(0, 0);
		
		final IMenuItem restartButton = new ScaleMenuItemDecorator(
				new SpriteMenuItem(RESTART, resourceManager.restartButton, vbom), 
				1.2f, 
				1);
		final IMenuItem quitButton = new ScaleMenuItemDecorator(
				new SpriteMenuItem(QUIT, resourceManager.quitButton, vbom), 
				1.2f, 
				1);
		
		Sprite bg = new Sprite(0, 0, resourceManager.failedBG, vbom);
		
		levelFailed.attachChild(bg);
		levelFailed.setBackgroundEnabled(false);
		levelFailed.addMenuItem(restartButton);
		levelFailed.addMenuItem(quitButton);
		levelFailed.buildAnimations();
		levelFailed.setOnMenuItemClickListener(this);
	}
	
	private void createLevelClearedScene() {
		levelCleared = new MenuScene(camera);
		levelCleared.setPosition(0, 0);
		
		final IMenuItem restartButton = new ScaleMenuItemDecorator(
				new SpriteMenuItem(RESTART, resourceManager.restartButton, vbom), 
				1.2f, 
				1);
		final IMenuItem quitButton = new ScaleMenuItemDecorator(
				new SpriteMenuItem(QUIT, resourceManager.quitButton, vbom), 
				1.2f, 
				1);
		
		Sprite bg = new Sprite(0, 0, resourceManager.clearedBG, vbom);
		
		levelCleared.attachChild(bg);
		levelCleared.setBackgroundEnabled(false);
		levelCleared.addMenuItem(restartButton);
		levelCleared.addMenuItem(quitButton);
		levelCleared.buildAnimations();
		levelCleared.setOnMenuItemClickListener(this);
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (pSceneTouchEvent.isActionDown()) {
			player.jump();
		}
		return false;
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch (pMenuItem.getID()) {
			case RESTART:
				// restart level
				restartLevel();
				break;
			case QUIT:
				// go to main menu
				quit();
				break;
		}
		return false;
	}
	
	private void quit() {
		mPhysicsWorld.clearForces();
		mPhysicsWorld.clearPhysicsConnectors();
		mPhysicsWorld.dispose();
		
		clearTouchAreas();
		clearEntityModifiers();
		clearUpdateHandlers();
		dispose();
		
		ResourceManager.getInstance().camera.setCenter(0, 0);
		
		SceneManager.getInstance().setMainMenu();
	}
	
	private void restartLevel() {
		mPhysicsWorld.clearForces();
		mPhysicsWorld.clearPhysicsConnectors();
		mPhysicsWorld.dispose();
		
		clearTouchAreas();
		clearEntityModifiers();
		clearUpdateHandlers();
		dispose();
		
		SceneManager.getInstance().createGameScene();
	}

}
