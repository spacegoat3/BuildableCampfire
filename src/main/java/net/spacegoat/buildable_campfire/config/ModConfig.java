package net.spacegoat.buildable_campfire.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import net.minecraft.util.Formatting;
import net.spacegoat.buildable_campfire.BuildableCampfireMod;

@Config(name = BuildableCampfireMod.MOD_ID)
@Config.Gui.Background("minecraft:textures/block/oak_log.png")
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
    public static class Gameplay {
        @Comment("Players can pick Campfire Logs by right-clicking them with ")
        public boolean campfireLogsArePickable = true;
        @Comment("Plays a sound effect when players pick a Campfire Log.")
        public boolean playSoundWhenCampfireLogGetsPicked = true;
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Deletes Campfire and Soul Campfire Blocks' recipes, so players will need to build them instead.")
        public boolean deleteCampfireBlockRecipes = true;
        @Comment("Campfire Logs Block will drop its item form depending on how much log it has.")
        public boolean enableCampfireLogDrops = true;
        @Comment("Players can gather Campfire Logs by left-clicking log or log-like them with an Axe while crouching.")
        public boolean enableLogChopping = true;
        @Comment("Players can turn Campfire Logs into 6 Sticks by left-clicking onto them with an Axe while crouching.")
        public boolean enableCampfireLogChopping = true;
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Players can make a Campfire Log using 6 Sticks.")
        public boolean enableSticksToLogRecipes = false;
        @ConfigEntry.Gui.RequiresRestart
        @Comment("Players can use any log on a Stone Cutter to craft 2 Campfire Logs")
        public boolean enableLogToCampfireLogRecipes = true;
        @Comment("Shows a text under the item's name explaining the use of the Campfire Log.")
        public boolean enableItemTooltip = true;
        @Comment("The tooltip itself. Leave empty/null if you want it to be default.")
        public String tooltip = "";
        @Comment("The color of the tooltip text.")
        public Formatting tooltipColor = Formatting.GRAY;
        @Comment("Changes the Campfire Log's name to Firewood. (User Suggestion)")
        public boolean changeCampfireLogName = false;
        @Comment("The amount of light Campfire Log gives off to its surroundings.")
        public int campfireLogLuminance = 0;
    }
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("logs")
    public Logs logs = new Logs();
    public static class Logs {
        public boolean oakLog = true;
    }

    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("campfire_block")
    public CampfireBlock CampfireBlock = new CampfireBlock();
    public static class CampfireBlock {
        public boolean enableBuildableCampfire = true;
        @Comment("Should your Campfire be lit when you build it?")
        public boolean campfireIsLitWhenBuild = false;
        @Comment("Plays a sound effect when you right-click a finished Campfire Template with a coal or charcoal.")
        public boolean playSoundEffect = true;
        @Comment("The amount of Coal/Charcoal you will need to build a Campfire.")
        public int howMuchCoalBuildingACampfireTakes = 1;
    }
    @ConfigEntry.Gui.TransitiveObject
    @ConfigEntry.Category("soul_campfire_block")
    public SoulCampfireBlock SoulCampfireBlock = new SoulCampfireBlock();
    public static class SoulCampfireBlock {
        public boolean enableBuildableSoulCampfire = true;
        @Comment("Should your Soul Campfire be lit when you build it?")
        public boolean soulCampfireIsLitWhenBuild = false;
        @Comment("Plays a sound effect when you right-click a finished Campfire Template with Soul Sand.")
        public boolean playSoundEffect = true;
        @Comment("The amount of Soul Sand you will need to build a Soul Campfire.")
        public int howMuchSoulSandBuildingASoulCampfireTakes = 1;
    }
}
