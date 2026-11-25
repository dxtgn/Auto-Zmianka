package pl.autozmianka;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoZmiankaClient implements ClientModInitializer {
    public static final AutoZmiankaConfig CONFIG = new AutoZmiankaConfig();
    private static final Pattern PERCENT = Pattern.compile("(\\d{1,3})\\s*%");
    private static KeyBinding toggleKey, menuKey;
    private static boolean running;
    private static long nextClickAt;
    private static String previousLore = "";
    private static String status = "Wyłączona";

    @Override
    public void onInitializeClient() {
        CONFIG.load();
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autozmianka.toggle", GLFW.GLFW_KEY_R, "category.autozmianka"));
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.autozmianka.menu", GLFW.GLFW_KEY_RIGHT_SHIFT, "category.autozmianka"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                if (running) stopAndReset(client, "Zatrzymana ręcznie");
                else start(client);
            }
            while (menuKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new AutoZmiankaScreen());
            }
            tickAutomation(client);
        });
    }

    public static boolean isRunning() { return running; }
    public static String getStatus() { return status; }

    public static void start(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) {
            status = "Wejdź do świata";
            return;
        }
        running = true;
        nextClickAt = 0;
        previousLore = currentBlueDyeText(client);
        status = "Losowanie aktywne";
        client.player.sendMessage(Text.literal("[Auto-Zmianka] START").formatted(Formatting.GREEN), true);
    }

    public static void stopAndReset(MinecraftClient client, String reason) {
        running = false;
        nextClickAt = 0;
        previousLore = "";
        status = reason + " — reset";
        if (client.player != null) {
            client.player.sendMessage(Text.literal("[Auto-Zmianka] " + status).formatted(Formatting.YELLOW), true);
        }
    }

    private static void tickAutomation(MinecraftClient client) {
        if (!running || client.player == null || client.interactionManager == null) return;
        if (!(client.currentScreen instanceof HandledScreen<?>)) {
            stopAndReset(client, "Zamknięto GUI");
            return;
        }

        long now = Util.getMeasuringTimeMs();
        String currentText = currentBlueDyeText(client);
        if (!currentText.isEmpty() && !currentText.equals(previousLore)) {
            previousLore = currentText;
            Integer result = readPercent(currentText);
            if (result != null && result >= CONFIG.threshold) {
                stopAndReset(client, "Osiągnięto " + result + "%");
                return;
            }
        }

        if (now < nextClickAt) return;
        int slotId = findBlueDyeSlot(client);
        if (slotId < 0) {
            status = "Nie znaleziono niebieskiego barwnika";
            return;
        }

        client.interactionManager.clickSlot(
                client.player.currentScreenHandler.syncId, slotId, 0,
                SlotActionType.PICKUP, client.player);
        nextClickAt = now + CONFIG.clickDelayMs;
        status = "Losowanie…";
    }

    private static int findBlueDyeSlot(MinecraftClient client) {
        for (int i = 0; i < client.player.currentScreenHandler.slots.size(); i++) {
            Slot slot = client.player.currentScreenHandler.slots.get(i);
            ItemStack stack = slot.getStack();
            if (!stack.isEmpty() && stack.isOf(Items.BLUE_DYE)) return i;
        }
        return -1;
    }

    private static String currentBlueDyeText(MinecraftClient client) {
        if (client.player == null) return "";
        for (Slot slot : client.player.currentScreenHandler.slots) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty() || !stack.isOf(Items.BLUE_DYE)) continue;
            StringBuilder text = new StringBuilder(stack.getName().getString());
            LoreComponent lore = stack.get(DataComponentTypes.LORE);
            if (lore != null) {
                for (Text line : lore.lines()) text.append(' ').append(line.getString());
            }
            return text.toString();
        }
        return "";
    }

    private static Integer readPercent(String text) {
        Matcher matcher = PERCENT.matcher(text);
        Integer found = null;
        while (matcher.find()) {
            try {
                int value = Integer.parseInt(matcher.group(1));
                if (value >= 0 && value <= 100) found = value;
            } catch (NumberFormatException ignored) { }
        }
        return found;
    }
}
