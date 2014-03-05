package com.udemy.runalienrun;

import org.andengine.engine.camera.BoundCamera;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

public abstract class Player extends AnimatedSprite {
	
	private Body body;
	
	public boolean canJump = true;

	public Player(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager vbom, BoundCamera camera, PhysicsWorld physicsWorld) {
		super(pX, pY, pWidth, pHeight, ResourceManager.getInstance().playerTexture, vbom);
		
		createPhysics(camera, physicsWorld);
		camera.setChaseEntity(this);
		camera.setBounds(0, 240, 9600, 290);
		camera.setBoundsEnabled(true);
		camera.offsetCenter(0, 0);
	}
	
	public abstract void onDie();
	
	public void setRunning() {
		if (!isAnimationRunning()) {
			final long[] PLAYER_ANIMATE = new long[] { 100, 100, 100, 100 };
			animate(PLAYER_ANIMATE, 0, 3, true);
		}
	}
	
	public void jump() {
		if (canJump) {
			
			ResourceManager.getInstance().jumpSound.play();
			
			stopAnimation();
			setCurrentTileIndex(5);
			
			canJump = false;
			
			Vector2 velocity = new Vector2(0, -10);
			body.setLinearVelocity(velocity);
		}
	}
	
	private void createPhysics(final BoundCamera camera, PhysicsWorld physicsWorld) {
		FixtureDef fixtureDef = PhysicsFactory.createFixtureDef(0, 0, 0); 
		
		body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, fixtureDef);
		body.setUserData("player");
		body.setFixedRotation(true);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body, true, false) {

			@Override
			public void onUpdate(float pSecondsElapsed) {
				super.onUpdate(pSecondsElapsed);
				camera.onUpdate(.1f);
				
				if (getY() >= 480) {
					onDie();
					camera.setChaseEntity(null);
				}
				body.setLinearVelocity(new Vector2(2, body.getLinearVelocity().y));
			}
			
		});
	}

}
