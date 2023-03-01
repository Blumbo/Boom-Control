package net.blumbo.boomcontrol.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.blumbo.boomcontrol.BoomControl;
import net.blumbo.boomcontrol.custom.ExplosionValues;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.text.DecimalFormat;

public class BoomControlCmd {

    private static final String EXPLOSION_TYPE_ARG = "explosionType";

    private static final String POWER_PERCENTAGE_ARG = "powerPercentage";
    private static final String FIRE_PERCENTAGE_ARG = "firePercentage";
    private static final String DESTROY_ITEMS_ARG = "destroyItems";

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess access,
                                CommandManager.RegistrationEnvironment environment) {

        LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal(BoomControl.MOD_ID);
        builder.requires(source -> source.hasPermissionLevel(2));

        builder.then(CommandManager.literal("list")
                .then(CommandManager.literal(POWER_PERCENTAGE_ARG)
                        .executes(context -> list(context, POWER_PERCENTAGE_ARG)))
                .then(CommandManager.literal(FIRE_PERCENTAGE_ARG)
                        .executes(context -> list(context, FIRE_PERCENTAGE_ARG)))
                .then(CommandManager.literal(DESTROY_ITEMS_ARG)
                        .executes(context -> list(context, DESTROY_ITEMS_ARG)))
        );

        builder.then(CommandManager.literal("set")

                .then(CommandManager.argument(EXPLOSION_TYPE_ARG, StringArgumentType.word())
                        .suggests((context, suggestionsBuilder) ->
                                CommandSource.suggestMatching(ExplosionValues.valuesMap.keySet(), suggestionsBuilder))

                        .then(CommandManager.literal(POWER_PERCENTAGE_ARG)
                                .then(CommandManager.argument(POWER_PERCENTAGE_ARG, FloatArgumentType.floatArg(0, Integer.MAX_VALUE))
                                        .executes(BoomControlCmd::setPower)))

                        .then(CommandManager.literal(FIRE_PERCENTAGE_ARG)
                                .then(CommandManager.argument(FIRE_PERCENTAGE_ARG, FloatArgumentType.floatArg(0, 100))
                                        .executes(BoomControlCmd::setFire)))

                        .then(CommandManager.literal(DESTROY_ITEMS_ARG)
                                .then(CommandManager.argument(DESTROY_ITEMS_ARG, BoolArgumentType.bool())
                                        .executes(BoomControlCmd::setDestroyItems)))
                )
        );

        dispatcher.register(builder);
    }

    private static int list(CommandContext<ServerCommandSource> context, String valueType) {
        StringBuilder sb = new StringBuilder("\n");

        for (ExplosionValues values : ExplosionValues.valuesMap.values()) {
            sb.append("§7").append(values.name).append(": ")
                    .append(valueString(values, valueType)).append("\n");
        }

        context.getSource().sendFeedback(Text.of(sb.toString()), false);
        return 0;
    }

    private static String valueString(ExplosionValues values, String valueType) {
        if (valueType.equals(POWER_PERCENTAGE_ARG)) {
            return percentText(values.powerPercentage);
        } else if (valueType.equals(FIRE_PERCENTAGE_ARG)) {
            return percentText(values.firePercentage);
        } else if (valueType.equals(DESTROY_ITEMS_ARG)) {
            return boolText(values.destroyItems);
        }
        return "§cnull";
    }

    private static int setPower(CommandContext<ServerCommandSource> context) {
        ExplosionValues values = ensureExplosionType(context);
        if (values == null) return 0;

        float powerPercentage = FloatArgumentType.getFloat(context, POWER_PERCENTAGE_ARG);
        values.powerPercentage = powerPercentage;
        context.getSource().sendFeedback(Text.of("§7" + camelToLookGood(POWER_PERCENTAGE_ARG) +
                " set to §b" + percentText(powerPercentage) + " §7for §b" + values.name), false);

        return 0;
    }

    private static int setFire(CommandContext<ServerCommandSource> context) {
        ExplosionValues values = ensureExplosionType(context);
        if (values == null) return 0;

        float firePercentage = FloatArgumentType.getFloat(context, FIRE_PERCENTAGE_ARG);
        values.firePercentage = firePercentage;
        context.getSource().sendFeedback(Text.of("§7" + camelToLookGood(FIRE_PERCENTAGE_ARG) +
                " set to §b" + percentText(firePercentage) + " §7for §b" + values.name), false);

        return 0;
    }

    private static int setDestroyItems(CommandContext<ServerCommandSource> context) {
        ExplosionValues values = ensureExplosionType(context);
        if (values == null) return 0;

        boolean destroyItems = BoolArgumentType.getBool(context, DESTROY_ITEMS_ARG);
        values.destroyItems = destroyItems;
        context.getSource().sendFeedback(Text.of("§7" + camelToLookGood(DESTROY_ITEMS_ARG) +
                " set to " + boolText(destroyItems) + " §7for §b" + values.name), false);

        return 0;
    }

    private static ExplosionValues ensureExplosionType(CommandContext<ServerCommandSource> context) {
        String argument = StringArgumentType.getString(context, EXPLOSION_TYPE_ARG);
        if (argument == null) return null;

        ExplosionValues values = ExplosionValues.valuesMap.get(argument);
        if (values == null) {
            context.getSource().sendError(Text.of("§8Invalid explosion type"));
        }
        return values;
    }

    private static String boolText(boolean bool) {
        return bool ? "§etrue" : "§6false";
    }

    private static String percentText(float percentage) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String shownPercentage = decimalFormat.format(percentage);
        return "§b" + shownPercentage + "%";
    }

    public static String camelToLookGood(String camel) {
        StringBuilder sb = new StringBuilder(camel);
        for (int i = 1; i < sb.length(); i++) {
            if (Character.isUpperCase(sb.charAt(i))) {
                sb.insert(i, " ");
                i++;
            }
        }
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

}
