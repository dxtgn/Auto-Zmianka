package pl.autozmianka;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class AutoZmiankaScreen extends Screen {
    public AutoZmiankaScreen() { super(Text.literal("Auto-Zmianka")); }

    @Override
    protected void init() {
        int center = width / 2;
        int top = height / 2 - 90;

        addDrawableChild(ButtonWidget.builder(
                Text.literal(AutoZmiankaClient.isRunning() ? "STOP / RESET" : "START"),
                button -> {
                    if (AutoZmiankaClient.isRunning())
                        AutoZmiankaClient.stopAndReset(client, "Zatrzymana ręcznie");
                    else AutoZmiankaClient.start(client);
                    clearAndInit();
                }).dimensions(center - 100, top, 200, 20).build());

        addDrawableChild(new SliderWidget(center - 100, top + 34, 200, 20,
                Text.literal("Próg: " + AutoZmiankaClient.CONFIG.threshold + "%"),
                (AutoZmiankaClient.CONFIG.threshold - 1) / 99.0) {
            @Override protected void updateMessage() {
                int n = 1 + (int) Math.round(value * 99);
                setMessage(Text.literal("Próg: " + n + "%"));
            }
            @Override protected void applyValue() {
                AutoZmiankaClient.CONFIG.threshold = 1 + (int) Math.round(value * 99);
                AutoZmiankaClient.CONFIG.save();
            }
        });

        addDrawableChild(new SliderWidget(center - 100, top + 64, 200, 20,
                Text.literal("Szybkość: " + AutoZmiankaClient.CONFIG.clickDelayMs + " ms"),
                (AutoZmiankaClient.CONFIG.clickDelayMs - 200) / 1800.0) {
            @Override protected void updateMessage() {
                int ms = 200 + (int) Math.round(value * 36) * 50;
                setMessage(Text.literal("Szybkość: " + ms + " ms"));
            }
            @Override protected void applyValue() {
                AutoZmiankaClient.CONFIG.clickDelayMs = 200 + (int) Math.round(value * 36) * 50;
                AutoZmiankaClient.CONFIG.save();
            }
        });

        addDrawableChild(ButtonWidget.builder(Text.literal("Zamknij"), button -> close())
                .dimensions(center - 100, top + 98, 200, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, height / 2 - 116, 0xFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("Status: " + AutoZmiankaClient.getStatus()),
                width / 2, height / 2 + 34, 0xAAAAAA);
        context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("R = START/STOP   |   Right Shift = menu"),
                width / 2, height / 2 + 48, 0x888888);
    }
}
