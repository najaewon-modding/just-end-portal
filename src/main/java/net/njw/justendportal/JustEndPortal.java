package net.njw.justendportal;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(JustEndPortal.MODID)
public class JustEndPortal {
    public static final String MODID = "justendportal";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JustEndPortal(IEventBus modEventBus, ModContainer modContainer) {
    }
}
