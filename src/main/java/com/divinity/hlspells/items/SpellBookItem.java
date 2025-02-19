package com.divinity.hlspells.items;

import com.divinity.hlspells.misc.CastSpells;
import com.divinity.hlspells.spell.SpellBookObject;
import com.divinity.hlspells.spell.SpellInstance;
import com.divinity.hlspells.init.SpellBookInit;
import com.divinity.hlspells.util.SpellUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;


public class SpellBookItem extends ShootableItem
{
    public static boolean isHeldActive = false;
    private List<SpellInstance> spell = new ArrayList<>();

    public SpellBookItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public ItemStack getDefaultInstance()
    {
        return SpellUtils.setSpellBook(super.getDefaultInstance(), SpellBookInit.EMPTY.get());
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> text, ITooltipFlag flag)
    {
        SpellUtils.addSpellBookTooltip(stack, text, 1.0F);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return super.isFoil(stack) || !SpellUtils.getSpell(stack).isEmpty();
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> stacks) {

        if (this.allowdedIn(group)) {
            for (SpellBookObject spellBookObject : SpellBookInit.SPELL_BOOK_REGISTRY.get())
            {
                stacks.add(SpellUtils.setSpellBook(new ItemStack(this), spellBookObject));
            }
        }
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return ARROW_ONLY;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }

    @Override
    public void releaseUsing(ItemStack stack, World world, LivingEntity entity, int power)
    {
        if (entity instanceof PlayerEntity)
        {
            PlayerEntity playerEntity = (PlayerEntity) entity;
            isHeldActive = false;
            
            if (playerEntity.getUseItemRemainingTicks() < 71988)
            {
                if (!playerEntity.getCommandSenderWorld().isClientSide())
                {
                    CastSpells.doCastSpell(playerEntity, world, stack);
                }

                if (playerEntity.getCommandSenderWorld().isClientSide())
                {
                    CastSpells.doParticles(playerEntity);
                }
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int value, boolean bool)
    {
        if (stack.getEnchantmentTags().size() > 1)
        {
            for (int i = 0; i < stack.getEnchantmentTags().size() && stack.getEnchantmentTags().size() > 1; i++)
            {
                stack.getEnchantmentTags().remove(i);
            }
        }

        if (entity instanceof PlayerEntity)
        {
            PlayerEntity playerEntity =  (PlayerEntity) entity;
            if (isHeldActive)
            {
                if (playerEntity.getMainHandItem().getItem() instanceof SpellBookItem || playerEntity.getOffhandItem().getItem() instanceof SpellBookItem)
                {

                    if (SpellUtils.getSpellBook(playerEntity.getMainHandItem().getStack()).getSpells() == spell ||
                        SpellUtils.getSpellBook(playerEntity.getOffhandItem().getStack()).getSpells() == spell)
                    {
                        return;
                    }
                }
                isHeldActive = false;
            }
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack)
    {
        return true;
    }

    @Override
    public ActionResult<ItemStack> use (World worldIn, PlayerEntity playerIn, Hand handIn)
    {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        playerIn.startUsingItem(handIn);
        this.spell = SpellUtils.getSpellBook(itemstack).getSpells();
        isHeldActive = true;
        return ActionResult.success(itemstack);
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return 72000;
    }

    @Override
    public UseAction getUseAnimation(ItemStack stack)
    {
        return UseAction.CROSSBOW;
    }
}