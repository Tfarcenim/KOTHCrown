package tfar.kothcrown;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class ThroneSavedData extends SavedData {

    protected final ServerLevel level;
    CompoundTag storage = new CompoundTag();
    ThroneInventory cache;

    public ThroneInventory getThroneInventory() {
        if (cache == null) {
            cache = new ThroneInventory(this);
            cache.read(storage);
        }
        return cache;
    }

    public ThroneSavedData(ServerLevel level) {
        this.level = level;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put("contents",cache.save());
        return compoundTag;
    }

    protected void load(CompoundTag compoundTag) {
        storage = compoundTag.getCompound("contents");
    }


    @Nullable
    public static ThroneSavedData getInstance(ServerLevel serverLevel) {
        return serverLevel.getDataStorage()
                .get(compoundTag -> loadStatic(compoundTag, serverLevel), name(serverLevel));
    }

    @Nullable
    public static ThroneSavedData getDefaultInstance(MinecraftServer server) {
        return getInstance(server.overworld());
    }

    public static ThroneSavedData getOrCreateInstance(ServerLevel serverLevel) {
        return serverLevel.getDataStorage()
                .computeIfAbsent(compoundTag -> loadStatic(compoundTag,serverLevel),
                        () -> new ThroneSavedData(serverLevel),name(serverLevel));
    }
    public static ThroneSavedData getOrCreateDefaultInstance(MinecraftServer server) {
        return getOrCreateInstance(server.overworld());
    }

    private static String name(ServerLevel level) {
        return  KothCrown.MOD_ID+"_"+level.dimension().location().toString().replace(':','.');
    }


    @Override
    public void save(File file) {
        super.save(file);
        //DankStorageForge.LOGGER.debug("Saving Dank Contents");
    }

    public static ThroneSavedData loadStatic(CompoundTag compoundTag,ServerLevel level) {
        ThroneSavedData dankSavedData = new ThroneSavedData(level);
        dankSavedData.load(compoundTag);
        return dankSavedData;
    }
}
