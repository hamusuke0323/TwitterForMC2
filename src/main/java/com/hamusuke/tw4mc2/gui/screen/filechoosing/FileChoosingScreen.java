package com.hamusuke.tw4mc2.gui.screen.filechoosing;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.util.List;

public class FileChoosingScreen extends Screen {
    private static final TranslationTextComponent OPEN_FILE = new TranslationTextComponent("tw.open.file");
    private static final TranslationTextComponent NAME_AND_SAVE = new TranslationTextComponent("tw.save.file");
    protected final Mode mode;
    protected final boolean multipleSelectable;
    protected final List<File> currentSelectedFile = Lists.newArrayList();
    protected final List<File> currentlyShowing = Lists.newArrayList();
    @Nullable
    protected File currentDir;
    protected final FileSystemView fileSystemView = FileSystemView.getFileSystemView();

    public FileChoosingScreen(@Nullable File currentDir, @Nullable File current, Mode mode, boolean multipleSelectable) {
        super(mode.text);
        this.currentDir = currentDir;
        this.currentSelectedFile.clear();
        if (current != null) {
            this.currentSelectedFile.add(current);
        }
        this.mode = mode;
        this.multipleSelectable = this.mode != Mode.SAVE && multipleSelectable;
    }

    public static FileChoosingScreen createOpen(@Nullable File currentDir, boolean multipleSelectable) {
        return new FileChoosingScreen(currentDir, null, Mode.OPEN, multipleSelectable);
    }

    public static FileChoosingScreen createSave(@Nullable File currentDir, @Nullable File current) {
        return new FileChoosingScreen(currentDir, current, Mode.SAVE, false);
    }

    @Override
    protected void init() {
        super.init();

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {

        super.render(matrices, mouseX, mouseY, delta);
    }

    protected void update() {

    }

    public enum Mode {
        OPEN(OPEN_FILE),
        SAVE(NAME_AND_SAVE);

        private final ITextComponent text;

        Mode(ITextComponent text) {
            this.text = text;
        }

        public ITextComponent getText() {
            return this.text;
        }
    }

    class FileList extends ExtendedList<FileList.FileEntry> {
        public FileList() {
            super(FileChoosingScreen.this.minecraft, FileChoosingScreen.this.width, FileChoosingScreen.this.height, 15, FileChoosingScreen.this.height - 20, 10);


        }

        class FileEntry extends ExtendedList.AbstractListEntry<FileEntry> {

            @Override
            public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {

            }
        }
    }
}
