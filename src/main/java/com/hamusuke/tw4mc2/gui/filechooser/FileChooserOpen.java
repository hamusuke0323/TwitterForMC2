package com.hamusuke.tw4mc2.gui.filechooser;

import javax.swing.*;
import java.io.File;
import java.util.function.Consumer;

public class FileChooserOpen extends AbstractFileChooser {
    public FileChooserOpen(Consumer<File> onChose, File initDir) {
        super(onChose, initDir);
    }

    @Override
    protected void startChoosing() {
        SwingUtilities.invokeLater(() -> {
            JFrame jFrame = new JFrame();
            JFileChooser jFileChooser = new JFileChooser();
            this.jFileChooser.set(jFileChooser);
            jFileChooser.setCurrentDirectory(this.initDir);
            if (jFileChooser.showOpenDialog(jFrame) == JFileChooser.APPROVE_OPTION) {
                this.onChose.accept(jFileChooser.getSelectedFile());
            }
            this.choosing.set(false);
            jFrame.dispose();
            this.jFileChooser.set(null);
        });
    }
}
