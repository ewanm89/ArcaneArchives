package com.aranaira.arcanearchives.entity.render;

import com.aranaira.arcanearchives.ArcaneArchives;
import com.aranaira.arcanearchives.client.render.EntityOBJModel;
import com.aranaira.arcanearchives.entity.SpiritGeneric;
import com.aranaira.arcanearchives.util.helpers.MathHelper;
import com.aranaira.arcanearchives.util.helpers.MathHelper.EaseMode;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;

public class RenderSpiritGeneric extends EntityOBJModel<SpiritGeneric> {
	
	float animTime;
	public AnimMatterMoteState animState = AnimMatterMoteState.IDLE_IN;
	
	public RenderSpiritGeneric(RenderManager renderManager)
	{
		super(renderManager);
		ArcaneArchives.logger.info("beep boop, running the OBJ model model");
	}
	
	@Override
	protected ResourceLocation[] getEntityModels()
	{
		return new ResourceLocation[] {
			new ResourceLocation(ArcaneArchives.MODID, "entity/spiritmatter/mote/head.obj"),
			new ResourceLocation(ArcaneArchives.MODID, "entity/spiritmatter/mote/jaw.obj"),
			new ResourceLocation(ArcaneArchives.MODID, "entity/spiritmatter/mote/tail.obj"),
			new ResourceLocation(ArcaneArchives.MODID, "entity/spiritmatter/mote/arm_left.obj"),
			new ResourceLocation(ArcaneArchives.MODID, "entity/spiritmatter/mote/arm_right.obj"),
		};
	}
	
	@Override
	protected boolean preRender(SpiritGeneric entity, int model, BufferBuilder buffer, double x, double y, double z, float entityYaw, float partialTicks)
	{
		GlStateManager.rotate(180, 1, 0, 0);
		
		if(model == 0) //head
		{
			animTime += partialTicks * 0.05f;
		}
		if(model == 1) //jaw
		{
			//GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.600F, -0.149F);
			//GlStateManager.popMatrix();
		}
		if(model == 2) //tail
		{
			GlStateManager.translate(0.0F, 0.468F, 0.054F);
		}
		if(model == 3) //left arm
		{
			GlStateManager.translate(-0.200F, 0.425F, 0.101F);
		}
		if(model == 4) //left arm
		{
			GlStateManager.translate(0.200F, 0.425F, 0.101F);
		}
		
		if(animState == AnimMatterMoteState.IDLE_IN)
			AnimateStateIdleIn(model);
		else if(animState == AnimMatterMoteState.IDLE_OUT)
			AnimateStateIdleOut(model);
		
		return true;
	}
	
	private enum AnimMatterMoteState
	{
		IDLE_IN, IDLE_OUT
	}
	
	private void AnimateStateIdleIn(int model)
	{
		float duration = 1f; 
		GlStateManager.translate(0.0F, MathHelper.LinearTween(animTime, 0.150f, 0, duration), 0.0F);
		
		switch(model)
		{
			case 0: //HEAD
				
				break;
				
			case 1: //JAW
				GlStateManager.rotate(MathHelper.LinearTween(animTime, -10, 0, duration), 1, 0, 0);
				break;
				
			case 2: //TAIL
				GlStateManager.rotate(MathHelper.LinearTween(animTime, 20, 0, duration), 1, 0, 0);
				break;
				
			case 3: //LEFT ARM
				GlStateManager.rotate(MathHelper.LinearTween(animTime, 0, -15, duration), 0, 0.4f, 1);
				break;
				
			case 4: //RIGHT ARM
				GlStateManager.rotate(MathHelper.LinearTween(animTime, 0, 15, duration), 0, 0.4f, 1);
				break;
		}
		
		if(model == 4 && animTime >= duration)
		{
			animState = AnimMatterMoteState.IDLE_OUT;
			animTime = 0;
		}
	}
	
	private void AnimateStateIdleOut(int model)
	{
		float duration = 1f;
		GlStateManager.translate(0.0F, MathHelper.LinearTween(animTime, 0, 0.150f, duration), 0.0F);
		
		switch(model)
		{
			case 0: //HEAD
				
				break;
				
			case 1: //JAW
				GlStateManager.rotate(MathHelper.LinearTween(animTime, 0, -10, duration), 1, 0, 0);
				break;
				
			case 2: //TAIL
				GlStateManager.rotate(MathHelper.LinearTween(animTime, 0, 20, duration), 1, 0, 0);
				break;
				
			case 3: //LEFT ARM
				GlStateManager.rotate(MathHelper.LinearTween(animTime, -15, 0, duration), 0, 0.4f, 1);
				break;
				
			case 4: //RIGHT ARM
				GlStateManager.rotate(MathHelper.LinearTween(animTime, 15, 0, duration), 0, 0.4f, 1);
				break;
		}
		
		if(model == 4 && animTime >= duration)
		{
			animState = AnimMatterMoteState.IDLE_IN;
			animTime = 0;
		}
	}
}
