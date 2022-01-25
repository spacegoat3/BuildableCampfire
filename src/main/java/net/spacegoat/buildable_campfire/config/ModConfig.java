package net.spacegoat.buildable_campfire.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.spacegoat.buildable_campfire.ModMain;

@Config(name = ModMain.MOD_ID)
public class ModConfig implements ConfigData {
    @ConfigEntry.Gui.Excluded
    private transient static boolean registered = false;
    public static synchronized ModConfig getConfig() {
        if (!registered) {
            AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
            registered = true;
        }
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }

    @ConfigEntry.Gui.TransitiveObject
    public CampfireBlock campfireBlock = new CampfireBlock();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("soul_campfire_block")
    public SoulCampfireBlock soulCampfireBlock = new SoulCampfireBlock();

    public static class CampfireBlock{
        public boolean enableCampfireLog = true;
        public boolean campfireIsLitWhenBuild = false;
        public int howMuchCoalBuildingCampfireTakes = 1;
    }
    public static class SoulCampfireBlock{
        public boolean enableSoulCampfireLog = true;
        public boolean soulCampfireIsLitWhenBuild = false;
        public int howMuchCoalBuildingSoulCampfireTakes = 1;
    }
}
