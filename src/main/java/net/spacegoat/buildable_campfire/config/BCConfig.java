package net.spacegoat.buildable_campfire.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.util.Formatting;
import net.spacegoat.buildable_campfire.BuildableCampfire;

@Config(name = BuildableCampfire.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/oak_log.png")
public class BCConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    private static boolean registered = false;
    public static synchronized BCConfig getConfig() {
        if (!registered) {
            AutoConfig.register(BCConfig.class, JanksonConfigSerializer::new);
            registered = true;
        }
        return AutoConfig.getConfigHolder(BCConfig.class).getConfig();
    }

    @ConfigEntry.Gui.TransitiveObject
    public Gameplay Gameplay = new Gameplay();
    public static class Gameplay {
        @Comment("Players can pick Campfire Logs by right-clicking them with ")
        public boolean campfireLogsArePickable = true;
        @Comment("Plays a sound effect when players pick a Campfire Log.")
        public boolean playSoundWhenCampfireLogGetsPicked = true;
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Deletes Campfire and Soul Campfire Blocks' recipes, so players will need to build them instead.")
        public boolean disableCampfireBlockRecipes = true;
        @Comment("Campfire Logs Block will drop its item form depending on how much log it has.")
        public boolean enableCampfireLogDrops = true;
        @Comment("Players can turn Campfire Logs into 4 Sticks by left-clicking onto them with an Axe while crouching.")
        public boolean enableCampfireLogChopping = true;
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Players can make a Campfire Log using 6 Sticks.")
        public boolean enableSticksToLogRecipes = false;
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Players can use any log on a Stone Cutter to craft 2 Campfire Logs")
        public boolean enableLogToCampfireLogRecipes = true;
        @Comment("Shows a text under the item's name explaining the use of the Campfire Log.")
        public boolean enableItemTooltip = true;
        @ConfigEntry.Gui.CollapsibleObject
        public TooltipConfig tooltipConfig = new TooltipConfig();
        public static class TooltipConfig {
            @Comment("The tooltip itself. Leave empty/null if you want it to be default.")
            public String tooltip = null;
            @Comment("The color of the tooltip text.")
            public Formatting tooltipColor = Formatting.GRAY;
        }
    }
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("campfire_block")
    public CampfireBlockConfig CampfireBlock = new CampfireBlockConfig();
    public static class CampfireBlockConfig {
        @Comment("Minecraft - Orange/Yellow Smoked Campfire")
        public boolean enableBuildableCampfire = true;
        @Comment("Should your Campfire be lit when you build it?")
        public boolean campfireIsLitWhenBuild = false;
        @Comment("Plays a sound effect when you right-click a finished Campfire Template with a coal or charcoal.")
        public boolean playTuffSound = true;
        @Comment("The amount of Coal/Charcoal you will need to build a Campfire.")
        public int howMuchCoalBuildingACampfireCosts = 1;
    }
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("soul_campfire_block")
    public SoulCampfireBlockConfig SoulCampfireBlock = new SoulCampfireBlockConfig();
    public static class SoulCampfireBlockConfig {
        @Comment("Minecraft - Blue Smoked Campfire")
        public boolean enableBuildableSoulCampfire = true;
        @Comment("Should your Soul Campfire be lit when you build it?")
        public boolean soulCampfireIsLitWhenBuild = false;
        @Comment("Plays a sound effect when you right-click a finished Campfire Template with Soul Sand.")
        public boolean playSoulSandSound = true;
        @Comment("The amount of Soul Sand you will need to build a Soul Campfire.")
        public int howMuchSoulSandBuildingASoulCampfireCosts = 1;
    }

    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("modded_campfires")
    public ModdedCampfiresConfig ModdedCampfires = new ModdedCampfiresConfig();
    public static class ModdedCampfiresConfig {
        @ConfigEntry.Gui.CollapsibleObject
        @ConfigEntry.Category("byg_campfires")
        public BYGCampfiresConfig BYGCampfires = new BYGCampfiresConfig();

        public static class BYGCampfiresConfig {
            @ConfigEntry.Gui.CollapsibleObject
            @ConfigEntry.Category("boric_campfire_block")
            public BoricCampfireBlockConfig BoricCampfire = new BoricCampfireBlockConfig();

            public static class BoricCampfireBlockConfig {
                @Comment("Oh The Biomes You'll Go - Green Smoked Campfire")
                public boolean enableBuildableBoricCampfire = true;
                @Comment("Should your Boric Campfire be lit when you build it?")
                public boolean boricCampfireIsLitWhenBuild = false;
                @Comment("Plays a sound effect when you right-click a finished Campfire Template with Brim Powder.")
                public boolean playSandSound = true;
                @Comment("The amount of Brim Powder you will need to build a Boric Campfire.")
                public int howMuchBrimPowderBuildingABoricCampfireCosts = 1;
            }

            @ConfigEntry.Gui.CollapsibleObject
            @ConfigEntry.Category("cryptic_campfire_block")
            public CrypticCampfireBlockConfig CrypticCampfire = new CrypticCampfireBlockConfig();

            public static class CrypticCampfireBlockConfig {
                @Comment("Oh The Biomes You'll Go - Green Smoked Campfire")
                public boolean enableBuildableCrypticCampfire = true;
                @Comment("Should your Boric Campfire be lit when you build it?")
                public boolean crypticCampfireIsLitWhenBuild = false;
                @Comment("Plays a sound effect when you right-click a finished Campfire Template with Brim Powder.")
                public boolean playTuffSound = true;
                @Comment("The amount of Brim Powder you will need to build a Boric Campfire.")
                public int howMuchCrypticMagmaBlockBuildingACrypticCampfireCosts = 1;
            }
        }
    }
}
