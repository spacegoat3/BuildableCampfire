package net.spacegoat.buildable_campfire.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
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
    public Gameplay Gameplay = new Gameplay();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("campfire_block")
    public CampfireBlock CampfireBlock = new CampfireBlock();
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("soul_campfire_block")
    public SoulCampfireBlock SoulCampfireBlock = new SoulCampfireBlock();

    public static class Gameplay{
        @Comment("Deletes Campfire and Soul Campfire Blocks' recipes, so you will need to build them instead.")
        public boolean deleteCampfireBlockRecipes = true;
        @Comment("Campfire Logs Block will drop its item form depending on how much log it has.")
        public boolean enableCampfireLogDrops = true;
        @Comment("You can use any log on a Stone Cutter to craft 2 Campfire Logs")
        public boolean enableLogToCampfireLogRecipes = true;
        @Comment("The amount of light Campfire Log gives off to its surroundings.")
        public int campfireLogLuminance = 0;
    }
    public static class CampfireBlock{
        public boolean enableCampfireLog = true;
        @Comment("Will the Campfire Block going to be lit or unlit when you built it.")
        public boolean campfireIsLitWhenBuild = false;
        @Comment("The amount of Coal/Charcoal you will need to build a Campfire.")
        public int howMuchCoalBuildingACampfireTakes = 1;
    }
    public static class SoulCampfireBlock{
        public boolean enableSoulCampfireLog = true;
        @Comment("Will the Soul Campfire Block going to be lit or unlit when you built it.")
        public boolean soulCampfireIsLitWhenBuild = false;
        @Comment("The amount of Soul Sand you will need to build a Soul Campfire.")
        public int howMuchSoulSandBuildingASoulCampfireTakes = 1;
    }
}
