package net.blumbo.boomcontrol;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import net.blumbo.boomcontrol.commands.BoomControlCmd;
import net.blumbo.boomcontrol.custom.ExplosionValues;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BoomControl implements ModInitializer {

    public static final String MOD_ID = "boomcontrol";

    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register(BoomControlCmd::register);
        load();
    }

    private static final String dir = FabricLoader.getInstance().getConfigDir().toString() + "/" + MOD_ID;
    public static String fileName = "boomcontrol.json";

    private static void load() {
        try {
            String jsonString = Files.readString(Paths.get(dir + "/" + fileName));
            Gson gson = new Gson();

            Type type = new TypeToken<HashMap<String, ExplosionValues>>(){}.getType();
            HashMap<String, ExplosionValues> loadedMap = gson.fromJson(jsonString, type);

            for (Map.Entry<String, ExplosionValues> entry : ExplosionValues.valuesMap.entrySet()) {
                ExplosionValues loaded = loadedMap.get(entry.getKey());
                if (loaded == null) continue;
                ExplosionValues current = entry.getValue();

                current.firePercentage = loaded.firePercentage;
                current.destroyItems = loaded.destroyItems;
                current.powerPercentage = loaded.powerPercentage;
            }

        } catch (Exception ignored) {}
    }

    public static void save() {
        try {
            Files.createDirectories(Paths.get(dir));

            Gson gson = new Gson();
            String jsonString = gson.toJson(ExplosionValues.valuesMap);

            BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/" + fileName));
            writer.write(jsonString);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
