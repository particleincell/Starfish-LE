package starfish.gui.common;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.function.Consumer;

/**
 * Text area and button that brings up FileChooser dialog
 */
public class JTextFileChooserCombo extends JPanel {

    private String description;
    private String[] extensions;

    private JTextField textField;

    public JTextFileChooserCombo(String initialValue, String description, String... extensions) {
        this.description = description;
        this.extensions = extensions;

        textField = new JTextField(initialValue);
        textField.addActionListener(arg0 -> {
            File file = new File(textField.getText());
            if (file.exists()) {
                tryToCallOnUpdate(file);
            } else {
                JOptionPane.showMessageDialog(this, file.getAbsolutePath() + " does not exist");
            }
        });

        JButton chooseFileButton = new JButton("Choose file");
        chooseFileButton.addActionListener(arg0 -> {
            File file = fileChooserDialog();
            if (file != null) {
                textField.setText(file.getAbsolutePath());
                tryToCallOnUpdate(file);
            }
        });

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx += 1;
        c.weightx = 1;
        add(textField, c);
        c.gridx += 1;
        c.weightx = 0;
        add(chooseFileButton, c);
    }

    public void setValue(File file) {
        textField.setText(file.getAbsolutePath());
        tryToCallOnUpdate(file);
    }

    /**
     * @return Returns File if user selected something, null if no file is selected
     */
    public File getValue() {
        return textField.getText().isBlank() ? null : new File(textField.getText());
    }

    private Consumer<File> onUpdate;
    public void setOnUpdate(Consumer<File> onUpdate) {
        this.onUpdate = onUpdate;
    }
    private void tryToCallOnUpdate(File file) {
        if (onUpdate != null) {
            onUpdate.accept(file);
        }
    }

    /**
     * @return File of selected file, null if no file is selected
     */
    private File fileChooserDialog() {
        UIManager.put("FileChooser.readOnly", Boolean.TRUE); //disable new folder button

        //get last directory

        JFileChooser fileChooser = new JFileChooser(textField.getText());
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        //fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(description, extensions));
        fileChooser.setAcceptAllFileFilterUsed(true);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            return selectedFile;
        }
        return null;
    }

}
