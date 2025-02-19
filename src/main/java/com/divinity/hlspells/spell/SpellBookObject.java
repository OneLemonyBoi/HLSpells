package com.divinity.hlspells.spell;

import com.divinity.hlspells.init.SpellBookInit;
import com.google.common.collect.ImmutableList;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.List;

public class SpellBookObject extends ForgeRegistryEntry<SpellBookObject> {

    private final String name;
    private final ImmutableList<SpellInstance> spellInstances;

    public SpellBookObject(SpellInstance... spellInstances) {
        this(null, spellInstances);
    }

    public SpellBookObject(@Nullable String name, SpellInstance... spellInstances) {
        this.name = name;
        this.spellInstances = ImmutableList.copyOf(spellInstances);
    }

    public static SpellBookObject byName(String name) {
        return SpellBookInit.SPELL_BOOK_REGISTRY.get().getValue(ResourceLocation.tryParse(name));
    }

    public boolean isIn(ITag<SpellBookObject> tag) {
        return tag.contains(this);
    }

    public String getName(String name) {
        if (SpellBookInit.SPELL_BOOK_REGISTRY.get().getKey(this) == null) return "";
        return name + (this.name == null ? SpellBookInit.SPELL_BOOK_REGISTRY.get().getKey(this).getPath() : this.name);
    }

    public List<SpellInstance> getSpells() {
        return this.spellInstances;
    }

    public boolean hasInstantAlterations()
    {
        if (!this.spellInstances.isEmpty())
        {
            for (SpellInstance spellInstance : this.spellInstances) {

                if (spellInstance.getSpell().isInstantenous()) {
                    return true;
                }
            }
        }
        return false;
    }
}