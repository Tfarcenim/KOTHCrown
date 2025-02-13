package tfar.kothcrown.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import tfar.kothcrown.datagen.data.ModBlockTagsProvider;
import tfar.kothcrown.datagen.data.ModItemTagsProvider;
import tfar.kothcrown.datagen.data.ModLootTableProvider;
import tfar.kothcrown.datagen.data.ModRecipeProvider;

import java.util.concurrent.CompletableFuture;

public class ModDatagen {

    public static void gather(GatherDataEvent event) {
        boolean client = event.includeClient();
        DataGenerator dataGenerator = event.getGenerator();
        PackOutput packOutput = dataGenerator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        dataGenerator.addProvider(client,new ModLangProvider(packOutput));
        dataGenerator.addProvider(client,new ModBlockstateProvider(packOutput,existingFileHelper));
        dataGenerator.addProvider(client,new ModItemModelProvider(packOutput,existingFileHelper));
        if (event.includeServer()) {
            dataGenerator.addProvider(true, ModLootTableProvider.create(packOutput));
            BlockTagsProvider blockTagsProvider =  new ModBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
            dataGenerator.addProvider(true,blockTagsProvider);
            dataGenerator.addProvider(true,new ModItemTagsProvider(packOutput,lookupProvider, blockTagsProvider.contentsGetter(),existingFileHelper));

            dataGenerator.addProvider(true, new ModRecipeProvider(packOutput));
        }

    }
}
