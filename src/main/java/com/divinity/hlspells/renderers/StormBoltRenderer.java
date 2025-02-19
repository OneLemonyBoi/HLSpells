package com.divinity.hlspells.renderers;

import com.divinity.hlspells.HLSpells;
import com.divinity.hlspells.entities.StormBoltEntity;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class StormBoltRenderer extends EntityRenderer<StormBoltEntity>
{
    public StormBoltRenderer(EntityRendererManager manager)
    {
        super(manager);
    }

   @Override
   public boolean shouldRender(StormBoltEntity p_225626_1_, ClippingHelper p_225626_2_, double p_225626_3_, double p_225626_5_, double p_225626_7_)
   {
       return false;
   }

    @Override
    public ResourceLocation getTextureLocation(StormBoltEntity p_110775_1_)
    {
        return new ResourceLocation(HLSpells.MODID,  "textures");
    }
}
