package net.blumbo.boomcontrol.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.blumbo.boomcontrol.BoomControl;
import net.blumbo.boomcontrol.custom.ExplosionValues;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.Set;

public class BoomControlCmd {

    private static final String EXPLOSION_TYPE_ARG = "explosionType";
    private static final String VALUE_TYPE_ARG = "valueType";

    private static final String POWER_PERCENTAGE_ARG = "powerPercentage";
    private static final String FIRE_PERCENTAGE_ARG = "firePercentage";
    private static final String DESTROY_ITEMS_ARG = "destroyItems";
    private static final Set<String> VALUE_TYPE_ARGS = Set.of(
            POWER_PERCENTAGE_ARG,
            FIRE_PERCENTAGE_ARG,
            DESTROY_ITEMS_ARG
    );

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess access,
                                CommandManager.RegistrationEnvironment environment) {

        LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal(BoomControl.MOD_ID);
        builder.requires(source -> source.hasPermissionLevel(2));

        builder.then(CommandManager.literal("list")
            .then(CommandManager.argument(VALUE_TYPE_ARG, StringArgumentType.word())
                .suggests((context, sb) -> CommandSource.suggestMatching(VALUE_TYPE_ARGS, sb))
                .executes(BoomControlCmd::list)
            )
        );

        builder.then(CommandManager.literal("info")
            .then(CommandManager.argument(EXPLOSION_TYPE_ARG, StringArgumentType.word())
                .suggests((context, sb) -> CommandSource.suggestMatching(ExplosionValues.valuesMap.keySet(), sb))
                .executes(BoomControlCmd::info)
            )
        );

        builder.then(CommandManager.literal("set")
            .then(CommandManager.argument(EXPLOSION_TYPE_ARG, StringArgumentType.word())
                .suggests((context, sb) -> CommandSource.suggestMatching(ExplosionValues.valuesMap.keySet(), sb))

                .then(setArgument(POWER_PERCENTAGE_ARG,
                    FloatArgumentType.floatArg(0, Integer.MAX_VALUE), BoomControlCmd::setPower))
                .then(setArgument(FIRE_PERCENTAGE_ARG,
                    FloatArgumentType.floatArg(0, 100), BoomControlCmd::setFire))
                .then(setArgument(DESTROY_ITEMS_ARG,
                    BoolArgumentType.bool(), BoomControlCmd::setDestroyItems))
            )
        );

        dispatcher.register(builder);
    }

    private static ArgumentBuilder<ServerCommandSource, ?> setArgument(String argument, ArgumentType<?> argType,
                                                    Command<ServerCommandSource> command) {

        LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal(argument)
            .then(CommandManager.argument(argument, argType)
                .executes(command))
            .executes(context -> get(context, argument));

        return builder;
    }

    private static int list(CommandContext<ServerCommandSource> context) {
        String valueType = StringArgumentType.getString(context, VALUE_TYPE_ARG);
        StringBuilder sb = new StringBuilder("\n§7").append(camelToLookGood(valueType)).append(":\n");

        for (ExplosionValues values : ExplosionValues.valuesMap.values()) {
            sb.append("§7 ").append(values.name).append(": ")
                .append(valueString(values, valueType)).append("\n");
        }

        context.getSource().sendFeedback(() -> Text.of(sb.toString()), false);
        return 0;
    }

    private static int info(CommandContext<ServerCommandSource> context) {
        ExplosionValues values = ensureExplosionType(context);
        if (values == null) return 0;
        String explosionType = StringArgumentType.getString(context, EXPLOSION_TYPE_ARG);

        String string = "\n§7" + camelToLookGood(explosionType) + ":\n" +
            "§7 " + camelToLookGood(POWER_PERCENTAGE_ARG) + ": " + percentText(values.powerPercentage) + "\n" +
            "§7 " + camelToLookGood(FIRE_PERCENTAGE_ARG) + ": " + percentText(values.firePercentage) + "\n" +
            "§7 " + camelToLookGood(DESTROY_ITEMS_ARG) + ": " + boolText(values.destroyItems) + "\n";

        context.getSource().sendFeedback(() -> Text.of(string), false);
        return 0;
    }

    private static int get(CommandContext<ServerCommandSource> context, String valueType) {
        ExplosionValues values = ensureExplosionType(context);
        if (values == null) return 0;
        String valueString = valueString(values, valueType);
        if (valueString == null) return 0;

        String explosionType = StringArgumentType.getString(context, EXPLOSION_TYPE_ARG);

        context.getSource().sendFeedback(() -> Text.of("§7" + camelToLookGood(explosionType) + " " +
            camelToLookGood(valueType) + " is currently " + valueString), false);

        return 0;
    }

    private static int setPower(CommandContext<ServerCommandSource> context) {
        ExplosionValues values = ensureExplosionType(context);
        if (values == null) return 0;

        float powerPercentage = FloatArgumentType.getFloat(context, POWER_PERCENTAGE_ARG);
        values.powerPercentage = powerPercentage;
        context.getSource().sendFeedback(() -> Text.of("§7" + camelToLookGood(POWER_PERCENTAGE_ARG) +
            " set to §b" + percentText(powerPercentage) + " §7for §b" + values.name), false);

        return 0;
    }

    private static int setFire(CommandContext<ServerCommandSource> context) {
        ExplosionValues values = ensureExplosionType(context);
        if (values == null) return 0;

        float firePercentage = FloatArgumentType.getFloat(context, FIRE_PERCENTAGE_ARG);
        values.firePercentage = firePercentage;
        context.getSource().sendFeedback(() -> Text.of("§7" + camelToLookGood(FIRE_PERCENTAGE_ARG) +
            " set to §b" + percentText(firePercentage) + " §7for §b" + values.name), false);

        return 0;
    }

    private static int setDestroyItems(CommandContext<ServerCommandSource> context) {
        ExplosionValues values = ensureExplosionType(context);
        if (values == null) return 0;

        boolean destroyItems = BoolArgumentType.getBool(context, DESTROY_ITEMS_ARG);
        values.destroyItems = destroyItems;
        context.getSource().sendFeedback(() -> Text.of("§7" + camelToLookGood(DESTROY_ITEMS_ARG) +
            " set to " + boolText(destroyItems) + " §7for §b" + values.name), false);

        return 0;
    }

    @Nullable
    private static String valueString(ExplosionValues values, String valueType) {
        return switch (valueType) {
            case POWER_PERCENTAGE_ARG -> percentText(values.powerPercentage);
            case FIRE_PERCENTAGE_ARG -> percentText(values.firePercentage);
            case DESTROY_ITEMS_ARG -> boolText(values.destroyItems);
            default -> null;
        };
    }

    private static ExplosionValues ensureExplosionType(CommandContext<ServerCommandSource> context) {
        String argument = StringArgumentType.getString(context, EXPLOSION_TYPE_ARG);
        if (argument == null) return null;

        ExplosionValues values = ExplosionValues.valuesMap.get(argument);
        if (values == null) {
            context.getSource().sendError(Text.of("§7Invalid explosion type"));
        }
        return values;
    }

    private static String boolText(boolean bool) {
        return bool ? "§eTrue" : "§6False";
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
