package tfar.kothcrown.client;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import org.lwjgl.glfw.GLFW;


public class LootTableSuggestions {
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
    private static final Style UNPARSED_STYLE = Style.EMPTY.withColor(ChatFormatting.RED);
    private static final Style LITERAL_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
    private static final List<Style> ARGUMENT_STYLES = Stream.of(ChatFormatting.AQUA, ChatFormatting.YELLOW, ChatFormatting.GREEN, ChatFormatting.LIGHT_PURPLE, ChatFormatting.GOLD).map(Style.EMPTY::withColor).collect(ImmutableList.toImmutableList());
    final Minecraft minecraft;
    private final Screen screen;
    final EditBox input;
    final Font font;
    final int lineStartOffset;
    final int suggestionLineLimit;
    final boolean anchorToBottom;
    final int fillColor;
    private final List<FormattedCharSequence> commandUsage = Lists.newArrayList();
    private int commandUsagePosition;
    private int commandUsageWidth;
    @Nullable
    private ParseResults<SharedSuggestionProvider> currentParse;
    @Nullable
    private CompletableFuture<Suggestions> pendingSuggestions;
    @Nullable
    private LootTableSuggestions.SuggestionsList suggestions;
    boolean keepSuggestions;

    int panelX;
    int panelY;

    public LootTableSuggestions(Minecraft pMinecraft, Screen pScreen, EditBox pInput, Font pFont, boolean pOnlyShowIfCursorPastError, int pLineStartOffset, int pSuggestionLineLimit, boolean pAnchorToBottom, int pFillColor) {
        this.minecraft = pMinecraft;
        this.screen = pScreen;
        this.input = pInput;
        this.font = pFont;
        this.lineStartOffset = pLineStartOffset;
        this.suggestionLineLimit = pSuggestionLineLimit;
        this.anchorToBottom = pAnchorToBottom;
        this.fillColor = pFillColor;
        pInput.setFormatter(this::formatChat);
        panelY = 25;
    }

    public void setAllowSuggestions(boolean pAutoSuggest) {
        if (!pAutoSuggest) {
            this.suggestions = null;
        }

    }

    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (this.suggestions != null && this.suggestions.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            return true;
        } else if (this.screen.getFocused() == this.input && pKeyCode == GLFW.GLFW_KEY_TAB) {
            this.showSuggestions(true);
            return true;
        } else {
            return false;
        }
    }

    public boolean mouseScrolled(double pDelta) {
        return this.suggestions != null && this.suggestions.mouseScrolled(Mth.clamp(pDelta, -1.0D, 1.0D));
    }

    public boolean mouseClicked(double pMouseX, double pMouseY, int pMouseButton) {
        return this.suggestions != null && this.suggestions.mouseClicked((int)pMouseX, (int)pMouseY, pMouseButton);
    }

    public void showSuggestions(boolean pNarrateFirstSuggestion) {
        if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
            Suggestions suggestions = this.pendingSuggestions.join();
            if (!suggestions.isEmpty()) {
                int i = 0;

                for(Suggestion suggestion : suggestions.getList()) {
                    i = Math.max(i, this.font.width(suggestion.getText()));
                }

                int j = Mth.clamp(this.input.getScreenX(suggestions.getRange().getStart()), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - i) + panelX;
                int k = this.anchorToBottom ? this.screen.height - 12 : 72 + panelY;
                this.suggestions = new LootTableSuggestions.SuggestionsList(j, k, i, this.sortSuggestions(suggestions), pNarrateFirstSuggestion);
            }
        }

    }

    public void hide() {
        this.suggestions = null;
    }

    private List<Suggestion> sortSuggestions(Suggestions pSuggestions) {
        String s = this.input.getValue().substring(0, this.input.getCursorPosition());
        int i = getLastWordIndex(s);
        String s1 = s.substring(i).toLowerCase(Locale.ROOT);
        List<Suggestion> list = Lists.newArrayList();
        List<Suggestion> list1 = Lists.newArrayList();

        for(Suggestion suggestion : pSuggestions.getList()) {
            if (!suggestion.getText().startsWith(s1) && !suggestion.getText().startsWith("minecraft:" + s1)) {
                list1.add(suggestion);
            } else {
                list.add(suggestion);
            }
        }

        list.addAll(list1);
        return list;
    }

    public void updateCommandInfo() {
        String s = this.input.getValue();
        if (this.currentParse != null && !this.currentParse.getReader().getString().equals(s)) {
            this.currentParse = null;
        }

        if (!this.keepSuggestions) {
            this.input.setSuggestion(null);
            this.suggestions = null;
        }

        this.commandUsage.clear();


        int i = this.input.getCursorPosition();
        String s1 = s.substring(0, i);
        int k = getLastWordIndex(s1);
        Collection<String> collection = this.minecraft.player.connection.getSuggestionsProvider().getCustomTabSugggestions();
        this.pendingSuggestions = SharedSuggestionProvider.suggest(collection, new SuggestionsBuilder(s1, k));

    }

    private static int getLastWordIndex(String pText) {
        if (Strings.isNullOrEmpty(pText)) {
            return 0;
        } else {
            int i = 0;

            for(Matcher matcher = WHITESPACE_PATTERN.matcher(pText); matcher.find(); i = matcher.end()) {
            }

            return i;
        }
    }

    private FormattedCharSequence formatChat(String p_93915_, int p_93916_) {
        return this.currentParse != null ? formatText(this.currentParse, p_93915_, p_93916_) : FormattedCharSequence.forward(p_93915_, Style.EMPTY);
    }

    @Nullable
    static String calculateSuggestionSuffix(String pInputText, String pSuggestionText) {
        return pSuggestionText.startsWith(pInputText) ? pSuggestionText.substring(pInputText.length()) : null;
    }

    private static FormattedCharSequence formatText(ParseResults<SharedSuggestionProvider> pProvider, String pCommand, int pMaxLength) {
        List<FormattedCharSequence> list = Lists.newArrayList();
        int i = 0;
        int j = -1;
        CommandContextBuilder<SharedSuggestionProvider> commandcontextbuilder = pProvider.getContext().getLastChild();

        for(ParsedArgument<SharedSuggestionProvider, ?> parsedargument : commandcontextbuilder.getArguments().values()) {
            ++j;
            if (j >= ARGUMENT_STYLES.size()) {
                j = 0;
            }

            int k = Math.max(parsedargument.getRange().getStart() - pMaxLength, 0);
            if (k >= pCommand.length()) {
                break;
            }

            int l = Math.min(parsedargument.getRange().getEnd() - pMaxLength, pCommand.length());
            if (l > 0) {
                list.add(FormattedCharSequence.forward(pCommand.substring(i, k), LITERAL_STYLE));
                list.add(FormattedCharSequence.forward(pCommand.substring(k, l), ARGUMENT_STYLES.get(j)));
                i = l;
            }
        }

        if (pProvider.getReader().canRead()) {
            int i1 = Math.max(pProvider.getReader().getCursor() - pMaxLength, 0);
            if (i1 < pCommand.length()) {
                int j1 = Math.min(i1 + pProvider.getReader().getRemainingLength(), pCommand.length());
                list.add(FormattedCharSequence.forward(pCommand.substring(i, i1), LITERAL_STYLE));
                list.add(FormattedCharSequence.forward(pCommand.substring(i1, j1), UNPARSED_STYLE));
                i = j1;
            }
        }

        list.add(FormattedCharSequence.forward(pCommand.substring(i), LITERAL_STYLE));
        return FormattedCharSequence.composite(list);
    }

    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (!this.renderSuggestions(pGuiGraphics, pMouseX, pMouseY)) {
            this.renderUsage(pGuiGraphics);
        }

    }

    public boolean renderSuggestions(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        if (this.suggestions != null) {
            this.suggestions.render(pGuiGraphics, pMouseX, pMouseY);
            return true;
        } else {
            return false;
        }
    }

    public void renderUsage(GuiGraphics pGuiGraphics) {
        int i = 0;

        for(FormattedCharSequence formattedcharsequence : this.commandUsage) {
            int j = this.anchorToBottom ? this.screen.height - 14 - 13 - 12 * i : 72 + 12 * i;
            pGuiGraphics.fill(this.commandUsagePosition - 1, j, this.commandUsagePosition + this.commandUsageWidth + 1, j + 12, this.fillColor);
            pGuiGraphics.drawString(this.font, formattedcharsequence, this.commandUsagePosition, j + 2, -1);
            ++i;
        }

    }

    public class SuggestionsList {
        private final Rect2i rect;
        private final String originalContents;
        private final List<Suggestion> suggestionList;
        private int offset;
        private int current;
        private Vec2 lastMouse = Vec2.ZERO;
        private boolean tabCycles;
        private int lastNarratedEntry;

        SuggestionsList(int pXPos, int pYPos, int pWidth, List<Suggestion> pSuggestionList, boolean pNarrateFirstSuggestion) {
            int i = pXPos - 1;
            int j = LootTableSuggestions.this.anchorToBottom ? pYPos - 3 - Math.min(pSuggestionList.size(), LootTableSuggestions.this.suggestionLineLimit) * 12 : pYPos;
            this.rect = new Rect2i(i, j, pWidth + 1, Math.min(pSuggestionList.size(), LootTableSuggestions.this.suggestionLineLimit) * 12);
            this.originalContents = LootTableSuggestions.this.input.getValue();
            this.lastNarratedEntry = pNarrateFirstSuggestion ? -1 : 0;
            this.suggestionList = pSuggestionList;
            this.select(0);
        }

        public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
            int i = Math.min(this.suggestionList.size(), LootTableSuggestions.this.suggestionLineLimit);
            int j = -5592406;
            boolean flag = this.offset > 0;
            boolean flag1 = this.suggestionList.size() > this.offset + i;
            boolean flag2 = flag || flag1;
            boolean flag3 = this.lastMouse.x != (float)pMouseX || this.lastMouse.y != (float)pMouseY;
            if (flag3) {
                this.lastMouse = new Vec2((float)pMouseX, (float)pMouseY);
            }

            if (flag2) {
                pGuiGraphics.fill(this.rect.getX(), this.rect.getY() - 1, this.rect.getX() + this.rect.getWidth(), this.rect.getY(), LootTableSuggestions.this.fillColor);
                pGuiGraphics.fill(this.rect.getX(), this.rect.getY() + this.rect.getHeight(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight() + 1, LootTableSuggestions.this.fillColor);
                if (flag) {
                    for(int k = 0; k < this.rect.getWidth(); ++k) {
                        if (k % 2 == 0) {
                            pGuiGraphics.fill(this.rect.getX() + k, this.rect.getY() - 1, this.rect.getX() + k + 1, this.rect.getY(), -1);
                        }
                    }
                }

                if (flag1) {
                    for(int i1 = 0; i1 < this.rect.getWidth(); ++i1) {
                        if (i1 % 2 == 0) {
                            pGuiGraphics.fill(this.rect.getX() + i1, this.rect.getY() + this.rect.getHeight(), this.rect.getX() + i1 + 1, this.rect.getY() + this.rect.getHeight() + 1, -1);
                        }
                    }
                }
            }

            boolean flag4 = false;

            for(int l = 0; l < i; ++l) {
                Suggestion suggestion = this.suggestionList.get(l + this.offset);
                pGuiGraphics.fill(this.rect.getX(), this.rect.getY() + 12 * l, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * l + 12, LootTableSuggestions.this.fillColor);
                if (pMouseX > this.rect.getX() && pMouseX < this.rect.getX() + this.rect.getWidth() && pMouseY > this.rect.getY() + 12 * l && pMouseY < this.rect.getY() + 12 * l + 12) {
                    if (flag3) {
                        this.select(l + this.offset);
                    }

                    flag4 = true;
                }

                pGuiGraphics.drawString(font, suggestion.getText(), this.rect.getX() + 1, this.rect.getY() + 2 + 12 * l, l + this.offset == this.current ? -256 : -5592406);
            }

            if (flag4) {
                Message message = this.suggestionList.get(this.current).getTooltip();
                if (message != null) {
                    pGuiGraphics.renderTooltip(font, ComponentUtils.fromMessage(message), pMouseX, pMouseY);
                }
            }

        }

        public boolean mouseClicked(int pMouseX, int pMouseY, int pMouseButton) {
            if (!this.rect.contains(pMouseX, pMouseY)) {
                return false;
            } else {
                int i = (pMouseY - this.rect.getY()) / 12 + this.offset;
                if (i >= 0 && i < this.suggestionList.size()) {
                    this.select(i);
                    this.useSuggestion();
                }

                return true;
            }
        }

        public boolean mouseScrolled(double pDelta) {
            int i = (int)(LootTableSuggestions.this.minecraft.mouseHandler.xpos() * (double) LootTableSuggestions.this.minecraft.getWindow().getGuiScaledWidth() / (double) LootTableSuggestions.this.minecraft.getWindow().getScreenWidth());
            int j = (int)(LootTableSuggestions.this.minecraft.mouseHandler.ypos() * (double) LootTableSuggestions.this.minecraft.getWindow().getGuiScaledHeight() / (double) LootTableSuggestions.this.minecraft.getWindow().getScreenHeight());
            if (this.rect.contains(i, j)) {
                this.offset = Mth.clamp((int)((double)this.offset - pDelta), 0, Math.max(this.suggestionList.size() - LootTableSuggestions.this.suggestionLineLimit, 0));
                return true;
            } else {
                return false;
            }
        }

        public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
            if (pKeyCode == 265) {
                this.cycle(-1);
                this.tabCycles = false;
                return true;
            } else if (pKeyCode == 264) {
                this.cycle(1);
                this.tabCycles = false;
                return true;
            } else if (pKeyCode == 258) {
                if (this.tabCycles) {
                    this.cycle(Screen.hasShiftDown() ? -1 : 1);
                }

                this.useSuggestion();
                return true;
            } else if (pKeyCode == 256) {
                LootTableSuggestions.this.hide();
                return true;
            } else {
                return false;
            }
        }

        public void cycle(int pChange) {
            this.select(this.current + pChange);
            int i = this.offset;
            int j = this.offset + LootTableSuggestions.this.suggestionLineLimit - 1;
            if (this.current < i) {
                this.offset = Mth.clamp(this.current, 0, Math.max(this.suggestionList.size() - LootTableSuggestions.this.suggestionLineLimit, 0));
            } else if (this.current > j) {
                this.offset = Mth.clamp(this.current + LootTableSuggestions.this.lineStartOffset - LootTableSuggestions.this.suggestionLineLimit, 0, Math.max(this.suggestionList.size() - LootTableSuggestions.this.suggestionLineLimit, 0));
            }

        }

        public void select(int pIndex) {
            this.current = pIndex;
            if (this.current < 0) {
                this.current += this.suggestionList.size();
            }

            if (this.current >= this.suggestionList.size()) {
                this.current -= this.suggestionList.size();
            }

            Suggestion suggestion = this.suggestionList.get(this.current);
            LootTableSuggestions.this.input.setSuggestion(LootTableSuggestions.calculateSuggestionSuffix(LootTableSuggestions.this.input.getValue(), suggestion.apply(this.originalContents)));
            if (this.lastNarratedEntry != this.current) {
                LootTableSuggestions.this.minecraft.getNarrator().sayNow(this.getNarrationMessage());
            }

        }

        public void useSuggestion() {
            Suggestion suggestion = this.suggestionList.get(this.current);
            LootTableSuggestions.this.keepSuggestions = true;
            LootTableSuggestions.this.input.setValue(suggestion.apply(this.originalContents));
            int i = suggestion.getRange().getStart() + suggestion.getText().length();
            LootTableSuggestions.this.input.setCursorPosition(i);
            LootTableSuggestions.this.input.setHighlightPos(i);
            this.select(this.current);
            LootTableSuggestions.this.keepSuggestions = false;
            this.tabCycles = true;
        }

        Component getNarrationMessage() {
            this.lastNarratedEntry = this.current;
            Suggestion suggestion = this.suggestionList.get(this.current);
            Message message = suggestion.getTooltip();
            return message != null ? Component.translatable("narration.suggestion.tooltip", this.current + 1, this.suggestionList.size(), suggestion.getText(), message) : Component.translatable("narration.suggestion", this.current + 1, this.suggestionList.size(), suggestion.getText());
        }
    }
}