package com.udemy.runalienrun;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;

public class ResourceManager {
	
	public Engine engine;
	public MainGameActivity activity;
	public BoundCamera camera;
	public VertexBufferObjectManager vbom;
	
	public Font font;
	
	// SOUNDS
	
	public Music themeMusic;
	public Sound coinCollect;
	public Sound jumpSound;
	
	// TEXTURES
	private BuildableBitmapTextureAtlas mainMenuTexturesAtlas;
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	private BuildableBitmapTextureAtlas levelFailedAtlas;
	private BuildableBitmapTextureAtlas levelPassedAtlas;
	
	public ITextureRegion playButton;
	public ITextureRegion mainMenuBackground;
	public ITextureRegion coinTexture;
	public ITextureRegion restartButton;
	public ITextureRegion quitButton;
	public ITextureRegion failedBG;
	public ITextureRegion clearedBG;
	public ITiledTextureRegion playerTexture;
	
	private static final ResourceManager instance = new ResourceManager();
	
	private ResourceManager() {
	}
	
	public void loadGameResources() {
		loadGameGraphics();
		loadGameSounds();
	}
	
	public void loadMenuResources() {
		loadMenuGraphics();
		loadMenuSounds();
	}
	
	private void loadGameGraphics() {
		
		FontFactory.setAssetBasePath("font/");
		
		final ITexture fontTexture = new BitmapTextureAtlas(
				activity.getTextureManager(),
				256,
				256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		font = FontFactory.createFromAsset(
				activity.getFontManager(),
				fontTexture,
				activity.getAssets(),
				"BebasNeue.otf",
				30,
				true,
				Color.BLACK);

		font.load();
		
		gameTextureAtlas = new BuildableBitmapTextureAtlas(
				activity.getTextureManager(),
				1024,
				1024,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		levelFailedAtlas = new BuildableBitmapTextureAtlas(
				activity.getTextureManager(),
				1024,
				1024,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		levelPassedAtlas = new BuildableBitmapTextureAtlas(
				activity.getTextureManager(),
				1024,
				1024,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		coinTexture = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				gameTextureAtlas, 
				activity.getAssets(),
				"coin.png");
		
		clearedBG = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				levelPassedAtlas, 
				activity.getAssets(),
				"level_cleared_background.png");
		
		failedBG = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				levelFailedAtlas, 
				activity.getAssets(),
				"level_failed.png");
		
		restartButton = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				levelFailedAtlas, 
				activity.getAssets(),
				"restart.png");
		
		quitButton = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				levelFailedAtlas, 
				activity.getAssets(),
				"quit.png");
		
		playerTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
				gameTextureAtlas, 
				activity.getAssets(),
				"character.png",
				7,
				1);
		
		try {
			gameTextureAtlas.build(
					new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
			gameTextureAtlas.load();
			
			levelFailedAtlas.build(
					new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
			levelFailedAtlas.load();
			
			levelPassedAtlas.build(
					new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
			levelPassedAtlas.load();
		}
		catch (TextureAtlasBuilderException x) {
			Debug.e(x);
		}
	}
	
	private void loadGameSounds() {
		try {
			coinCollect = SoundFactory.createSoundFromAsset(
					activity.getSoundManager(), 
					activity, 
					"sfx/coin_pickup.ogg");
			
			jumpSound = SoundFactory.createSoundFromAsset(
					activity.getSoundManager(), 
					activity, 
					"sfx/jumping.ogg");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void loadMenuGraphics() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		
		mainMenuTexturesAtlas = new BuildableBitmapTextureAtlas(
				activity.getTextureManager(),
				1024,
				1024,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		
		mainMenuBackground = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				mainMenuTexturesAtlas, 
				activity.getAssets(),
				"background.png");
		
		playButton = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				mainMenuTexturesAtlas, 
				activity.getAssets(),
				"play.png");
		
		try {
			mainMenuTexturesAtlas.build(
					new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
			mainMenuTexturesAtlas.load();
		}
		catch (TextureAtlasBuilderException x) {
			Debug.e(x);
		}
	}
	
	private void loadMenuSounds() {
		try {
			themeMusic = MusicFactory.createMusicFromAsset(
					activity.getMusicManager(), 
					activity, 
					"sfx/theme_music.ogg");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void prepareManager(
			Engine engine,
			MainGameActivity activity,
			BoundCamera camera,
			VertexBufferObjectManager vbom) {
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}
	
	public static ResourceManager getInstance() {
		return instance;
	}

}
