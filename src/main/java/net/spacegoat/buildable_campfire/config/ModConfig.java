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
    public Default Default = new Default();
    public static class Default {
        public boolean enableBuildableCampfire = true;
    }

    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("buildable_campfire")
    public BuildableCampfire BuildableCampfire = new BuildableCampfire();
    public static class BuildableCampfire{
        @Comment("Deletes Campfire and Soul Campfire Blocks' recipes, so you will need to build them instead.")
        public boolean deleteCampfireBlockRecipes = true;
        @Comment("Campfire Logs Block will drop its item form depending on how much log it has.")
        public boolean enableCampfireLogDrops = true;
        @Comment("You can use any log on a Stone Cutter to craft 2 Campfire Logs")
        public boolean enableLogToCampfireLogRecipes = true;
        @Comment("The amount of light Campfire Log gives off to its surroundings.")
        public int campfireLogLuminance = 0;
        public boolean enableCampfireLog = true;
        @Comment("Will the Campfire Block going to be lit or unlit when you built it.")
        public boolean campfireIsLitWhenBuild = false;
        @Comment("The amount of Coal/Charcoal you will need to build a Campfire.")
        public int howMuchCoalBuildingACampfireTakes = 1;
        public boolean enableSoulCampfireLog = true;
        @Comment("Will the Soul Campfire Block going to be lit or unlit when you built it.")
        public boolean soulCampfireIsLitWhenBuild = false;
        @Comment("The amount of Soul Sand you will need to build a Soul Campfire.")
        public int howMuchSoulSandBuildingASoulCampfireTakes = 1;
    }
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("stripped_campfires")
    public StrippedCampfires StrippedCampfires = new StrippedCampfires();
    public static class StrippedCampfires {
        public boolean enableStrippedCampfire = true;
        public boolean enableStrippedSoulCampfire = true;
        @Comment("The amount of light Stripped Campfires gives off to their surroundings.")
        public int strippedCampfiresLuminance = 15;
    }
}
