package com.fuzs.aquaacrobatics.core;

import com.fuzs.aquaacrobatics.AquaAcrobatics;
import com.fuzs.aquaacrobatics.client.handler.NoMixinHandler;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.Map;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.Name(AquaAcrobaticsCore.NAME)
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class AquaAcrobaticsCore implements IFMLLoadingPlugin {

    public static final String MODID = AquaAcrobatics.MODID;
    public static final String NAME = AquaAcrobatics.NAME + " Transformer";
    public static final String VERSION = AquaAcrobatics.VERSION;
    public static final Logger LOGGER = LogManager.getLogger(AquaAcrobaticsCore.NAME);

    private static boolean isLoaded;
    public static boolean isModCompatLoaded;
    private static boolean isScreenRegistered;
    
    public AquaAcrobaticsCore() {
        try {
            Class.forName("org.spongepowered.asm.launch.MixinTweaker");
        } catch(ClassNotFoundException e) {
            AquaAcrobaticsCore.LOGGER.error("No instance of Mixin framework detected. Unable to proceed load.", e);
            return;
        }
        
        MixinBootstrap.init();
        Mixins.addConfiguration("META-INF/mixins." + AquaAcrobaticsCore.MODID + ".json");
        isLoaded = true;
        if("true".equals(System.getProperty("aquaacrobatics.fghack"))) {
            AquaAcrobaticsCore.LOGGER.info("Running in userdev, proceeding to apply workaround to ensure mod is loaded");
            CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                URL location = codeSource.getLocation();
                try {
                    File file = new File(location.toURI());
                    if (file.isFile()) {
                        CoreModManager.getReparseableCoremods().remove(file.getName());
                    }
                } catch (URISyntaxException ignored) {}
            } else {
                AquaAcrobaticsCore.LOGGER.warn("No CodeSource, if this is not a development environment we might run into problems!");
                AquaAcrobaticsCore.LOGGER.warn(this.getClass().getProtectionDomain());
            }
        } else {
            AquaAcrobaticsCore.LOGGER.info("Running in obf, thanks for playing with the mod!");
        }
        
    }
    @Override
    public String[] getASMTransformerClass() {

        return new String[0];
    }

    @Override
    public String getModContainerClass() {

        return null;
    }

    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    
    @Override
    public String getAccessTransformerClass() {

        return null;
    }

    public static boolean isLoaded() {

        if (!isScreenRegistered) {

            isScreenRegistered = true;
            if(FMLCommonHandler.instance().getSide() == Side.CLIENT)
                MinecraftForge.EVENT_BUS.register(new NoMixinHandler());
            else if(!isLoaded) {
                throw new RuntimeException("Mixin framework is missing, please install it.");
            }
        }

        return isLoaded;
    }

}
